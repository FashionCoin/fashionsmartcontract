package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public interface ClientRepository extends JpaRepository<Client,Long> {
    Client findClientByLogin(String login);
    Client findClientByWalletAddress(String walletAddress);
    Client findClientByApikey(String apikey);
//    Client findClientByEmail(String email);
    List<Client> findClientsByEmail(String email);
    List<Client> findClientsByPhone(String phone);
    List<Client> findClientsByPhoneEndingWith(String phone);
}
