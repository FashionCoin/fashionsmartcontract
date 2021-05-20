package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.TransactionCoins;
import fashion.coin.wallet.back.messenger.dto.*;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.ChatMessageRepository;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.service.FeedService;
import fashion.coin.wallet.back.nft.service.NftService;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    TransactionService transactionService;

    public static final String TEXT_MESSAGE = "text";
    public static final String NFT_MESSAGE = "nft";
    public static final String MONEY_MESSAGE = "money";

    Map<Long, WebSocketSession> wsChats = new HashMap<>();

    public List<ChatMessageDTO> getAllMessages(Long conversationId) {

        List<ChatMessage> chatMessageList = chatMessageRepository.findByConversationIdOrderByTimestamp(conversationId);
        if (chatMessageList == null) {
            logger.error("Message List: {}", chatMessageList);
            return new ArrayList<>();
        }
        chatMessageList.sort(Comparator.comparing(ChatMessage::getTimestamp));
        List<ChatMessageDTO> result = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessageList) {
            result.add(convertToChatMessageDTO(chatMessage));
        }

        return result;
    }

    private ChatMessageDTO convertToChatMessageDTO(ChatMessage chatMessage) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(chatMessage);
        if (chatMessage.getType().equals(NFT_MESSAGE)) {
            NftHistory nftHistory = nftService.getEvent(chatMessage.getEventid());
            chatMessageDTO.setNft(nftService.getOneNftDTO(nftHistory));
        } else if (chatMessage.getType().equals(MONEY_MESSAGE)) {
            TransactionCoins transactionCoins = transactionService.getTransactionCoins(chatMessage.getTxhash());
            chatMessageDTO.setTransaction(convertToChatDTO(transactionCoins));
        }
        return chatMessageDTO;
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
        logger.info("Send chat message notification...");
        List<MyConversation> members = chatListService.getMyconversationList(chatMessage.getConversationId());
        for (MyConversation myConversation : members) {
            sendWsMessage(myConversation.getMyId(), gson.toJson(myConversation));
        }
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
            if (nftHistory == null) {
                logger.error("Request: {}", gson.toJson(request));
                logger.error("Nft history: {}", nftHistory);
                return error236;
            }


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

    public ResultDTO sendFshn(SendFshnDTO request) {
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

            TransactionCoins transactionCoins = transactionService.getTransactionCoins(request.getTxhash());
            if (transactionCoins == null) {
                logger.error("Request: {}", gson.toJson(request));
                logger.error("Transaction: {}", transactionCoins);
                return error239;
            }


            if (!transactionCoins.getSender().getId().equals(client.getId())) {
                logger.error("Client ID: {}", client.getId());
                logger.error("Transaction sender: {}", gson.toJson(transactionCoins.getSender()));
                return error237;
            }
            if (!transactionCoins.getReceiver().getId().equals(myConversation.getFriendId())) {
                logger.error("Friend ID: {}", myConversation.getFriendId());
                logger.error("Transaction receiver: {}", gson.toJson(transactionCoins.getReceiver()));
                return error238;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTimestamp(System.currentTimeMillis());
            chatMessage.setAuthorId(client.getId());
            chatMessage.setConversationId(myConversation.getConversationId());
            chatMessage.setTxhash(transactionCoins.getTxhash());
            chatMessage.setType(MONEY_MESSAGE);
            chatMessageRepository.save(chatMessage);

            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(chatMessage);

            chatMessageDTO.setTransaction(convertToChatDTO(transactionCoins));

            notificateForNewMessage(chatMessage);

            chatListService.newMessage(myConversation, chatMessage);

            return new ResultDTO(true, chatMessageDTO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }


    }

    private TransactionChatDTO convertToChatDTO(TransactionCoins transactionCoins) {

        TransactionChatDTO transactionChat = new TransactionChatDTO();
        transactionChat.setId(transactionCoins.getId());
        transactionChat.setTxhash(transactionCoins.getTxhash());
        transactionChat.setAmount(transactionCoins.getAmount());
        transactionChat.setTimestamp(transactionCoins.getTimestamp());
        transactionChat.setSender(transactionCoins.getSender().getId());
        transactionChat.setReceiver(transactionCoins.getReceiver().getId());
        return transactionChat;
    }

    public boolean subscribe(WebSocketSession session, String apikey) {

        if (apikey != null) {
            Client client = clientService.findClientByApikey(apikey);
            if (client != null) {
                wsChats.put(client.getId(), session);
                return true;
            } else {
                logger.error("ApiKey: {}", apikey);
                logger.error("Client: {}", client);
            }
        } else {
            logger.error("ApiKey: {}", apikey);
        }
        return false;
    }

    public boolean sendWsMessage(Long clientId, String message) {
        WebSocketSession connection = wsChats.get(clientId);
        if (connection == null || !connection.isOpen()) {
            wsChats.remove(clientId);
            return false;
        }
        try {
            connection.sendMessage(new TextMessage(message));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            wsChats.remove(clientId);
        }
        return false;
    }
}
