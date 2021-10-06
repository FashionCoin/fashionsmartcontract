package fashion.coin.wallet.back.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class BrandCode {
    @Id
    String brcode;

    LocalDateTime added;

    LocalDateTime used;
    @Column(unique = true)
    Long client;
    @Column(unique = true)
    String brand;
    @Column(unique = true)
    String wallet;

    public BrandCode() {
    }

    public BrandCode(String brcode) {
        this.brcode = brcode;
        this.added = LocalDateTime.now();
    }

    public String getBrcode() {
        return brcode;
    }

    public void setBrcode(String brcode) {
        this.brcode = brcode;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    public void setAdded(LocalDateTime added) {
        this.added = added;
    }

    public LocalDateTime getUsed() {
        return used;
    }

    public void setUsed(LocalDateTime used) {
        this.used = used;
    }

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
