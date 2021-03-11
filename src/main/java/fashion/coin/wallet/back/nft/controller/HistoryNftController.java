package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.FeedNftRequestDTO;
import fashion.coin.wallet.back.nft.dto.HistoryNftRequestDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HistoryNftController {

    @Autowired
    NftService nftService;

    @PostMapping("/api/v1/nft/history")
    @ResponseBody
    ResultDTO feedNft(@RequestBody HistoryNftRequestDTO request){

        return nftService.getHistory(request);
    }

    @PostMapping("/api/v1/nft/myhistory")
    @ResponseBody
    ResultDTO myHistoryNft(@RequestBody HistoryNftRequestDTO request){

        return nftService.myHistory(request);
    }

}
