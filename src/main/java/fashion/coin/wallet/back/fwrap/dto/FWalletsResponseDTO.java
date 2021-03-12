package fashion.coin.wallet.back.fwrap.dto;

import java.math.BigDecimal;

public class FWalletsResponseDTO {

    String currency;
    BigDecimal amount;
    String approximateСost;

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

    public String getApproximateСost() {
        return approximateСost;
    }

    public void setApproximateСost(String approximateСost) {
        this.approximateСost = approximateСost;
    }
}
