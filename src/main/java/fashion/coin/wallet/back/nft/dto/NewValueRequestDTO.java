package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

import java.math.BigDecimal;

public class NewValueRequestDTO {

    String apikey;
    Long nftId;
    BigDecimal faceValue;
    BigDecimal creativeValue;
    TransactionRequestDTO transactionRequest;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getCreativeValue() {
        return creativeValue;
    }

    public void setCreativeValue(BigDecimal creativeValue) {
        this.creativeValue = creativeValue;
    }

    public TransactionRequestDTO getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequestDTO transactionRequest) {
        this.transactionRequest = transactionRequest;
    }
}
