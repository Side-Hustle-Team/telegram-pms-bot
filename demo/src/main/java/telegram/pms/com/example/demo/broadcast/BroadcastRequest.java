package telegram.pms.com.example.demo.broadcast;

import jakarta.validation.constraints.NotBlank;

// Represents the request json body for broadcasting a message to all users.
public record BroadcastRequest(
        @NotBlank String message
) {
}