package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.messenger.model.ChatMessage;
import fashion.coin.wallet.back.messenger.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    Logger logger = LoggerFactory.getLogger(ChatMessageService.class);

    @Autowired
    Gson gson;

    @Autowired
    ChatMessageRepository chatMessageRepository;


    public List<ChatMessage> getAllMessages(Long conversationId) {

        List<ChatMessage> chatMessageList = chatMessageRepository.findByConversationIdOrderByTimestamp(conversationId);
        if (chatMessageList == null) {
            logger.error("Message List: {}", chatMessageList);
            return new ArrayList<>();
        }
        return chatMessageList;
    }
}
