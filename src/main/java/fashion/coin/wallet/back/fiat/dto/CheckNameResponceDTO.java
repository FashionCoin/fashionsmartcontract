package fashion.coin.wallet.back.fiat.dto;


public class CheckNameResponceDTO {
    Boolean result;
    String cryptoname;

    public CheckNameResponceDTO() {
    }

    public CheckNameResponceDTO(Boolean result, String cryptoname) {
        this.result = result;
        this.cryptoname = cryptoname;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }
}
