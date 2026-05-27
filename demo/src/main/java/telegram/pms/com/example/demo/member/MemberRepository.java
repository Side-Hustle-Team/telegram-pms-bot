package telegram.pms.com.example.demo.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByTelegramChatId(Long telegramChatId);

    List<Member> findByTelegramConnectedTrue();

    List<Member> findByIdInAndTelegramConnectedTrue(List<Long> memberIds); //  find only selected members who are connected
}
