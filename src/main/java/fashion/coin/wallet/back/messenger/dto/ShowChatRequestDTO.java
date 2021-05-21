package fashion.coin.wallet.back.messenger.dto;

public class ShowChatRequestDTO {
    String apikey;
    Long conversationId;
    Long timestamp;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {

        this.conversationId = conversationId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
