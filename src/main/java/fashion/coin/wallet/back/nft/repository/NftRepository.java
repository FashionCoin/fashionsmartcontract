package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.Nft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NftRepository extends JpaRepository<Nft, Long> {

    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDesc(LocalDateTime localDateTime);

//
//    @Query(value = "SELECT * FROM Nft  WHERE local_date_time < ?1 ORDER BY local_date_time LIMIT ?2", nativeQuery = true)
//    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDescLimitedTo(LocalDateTime localDateTime, int limit);


}
