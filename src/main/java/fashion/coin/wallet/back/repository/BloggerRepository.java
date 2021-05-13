package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.Bloggers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloggerRepository extends JpaRepository<Bloggers,Long> {
    Bloggers findTopByCryptoname(String cryptoname);
}
