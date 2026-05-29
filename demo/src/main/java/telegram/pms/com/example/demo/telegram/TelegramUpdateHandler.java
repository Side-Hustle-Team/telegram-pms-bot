package telegram.pms.com.example.demo.telegram;

import org.springframework.stereotype.Service;

import telegram.pms.com.example.demo.member.Member;
import telegram.pms.com.example.demo.member.MemberService;
import telegram.pms.com.example.demo.telegram.properties.TelegramAdminProperties;

@Service
public class TelegramUpdateHandler {

    private final MemberService memberService;
    private final TelegramAdminProperties adminProperties;
    private final TelegramMessageSender telegramMessageSender;

    public TelegramUpdateHandler(
            MemberService memberService,
            TelegramAdminProperties adminProperties,
            TelegramMessageSender telegramMessageSender
    ) {
        this.memberService = memberService;
        this.adminProperties = adminProperties;
        this.telegramMessageSender = telegramMessageSender;
    }

    public void handleUpdate(TelegramUpdate update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        String text = update.message().text();

        if (text.startsWith("/start")) {
            handleStart(update);
            notifyAdminAboutStart(update); // Notify admin about new connection as well
            return;
        }

        notifyAdminAboutReply(update);

        Long chatId = update.message().chat().id();
        TelegramUpdate.TelegramUser user = update.message().from();

        memberService.registerTelegramMember(
                chatId,
                user.username(),
                user.first_name(),
                user.last_name()
        );

        // telegramMessageSender.sendMessage(chatId, "Telegram connected successfully.");   // No need to send a confirmation message for every reply, as the admin will receive the notification with the user's info anyway
    }

    private void handleStart(TelegramUpdate update) {
        Long chatId = update.message().chat().id();
        TelegramUpdate.TelegramUser user = update.message().from();

        memberService.registerTelegramMember(
                chatId,
                user.username(),
                user.first_name(),
                user.last_name()
        );

        telegramMessageSender.sendMessage(chatId, "Successfully connected to Qninja. Welcome aboard!");
    }

    private void notifyAdminAboutStart(TelegramUpdate update) {
        TelegramUpdate.TelegramUser user = update.message().from();

        String senderName = buildSenderName(user);
        String username = user.username() == null ? "N/A" : "@" + user.username();

        Long chatId = update.message().chat().id();
        Member member = memberService.findByTelegramChatId(chatId);
        Long memberId = member == null ? null : member.getId();

        String notification = """
            %s has successfully connected to Qninja.

            Username: %s
            User ID: %s
            """.formatted(
                senderName,
                username,
                memberId
        );

        telegramMessageSender.sendMessage(adminProperties.getChatId(), notification);
    }

    private void notifyAdminAboutReply(TelegramUpdate update) {
        // Long userChatId = update.message().chat().id();

        TelegramUpdate.TelegramUser user = update.message().from();

        String senderName = buildSenderName(user);
        String username = user.username() == null ? "N/A" : "@" + user.username();

        Long chatId = update.message().chat().id();
        Member member = memberService.findByTelegramChatId(chatId);
        Long memberId = member == null ? null : member.getId();

        String notification = """
            New Incoming Message 

            ID: %s
            %s (%s)

            Message:
            %s
            """.formatted(
                memberId,
                senderName,
                username,
                // userChatId,
                update.message().text()
        );
        telegramMessageSender.sendMessage(adminProperties.getChatId(), notification);
    }

    private String buildSenderName(TelegramUpdate.TelegramUser user) {
        if (user.first_name() == null && user.last_name() == null) {
            return "Unknown";
        }

        if (user.first_name() == null) {
            return user.last_name();
        }

        if (user.last_name() == null) {
            return user.first_name();
        }

        return user.first_name() + " " + user.last_name();
    }
}
