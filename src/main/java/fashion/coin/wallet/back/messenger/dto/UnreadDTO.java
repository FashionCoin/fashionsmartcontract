package fashion.coin.wallet.back.messenger.dto;

public class UnreadDTO {
    Integer unread;

    public UnreadDTO(Integer unread) {
        this.unread = unread;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }
}
