package telegram.pms.com.example.demo.telegram;

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

    private record TelegramSendMessageRequest(Long chat_id, String text) {

    }
}
