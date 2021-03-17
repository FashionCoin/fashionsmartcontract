package fashion.coin.wallet.back.fwrap.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

public class FWrapRequestDTO {

    String apikey;
    String currency;
    TransactionRequestDTO transactionRequest;

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

    public TransactionRequestDTO getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequestDTO transactionRequest) {
        this.transactionRequest = transactionRequest;
    }
}
