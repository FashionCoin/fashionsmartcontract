package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.ProofHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProofHistoryRepository extends JpaRepository<ProofHistory,Long> {

    List<ProofHistory> findByClientIdAndAndTimestampGreaterThan(Long clientId, Long timestamp);

    List<ProofHistory> findByNftId(Long nftId);

    List<ProofHistory> findByTimestampIsGreaterThan(Long timestamp);

    List<ProofHistory> findByClientIdAndNftId(Long clientId, Long nftId);

    List<ProofHistory> findByClientId(Long clientId);
}
