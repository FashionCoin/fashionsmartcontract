package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.dto.NewValueRequestDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NftController {

    Logger logger = LoggerFactory.getLogger(NftController.class);

    @Autowired
    NftService nftService;

    @PostMapping("/api/v1/nft/one")
    @ResponseBody
    ResultDTO getNft(@RequestBody NftRequestDTO request){
        return nftService.getOneNft(request);
    }


    @PostMapping("/api/v1/nft/newvalue")
    @ResponseBody
    ResultDTO newValue(@RequestBody NewValueRequestDTO request){
        logger.info("new value "+request.getNftId());
        return nftService.setNewValue(request);
    }


    @PostMapping("/api/v1/nft/burn")
    @ResponseBody
    ResultDTO burnNft(@RequestBody NftRequestDTO request){
        logger.info("burn "+ request.getNftId());
        return nftService.burnNft(request);
    }




}
