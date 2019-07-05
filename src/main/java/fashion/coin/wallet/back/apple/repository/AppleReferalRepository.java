package fashion.coin.wallet.back.apple.repository;


import fashion.coin.wallet.back.apple.entity.AppleRefInstall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppleReferalRepository extends JpaRepository<AppleRefInstall, Long> {
    AppleRefInstall findTopByUserAgentAndLocalDateTimeAfter(String useragent, LocalDateTime afterTime);

    List<AppleRefInstall> findAllByIpAddressAndLocalDateTimeAfter(String ipAdress, LocalDateTime afterTime);
}
