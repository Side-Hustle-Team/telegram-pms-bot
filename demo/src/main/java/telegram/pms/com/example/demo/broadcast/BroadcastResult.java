package telegram.pms.com.example.demo.broadcast;

// returned API - after sending a broadcast message
public record BroadcastResult(
        int totalRecipients,
        int successCount,
        int failedCount
) {
}