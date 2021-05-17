package fashion.coin.wallet.back.messenger.repository;

import fashion.coin.wallet.back.messenger.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation,Long> {
}
