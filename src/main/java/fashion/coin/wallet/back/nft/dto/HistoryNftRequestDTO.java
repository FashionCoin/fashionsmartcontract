package fashion.coin.wallet.back.nft.dto;

public class HistoryNftRequestDTO {

    String apikey;
    long nftId;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public long getNftId() {
        return nftId;
    }

    public void setNftId(long nftId) {
        this.nftId = nftId;
    }
}
