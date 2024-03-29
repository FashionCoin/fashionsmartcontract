package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.NftHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NftHistoryRepository extends JpaRepository<NftHistory, Long> {

    List<NftHistory> findByNftIdOrderByTimestampDesc(Long nftId);
    List<NftHistory> findByCryptonameFromOrCryptonameToOrderByTimestampDesc(String cryptonameFrom,String cryptonameTo);

    List<NftHistory> findByTimestampIsGreaterThanOrderByTimestampDesc(Long timestamp);

    NftHistory findTopByNftIdOrderByTimestampDesc(Long nftId);
}
