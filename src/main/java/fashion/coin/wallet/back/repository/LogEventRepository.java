package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.LogEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEventRepository extends JpaRepository<LogEvent,Long> {
}
