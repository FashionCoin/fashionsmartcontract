package fashion.coin.wallet.back.tranzzo.dto;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

public class GetTanzzoStatusDTO {

    String apikey;
    Long paymentId;


    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
