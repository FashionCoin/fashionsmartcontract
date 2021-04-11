package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class NftTirage {

    @Id @GeneratedValue
    Long id;
    Long nftId;

    @Column(precision = 30, scale = 3)
    BigDecimal creativeValue;
    Long ownerId;
    Long timestamp;
    @Column(name ="can_change_value", columnDefinition="boolean default false",nullable = false)
    boolean canChangeValue;
    @Column(name ="insale", columnDefinition="boolean default false",nullable = false)
    boolean insale;
    String txhash;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNftId() {
        return nftId;
    }

    public void setNftId(Long nftId) {
        this.nftId = nftId;
    }

    public BigDecimal getCreativeValue() {
        return creativeValue;
    }

    public void setCreativeValue(BigDecimal creativeValue) {
        this.creativeValue = creativeValue;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCanChangeValue() {
        return canChangeValue;
    }

    public void setCanChangeValue(boolean canChangeValue) {
        this.canChangeValue = canChangeValue;
    }

    public boolean isInsale() {
        return insale;
    }

    public void setInsale(boolean insale) {
        this.insale = insale;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }
}
