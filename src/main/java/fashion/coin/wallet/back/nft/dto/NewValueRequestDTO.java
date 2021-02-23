package fashion.coin.wallet.back.nft.dto;

import java.math.BigDecimal;

public class NewValueRequestDTO {

    String apikey;
    Long nftId;
    BigDecimal faceValue;
    BigDecimal creativeValue;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getCreativeValue() {
        return creativeValue;
    }

    public void setCreativeValue(BigDecimal creativeValue) {
        this.creativeValue = creativeValue;
    }
}
