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
        String test = gson.toJson(buyNftDTO);
        logger.info(test);
        logger.info(String.valueOf(buyNftDTO));
        ResultDTO result = nftService.buy(buyNftDTO);
        nftService.setInSale(buyNftDTO.getNftId(),false);
        return result;
    }


    @PostMapping("/api/v1/nft/checkshare")
    @ResponseBody
    ResultDTO checkShare(@RequestBody BuyNftDTO request) {

        if(request.getPieces()!=null && request.getOwnerId() != null){
            return nftService.checkShareTirage(request);
        }

        return nftService.checkShare(request);
    }


}
