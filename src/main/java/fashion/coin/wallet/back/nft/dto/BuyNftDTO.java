package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

public class BuyNftDTO {
    Long nftId;
    TransactionRequestDTO transactionRequest;

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public TransactionRequestDTO getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequestDTO transactionRequest) {
        this.transactionRequest = transactionRequest;
    }
}
