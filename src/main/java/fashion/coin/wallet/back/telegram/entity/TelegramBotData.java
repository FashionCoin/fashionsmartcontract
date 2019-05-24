package fashion.coin.wallet.back.telegram.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TelegramBotData {

    @Id
    @GeneratedValue
    Long id;

    String userId;

    String paramname;

    String paramvalue;


    public TelegramBotData() {
    }

    public TelegramBotData(String userId, String paramname) {
        this.userId = userId;
        this.paramname = paramname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }
}
