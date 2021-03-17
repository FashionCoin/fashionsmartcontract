package fashion.coin.wallet.back.nft.dto;

public class NftTransferDTO {
    Long nftId;
    String apikey;
    String receiver;

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
}
