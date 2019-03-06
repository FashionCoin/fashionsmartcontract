package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionDTO;
import fashion.coin.wallet.back.dto.TransactionListRequestDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

    TransactionService transactionService;

    @PostMapping("/api/v1/send")
    @ResponseBody
    ResultDTO sendTransaction(@RequestBody TransactionRequestDTO request){
        return transactionService.send(request);
    }

    @PostMapping("/api/v1/transactionlist")
    @ResponseBody
    List<TransactionDTO> getList(@RequestBody TransactionListRequestDTO request){
        return transactionService.getList(request);
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
