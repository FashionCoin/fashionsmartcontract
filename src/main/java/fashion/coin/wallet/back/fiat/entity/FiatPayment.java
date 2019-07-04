package fashion.coin.wallet.back.fiat.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
public class FiatPayment {
    @Id
    String id;
    Long timestamp;
    BigDecimal amount;
    @Digits(integer = 19,fraction = 3)
    BigDecimal fee;
    String currency;
    @Digits(integer = 19,fraction = 3)
    BigDecimal fshn;
    String phone;
    String cryptoname;
    Boolean result;
    String msg;

    public FiatPayment() {
    }

    public FiatPayment(String id, Long timestamp, BigDecimal amount, BigDecimal fee, String currency,
                       BigDecimal fshn, String phone, String cryptoname, Boolean result, String msg) {
        this.id = id;
        this.timestamp = timestamp;
        this.amount = amount;
        this.fee = fee;
        this.currency = currency;
        this.fshn = fshn;
        this.phone = phone;
        this.cryptoname = cryptoname;
        this.result = result;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getFshn() {
        return fshn;
    }

    public void setFshn(BigDecimal fshn) {
        this.fshn = fshn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
