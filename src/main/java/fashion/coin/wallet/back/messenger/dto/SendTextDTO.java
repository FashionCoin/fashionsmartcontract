package fashion.coin.wallet.back.messenger.dto;

import java.util.List;

public class SendTextDTO {
    String apikey;
    Long conversationId;
    String text;
    List<Long> nftList;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Long> getNftList() {
        return nftList;
    }

    public void setNftList(List<Long> nftList) {
        this.nftList = nftList;
    }
}
