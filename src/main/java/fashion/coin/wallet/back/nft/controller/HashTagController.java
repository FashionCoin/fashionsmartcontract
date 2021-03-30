package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.HashTagFindAllDTO;
import fashion.coin.wallet.back.nft.service.HashtagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HashTagController {

    @Autowired
    HashtagService hashtagService;

    Logger logger = LoggerFactory.getLogger(HashTagController.class);

    @PostMapping("/api/v1/nft/hashtag/find")
    @ResponseBody
    ResultDTO findTags(@RequestBody HashTagFindAllDTO request){

        return hashtagService.findTags(request);
    }

    @PostMapping("/api/v1/nft/hashtag/get")
    @ResponseBody
    ResultDTO fingNft(@RequestBody HashTagFindAllDTO request){

        return hashtagService.findNft(request);
    }

}
