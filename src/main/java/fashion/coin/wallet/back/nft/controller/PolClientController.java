package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.PolClientRequestDTO;
import fashion.coin.wallet.back.nft.service.PolClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PolClientController {

    Logger logger = LoggerFactory.getLogger(PolClientController.class);

    @Autowired
    PolClientService polClientService;

    @PostMapping("/api/v1/pol/client")
    @ResponseBody
    ResultDTO getClientInfo(@RequestBody PolClientRequestDTO request){
        return polClientService.getClientInfo(request);
    }
}
