package fashion.coin.wallet.back.dto;

import java.util.List;

public class WFSHNwallet {
   long balance;
   List<WFSHNhistory> history;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<WFSHNhistory> getHistory() {
        return history;
    }

    public void setHistory(List<WFSHNhistory> history) {
        this.history = history;
    }
}
