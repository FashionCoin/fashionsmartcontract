package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.service.WrapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WrappedController {

    Logger logger = LoggerFactory.getLogger(WrappedController.class);

    WrapService wrapService;

    @PostMapping("/api/v1/wrap")
    @ResponseBody
    ResultDTO wrap(@RequestBody WrappedRequestDTO request) {
        logger.info("wrap "+request.getEthereumWallet());
        return wrapService.wrap(request);
    }

    @PostMapping("/api/v1/unwrap")
    @ResponseBody
    ResultDTO unwrap(@RequestBody UnwrapRequestDTO request) {
        logger.info("unwrap "+request.getEthereumWallet());
        return wrapService.unwrap(request);
    }

    @PostMapping("/api/v1/history")
    @ResponseBody
    ResultDTO history(@RequestBody WrapHistoryRequestDTO request) {
        logger.info("Get Ethereum History");
        if(request.getNetwork()==null){
            request.setNetwork("ethereum");
        }
        return wrapService.getWalletHistoy(request);
    }


    @Autowired
    public void setWrapService(WrapService wrapService) {
        this.wrapService = wrapService;
    }
}
