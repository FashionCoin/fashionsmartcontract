package fashion.coin.wallet.back.dto;

import fashion.coin.wallet.back.entity.Client;

import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class TransactionDTO implements Comparable<TransactionDTO>{

    Long timestamp;

    String sender;

    String receiver;

    BigDecimal amount;

    String txhash;

    public TransactionDTO() {
    }

    public TransactionDTO(Long timestamp, String sender, String receiver, BigDecimal amount, String txhash) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.txhash = txhash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
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

    @Override
    public int compareTo(TransactionDTO o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
