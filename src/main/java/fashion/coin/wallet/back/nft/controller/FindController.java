package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.FindByDurationRequestDTO;
import fashion.coin.wallet.back.nft.dto.FindNameRequestDTO;
import fashion.coin.wallet.back.nft.service.FindPolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FindController {

    Logger logger = LoggerFactory.getLogger(FindController.class);

    @Autowired
    FindPolService findPolService;


    @PostMapping("/api/v1/find/name")
    @ResponseBody
    ResultDTO findName(@RequestBody FindNameRequestDTO request) {
        return findPolService.byName(request);
    }


    @PostMapping("/api/v1/find/creators")
    @ResponseBody
    ResultDTO findCreators(@RequestBody FindByDurationRequestDTO request) {
        return findPolService.creators(request);
    }


}
