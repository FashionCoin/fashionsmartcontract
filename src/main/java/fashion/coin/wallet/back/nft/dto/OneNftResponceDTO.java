package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.nft.entity.Nft;

public class OneNftResponceDTO extends Nft {

    String avatar;
    boolean avaExists;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isAvaExists() {
        return avaExists;
    }

    public void setAvaExists(boolean avaExists) {
        this.avaExists = avaExists;
    }
}
