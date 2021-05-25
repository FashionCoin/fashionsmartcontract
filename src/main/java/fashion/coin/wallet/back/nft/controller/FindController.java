package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.FindByDurationRequestDTO;
import fashion.coin.wallet.back.nft.dto.FindNameRequestDTO;
import fashion.coin.wallet.back.nft.dto.TopTenDTO;
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
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.byName(request);
        logger.info("Request end");
        return resultDTO;

    }


    @PostMapping("/api/v1/find/creators")
    @ResponseBody
    ResultDTO findCreators(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.creators(request);
        logger.info("Request end");
        return resultDTO;
    }


    @PostMapping("/api/v1/find/collectors")
    @ResponseBody
    ResultDTO findCollectors(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.collectors(request);
        logger.info("Request end");
        return resultDTO;
    }

    @PostMapping("/api/v1/find/topproofs")
    @ResponseBody
    ResultDTO findTopProofs(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.topProofs(request);
        logger.info("Request end");
        return resultDTO;
    }

    @PostMapping("/api/v1/find/sold")
    @ResponseBody
    ResultDTO findRecentlySold(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.recentlySold(request);
        logger.info("Request end");
        return resultDTO;
    }

    @PostMapping("/api/v1/find/mostexpface")
    @ResponseBody
    ResultDTO findMostExpensiveFaceValue(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.mostExpensiveFaceValue(request);
        logger.info("Request end");
        return resultDTO;
    }

    @PostMapping("/api/v1/find/mostexpcreative")
    @ResponseBody
    ResultDTO findMostExpensiveCreativeValue(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Request start");
        ResultDTO resultDTO = findPolService.mostExpensiveCreativeValue(request);
        logger.info("Request end");
        return resultDTO;
    }


    @PostMapping("/api/v1/find/topten")
    @ResponseBody
    ResultDTO topTen(@RequestBody FindByDurationRequestDTO request) {
        logger.info("Top ten start");
        ResultDTO resultDTO = findPolService.topTen(request);
        logger.info("Request end");
        return resultDTO;
    }



}
