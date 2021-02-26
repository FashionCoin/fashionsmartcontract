package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.ProofHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProofHistoryRepository extends JpaRepository<ProofHistory,Long> {
}
