package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate,Long> {

    CurrencyRate findTopByCurrencyAndDateTimeIsAfter(String currency, LocalDateTime afterDateTime);
    CurrencyRate findTopByCurrencyOrderByDateTimeDesc(String currency);
    CurrencyRate findTopByCurrencyAndDateTimeIsBeforeOrderByDateTime(String currency, LocalDateTime beforeTime);
}
