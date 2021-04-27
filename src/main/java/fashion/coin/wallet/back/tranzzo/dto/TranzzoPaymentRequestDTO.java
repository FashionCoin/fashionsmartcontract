package fashion.coin.wallet.back.tranzzo.dto;

import java.math.BigDecimal;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

public class TranzzoPaymentRequestDTO {
     String posId;
     String mode;
     String method;
     Double amount;
     String currency;
     String description;
     String orderId;
     String order3dsBypass;
     String ccNumber;
     Integer expMonth;
     Integer expYear;
     String cardCvv;
     String serverUrl;
     String resultUrl;
     String payload;
     BrowserFingerprintDTO browserFingerprint;

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrder3dsBypass() {
        return order3dsBypass;
    }

    public void setOrder3dsBypass(String order3dsBypass) {
        this.order3dsBypass = order3dsBypass;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
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
