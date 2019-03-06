package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings,Long> {
    Settings findOneByKey(String key);
}
