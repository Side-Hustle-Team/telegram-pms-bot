package telegram.pms.com.example.demo.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@ConditionalOnProperty(name = "telegram.polling.enabled", havingValue = "true")
public class TelegramUpdatePoller {

    private final TelegramBotProperties botProperties;
    private final TelegramApiProperties apiProperties;
    private final TelegramUpdateHandler updateHandler;
    private final RestClient restClient = RestClient.create();

    private Long nextOffset = null;

    public TelegramUpdatePoller(
            TelegramBotProperties botProperties,
            TelegramApiProperties apiProperties,
            TelegramUpdateHandler updateHandler
    ) {
        this.botProperties = botProperties;
        this.apiProperties = apiProperties;
        this.updateHandler = updateHandler;
    }

    @Scheduled(fixedDelay = 3000)
    public void pollUpdates() {
        String url = apiProperties.getBaseUrl()
                + "/bot"
                + botProperties.getToken()
                + "/getUpdates";

        String requestUrl = url + "?timeout=1";

        if (nextOffset != null) {
            requestUrl = requestUrl + "&offset=" + nextOffset;
        }

        TelegramUpdateResponse response = restClient.get()
                .uri(requestUrl)
                .retrieve()
                .body(TelegramUpdateResponse.class);

        if (response == null || response.result() == null) {
            return;
        }

        for (TelegramUpdate update : response.result()) {
            updateHandler.handleUpdate(update);
            nextOffset = update.update_id() + 1;
        }
    }
}
