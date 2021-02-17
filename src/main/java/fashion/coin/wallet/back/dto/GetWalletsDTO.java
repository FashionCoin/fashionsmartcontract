package fashion.coin.wallet.back.dto;

import java.util.ArrayList;
import java.util.List;

public class GetWalletsDTO {
    String apikey;
   List<String> cryptonames = new ArrayList<>();

    public List<String> getCryptonames() {
        return cryptonames;
    }

    public void setCryptonames(List<String> cryptonames) {
        this.cryptonames = cryptonames;
    }
}
