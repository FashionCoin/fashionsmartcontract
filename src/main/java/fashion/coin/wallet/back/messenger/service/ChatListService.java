package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.messenger.dto.ChatListRequestDTO;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.MyConversationRepository;
import fashion.coin.wallet.back.nft.service.ProofService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

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
            if(System.currentTimeMillis() < 1625107868000L){
                myConversationList = refreshConversationList(client);
            }
            //////////////////////////////////////////////////////////////////

            if (myConversationList == null) {
                myConversationList = refreshConversationList(client);
            }

            return new ResultDTO(true, myConversationList, 0);

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
                checkConversationExists(friendId, client.getId());
                checkConversationExists(client.getId(), friendId);
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

    private void checkConversationExists(Long friendId, Long myId) {
        MyConversation myConversation = myConversationRepository.findTopByMyIdAndFriendId(myId, friendId);
        if (myConversation == null) {
            Client friend = clientService.getClient(friendId);
            myConversation = new MyConversation();
            myConversation.setFriendId(friendId);
            myConversation.setCryptoname(friend.getCryptoname());
            myConversation.setLastMessage("");
            myConversation.setMyId(myId);
            myConversation.setRead(true);
            myConversation.setTimestamp(System.currentTimeMillis());
            myConversationRepository.save(myConversation);
        }
    }
}
