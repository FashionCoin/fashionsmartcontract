package fashion.coin.wallet.back.redirectpage;

import fashion.coin.wallet.back.redirectpage.entity.Refcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefcodeRepository extends JpaRepository<Refcode, String> {
    List<Refcode> findByIpadress(String ipaddress);
}
