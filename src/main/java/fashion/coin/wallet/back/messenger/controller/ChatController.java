package fashion.coin.wallet.back.messenger.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.messenger.dto.ShowChatRequestDTO;
import fashion.coin.wallet.back.messenger.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController {

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    Gson gson;

    @Autowired
    ConversationService conversationService;

    @PostMapping("/api/v1/messenger/showchat")
    @ResponseBody
    ResultDTO showChat(@RequestBody ShowChatRequestDTO request) {
        return conversationService.showChat(request);
    }

    @PostMapping("/api/v1/messenger/block")
    @ResponseBody
    ResultDTO blockFriend(@RequestBody ShowChatRequestDTO request) {
        return conversationService.blockFriend(request);
    }


}
