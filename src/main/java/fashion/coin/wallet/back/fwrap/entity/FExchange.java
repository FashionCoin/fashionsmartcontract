package fashion.coin.wallet.back.fwrap.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class FExchange {

    @Id @GeneratedValue
    Long id;

    Long timstamp;

    Long clientId;
    String cryptoname;

    String currencyFrom;
    String currencyTo;
    @Column(precision = 30, scale = 3)
    BigDecimal valueFrom;
    @Column(precision = 30, scale = 3)
    BigDecimal valueTo;
    @Column(precision = 30, scale = 3)
    BigDecimal fee;

    String txHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimstamp() {
        return timstamp;
    }

    public void setTimstamp(Long timstamp) {
        this.timstamp = timstamp;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public BigDecimal getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(BigDecimal valueFrom) {
        this.valueFrom = valueFrom;
    }

    public BigDecimal getValueTo() {
        return valueTo;
    }

    public void setValueTo(BigDecimal valueTo) {
        this.valueTo = valueTo;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}
