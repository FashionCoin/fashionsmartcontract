package fashion.coin.wallet.back.dto.blockchain;

public class FshnHistoryTxDTO {

    public String amount;

    public String from;

    public String refFrom;

    public String refTo;

    public FshnTimeDTO time;

    public String to;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRefFrom() {
        return refFrom;
    }

    public void setRefFrom(String refFrom) {
        this.refFrom = refFrom;
    }

    public String getRefTo() {
        return refTo;
    }

    public void setRefTo(String refTo) {
        this.refTo = refTo;
    }

    public FshnTimeDTO getTime() {
        return time;
    }

    public void setTime(FshnTimeDTO time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
