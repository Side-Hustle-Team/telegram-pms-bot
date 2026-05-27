package telegram.pms.com.example.demo.broadcast;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class BroadcastController {

    private final BroadcastService broadcastService;

    public BroadcastController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @PostMapping("/api/broadcast/send") // POST /api/broadcast/send
    public BroadcastResult sendBroadcast(@Valid @RequestBody BroadcastRequest request) {
        return broadcastService.sendBroadcast(request);
    }
}