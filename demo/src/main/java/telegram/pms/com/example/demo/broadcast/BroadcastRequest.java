package telegram.pms.com.example.demo.broadcast;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;

// Represents the request json body for broadcasting a message to all users.
public record BroadcastRequest(
        // @NotBlank
        String message,
        String imageUrl,
        List<Long> memberIds
        ) {

    @AssertTrue(message = "Either message or imageUrl must be provided")
    public boolean hasMessageOrImage() {
        return hasText(message) || hasText(imageUrl);
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }
}
