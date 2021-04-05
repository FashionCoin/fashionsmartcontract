package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.SocialLinkDTO;
import fashion.coin.wallet.back.nft.entity.Nft;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

public class PolClientResponseDTO {
    Long id;
    String cryptoname;

    BigDecimal faceValue;

    BigDecimal creativeValue;

    BigDecimal proofs = BigDecimal.ZERO;
    BigDecimal proof = BigDecimal.ZERO;

    List<Nft> collection;
    List<Nft> creation;

    String avatar;
    boolean avaExists;

    boolean proofSender;
    boolean proofReceiver;

    String about;

    List<SocialLinkDTO> socialLinks;


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

    public List<Nft> getCollection() {
        return collection;
    }

    public void setCollection(List<Nft> collection) {
        this.collection = collection;
    }

    public List<Nft> getCreation() {
        return creation;
    }

    public void setCreation(List<Nft> creation) {
        this.creation = creation;
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

    public boolean isProofSender() {
        return proofSender;
    }

    public void setProofSender(boolean proofSender) {
        this.proofSender = proofSender;
    }

    public boolean isProofReceiver() {
        return proofReceiver;
    }

    public void setProofReceiver(boolean proofReceiver) {
        this.proofReceiver = proofReceiver;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<SocialLinkDTO> getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(List<SocialLinkDTO> socialLinks) {
        this.socialLinks = socialLinks;
    }
}
