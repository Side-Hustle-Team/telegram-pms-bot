package telegram.pms.com.example.demo.telegram;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import telegram.pms.com.example.demo.telegram.properties.TelegramApiProperties;
import telegram.pms.com.example.demo.telegram.properties.TelegramBotProperties;

@Service
public class TelegramMessageSender {

    private final TelegramBotProperties botProperties;
    private final TelegramApiProperties apiProperties;
    private final RestClient restClient;

    public TelegramMessageSender(TelegramApiProperties apiProperties, TelegramBotProperties botProperties) {
        this.apiProperties = apiProperties;
        this.botProperties = botProperties;
        this.restClient = RestClient.create();
    }

    public void sendMessage(Long chatId, String text) {
        String url = apiProperties.getBaseUrl()
                + "/bot"
                + botProperties.getToken()
                + "/sendMessage";

        TelegramSendMessageRequest request = new TelegramSendMessageRequest(chatId, text);

        restClient.post()
                .uri(url)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendPhoto(Long chatId, String photoUrl, String caption) {
        String url = apiProperties.getBaseUrl()
                + "/bot" 
                + botProperties.getToken()
                + "/sendPhoto";

        // TelegramSendPhotoRequest request = new TelegramSendPhotoRequest(chatId, photoUrl, caption);

        // Fix: only include caption when it has a real text
        Map<String, Object> request = new HashMap<>(); // used as a flexible request body to conditionally include caption
        request.put("chat_id", chatId);
        request.put("photo", photoUrl);

        if(caption != null && !caption.isBlank()) {
            request.put("caption", caption);
        }

        restClient.post()
                .uri(url)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private record TelegramSendMessageRequest(Long chat_id, String text) {

    }

    private record TelegramSendPhotoRequest(Long chat_id, String photo, String caption) {

    }
}
