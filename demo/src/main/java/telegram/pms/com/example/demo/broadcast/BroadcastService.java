package telegram.pms.com.example.demo.broadcast;

import java.util.List;

import org.springframework.stereotype.Service;

import telegram.pms.com.example.demo.member.Member;
import telegram.pms.com.example.demo.member.MemberService;
import telegram.pms.com.example.demo.telegram.TelegramMessageSender;

// BroadcastService business logic for sending broadcast messages to all connected Telegram users. 
// It retrieves the list of connected members and sends the message to each member's Telegram chat ID, while keeping track of successful and failed deliveries.
@Service
public class BroadcastService {

    private final MemberService memberService;
    private final TelegramMessageSender telegramMessageSender;

    public BroadcastService(
            MemberService memberService,
            TelegramMessageSender telegramMessageSender
    ) {
        this.memberService = memberService;
        this.telegramMessageSender = telegramMessageSender;
    }

    public BroadcastResult sendBroadcast(BroadcastRequest request) {
        List<Member> recipients = memberService.getConnectedMembers(); // get all member connected through /start command

        int successCount = 0;
        int failedCount = 0;

        for (Member recipient : recipients) {
            try { // Prevents one failed recipient from stopping the whole broadcast.
                telegramMessageSender.sendMessage(
                        recipient.getTelegramChatId(),
                        request.message()
                );

                successCount++;
            } catch (Exception exception) {
                failedCount++;
            }
        }

        return new BroadcastResult(
                recipients.size(),
                successCount,
                failedCount
        );
    }
}