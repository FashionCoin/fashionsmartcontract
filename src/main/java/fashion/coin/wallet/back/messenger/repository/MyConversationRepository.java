package fashion.coin.wallet.back.messenger.repository;

import fashion.coin.wallet.back.messenger.model.MyConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyConversationRepository extends JpaRepository<MyConversation,Long> {
}
