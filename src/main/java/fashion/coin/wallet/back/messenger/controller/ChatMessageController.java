package fashion.coin.wallet.back.messenger.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.messenger.dto.SendFshnDTO;
import fashion.coin.wallet.back.messenger.dto.SendNftDTO;
import fashion.coin.wallet.back.messenger.dto.SendTextDTO;
import fashion.coin.wallet.back.messenger.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatMessageController {

    Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @Autowired
    Gson gson;

    @Autowired
    ChatMessageService chatMessageService;

    @PostMapping("/api/v1/messenger/send/text")
    @ResponseBody
    ResultDTO sendText(@RequestBody SendTextDTO request) {
        return chatMessageService.sendText(request);
    }

    @PostMapping("/api/v1/messenger/send/nft")
    @ResponseBody
    ResultDTO sendNft(@RequestBody SendNftDTO request) {
        return chatMessageService.sendNft(request);
    }

    @PostMapping("/api/v1/messenger/send/fshn")
    @ResponseBody
    ResultDTO sendFshn(@RequestBody SendFshnDTO request) {
        return chatMessageService.sendFshn(request);
    }


}
