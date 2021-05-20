package fashion.coin.wallet.back.messenger.dto;

import fashion.coin.wallet.back.entity.TransactionCoins;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.nft.dto.OneNftResponceDTO;

public class ChatMessageDTO extends ChatMessage {

    OneNftResponceDTO nft;

    TransactionChatDTO transaction;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(ChatMessage chatMessage) {
        setId(chatMessage.getId());
        setTimestamp(chatMessage.getTimestamp());
        setType(chatMessage.getType());
        setConversationId(chatMessage.getConversationId());
        setAuthorId(chatMessage.getAuthorId());
        setText(chatMessage.getText());
        setEventid(chatMessage.getEventid());
    }

    public OneNftResponceDTO getNft() {
        return nft;
    }

    public void setNft(OneNftResponceDTO nft) {
        this.nft = nft;
    }

    public TransactionChatDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionChatDTO transaction) {
        this.transaction = transaction;
    }
}
