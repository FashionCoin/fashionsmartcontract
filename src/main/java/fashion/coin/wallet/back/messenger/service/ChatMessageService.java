package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import com.google.inject.internal.asm.$ClassTooLargeException;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.messenger.dto.ChatMessageDTO;
import fashion.coin.wallet.back.messenger.dto.SendNftDTO;
import fashion.coin.wallet.back.messenger.dto.SendTextDTO;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.ChatMessageRepository;
import fashion.coin.wallet.back.nft.dto.OneNftResponceDTO;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.service.FeedService;
import fashion.coin.wallet.back.nft.service.NftService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class ChatMessageService {

    Logger logger = LoggerFactory.getLogger(ChatMessageService.class);

    @Autowired
    Gson gson;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    ConversationService conversationService;

    @Autowired
    ChatListService chatListService;

    @Autowired
    NftService nftService;

    @Autowired
    FeedService feedService;

    public static final String TEXT_MESSAGE = "text";
    public static final String NFT_MESSAGE = "nft";
    public static final String MONEY_MESSAGE = "money";


    public List<ChatMessage> getAllMessages(Long conversationId) {

        List<ChatMessage> chatMessageList = chatMessageRepository.findByConversationIdOrderByTimestamp(conversationId);
        if (chatMessageList == null) {
            logger.error("Message List: {}", chatMessageList);
            return new ArrayList<>();
        }
        chatMessageList.sort(Comparator.comparing(ChatMessage::getTimestamp));
        return chatMessageList;
    }

    public ResultDTO sendText(SendTextDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                logger.error(request.getApikey());
                logger.error("Client: {}", client);
                return error109;
            }
            Conversation conversation = conversationService.getById(request.getConversationId());
            MyConversation myConversation = chatListService.getMyConversation(client.getId(), conversation.getId());
            if (myConversation == null) {
                logger.error("Request: {}", gson.toJson(request));
                logger.error("My Conversation: {}", myConversation);
                return error233;
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(System.currentTimeMillis());
            chatMessage.setAuthorId(client.getId());
            chatMessage.setConversationId(myConversation.getConversationId());
            chatMessage.setText(request.getText());
            chatMessage.setType(TEXT_MESSAGE);

            chatMessageRepository.save(chatMessage);

            notificateForNewMessage(chatMessage);


            chatListService.newMessage(myConversation, chatMessage);

            return new ResultDTO(true, chatMessage, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void notificateForNewMessage(ChatMessage chatMessage) {
        // TODO: Сделать уведломление всем участникам чата про новое сообщение
        logger.info("Send chat message notification...");
        logger.info(gson.toJson(chatMessage));
    }

    public ResultDTO sendNft(SendNftDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                logger.error(request.getApikey());
                logger.error("Client: {}", client);
                return error109;
            }
            Conversation conversation = conversationService.getById(request.getConversationId());
            MyConversation myConversation = chatListService.getMyConversation(client.getId(), conversation.getId());
            if (myConversation == null) {
                logger.error("Request: {}", gson.toJson(request));
                logger.error("My Conversation: {}", myConversation);
                return error233;
            }

            NftHistory nftHistory = nftService.getEvent(request.getEventid());
            if (!nftHistory.getIdFrom().equals(client.getId())) {
                logger.error("Client ID: {}", client.getId());
                logger.error("Nft event: {}", gson.toJson(nftHistory));
                return error234;
            }
            if (!nftHistory.getIdTo().equals(myConversation.getFriendId())) {
                logger.error("Friend ID: {}", myConversation.getFriendId());
                logger.error("Nft event: {}", gson.toJson(nftHistory));
                return error235;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(System.currentTimeMillis());
            chatMessage.setAuthorId(client.getId());
            chatMessage.setConversationId(myConversation.getConversationId());
            chatMessage.setEventid(nftHistory.getId());
            chatMessage.setType(NFT_MESSAGE);
            chatMessageRepository.save(chatMessage);

            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(chatMessage);

            chatMessageDTO.setNft(nftService.getOneNftDTO(nftHistory));

            notificateForNewMessage(chatMessage);

            chatListService.newMessage(myConversation, chatMessage);

            return new ResultDTO(true, chatMessageDTO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }


    }
}
