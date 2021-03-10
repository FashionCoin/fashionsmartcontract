package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

import java.util.HashMap;
import java.util.Map;

public class BuyNftDTO {
    Long nftId;
    String apikey;
    Map<String, TransactionRequestDTO> transactionsRequestMap = new HashMap<>();

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

    public Map<String, TransactionRequestDTO> getTransactionsRequestMap() {
        return transactionsRequestMap;
    }

    public void setTransactionsRequestMap(Map<String, TransactionRequestDTO> transactionsRequestMap) {
        this.transactionsRequestMap = transactionsRequestMap;
    }

    @Override
    public String toString() {
        return "BuyNftDTO{" +
                "nftId=" + nftId +
                ", transactionsRequestMap=" + transactionsRequestMap +
                '}';
    }
}
