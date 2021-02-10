package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.Nft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NftRepository extends JpaRepository<Nft,Long> {

}
