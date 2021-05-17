package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.messenger.model.Conversation;
import fashion.coin.wallet.back.messenger.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public Conversation createDialog() {
        Conversation conversation = new Conversation();
        conversation.setType(CONVERSATION_DIALOG);
        conversationRepository.save(conversation);
        return conversation;
    }
}
