package telegram.pms.com.example.demo.messagelog;

import org.springframework.stereotype.Service;

import telegram.pms.com.example.demo.member.Member;


// A way to log the result in a clean way
@Service
public class MessageLogService {

    private final MessageLogRepository messageLogRepository;

    public MessageLogService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    public MessageLog logSent(Member member, String messageContent){
        MessageLog messageLog = MessageLog.sent(member, messageContent);
        return messageLogRepository.save(messageLog);
    }

    public MessageLog logFailed(Member member, String messageContent, String errorMessage) {
        MessageLog messageLog = MessageLog.failed(member, messageContent, errorMessage);
        return messageLogRepository.save(messageLog);
    }       
}