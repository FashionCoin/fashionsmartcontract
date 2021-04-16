package fashion.coin.wallet.back.fwrap.repository;

import fashion.coin.wallet.back.fwrap.entity.FExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FExchangeRepository extends JpaRepository<FExchange, Long> {
    @Query(value = "SELECT * FROM fexchange WHERE client_id = ?1 AND ( currency_from = ?2 OR currency_to = ?2)", nativeQuery = true)
    List<FExchange> findByClientAndCurrency(Long clientId, String currency);
}
