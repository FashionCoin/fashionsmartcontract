package fashion.coin.wallet.back.messenger.dto;

import fashion.coin.wallet.back.messenger.model.ChatMessage;

import java.math.BigDecimal;
import java.util.List;

public class ShowChatResponseDTO {

    Long conversationId;
    Long myConversation;

    Long myId;

    Long friendId;
    String cryptoname;
    String avatar;
    boolean avaExists;

    BigDecimal faceValue;

    BigDecimal creativeValue;

    BigDecimal proofs = BigDecimal.ZERO;
    BigDecimal proof = BigDecimal.ZERO;

    boolean block;

    List<ChatMessage> chatMessageList;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMyConversation() {
        return myConversation;
    }

    public void setMyConversation(Long myConversation) {
        this.myConversation = myConversation;
    }

    public Long getMyId() {
        return myId;
    }

    public void setMyId(Long myId) {
        this.myId = myId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
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

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getCreativeValue() {
        return creativeValue;
    }

    public void setCreativeValue(BigDecimal creativeValue) {
        this.creativeValue = creativeValue;
    }

    public BigDecimal getProofs() {
        return proofs;
    }

    public void setProofs(BigDecimal proofs) {
        this.proofs = proofs;
    }

    public BigDecimal getProof() {
        return proof;
    }

    public void setProof(BigDecimal proof) {
        this.proof = proof;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }
}
