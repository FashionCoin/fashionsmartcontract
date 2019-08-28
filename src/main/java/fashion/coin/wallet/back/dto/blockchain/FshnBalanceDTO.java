package fashion.coin.wallet.back.dto.blockchain;

public class FshnBalanceDTO {

    public String balance;

    public String name_hash;

    public String pub_key;

    public String ref_prev;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getName_hash() {
        return name_hash;
    }

    public void setName_hash(String name_hash) {
        this.name_hash = name_hash;
    }

    public String getPub_key() {
        return pub_key;
    }

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public String getRef_prev() {
        return ref_prev;
    }

    public void setRef_prev(String ref_prev) {
        this.ref_prev = ref_prev;
    }
}
