package telegram.pms.com.example.demo.broadcast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import telegram.pms.com.example.demo.member.Member;
import telegram.pms.com.example.demo.member.MemberService;
import telegram.pms.com.example.demo.messagelog.MessageLogService;
import telegram.pms.com.example.demo.telegram.TelegramMessageSender;

// BroadcastService business logic for sending broadcast messages to all connected Telegram users. 
// It retrieves the list of connected members and sends the message to each member's Telegram chat ID, while keeping track of successful and failed deliveries.
@Service
public class BroadcastService {

    private final MemberService memberService;
    private final TelegramMessageSender telegramMessageSender;
    private final MessageLogService messageLogService;

    public BroadcastService(
            MemberService memberService,
            TelegramMessageSender telegramMessageSender,
            MessageLogService messageLogService) {
        this.memberService = memberService;
        this.telegramMessageSender = telegramMessageSender;
        this.messageLogService = messageLogService;
    }

    public BroadcastResult sendBroadcast(BroadcastRequest request) {
        List<Member> recipients = memberService.getConnectedMembers(request.memberIds()); // get all member connected through /start command
        // If memberIds is provided, send to selected members, except excluded ones
        // If memberIds is not provided, send to all connected members, except excluded
        // ones
        if (request.excludeMemberIds() != null && !request.excludeMemberIds().isEmpty()) {
            Set<Long> excludeIds = new HashSet<>(request.excludeMemberIds());

            recipients = recipients.stream()
                .filter(member -> !excludeIds.contains(member.getId()))
                .toList();
        }

        int successCount = 0;
        int failedCount = 0;

        for (Member recipient : recipients) {
            // Prevents one failed recipient from stopping the whole broadcast.
            try {
                // Send a photo if imageUrl is provided, otherwise send a text message.
                if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
                    telegramMessageSender.sendPhoto(
                            recipient.getTelegramChatId(),
                            request.imageUrl(),
                            blankToNull(request.message()));
                } else {
                    telegramMessageSender.sendMessage(
                            recipient.getTelegramChatId(),
                            request.message());
                }

                messageLogService.logSent(recipient, request.message());
                successCount++;
            } catch (Exception exception) {
                messageLogService.logFailed(recipient, request.message(), exception.getMessage());
                failedCount++;
            }
        }

        return new BroadcastResult(
                recipients.size(),
                successCount,
                failedCount);
    }

    // Converts blank strings to null to avoid sending empty captions in Telegram
    // API, which can cause errors.
    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }
}
