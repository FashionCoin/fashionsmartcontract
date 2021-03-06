package fashion.coin.wallet.back.entity;

import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class WrapLog {
    @Id
    @GeneratedValue
    Long id;
    LocalDateTime time;
    boolean isWrap;
    long amount;
    String fshnWallet;
    String ethWallet;
    String txHash;
    @Column(name="network",columnDefinition = "varchar(255) default 'ethereum'")
    String network;

    public WrapLog() {
    }

    public WrapLog(boolean isWrap, long amount, String fshnWallet, String ethWallet, String network) {
        this.time = LocalDateTime.now();
        this.isWrap = isWrap;
        this.amount = amount;
        this.fshnWallet = fshnWallet;
        this.ethWallet = ethWallet;
        this.network = network;
    }

    public WrapLog(boolean isWrap, long amount, String fshnWallet, String ethWallet,String network, String txHash) {
        this.time = LocalDateTime.now();
        this.isWrap = isWrap;
        this.amount = amount;
        this.fshnWallet = fshnWallet;
        this.ethWallet = ethWallet;
        this.network = network;
        this.txHash = txHash;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isWrap() {
        return isWrap;
    }

    public void setWrap(boolean wrap) {
        isWrap = wrap;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getFshnWallet() {
        return fshnWallet;
    }

    public void setFshnWallet(String fshnWallet) {
        this.fshnWallet = fshnWallet;
    }

    public String getEthWallet() {
        return ethWallet;
    }

    public void setEthWallet(String ethWallet) {
        this.ethWallet = ethWallet;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
