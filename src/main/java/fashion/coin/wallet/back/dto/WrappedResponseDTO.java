package fashion.coin.wallet.back.dto;

public class WrappedResponseDTO {
    String wallet;
    Long amount;
    Long nonce;
    String smartconract;
    Long v;
    String r;
    String s;

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public String getSmartconract() {
        return smartconract;
    }

    public void setSmartconract(String smartconract) {
        this.smartconract = smartconract;
    }

    public Long getV() {
        return v;
    }

    public void setV(Long v) {
        this.v = v;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
