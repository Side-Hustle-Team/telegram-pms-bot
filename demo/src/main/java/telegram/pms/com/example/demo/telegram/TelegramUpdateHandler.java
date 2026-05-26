package telegram.pms.com.example.demo.telegram;

import org.springframework.stereotype.Service;

import telegram.pms.com.example.demo.member.MemberService;

@Service
public class TelegramUpdateHandler {

    private final MemberService memberService;
    private final TelegramMessageSender telegramMessageSender;

    public TelegramUpdateHandler(
            MemberService memberService,
            TelegramMessageSender telegramMessageSender
    ) {
        this.memberService = memberService;
        this.telegramMessageSender = telegramMessageSender;
    }

    public void handleUpdate(TelegramUpdate update) {
        if (update.message() == null || update.message().text() == null) {
            return;
        }

        if (!update.message().text().startsWith("/start")) {
            return;
        }

        Long chatId = update.message().chat().id();
        TelegramUpdate.TelegramUser user = update.message().from();

        memberService.registerTelegramMember(
                chatId,
                user.username(),
                user.first_name(),
                user.last_name()
        );

        telegramMessageSender.sendMessage(chatId, "Telegram connected successfully.");
    }
}