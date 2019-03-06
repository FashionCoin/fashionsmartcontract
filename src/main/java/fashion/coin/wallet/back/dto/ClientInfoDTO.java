package fashion.coin.wallet.back.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */

public class ClientInfoDTO {


    String login;
    boolean loginChanged;
    String apikey;
    String phone;
    boolean showPhone;
    String realname;


    public ClientInfoDTO() {

    }


    public String getLogin() {
        return login;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isLoginChanged() {
        return loginChanged;
    }

    public void setLoginChanged(boolean loginChanged) {
        this.loginChanged = loginChanged;
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
