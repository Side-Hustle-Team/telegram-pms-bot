package telegram.pms.com.example.demo.telegram;

import java.util.List;

public record TelegramUpdateResponse(
        Boolean ok,
        List<TelegramUpdate> result
) {
}