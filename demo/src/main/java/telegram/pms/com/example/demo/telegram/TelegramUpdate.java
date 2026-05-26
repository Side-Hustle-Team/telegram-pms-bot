package telegram.pms.com.example.demo.telegram;

public record TelegramUpdate(
        Long update_id,
        TelegramMessage message
) {
    public record TelegramMessage(
            TelegramChat chat,
            TelegramUser from,
            String text
    ) {
    }

    public record TelegramChat(
            Long id
    ) {
    }

    public record TelegramUser(
            Long id,
            String username,
            String first_name,
            String last_name
    ) {
    }
}