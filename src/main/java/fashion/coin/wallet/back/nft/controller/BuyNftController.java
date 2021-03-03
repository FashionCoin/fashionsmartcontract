package fashion.coin.wallet.back.nft.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.BuyNftDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BuyNftController {

    Logger logger = LoggerFactory.getLogger(BuyNftDTO.class);

    @Autowired
    NftService nftService;

    @Autowired
    Gson gson;

    @PostMapping("/api/v1/nft/buy")
    @ResponseBody
    ResultDTO buyNft(@RequestBody String request) {

        logger.info(request);
        BuyNftDTO buyNftDTO = gson.fromJson(request, BuyNftDTO.class);
        logger.info(String.valueOf(buyNftDTO));
        return nftService.buy(buyNftDTO);
    }


    @PostMapping("/api/v1/nft/checkshare")
    @ResponseBody
    ResultDTO checkShare(@RequestBody BuyNftDTO buyNftDTO) {
        return nftService.checkShare(buyNftDTO.getNftId());
    }


}
