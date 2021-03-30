package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag, String> {

    List<HashTag> findByIdContains(String id);


}
