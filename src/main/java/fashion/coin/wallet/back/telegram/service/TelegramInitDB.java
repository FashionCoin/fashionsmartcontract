package fashion.coin.wallet.back.telegram.service;

import fashion.coin.wallet.back.telegram.repository.TelegramTextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;

/**
 * Created by JAVA-P on 04.06.2019.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class TelegramInitDB {

    @Autowired
    TelegramTextRepository telegramTextRepository;

    public void initDB(){



    }

}
