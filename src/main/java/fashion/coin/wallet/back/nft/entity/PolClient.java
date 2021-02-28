package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class PolClient {
    @Id
    Long id;

    @Column(unique = true, name = "login")
    String cryptoname;
    @Column(precision = 30, scale = 3)
    BigDecimal faceValue;
    @Column(precision = 30, scale = 3)
    BigDecimal creativeValue;
    @Column(precision = 30, scale = 3)
    BigDecimal proofs = BigDecimal.ZERO;

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
}
