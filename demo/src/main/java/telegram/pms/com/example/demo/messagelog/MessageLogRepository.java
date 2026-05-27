package telegram.pms.com.example.demo.messagelog;

import org.springframework.data.jpa.repository.JpaRepository;

// To provide database access for MessageLog entity, allowing us to save and retrieve message logs from the database.
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {

// automatically provide basic CRUD operations:
    // save(messageLog)
    // findAll()
    // findById(id)
    // delete(messageLog)
}
