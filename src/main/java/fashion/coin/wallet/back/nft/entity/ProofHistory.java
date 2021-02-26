package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class ProofHistory {
    @Id @GeneratedValue
    Long id;

    Long timestamp;
    Long clientId;
    Long nftId;
    Long ownerId;
    @Column(precision = 30, scale = 3)
    BigDecimal proofValue;
    int clientRate;
    int nftRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getProofValue() {
        return proofValue;
    }

    public void setProofValue(BigDecimal proofValue) {
        this.proofValue = proofValue;
    }

    public int getClientRate() {
        return clientRate;
    }

    public void setClientRate(int clientRate) {
        this.clientRate = clientRate;
    }

    public int getNftRate() {
        return nftRate;
    }

    public void setNftRate(int nftRate) {
        this.nftRate = nftRate;
    }
}
