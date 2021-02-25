package fashion.coin.wallet.back.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Entity
public class TransactionCoins {

    @Id @GeneratedValue
    Long id;

    Long timestamp;

    @ManyToOne
    Client sender;

    @ManyToOne
    Client receiver;
    @Column(precision = 30, scale = 3)
    BigDecimal amount;

    String txhash;

    public TransactionCoins() {
    }

    public TransactionCoins(Client sender, Client receiver, BigDecimal amount) {
        this.timestamp = System.currentTimeMillis();
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.txhash = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yy hh:mm");
        return dateFormat.format(new Date(this.timestamp));
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }
}
