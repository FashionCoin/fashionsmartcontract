package fashion.coin.wallet.back.tranzzo.dto;

import java.math.BigDecimal;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

public class TranzzoPaymentResponseDTO {

    private String payment_id;
    private String order_id;
    private String gateway_order_id;
    private String billing_order_id;
    private String transaction_id;
    private String pos_id;
    private String mode;
    private String method;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String status;
    private String status_code;
    private String status_description;
    private Boolean user_action_required;
    private String user_action_url;
    private String eci;
    private String mcc;
    private String options3ds;
    private String cc_mask;
    private String cc_token;
    private String cc_token_expiration;
    private String customer_id;
    private String customer_ip;
    private String customer_fname;
    private String customer_lname;
    private String customer_email;
    private String customer_phone;
    private String customer_country;
    private String result_url;
    private String created_at;
    private String processing_time;
    private String payload;
    private String bank_short_name;

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getGateway_order_id() {
        return gateway_order_id;
    }

    public void setGateway_order_id(String gateway_order_id) {
        this.gateway_order_id = gateway_order_id;
    }

    public String getBilling_order_id() {
        return billing_order_id;
    }

    public void setBilling_order_id(String billing_order_id) {
        this.billing_order_id = billing_order_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

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

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_description() {
        return status_description;
    }

    public void setStatus_description(String status_description) {
        this.status_description = status_description;
    }

    public Boolean getUser_action_required() {
        return user_action_required;
    }

    public void setUser_action_required(Boolean user_action_required) {
        this.user_action_required = user_action_required;
    }

    public String getUser_action_url() {
        return user_action_url;
    }

    public void setUser_action_url(String user_action_url) {
        this.user_action_url = user_action_url;
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

    public String getCc_mask() {
        return cc_mask;
    }

    public void setCc_mask(String cc_mask) {
        this.cc_mask = cc_mask;
    }

    public String getCc_token() {
        return cc_token;
    }

    public void setCc_token(String cc_token) {
        this.cc_token = cc_token;
    }

    public String getCc_token_expiration() {
        return cc_token_expiration;
    }

    public void setCc_token_expiration(String cc_token_expiration) {
        this.cc_token_expiration = cc_token_expiration;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_ip() {
        return customer_ip;
    }

    public void setCustomer_ip(String customer_ip) {
        this.customer_ip = customer_ip;
    }

    public String getCustomer_fname() {
        return customer_fname;
    }

    public void setCustomer_fname(String customer_fname) {
        this.customer_fname = customer_fname;
    }

    public String getCustomer_lname() {
        return customer_lname;
    }

    public void setCustomer_lname(String customer_lname) {
        this.customer_lname = customer_lname;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_country() {
        return customer_country;
    }

    public void setCustomer_country(String customer_country) {
        this.customer_country = customer_country;
    }

    public String getResult_url() {
        return result_url;
    }

    public void setResult_url(String result_url) {
        this.result_url = result_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getProcessing_time() {
        return processing_time;
    }

    public void setProcessing_time(String processing_time) {
        this.processing_time = processing_time;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getBank_short_name() {
        return bank_short_name;
    }

    public void setBank_short_name(String bank_short_name) {
        this.bank_short_name = bank_short_name;
    }
}
