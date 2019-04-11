package fashion.coin.wallet.back.fiat;

import java.math.BigDecimal;

public class PayRequestDTO {
    String id;
    Long timestamp;
    BigDecimal amount;
    BigDecimal fee;
    String currency;
    BigDecimal fshn;
    String phone;
    String cryptoname;
    String signature;

    public PayRequestDTO() {
    }

    public PayRequestDTO(String id, Long timestamp, BigDecimal amount, BigDecimal fee, String currency,
                         BigDecimal fshn, String phone, String cryptoname, String signature) {
        this.id = id;
        this.timestamp = timestamp;
        this.amount = amount;
        this.fee = fee;
        this.currency = currency;
        this.fshn = fshn;
        this.phone = phone;
        this.cryptoname = cryptoname;
        this.signature = signature;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
