package fashion.coin.wallet.back.nft.dto;

import java.math.BigDecimal;

public class PolClientData {

    Long id;
    String cryptoname;

    BigDecimal faceValue;

    BigDecimal creativeValue;

    BigDecimal proofs = BigDecimal.ZERO;
    BigDecimal proof = BigDecimal.ZERO;


    String avatar;
    boolean avaExists;

    String about;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
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

    public BigDecimal getProofs() {
        return proofs;
    }

    public void setProofs(BigDecimal proofs) {
        this.proofs = proofs;
    }

    public BigDecimal getProof() {
        return proof;
    }

    public void setProof(BigDecimal proof) {
        this.proof = proof;
    }

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
