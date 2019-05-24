package fashion.coin.wallet.back.dto;

public class CryptonameTelegramDTO {
    String cryptoname;
    Integer telegramId;

    public CryptonameTelegramDTO() {
    }

    public CryptonameTelegramDTO(String cryptoname, Integer telegramId) {
        this.cryptoname = cryptoname;
        this.telegramId = telegramId;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public Integer getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Integer telegramId) {
        this.telegramId = telegramId;
    }
}
