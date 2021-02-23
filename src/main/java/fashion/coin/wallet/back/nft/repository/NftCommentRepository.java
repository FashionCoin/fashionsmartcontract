package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.NftComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NftCommentRepository extends JpaRepository<NftComment, Long> {
    List<NftComment> findByNftIdOrderByTimestampDesc(Long nftId);
}
