package fashion.coin.wallet.back.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class CurrencyRate {

    @Id @GeneratedValue
    Long id;

    String currency;

    @Digits(integer=18, fraction=6)
    BigDecimal rate;

    LocalDateTime dateTime;

    public CurrencyRate() {
    }

    public CurrencyRate(String currency, @Digits(integer = 18, fraction = 6) BigDecimal rate, LocalDateTime dateTime) {
        this.currency = currency;
        this.rate = rate;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
