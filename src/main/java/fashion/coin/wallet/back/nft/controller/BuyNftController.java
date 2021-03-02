package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.BuyNftDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BuyNftController {

    @Autowired
    NftService nftService;


    @PostMapping("/api/v1/nft/buy")
    @ResponseBody
    ResultDTO buyNft(@RequestBody BuyNftDTO buyNftDTO) {
        return nftService.buy(buyNftDTO.getNftId(), buyNftDTO.getTransactionRequest());
    }


    @PostMapping("/api/v1/nft/checkshare")
    @ResponseBody
    ResultDTO checkShare(@RequestBody BuyNftDTO buyNftDTO) {
        return nftService.checkShare(buyNftDTO.getNftId());
    }


}
