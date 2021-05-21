package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.messenger.dto.ChatListRequestDTO;
import fashion.coin.wallet.back.messenger.dto.ChatListResponseDTO;
import fashion.coin.wallet.back.messenger.dto.UnreadDTO;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.MyConversationRepository;
import fashion.coin.wallet.back.nft.service.ProofService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.messenger.service.ConversationService.CONVERSATION_DIALOG;

@Service
public class ChatListService {

    Logger logger = LoggerFactory.getLogger(ChatListService.class);

    @Autowired
    Gson gson;

    @Autowired
    ClientService clientService;

    @Autowired
    MyConversationRepository myConversationRepository;

    @Autowired
    ProofService proofService;

    @Autowired
    ConversationService conversationService;

    @Autowired
    ChatMessageService chatMessageService;

    public ResultDTO chatList(ChatListRequestDTO request) {
        try {

            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                logger.error(request.getApikey());
                logger.error("Client: {}", client);
                return error109;
            }

            List<MyConversation> myConversationList = myConversationRepository.findByMyId(client.getId());

            // TODO: Временно, пока обновится информация по всем пользователям
            // удалить после 1 июля 2021
            if (System.currentTimeMillis() < 1625107868000L) {
                myConversationList = refreshConversationList(client);
            }
            //////////////////////////////////////////////////////////////////

            if (myConversationList == null) {
                myConversationList = refreshConversationList(client);
            }

            List<ChatListResponseDTO> result = new ArrayList<>();
            for (MyConversation myConversation : myConversationList) {
                ChatListResponseDTO response = new ChatListResponseDTO(myConversation);
                Client friend = clientService.getClient(response.getFriendId());
                response.setAvaExists(friend.avaExists());
                response.setAvatar(friend.getAvatar());
                Conversation conversation = conversationService.getById(myConversation.getConversationId());
                response.setType(conversation.getType());
                result.add(response);
            }
            result.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            return new ResultDTO(true, result, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private List<MyConversation> refreshConversationList(Client client) {

        try {
            List<Long> friendIdList = proofService.mutual(client);
            if (friendIdList == null || friendIdList.size() == 0) {
                return new ArrayList<>();
            }

            for (Long friendId : friendIdList) {
                Conversation conversation = checkConversationExists(friendId, client.getId(), null);
                checkConversationExists(client.getId(), friendId, conversation);
            }

            List<MyConversation> result = myConversationRepository.findByMyId(client.getId());
            if (result == null || result.size() == 0) {
                result = new ArrayList<>();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private Conversation checkConversationExists(Long friendId, Long myId, Conversation conversation) {
        MyConversation myConversation = myConversationRepository.findTopByMyIdAndFriendId(myId, friendId);
        if (myConversation == null) {
            if (conversation == null) {
                conversation = conversationService.createDialog();
            }
            Client friend = clientService.getClient(friendId);
            myConversation = new MyConversation();
            myConversation.setConversationId(conversation.getId());
            myConversation.setFriendId(friendId);
            myConversation.setCryptoname(friend.getCryptoname());
            myConversation.setLastMessage("");
            myConversation.setMyId(myId);
            myConversation.setRead(true);
            myConversation.setTimestamp(System.currentTimeMillis());
            myConversation.setLastReadTime(System.currentTimeMillis());
            myConversation.setUnread(0);
            myConversationRepository.save(myConversation);
        }
        return conversation;
    }

    public MyConversation getMyConversation(Long myId, Long conversationId) {
        MyConversation result = myConversationRepository.findTopByMyIdAndConversationId(myId, conversationId);
        if (result == null) {
            logger.error("My ID: {}", myId);
            logger.error("Conversation ID: {}", conversationId);
            logger.error("My Conversation: {}", result);
        }
        return result;
    }

    public void newMessage(MyConversation myConversation, ChatMessage chatMessage) {

        List<MyConversation> myConversationList = getMyconversationList(myConversation.getConversationId());
        for (MyConversation mc : myConversationList) {
            mc.setTimestamp(chatMessage.getTimestamp());
            mc.setLastMessage(chatMessage.getText());
            mc.setRead(false);
            if (!mc.getMyId().equals(myConversation.getId())) {
                mc.setUnread(mc.getUnread() + 1);
            }
            myConversationRepository.save(mc);
            chatMessageService.sendWsMessage(myConversation.getMyId(), gson.toJson(mc));
        }
    }

    public MyConversation setBlock(MyConversation myConversation) {
        myConversation.setBlock(true);
        myConversationRepository.save(myConversation);
        return myConversation;
    }

    public List<MyConversation> getMyconversationList(Long conversationId) {
        List<MyConversation> myConversationList = myConversationRepository.findByConversationId(conversationId);
        if (myConversationList == null || myConversationList.size() == 0) {
            logger.error("Conversation Id: {}", conversationId);
            logger.error("My Conversations: {}", myConversationList);
        }
        return myConversationList;
    }

    public UnreadDTO setUnread(Long myconversationId, Long readTimestamp, int unreadTotal) {
        MyConversation myConversation = myConversationRepository.findById(myconversationId).orElse(null);
        if (myConversation != null) {
            myConversation.setLastReadTime(readTimestamp);
            myConversation.setUnread(unreadTotal);
            myConversationRepository.save(myConversation);
        } else {
            logger.error("My Conversation ID: {}", myconversationId);
            logger.error("My Conversation: {}", myConversation);
        }

        return countMyUnreadMessages(myConversation.getMyId());
    }

    private UnreadDTO countMyUnreadMessages(Long myId) {

        List<MyConversation> myConversationList = myConversationRepository.findByMyId(myId);
        Integer total = 0;
        for (MyConversation myConversation : myConversationList) {
            if(myConversation.getUnread()==null){
                myConversation.setUnread(0);
            }
            total += myConversation.getUnread();
        }
        return new UnreadDTO(total);
    }
}
