package fashion.coin.wallet.back.dto;

public class WrapHistoryRequestDTO {

    String apikey;
    String network;



    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
