package fashion.coin.wallet.back.fwrap.dto;

public class FCurrencyRequestDTO {
    String apikey;
    String currency;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
