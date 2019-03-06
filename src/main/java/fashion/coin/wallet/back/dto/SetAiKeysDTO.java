package fashion.coin.wallet.back.dto;

public class SetAiKeysDTO {
    String pub_key;
    String priv_key;

    public String getPub_key() {
        return pub_key;
    }

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public String getPriv_key() {
        return priv_key;
    }

    public void setPriv_key(String priv_key) {
        this.priv_key = priv_key;
    }
}
