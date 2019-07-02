package fashion.coin.wallet.back.dto.blockchain;

public class FshnTimeDTO {

    public Long nanos;

    public String secs;

    public Long getNanos() {
        return nanos;
    }

    public void setNanos(Long nanos) {
        this.nanos = nanos;
    }

    public String getSecs() {
        return secs;
    }

    public void setSecs(String secs) {
        this.secs = secs;
    }
}
