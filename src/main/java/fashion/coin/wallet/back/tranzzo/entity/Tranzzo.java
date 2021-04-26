package fashion.coin.wallet.back.tranzzo.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Tranzzo {
    @Id
    @GeneratedValue
    Long id;
    Long timestamp;
    LocalDateTime localDateTime;

//    Request

    String posId;
    String mode;
    String method;
    BigDecimal amount;
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

//    Fingerprint

    String browserColorDepth;
    String browserScreenHeight;
    String browserScreenWidth;
    String browserJavaEnabled;
    String browserLanguage;
    String browserTimeZone;
    String browserTimeZoneOffset;
    String browserAcceptHeader;
    String browserIpAddress;
    String browserUserAgent;

//    Response

    String paymentId;

    String gatewayOrderId;
    String billingOrderId;
    String transactionId;
    String status;
    String statusCode;
    String statusDescription;
    Boolean userActionRequired;
    String userActionUrl;
    String eci;
    String mcc;
    String options3ds;
    String ccMask;
    String ccToken;
    String ccTokenExpiration;
    String customerId;
    String customerIp;
    String customerFname;
    String customerLname;
    String customerEmail;
    String customerPhone;
    String customerCountry;
    String createdAt;
    String processingTime;
    String bankShortName;

    //    Error
    @Column(length = 1023)
    String error;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
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

    public String getBrowserColorDepth() {
        return browserColorDepth;
    }

    public void setBrowserColorDepth(String browserColorDepth) {
        this.browserColorDepth = browserColorDepth;
    }

    public String getBrowserScreenHeight() {
        return browserScreenHeight;
    }

    public void setBrowserScreenHeight(String browserScreenHeight) {
        this.browserScreenHeight = browserScreenHeight;
    }

    public String getBrowserScreenWidth() {
        return browserScreenWidth;
    }

    public void setBrowserScreenWidth(String browserScreenWidth) {
        this.browserScreenWidth = browserScreenWidth;
    }

    public String getBrowserJavaEnabled() {
        return browserJavaEnabled;
    }

    public void setBrowserJavaEnabled(String browserJavaEnabled) {
        this.browserJavaEnabled = browserJavaEnabled;
    }

    public String getBrowserLanguage() {
        return browserLanguage;
    }

    public void setBrowserLanguage(String browserLanguage) {
        this.browserLanguage = browserLanguage;
    }

    public String getBrowserTimeZone() {
        return browserTimeZone;
    }

    public void setBrowserTimeZone(String browserTimeZone) {
        this.browserTimeZone = browserTimeZone;
    }

    public String getBrowserTimeZoneOffset() {
        return browserTimeZoneOffset;
    }

    public void setBrowserTimeZoneOffset(String browserTimeZoneOffset) {
        this.browserTimeZoneOffset = browserTimeZoneOffset;
    }

    public String getBrowserAcceptHeader() {
        return browserAcceptHeader;
    }

    public void setBrowserAcceptHeader(String browserAcceptHeader) {
        this.browserAcceptHeader = browserAcceptHeader;
    }

    public String getBrowserIpAddress() {
        return browserIpAddress;
    }

    public void setBrowserIpAddress(String browserIpAddress) {
        this.browserIpAddress = browserIpAddress;
    }

    public String getBrowserUserAgent() {
        return browserUserAgent;
    }

    public void setBrowserUserAgent(String browserUserAgent) {
        this.browserUserAgent = browserUserAgent;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
