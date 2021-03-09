package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class NftHistory {
    @Id @GeneratedValue
    Long id;

    Long nftId;
    Long timestamp;

    String cryptonameFrom;
    String cryptonameTo;
    @Column(precision = 30, scale = 3)
    BigDecimal amount;

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCryptonameFrom() {
        return cryptonameFrom;
    }

    public void setCryptonameFrom(String cryptonameFrom) {
        this.cryptonameFrom = cryptonameFrom;
    }

    public String getCryptonameTo() {
        return cryptonameTo;
    }

    public void setCryptonameTo(String cryptonameTo) {
        this.cryptonameTo = cryptonameTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }
}
