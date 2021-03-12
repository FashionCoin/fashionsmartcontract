package fashion.coin.wallet.back.fwrap.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class FTransaction {
    @Id
    @GeneratedValue
    Long id;

    Long timestamp;
    String currency;
    Long fromId;
    String fromCryptoname;
    Long toId;
    String toCryptoname;
    @Column(precision = 30, scale = 3)
    BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getFromCryptoname() {
        return fromCryptoname;
    }

    public void setFromCryptoname(String fromCryptoname) {
        this.fromCryptoname = fromCryptoname;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public String getToCryptoname() {
        return toCryptoname;
    }

    public void setToCryptoname(String toCryptoname) {
        this.toCryptoname = toCryptoname;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
