package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.CurrencyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    Logger logger = LoggerFactory.getLogger(CurrencyController.class);

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

    @PostMapping("/api/v1/currencyhistory")
    @ResponseBody
    List<CurrencyDTO> getCurrencyHistory(@RequestBody Map<String,Long> params) {
        if(params.containsKey("time")){
            Long time = params.get("time");
            logger.info("time:"+time);
            return currencyService.getCurrencyHistory(LocalDateTime.ofEpochSecond(time,0, ZoneOffset.UTC));
        }
        return currencyService.getCurrencyList();
    }

    @PostMapping("/api/v1/currencyavg")
    @ResponseBody
    List<CurrencyDTO> getCurrencyAverage(@RequestBody Map<String,String> params) {
        if(params.containsKey("date")){
            return currencyService.getAverageByDate(params.get("date"));
        }
        return new ArrayList<>();
    }



    @Autowired
    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
}
