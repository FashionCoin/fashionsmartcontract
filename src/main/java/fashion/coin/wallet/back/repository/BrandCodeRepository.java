package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.BrandCode;
import fashion.coin.wallet.back.entity.EmojiCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandCodeRepository extends JpaRepository<BrandCode, String> {
    BrandCode findByBrand(String brand);
}
