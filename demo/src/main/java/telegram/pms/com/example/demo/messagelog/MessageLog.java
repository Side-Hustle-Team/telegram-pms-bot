package telegram.pms.com.example.demo.messagelog;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import telegram.pms.com.example.demo.member.Member;

// creating message log entity to store the result of each message sent to Telegram, whether it is successful or failed, and the error message if failed. 
// This will help us to track the history of messages sent and troubleshoot any issues with Telegram API.
@Entity
@Table(name = "message_logs")
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "telegram_chat_id", nullable = false)
    private Long telegramChatId;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected MessageLog() {
    }

    private MessageLog(
            Member member,
            Long telegramChatId,
            String messageContent,
            MessageStatus status,
            String errorMessage,
            LocalDateTime sentAt
    ) {
        this.member = member;
        this.telegramChatId = telegramChatId;
        this.messageContent = messageContent;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
    }

    public static MessageLog sent(Member member, String messageContent) {
        return new MessageLog(
                member,
                member.getTelegramChatId(),
                messageContent,
                MessageStatus.SENT,
                null,
                LocalDateTime.now()
        );
    }

    public static MessageLog failed(Member member, String messageContent, String errorMessage) {
        return new MessageLog(
                member,
                member.getTelegramChatId(),
                messageContent,
                MessageStatus.FAILED,
                errorMessage,
                null
        );
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
