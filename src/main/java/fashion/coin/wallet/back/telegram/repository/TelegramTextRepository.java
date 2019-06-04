package fashion.coin.wallet.back.telegram.repository;

import fashion.coin.wallet.back.telegram.entity.TelegramText;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by JAVA-P on 04.06.2019.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public interface TelegramTextRepository extends JpaRepository<TelegramText, Long> {
    TelegramText findTopByBotnameAndLangAndTag(String botname, String lang, String tag);
}
