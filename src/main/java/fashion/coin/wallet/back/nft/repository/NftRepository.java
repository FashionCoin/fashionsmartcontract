package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.Nft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NftRepository extends JpaRepository<Nft, Long> {

//    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDesc(LocalDateTime localDateTime);


    @Query(value = "SELECT * FROM nft WHERE timestamp > ?1 AND burned = FALSE ORDER BY timestamp DESC LIMIT ?2", nativeQuery = true)
    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDescLimitedTo(Long timestamp, int limit);


    List<Nft> findByFileName(String filename);

    List<Nft> findByOwnerId(Long id);
}
