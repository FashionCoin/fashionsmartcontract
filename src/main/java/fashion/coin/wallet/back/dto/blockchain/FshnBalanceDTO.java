package fashion.coin.wallet.back.dto.blockchain;

public class FshnBalanceDTO {

    public String balance;

    public String nameHash;

    public String pubKey;

    public String refPrev;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getNameHash() {
        return nameHash;
    }

    public void setNameHash(String nameHash) {
        this.nameHash = nameHash;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getRefPrev() {
        return refPrev;
    }

    public void setRefPrev(String refPrev) {
        this.refPrev = refPrev;
    }
}
