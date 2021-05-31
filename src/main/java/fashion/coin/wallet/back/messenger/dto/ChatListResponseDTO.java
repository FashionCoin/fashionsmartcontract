package fashion.coin.wallet.back.messenger.dto;

import fashion.coin.wallet.back.messenger.model.MyConversation;

public class ChatListResponseDTO extends MyConversation {

    String avatar;
    boolean avaExists;
    String type;
    int totalMessages;

    public ChatListResponseDTO(MyConversation myConversation) {
        setConversationId(myConversation.getConversationId());
        setCryptoname(myConversation.getCryptoname());
        setFriendId(myConversation.getFriendId());
        setId(myConversation.getId());
        setLastMessage(myConversation.getLastMessage());
        setMyId(myConversation.getMyId());
        setRead(myConversation.isRead());
        setTimestamp(myConversation.getTimestamp());
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isAvaExists() {
        return avaExists;
    }

    public void setAvaExists(boolean avaExists) {
        this.avaExists = avaExists;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }
}
