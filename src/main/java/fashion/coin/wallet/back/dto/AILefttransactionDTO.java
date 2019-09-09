package fashion.coin.wallet.back.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AILefttransactionDTO {

    LocalDateTime time;
    String from;
    String to;
    BigDecimal amount;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
