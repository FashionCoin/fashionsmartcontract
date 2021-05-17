package fashion.coin.wallet.back.messenger.dto;

public class ShowChatRequestDTO {
    String apikey;
    Long conversationId;

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
}
