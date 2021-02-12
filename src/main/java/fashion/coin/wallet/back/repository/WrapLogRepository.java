package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.WrapLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrapLogRepository extends JpaRepository<WrapLog,Long> {
    List<WrapLog> findByTxHash(String txHash);
}
