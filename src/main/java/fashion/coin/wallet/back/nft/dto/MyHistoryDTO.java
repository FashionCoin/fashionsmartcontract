package fashion.coin.wallet.back.nft.dto;

import javax.persistence.Column;
import java.math.BigDecimal;

public class MyHistoryDTO {

    Long id;
    Long nftId;
    String fileName;
    String title;

    Long timestamp;

    String cryptonameFrom;
    String cryptonameTo;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
