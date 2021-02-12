package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.WrappedRequestDTO;
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
    ResultDTO sendTransaction(@RequestBody WrappedRequestDTO request){
        logger.info("wrap");
        return wrapService.wrap(request);
    }


    @Autowired
    public void setWrapService(WrapService wrapService) {
        this.wrapService = wrapService;
    }
}
