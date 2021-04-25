package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.nft.entity.Nft;

public class OneNftResponceDTO extends Nft {

    String avatar;
    boolean avaExists;
    Long pieces;
    Long total;

    String height;
    String width;

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

    public Long getPieces() {
        return pieces;
    }

    public void setPieces(Long pieces) {
        this.pieces = pieces;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
