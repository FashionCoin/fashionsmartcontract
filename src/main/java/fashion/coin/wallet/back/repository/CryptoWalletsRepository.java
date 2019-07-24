package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.CryptoWallets;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by JAVA-P on 24.07.2019.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public interface CryptoWalletsRepository extends JpaRepository<CryptoWallets, Long> {

    List<CryptoWallets> findAllByCryptoname(String cryptoname);

    CryptoWallets findTopByCryptonameAndCurrency(String cryptoname, String currency);

    CryptoWallets findTopByCurrencyAndWallet(String currency, String wallet);


}
