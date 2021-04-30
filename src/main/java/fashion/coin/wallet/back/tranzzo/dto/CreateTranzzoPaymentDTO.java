package fashion.coin.wallet.back.tranzzo.dto;

import fashion.coin.wallet.back.nft.dto.BuyNftDTO;

import java.math.BigDecimal;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

public class CreateTranzzoPaymentDTO {

    String apikey;
    BigDecimal fshnAmount;
    BigDecimal uahAmount;
    BigDecimal usdAmount;
    String email;
    String phone;
    BuyNftDTO buyNft;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public BigDecimal getFshnAmount() {
        return fshnAmount;
    }

    public void setFshnAmount(BigDecimal fshnAmount) {
        this.fshnAmount = fshnAmount;
    }

    public BigDecimal getUahAmount() {
        return uahAmount;
    }

    public void setUahAmount(BigDecimal uahAmount) {
        this.uahAmount = uahAmount;
    }

    public BigDecimal getUsdAmount() {
        return usdAmount;
    }

    public void setUsdAmount(BigDecimal usdAmount) {
        this.usdAmount = usdAmount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BuyNftDTO getBuyNft() {
        return buyNft;
    }

    public void setBuyNft(BuyNftDTO buyNft) {
        this.buyNft = buyNft;
    }
}
