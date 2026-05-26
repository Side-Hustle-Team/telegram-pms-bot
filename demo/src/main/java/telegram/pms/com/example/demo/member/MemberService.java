package telegram.pms.com.example.demo.member;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member registerTelegramMember(
            Long telegramChatId,
            String telegramUsername,
            String firstName,
            String lastName
    ) {
        return memberRepository.findByTelegramChatId(telegramChatId).map(existingMember -> {
            existingMember.reconnect(telegramUsername, firstName, lastName);
            return existingMember;
        }).orElseGet(() -> {
            Member newMember = new Member(telegramChatId, telegramUsername, firstName, lastName);
            return memberRepository.save(newMember);
        });
    }

    public List<Member> getConnectedMembers() {
        return memberRepository.findByTelegramConnectedTrue();
    }
}
