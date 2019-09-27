package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    CurrencyRate findTopByCurrencyAndDateTimeIsAfter(String currency, LocalDateTime afterDateTime);

    CurrencyRate findTopByCurrencyOrderByDateTimeDesc(String currency);

    CurrencyRate findTopByCurrencyAndDateTimeIsBeforeOrderByDateTimeDesc(String currency, LocalDateTime beforeTime);

    @Query(value = "SELECT avg(currency_rate.rate) FROM currency_rate WHERE currency=?1 AND date_time BETWEEN ?2  AND ?3 ",
            nativeQuery = true)
    Double getAverageCurrency(String currency, LocalDateTime startDate, LocalDateTime endDate);
}
