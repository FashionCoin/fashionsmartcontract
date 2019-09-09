package fashion.coin.wallet.back.api;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.AILefttransactionDTO;
import fashion.coin.wallet.back.dto.AiPrepareDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.SetAiKeysDTO;
import fashion.coin.wallet.back.dto.blockchain.ResponceDTO;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by JAVA-P on 26.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class AdminController {

    AIService aiService;

    TransactionService transactionService;

    Gson gson;

//    @PostMapping("/setkeys")
//    @ResponseBody
//    ResponceDTO setKeys(@RequestBody SetAiKeysDTO keys) {
//        System.out.println(keys);
//        return aiService.setKeys(keys.getPub_key(), keys.getPriv_key());
//    }


    @GettMapping("/api/v1/aiprepare")
    @ResponseBody
    public String prepare(@RequestBody AiPrepareDTO aiprepareParams) {
        return transactionService.prepareAiTransactions(aiprepareParams.getStart(), aiprepareParams.getEnd());
    }

    @PostMapping("/api/v1/aigetlist")
    public String getList(ModelMap modelMap) {

        modelMap.addAttribute("txlist", transactionService.getAiTransactions());
    return "aigetlist";
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
