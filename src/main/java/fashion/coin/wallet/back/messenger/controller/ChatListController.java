package fashion.coin.wallet.back.messenger.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.messenger.dto.ChatListRequestDTO;
import fashion.coin.wallet.back.messenger.service.ChatListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatListController {

    Logger logger = LoggerFactory.getLogger(ChatListController.class);

    @Autowired
    Gson gson;

    @Autowired
    ChatListService chatListService;

    @PostMapping("/api/v1/messenger/chatlist")
    @ResponseBody
    ResultDTO chatList(@RequestBody ChatListRequestDTO request) {
        return chatListService.chatList(request);
    }

    @PostMapping("/api/v1/messenger/unread")
    @ResponseBody
    ResultDTO unreadTotal(@RequestBody ChatListRequestDTO request) {
        return chatListService.unreadTotal(request);
    }



}
