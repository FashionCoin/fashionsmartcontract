package fashion.coin.wallet.back.tranzzo.dto;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

public class TranzzoPaymentRequestDTO {
     String pos_id;
     String mode;
     String method;
     Double amount;
     String currency;
     String description;
     String order_id;
     String order_3ds_bypass;
     String cc_number;
     Integer exp_month;
     Integer exp_year;
     String card_cvv;
     String customer_ip;
     String server_url;
     String result_url;
     String payload;
     BrowserFingerprintDTO browserFingerprint;

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_3ds_bypass() {
        return order_3ds_bypass;
    }

    public void setOrder_3ds_bypass(String order_3ds_bypass) {
        this.order_3ds_bypass = order_3ds_bypass;
    }

    public String getCc_number() {
        return cc_number;
    }

    public void setCc_number(String cc_number) {
        this.cc_number = cc_number;
    }

    public Integer getExp_month() {
        return exp_month;
    }

    public void setExp_month(Integer exp_month) {
        this.exp_month = exp_month;
    }

    public Integer getExp_year() {
        return exp_year;
    }

    public void setExp_year(Integer exp_year) {
        this.exp_year = exp_year;
    }

    public String getCard_cvv() {
        return card_cvv;
    }

    public void setCard_cvv(String card_cvv) {
        this.card_cvv = card_cvv;
    }

    public String getCustomer_ip() {
        return customer_ip;
    }

    public void setCustomer_ip(String customer_ip) {
        this.customer_ip = customer_ip;
    }

    public String getServer_url() {
        return server_url;
    }

    public void setServer_url(String server_url) {
        this.server_url = server_url;
    }

    public String getResult_url() {
        return result_url;
    }

    public void setResult_url(String result_url) {
        this.result_url = result_url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public BrowserFingerprintDTO getBrowserFingerprint() {
        return browserFingerprint;
    }

    public void setBrowserFingerprint(BrowserFingerprintDTO browserFingerprint) {
        this.browserFingerprint = browserFingerprint;
    }
}
