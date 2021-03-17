package fashion.coin.wallet.back.fwrap.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.fwrap.dto.FExchangeRequestDTO;
import fashion.coin.wallet.back.fwrap.dto.FWrapRequestDTO;
import fashion.coin.wallet.back.fwrap.service.FExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FExchangeController {

    Logger logger = LoggerFactory.getLogger(FExchangeController.class);

    @Autowired
    FExchangeService fExchangeService;

    @PostMapping("/api/v1/fwrap/wrap")
    @ResponseBody
    ResultDTO wrapCurrency(@RequestBody FWrapRequestDTO request){

        return fExchangeService.wrapCurrency(request);
    }


    @PostMapping("/api/v1/fwrap/exchange")
    @ResponseBody
    ResultDTO exchange(@RequestBody FExchangeRequestDTO request){

        return fExchangeService.exchange(request);
    }


}
