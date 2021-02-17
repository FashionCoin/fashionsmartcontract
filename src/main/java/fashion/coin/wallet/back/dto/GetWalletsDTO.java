package fashion.coin.wallet.back.dto;

import java.util.ArrayList;
import java.util.List;

public class GetWalletsDTO {
    String apikey;
    List<String> cryptonames = new ArrayList<>();

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public List<String> getCryptonames() {
        return cryptonames;
    }

    public void setCryptonames(List<String> cryptonames) {
        this.cryptonames = cryptonames;
    }
}
