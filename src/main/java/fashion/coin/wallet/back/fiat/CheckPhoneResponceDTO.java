package fashion.coin.wallet.back.fiat;


public class CheckPhoneResponceDTO {
    Boolean result;
    String cryptoname;

    public CheckPhoneResponceDTO() {
    }

    public CheckPhoneResponceDTO(Boolean result, String cryptoname) {
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
