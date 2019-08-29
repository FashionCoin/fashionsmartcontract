package fashion.coin.wallet.back.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class EmojiCode {
    @Id
    String emcode;

    LocalDateTime added;

    LocalDateTime used;
    @Column(unique = true)
    Long client;
    @Column(unique = true)
    String emoji;
    @Column(unique = true)
    String wallet;

    public EmojiCode() {
    }

    public EmojiCode(String emcode) {
        this.emcode = emcode;
        this.added = LocalDateTime.now();
    }

    public String getEmcode() {
        return emcode;
    }

    public void setEmcode(String emcode) {
        this.emcode = emcode;
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

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
