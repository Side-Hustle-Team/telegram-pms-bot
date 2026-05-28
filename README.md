# Telegram PMS Bot

Spring Boot backend for sending private Telegram bot messages to registered users.

The app can:

- Register Telegram users when they send `/start` to the bot
- Store member details in PostgreSQL
- View connected members through an API
- Send broadcasts to all connected members
- Send broadcasts to selected members by internal member ID
- Log successful and failed broadcast sends
- Notify an admin chat when users reply to the bot

## Project Structure

```text
telegram-pms-bot/
  README.md
  demo/
    pom.xml
    .env
    src/main/java/...
    src/main/resources/application.properties
    src/test/resources/application.properties
```

Run Maven commands from the `demo` folder.

```powershell
cd demo
```

## Requirements

- Java 21
- Maven
- PostgreSQL running locally
- Telegram bot token from BotFather
- VS Code PostgreSQL extension or another PostgreSQL client
- Postman, optional but useful for testing APIs

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE qninja_bypass_tele_bot;
```

The app currently expects:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/qninja_bypass_tele_bot
spring.datasource.username=postgres
spring.datasource.password=******
```

Update `demo/src/main/resources/application.properties` if your local PostgreSQL username or password is different.

During development, Hibernate creates and updates tables automatically:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## Environment Variables

Create this file:

```text
demo/.env
```

Example:

```properties
TELEGRAM_BOT_TOKEN=your_bot_token_here
TELEGRAM_ADMIN_CHAT_ID=your_admin_telegram_chat_id_here
```

Important:

- Do not add spaces around `=`.
- Do not add comments on the same line.
- Do not commit `.env`.
- If your bot token was exposed, regenerate it in BotFather.

The app loads `.env` through:

```properties
spring.config.import=optional:file:.env[.properties]
```

This means you can run the app without manually setting `$env:...` in PowerShell.

## Run Tests

```powershell
mvn test
```

Tests use H2 from `src/test/resources/application.properties`, while the running app uses PostgreSQL.

## Run The App

From `demo`:

```powershell
mvn spring-boot:run
```

Wait for:

```text
Started DemoApplication
```

Health check:

```text
GET http://localhost:8080/health
```

Expected response:

```text
Telegram PMS Bot is running
```

## Telegram Registration Flow

1. Start the Spring Boot app.
2. Open the Telegram bot chat.
3. Send:

```text
/start
```

Expected bot reply:

```text
Telegram connected successfully.
```

The user is saved in the `members` table.

## API Endpoints

### Health

```text
GET /health
```

### List Connected Members

```text
GET /api/members
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Zann Ameno",
    "telegramUsername": "jclenggg24"
  }
]
```

Use `id` when sending to selected recipients.

### Send Broadcast To All Connected Members

```text
POST /api/broadcast/send
Content-Type: application/json
```

Body:

```json
{
  "message": "Hello all connected members"
}
```

### Send Broadcast To Selected Members

```text
POST /api/broadcast/send
Content-Type: application/json
```

Body:

```json
{
  "message": "Hello selected members",
  "memberIds": [1, 3, 5]
}
```

`memberIds` are internal PostgreSQL `members.id` values, not Telegram chat IDs.

Example response:

```json
{
  "totalRecipients": 3,
  "successCount": 3,
  "failedCount": 0
}
```

## Useful PostgreSQL Queries

View registered members:

```sql
SELECT
    id,
    telegram_chat_id,
    telegram_username,
    first_name,
    last_name,
    telegram_connected,
    connected_at
FROM members
ORDER BY id;
```

View message logs:

```sql
SELECT *
FROM message_logs
ORDER BY created_at DESC;
```

Cleaner message log view:

```sql
SELECT
    ml.id,
    ml.member_id,
    m.first_name,
    m.last_name,
    m.telegram_username,
    ml.telegram_chat_id,
    ml.message_content,
    ml.status,
    ml.error_message,
    ml.sent_at,
    ml.created_at
FROM message_logs ml
JOIN members m ON m.id = ml.member_id
ORDER BY ml.created_at DESC;
```

## Telegram Reply Notifications

When a user sends a normal message to the bot, the app sends a notification to:

```properties
TELEGRAM_ADMIN_CHAT_ID
```

This uses the same Telegram `sendMessage` API. The notification appears inside the admin's Telegram chat with the bot.

## Common Issues

### Bot does not reply

Check:

- Spring Boot is running
- `.env` is in the `demo` folder
- `TELEGRAM_BOT_TOKEN` is valid
- You started the app from the `demo` folder
- You sent a fresh `/start` message

Test the token:

```powershell
Invoke-RestMethod "https://api.telegram.org/botYOUR_TOKEN_HERE/getMe"
```

Expected:

```text
ok : True
```

### 401 Unauthorized from Telegram

The bot token is invalid, empty, revoked, or not loaded from `.env`.

Fix:

- Regenerate token in BotFather if needed
- Update `demo/.env`
- Restart Spring Boot

### `psql` is not recognized

PostgreSQL may still be working. This only means the `psql` command-line tool is not in your Windows PATH.

You can keep using the VS Code PostgreSQL extension.

## Current Development Notes

- There is no admin authentication yet.
- Broadcast endpoints should only be used locally for now.
- H2 is used for tests.
- PostgreSQL is used for local runtime data.
- Message logs are stored for each broadcast recipient.
