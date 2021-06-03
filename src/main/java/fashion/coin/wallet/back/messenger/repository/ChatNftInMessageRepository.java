package fashion.coin.wallet.back.messenger.repository;

import fashion.coin.wallet.back.messenger.model.ChatNftInMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatNftInMessageRepository extends JpaRepository<ChatNftInMessage, Long> {
    List<ChatNftInMessage> findByMessageId(Long messageId);
}
