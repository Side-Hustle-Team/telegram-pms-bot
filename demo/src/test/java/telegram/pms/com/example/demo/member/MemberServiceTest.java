package telegram.pms.com.example.demo.member;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void cleaDatabase() {
        memberRepository.deleteAll();
    }

    @Test
    void registerTelegramMemberCreatesNewMember() {
        Member member = memberService.registerTelegramMember(
                123456789L,
                "john_doe",
                "John",
                "Doe"
        );

        assertThat(member.getId()).isNotNull();
        assertThat(member.getTelegramChatId()).isEqualTo(123456789L);
        assertThat(member.getTelegramUsername()).isEqualTo("john_doe");
        assertThat(member.getFirstName()).isEqualTo("John");
        assertThat(member.getLastName()).isEqualTo("Doe");
        assertThat(member.getTelegramConnected()).isTrue();
        assertThat(member.getConnectedAt()).isNotNull();
    }

    @Test
    void registerTelegramMemberReconnectsExistingMember() {
        memberService.registerTelegramMember(
                987654321L,
                "old_username",
                "Old",
                "Name"
        );

        Member updatedMember = memberService.registerTelegramMember(
                987654321L,
                "new_username",
                "New",
                "Name"
        );

        assertThat(memberRepository.findAll()).hasSize(1);
        assertThat(updatedMember.getTelegramChatId()).isEqualTo(987654321L);
        assertThat(updatedMember.getTelegramUsername()).isEqualTo("new_username");
        assertThat(updatedMember.getFirstName()).isEqualTo("New");
        assertThat(updatedMember.getLastName()).isEqualTo("Name");
        assertThat(updatedMember.getTelegramConnected()).isTrue();
    }
}
