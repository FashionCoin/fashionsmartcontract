package fashion.coin.wallet.back.dto;

import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class SignInDTO {
    String cryptoname;
    String apikey;
    String signature;

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
