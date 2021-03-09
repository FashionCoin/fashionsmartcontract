package fashion.coin.wallet.back.nft.dto;

public class FindByDurationRequestDTO {
    String apikey;
    int duration;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
