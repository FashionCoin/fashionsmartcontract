package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.Nft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NftRepository extends JpaRepository<Nft, Long> {

//    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDesc(LocalDateTime localDateTime);


    @Query(value = "SELECT * FROM nft WHERE timestamp < ?1 AND burned = FALSE ORDER BY timestamp DESC LIMIT ?2", nativeQuery = true)
    List<Nft> findByLocalDateTimeBeforeAndOrderByLocalDateTimeDescLimitedTo(Long timestamp, int limit);

    @Query(value = "SELECT * FROM nft ORDER BY timestamp DESC LIMIT 10000", nativeQuery = true)
    List<Nft> findLastTenThousand();

    List<Nft> findByFileName(String filename);

    List<Nft> findByOwnerId(Long id);

    List<Nft> findByAuthorId(Long id);

    List<Nft> findByTimestampIsGreaterThan(long durationStart);

    @Query(value = "SELECT * FROM nft WHERE description LIKE %?1% ORDER BY timestamp DESC LIMIT 10000", nativeQuery = true)
    List<Nft> findHashTags(String hashtag);


    List<Nft> findByAuthorNameAndTimestampIsGreaterThan(String authorName,Long timestamp);
}
