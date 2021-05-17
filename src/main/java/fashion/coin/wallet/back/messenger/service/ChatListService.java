package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.messenger.dto.ChatListRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatListService {

    Logger logger = LoggerFactory.getLogger(ChatListService.class);

    @Autowired
    Gson gson;


    public ResultDTO chatList(ChatListRequestDTO requst) {
        try {
            logger.info("Chat list");

            //TODO: Chat List

            return new ResultDTO();

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
