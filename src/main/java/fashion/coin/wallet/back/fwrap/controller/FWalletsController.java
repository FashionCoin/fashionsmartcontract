package fashion.coin.wallet.back.fwrap.controller;

import fashion.coin.wallet.back.dto.ApiKeyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.fwrap.service.FWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FWalletsController {

    Logger logger = LoggerFactory.getLogger(FWalletsController.class);

    @Autowired
    FWalletService fWalletService;


    @PostMapping("/api/v1/fwrap/wallets")
    @ResponseBody
    ResultDTO getClientWallets(ApiKeyDTO request){
        return fWalletService.getClientWallets(request);
    }

}
