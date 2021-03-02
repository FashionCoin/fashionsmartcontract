package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.DividendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendHistoryRepository extends JpaRepository<DividendHistory,Long> {
}
