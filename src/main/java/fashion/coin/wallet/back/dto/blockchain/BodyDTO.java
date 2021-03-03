package fashion.coin.wallet.back.dto.blockchain;

public class BodyDTO {
    private String left_right;
    private String name_hash;
    private String pub_key;
    private String from;
    private String to;
    private String amount;
    private String seed;

    public String getLeft_right() {
        return left_right;
    }

    public void setLeft_right(String left_right) {
        this.left_right = left_right;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return "BodyDTO{" +
                "left_right='" + left_right + '\'' +
                ", name_hash='" + name_hash + '\'' +
                ", pub_key='" + pub_key + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount='" + amount + '\'' +
                ", seed='" + seed + '\'' +
                '}';
    }
}
