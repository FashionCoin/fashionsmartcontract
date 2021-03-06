package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.TransactionCoins;
import fashion.coin.wallet.back.messenger.dto.*;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.messenger.model.ChatNftInMessage;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.ChatMessageRepository;
import fashion.coin.wallet.back.messenger.repository.ChatNftInMessageRepository;
import fashion.coin.wallet.back.nft.entity.Nft;
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

    @Autowired
    ChatNftInMessageRepository chatNftInMessageRepository;

    public static final String TEXT_MESSAGE = "text";
    public static final String NFT_MESSAGE = "nft";
    public static final String MONEY_MESSAGE = "money";

    Map<Long, List<WebSocketSession>> wsChats = new HashMap<>();


    public static final String UNREAD_MESSAGES = "unread";
    public static final String NEW_MESSAGE = "newmessage";


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
        if (chatMessage.getType().equals(TEXT_MESSAGE)) {
            chatMessageDTO.setNftList(getNftListByChatMessageId(chatMessage.getId()));
        }
        if (chatMessage.getType().equals(NFT_MESSAGE)) {
            NftHistory nftHistory = nftService.getEvent(chatMessage.getEventid());
            chatMessageDTO.setNft(nftService.getOneNftDTO(nftHistory));
        } else if (chatMessage.getType().equals(MONEY_MESSAGE)) {
            TransactionCoins transactionCoins = transactionService.getTransactionCoins(chatMessage.getTxhash());
            chatMessageDTO.setTransaction(convertToChatDTO(transactionCoins));
        }
        return chatMessageDTO;
    }

    private List<Nft> getNftListByChatMessageId(Long messageId) {
        List<Nft> nftList = null;
        try {
            List<ChatNftInMessage> chatNftInMessageList = chatNftInMessageRepository.findByMessageId(messageId);
            if (chatNftInMessageList != null && chatNftInMessageList.size() > 0) {
                nftList = new ArrayList<>();
                for (ChatNftInMessage chatNftInMessage : chatNftInMessageList) {
                    Nft nft = nftService.findNft(chatNftInMessage.getNftId());
                    nftList.add(nft);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nftList;
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
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(chatMessage);
            if (request.getNftList() != null) {
                appendNftList(chatMessageDTO, request.getNftList());
            }

            chatListService.newMessage(myConversation, chatMessageDTO);

            return new ResultDTO(true, chatMessageDTO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private ChatMessageDTO appendNftList(ChatMessageDTO chatMessageDTO, List<Long> nftList) {
        try {
            if (nftList != null && nftList.size() > 0) {
                List<Nft> nfts = new ArrayList<>();
                for (Long nftId : nftList) {
                    Nft nft = nftService.findNft(nftId);
                    if (nft != null) {
                        ChatNftInMessage chatNftInMessage = new ChatNftInMessage();
                        chatNftInMessage.setMessageId(chatMessageDTO.getId());
                        chatNftInMessage.setNftId(nft.getId());
                        chatNftInMessageRepository.save(chatNftInMessage);
                        nfts.add(nft);
                    }
                }
                chatMessageDTO.setNftList(nfts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessageDTO;
    }

//    private void notificateForNewMessage(ChatMessage chatMessage) {
//        logger.info("Send chat message notification...");
//        List<MyConversation> members = chatListService.getMyconversationList(chatMessage.getConversationId());
//        logger.info("Members: {}",gson.toJson(members));
//        for (MyConversation myConversation : members) {
//            sendWsMessage(myConversation.getMyId(), gson.toJson(myConversation));
//        }
//    }

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

//            notificateForNewMessage(chatMessage);

            chatListService.newMessage(myConversation, chatMessageDTO);

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

//            notificateForNewMessage(chatMessage);

            chatListService.newMessage(myConversation, chatMessageDTO);

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
                if (!wsChats.containsKey(client.getId())) {
                    wsChats.put(client.getId(), new ArrayList<>());
                }
                wsChats.get(client.getId()).add(session);
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

    public void sendWsMessage(Long clientId, WsResultDTO message) {
        logger.info("Send Message");

        List<WebSocketSession> connectionList = wsChats.get(clientId);
//        logger.info("Connection list: {}",connectionList);
        if (connectionList != null && connectionList.size() > 0) {
            for (int i = 0; i < connectionList.size(); i++) {
                WebSocketSession connection = connectionList.get(i);

                if (connection == null || !connection.isOpen()) {
                    logger.error("Connection: {}", gson.toJson(connection.getId()));
                    wsChats.get(clientId).remove(i);
                } else {
                    try {
                        connection.sendMessage(new TextMessage(gson.toJson(message)));
                        logger.info("Sent message: {}", message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        wsChats.get(clientId).remove(i);
                    }
                }
            }
        } else {
            logger.error("Clietn ID: {}", clientId);
            logger.error("Connection list: {}", gson.toJson(connectionList));
            wsChats.remove(clientId);
        }

    }

    public List<ChatMessageDTO> getLastMessages(Long conversationId, Long timestamp) {
        List<ChatMessage> chatMessageList = chatMessageRepository
                .findByConversationIdAndTimestampGreaterThanOrderByTimestamp(conversationId, timestamp);
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


    public int totalMessages(Long conversationId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByConversationIdOrderByTimestamp(conversationId);
        if (chatMessageList == null) {
            return 0;
        } else {
            return chatMessageList.size();
        }
    }
}
