package fashion.coin.wallet.back.messenger.repository;

import fashion.coin.wallet.back.messenger.model.MyConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyConversationRepository extends JpaRepository<MyConversation,Long> {

    List<MyConversation> findByMyId(Long myId);
    MyConversation findTopByMyIdAndFriendId(Long myId, Long friendId);

}
