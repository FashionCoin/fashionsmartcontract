package fashion.coin.wallet.back.fwrap.dto;

import java.math.BigDecimal;

public class FSendMoneyRequestDTO {
    String receiver;
    String currency;
    BigDecimal amount;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
