package code.domain.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.mail.Message;
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long>
{


}
