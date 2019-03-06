package fashion.coin.wallet.back.dto.blockchain;

import com.google.gson.Gson;

/**
 * Created by JAVA-P on 24.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class ResponceDTO {
    String tx_hash;

    public String getTx_hash() {
        return tx_hash;
    }

    public void setTx_hash(String tx_hash) {
        this.tx_hash = tx_hash;
    }

    @Override
    public String toString() {

        Gson gson = new Gson();

        return gson.toJson(this);
    }
}
