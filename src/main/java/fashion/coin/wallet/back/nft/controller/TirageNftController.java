package fashion.coin.wallet.back.nft.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.FeedNftRequestDTO;
import fashion.coin.wallet.back.nft.dto.TirageDisributionRequestDTO;
import fashion.coin.wallet.back.nft.service.TirageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TirageNftController {

    Logger logger = LoggerFactory.getLogger(TirageNftController.class);

    @Autowired
    Gson gson;

    @Autowired
    TirageService tirageService;

    @PostMapping("/api/v1/tirage/distribution")
    @ResponseBody
    ResultDTO distribution(@RequestBody TirageDisributionRequestDTO request){

        return tirageService.distribution(request);

    }
}
