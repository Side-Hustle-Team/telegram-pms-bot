package telegram.pms.com.example.demo.member;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/members")
    public List<MemberResponse> getMembers() {
        return memberService.getConnectedMembers()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
