package fashion.coin.wallet.back.fwrap.controller;

import fashion.coin.wallet.back.dto.ApiKeyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.fwrap.dto.FCurrencyRequestDTO;
import fashion.coin.wallet.back.fwrap.dto.FSendMoneyRequestDTO;
import fashion.coin.wallet.back.fwrap.entity.FTransaction;
import fashion.coin.wallet.back.fwrap.service.FTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FTransactionsController {

    Logger logger = LoggerFactory.getLogger(FTransactionsController.class);

    @Autowired
    FTransactionService fTransactionService;

    @PostMapping("/api/v1/fwrap/transaction")
    @ResponseBody
    ResultDTO getByCurrency(@RequestBody FCurrencyRequestDTO request) {
        logger.info("transaction " + request.getApikey());
        return fTransactionService.getByCurrency(request);
    }

    @PostMapping("/api/v1/fwrap/send")
    @ResponseBody
    ResultDTO sendMoney(@RequestBody FSendMoneyRequestDTO request) {
        logger.info("send " + request.getReceiver());
        return fTransactionService.sendMoney(request);
    }


}
