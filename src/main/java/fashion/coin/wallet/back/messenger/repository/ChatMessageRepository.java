package fashion.coin.wallet.back.messenger.repository;

import fashion.coin.wallet.back.messenger.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationIdOrderByTimestamp(Long conversationId);

    List<ChatMessage> findByConversationIdAndTimestampGreaterThanOrderByTimestamp(Long conversationId, Long timestamp);
}
