package fashion.coin.wallet.back.nft.dto;

import java.math.BigDecimal;

public class AllocatedFundsDTO {

   String purpose;
   String wallet;
   BigDecimal amount;



    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
