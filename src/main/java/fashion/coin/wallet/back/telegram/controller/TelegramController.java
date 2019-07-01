package fashion.coin.wallet.back.telegram.controller;

import fashion.coin.wallet.back.telegram.FashionBot;
import fashion.coin.wallet.back.telegram.service.TelegramCheckService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TelegramController {

   TelegramCheckService telegramCheckService = null;

    @GetMapping("/api/telegram/start/bulk")
    @ResponseBody
    public String startBulk(){

        if(telegramCheckService == null) telegramCheckService = TelegramCheckService.getInstance();

        telegramCheckService.startBulk();

        return "<h1>Рассылка началась</h1>";
    }

}
