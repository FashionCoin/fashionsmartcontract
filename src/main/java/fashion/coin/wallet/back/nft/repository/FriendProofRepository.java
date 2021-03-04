package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.FriendProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendProofRepository extends JpaRepository<FriendProof,Long> {
    List<FriendProof> findByProofReceiverId(Long proofReceiverId);
    List<FriendProof> findByProofSenderId(Long proofSenderId);
    List<FriendProof> findByProofSenderIdAndProofReceiverId(Long proofSenderId,Long proofReceiverId);
}
