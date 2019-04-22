package fashion.coin.wallet.back.dto;

public class CryptonameEmailDTO {
    String cryptoname;
    String email;

    public CryptonameEmailDTO() {
    }

    public CryptonameEmailDTO(String cryptoname, String email) {
        this.cryptoname = cryptoname;
        this.email = email;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
