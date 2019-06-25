package fashion.coin.wallet.back.dto;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */

public class ClientInfoDTO {


    String cryptoname;
    boolean cryptonameChanged;
    String apikey;
    String phone;
    boolean showPhone;
    String realname;


    public ClientInfoDTO() {

    }


    public String getCryptoname() {
        return cryptoname;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public boolean isCryptonameChanged() {
        return cryptonameChanged;
    }

    public void setCryptonameChanged(boolean cryptonameChanged) {
        this.cryptonameChanged = cryptonameChanged;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isShowPhone() {
        return showPhone;
    }

    public void setShowPhone(boolean showPhone) {
        this.showPhone = showPhone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }


}
