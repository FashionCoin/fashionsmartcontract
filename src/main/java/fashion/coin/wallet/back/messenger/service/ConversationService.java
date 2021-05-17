package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.messenger.dto.ShowChatRequestDTO;
import fashion.coin.wallet.back.messenger.dto.ShowChatResponseDTO;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.model.MyConversation;
import fashion.coin.wallet.back.messenger.repository.ConversationRepository;
import fashion.coin.wallet.back.nft.dto.PolClientRequestDTO;
import fashion.coin.wallet.back.nft.dto.PolClientResponseDTO;
import fashion.coin.wallet.back.nft.service.PolClientService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

import fashion.coin.wallet.back.nft.dto.PolClientData;

import java.util.ArrayList;

@Service
public class ConversationService {

    Logger logger = LoggerFactory.getLogger(ConversationService.class);

    @Autowired
    Gson gson;

    public static final String CONVERSATION_DIALOG = "dialog";
    public static final String CONVERSATION_CHANNEL = "channel";
    public static final String CONVERSATION_GROUP = "group";

    @Autowired
    ConversationRepository conversationRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    ChatListService chatListService;

    @Autowired
    PolClientService polClientService;

    @Autowired
    ChatMessageService chatMessageService;

    public Conversation createDialog() {
        Conversation conversation = new Conversation();
        conversation.setType(CONVERSATION_DIALOG);
        conversationRepository.save(conversation);
        return conversation;
    }

    public ResultDTO showChat(ShowChatRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                logger.error(request.getApikey());
                logger.error("Client: {}", client);
                return error109;
            }

            Conversation conversation = conversationRepository.findById(request.getConversationId()).orElse(null);

            MyConversation myConversation = chatListService.getMyConversation(client.getId(), request.getConversationId());

            ShowChatResponseDTO response = new ShowChatResponseDTO();
            response.setConversationId(myConversation.getConversationId());
            response.setMyConversation(myConversation.getId());
            response.setMyId(client.getId());

            Client friend = clientService.getClient(myConversation.getFriendId());

            response.setFriendId(friend.getId());
            response.setCryptoname(friend.getCryptoname());
            response.setAvatar(friend.getAvatar());
            response.setAvaExists(friend.avaExists());

            PolClientRequestDTO requestDetailedInfo = new PolClientRequestDTO();
            requestDetailedInfo.setId(friend.getId());
            requestDetailedInfo.setApikey(request.getApikey());
            requestDetailedInfo.setCryptoname(friend.getCryptoname());
            ResultDTO resultDetailedInfo = polClientService.getClientInfo(requestDetailedInfo);
            if (!resultDetailedInfo.isResult()) {
                logger.error(gson.toJson(resultDetailedInfo));
                return resultDetailedInfo;
            }
            if (resultDetailedInfo.getData() instanceof PolClientResponseDTO) {
                PolClientResponseDTO details = (PolClientResponseDTO) resultDetailedInfo.getData();
                response.setFaceValue(details.getFaceValue());
                response.setCreativeValue(details.getCreativeValue());
                response.setProofs(details.getProofs());
                response.setProof(details.getProof());
            } else {
                logger.error("Details Type: {}", resultDetailedInfo.getData().getClass());
            }

            response.setBlock(conversation.isBlock());
            response.setType(conversation.getType());

            response.setChatMessageList(chatMessageService.getAllMessages(conversation.getId()));

            return new ResultDTO(true, response, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Conversation getById(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) {
            logger.error("Conversation ID: {}", conversationId);
            logger.error("Conversation: {}", conversation);
        }
        return conversation;
    }
}
