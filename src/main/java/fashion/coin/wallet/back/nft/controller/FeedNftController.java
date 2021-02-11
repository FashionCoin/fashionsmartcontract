package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.FeedNftRequestDTO;
import fashion.coin.wallet.back.nft.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FeedNftController {

    FeedService feedService;

    @PostMapping("/api/v1/nft/feed")
    @ResponseBody
    ResultDTO feedNft(@RequestBody FeedNftRequestDTO request){

        return feedService.getFeed(request);

    }

    @Autowired
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }
}
