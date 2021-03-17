package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.NewCommentNftDTO;
import fashion.coin.wallet.back.nft.dto.NftTransferDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransferNftController {

    Logger logger = LoggerFactory.getLogger(TransferNftController.class);

    @Autowired
    NftService nftService;

    @PostMapping("/api/v1/nft/transfer")
    @ResponseBody
    ResultDTO transfer(@RequestBody NftTransferDTO request) {
        return nftService.transfer(request);
    }



}
