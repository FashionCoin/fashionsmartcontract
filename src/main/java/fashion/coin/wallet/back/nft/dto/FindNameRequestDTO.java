package fashion.coin.wallet.back.nft.dto;

public class FindNameRequestDTO {
    String name;
    String apikey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}
