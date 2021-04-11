package fashion.coin.wallet.back.nft.repository;

import fashion.coin.wallet.back.nft.entity.NftTirage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NftTirageRepository extends JpaRepository<NftTirage,Long> {
    List<NftTirage> findByOwnerId(Long ownerId);
}
