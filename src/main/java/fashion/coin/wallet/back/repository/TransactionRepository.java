package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.TransactionCoins;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public interface TransactionRepository extends JpaRepository<TransactionCoins,Long> {

    List<TransactionCoins> findAllBySender(Client sender);
    List<TransactionCoins> findAllByReceiver(Client receiver);
}
