package fashion.coin.wallet.back.apple.entity;

import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class AppleRefInstall {

    @Id @GeneratedValue
    Long id;

    String apiKey;
    String cryptoname;
    String userAgent;
    String ipAddress;
    LocalDateTime localDateTime;

    public AppleRefInstall() {
    }

    public AppleRefInstall(String apiKey, String cryptoname, String userAgent, String ipAddress, LocalDateTime localDateTime) {
        this.apiKey = apiKey;
        this.cryptoname = cryptoname;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.localDateTime = localDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
