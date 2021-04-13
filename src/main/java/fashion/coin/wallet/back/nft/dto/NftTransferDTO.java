package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

public class NftTransferDTO {
    Long nftId;
    String apikey;
    String receiver;
    Long pieces;
    TransactionRequestDTO transactionRequest;

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public TransactionRequestDTO getTransactionRequest() {
        return transactionRequest;
    }

    public void setTransactionRequest(TransactionRequestDTO transactionRequest) {
        this.transactionRequest = transactionRequest;
    }

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }
}
