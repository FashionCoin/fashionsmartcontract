package fashion.coin.wallet.back.fiat.repository;

import fashion.coin.wallet.back.fiat.entity.FiatPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiatPaymentRepository extends JpaRepository<FiatPayment, String> {
    List<FiatPayment> findAllByTimestampBetweenAndResult(Long start, Long end, Boolean result);
    List<FiatPayment> findAllByTimestampBetween(Long start, Long end);
}
