package fashion.coin.wallet.back.nft.repository;


import fashion.coin.wallet.back.nft.entity.NftFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NftFileRepository extends JpaRepository<NftFile,Long> {
}
