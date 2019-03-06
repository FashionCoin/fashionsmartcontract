package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.CurrencyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.CurrencyService;
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
public class CurrencyController {

    CurrencyService currencyService;

    @PostMapping("/api/v1/currencylist")
    @ResponseBody
    List<CurrencyDTO> getCurrencyList() {
        return currencyService.getCurrencyList();
    }

    @PostMapping("/api/v1/currencyrate")
    @ResponseBody
    CurrencyDTO getCurrencyRate(@RequestBody String currency) {
        return currencyService.getCurrencyRate(currency);
    }

    @Autowired
    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
}
