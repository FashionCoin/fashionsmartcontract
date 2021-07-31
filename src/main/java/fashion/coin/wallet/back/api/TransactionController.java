package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionDTO;
import fashion.coin.wallet.back.dto.TransactionListRequestDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.service.LogEventService;
import fashion.coin.wallet.back.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by JAVA-P on 24.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    TransactionService transactionService;

    @Autowired
    LogEventService logEventService;

    @PostMapping("/api/v1/send")
    @ResponseBody
    ResultDTO sendTransaction(@RequestBody TransactionRequestDTO request, HttpServletRequest httpServletRequest) {
        logger.info("transaction send " + request.getSenderWallet());
        logEventService.saveTrans(httpServletRequest, request.getSenderWallet(), request.getReceiverWallet());

        return transactionService.send(request);
    }

    @PostMapping("/api/v1/transactionlist")
    @ResponseBody
    List<TransactionDTO> getList(@RequestBody TransactionListRequestDTO request) {
        return transactionService.getList(request);
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
