package fashion.coin.wallet.back.messenger.dto;

import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.nft.dto.OneNftResponceDTO;

public class ChatMessageDTO extends ChatMessage {

    OneNftResponceDTO nft;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(ChatMessage chatMessage) {
        super();
    }

    public OneNftResponceDTO getNft() {
        return nft;
    }

    public void setNft(OneNftResponceDTO nft) {
        this.nft = nft;
    }
}
