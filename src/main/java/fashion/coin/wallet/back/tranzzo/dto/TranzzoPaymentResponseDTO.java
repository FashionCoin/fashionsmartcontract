package fashion.coin.wallet.back.tranzzo.dto;

import java.math.BigDecimal;

public class TranzzoPaymentResponseDTO {

    private String paymentId;
    private String orderId;
    private String gatewayOrderId;
    private String billingOrderId;
    private String transactionId;
    private String posId;
    private String mode;
    private String method;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String status;
    private String statusCode;
    private String statusDescription;
    private Boolean userActionRequired;
    private String userActionUrl;
    private String eci;
    private String mcc;
    private String options3ds;
    private String ccMask;
    private String ccToken;
    private String ccTokenExpiration;
    private String customerId;
    private String customerIp;
    private String customerFname;
    private String customerLname;
    private String customerEmail;
    private String customerPhone;
    private String customerCountry;
    private String resultUrl;
    private String createdAt;
    private String processingTime;
    private String payload;
    private String bankShortName;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getGatewayOrderId() {
        return gatewayOrderId;
    }

    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }

    public String getBillingOrderId() {
        return billingOrderId;
    }

    public void setBillingOrderId(String billingOrderId) {
        this.billingOrderId = billingOrderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public Boolean getUserActionRequired() {
        return userActionRequired;
    }

    public void setUserActionRequired(Boolean userActionRequired) {
        this.userActionRequired = userActionRequired;
    }

    public String getUserActionUrl() {
        return userActionUrl;
    }

    public void setUserActionUrl(String userActionUrl) {
        this.userActionUrl = userActionUrl;
    }

    public String getEci() {
        return eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getOptions3ds() {
        return options3ds;
    }

    public void setOptions3ds(String options3ds) {
        this.options3ds = options3ds;
    }

    public String getCcMask() {
        return ccMask;
    }

    public void setCcMask(String ccMask) {
        this.ccMask = ccMask;
    }

    public String getCcToken() {
        return ccToken;
    }

    public void setCcToken(String ccToken) {
        this.ccToken = ccToken;
    }

    public String getCcTokenExpiration() {
        return ccTokenExpiration;
    }

    public void setCcTokenExpiration(String ccTokenExpiration) {
        this.ccTokenExpiration = ccTokenExpiration;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }

    public String getCustomerFname() {
        return customerFname;
    }

    public void setCustomerFname(String customerFname) {
        this.customerFname = customerFname;
    }

    public String getCustomerLname() {
        return customerLname;
    }

    public void setCustomerLname(String customerLname) {
        this.customerLname = customerLname;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }
}
