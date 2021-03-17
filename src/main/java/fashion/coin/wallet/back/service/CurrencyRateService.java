package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.entity.CurrencyRate;
import fashion.coin.wallet.back.repository.CurrencyRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CurrencyRateService {

    Logger logger = LoggerFactory.getLogger(CurrencyRateService.class);
    @Autowired
    Gson gson;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyRateRepository currencyRateRepository;

    @Value("${currency.coinmarketcap.api.key}")
    String cmcApiKey;

    private static final String cmcLink = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";

    public static final String params = "?symbol=USDT&convert=FSHN,BTC,ETH,USD,EUR,GBP,UAH";



    @Scheduled(cron = "0 */10 * * * *")
    public void updateExchangeRate() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", cmcApiKey);

            HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<CmcDTO> responseEntity = restTemplate.exchange(
                    "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest"+
                            params,
                    HttpMethod.GET,
                    entity,
                    CmcDTO.class
            );

            CmcDTO cmcDTO = responseEntity.getBody();

            Map<String, PriceDTO> priceMap = cmcDTO.data.get("USDT").quote;

            BigDecimal fshnPrice = new BigDecimal(priceMap.get("FSHN").price);
            for(Map.Entry<String,PriceDTO> entry : priceMap.entrySet()){
                if(!entry.getKey().equals("FSHN")) {
                    CurrencyRate currencyRate = new CurrencyRate();
                    currencyRate.setCurrency(entry.getKey());
                    currencyRate.setDateTime(LocalDateTime.now());
                    currencyRate.setRate(fshnPrice.divide(
                            new BigDecimal( entry.getValue().price
                            ),6, RoundingMode.HALF_UP));
                    logger.info(gson.toJson(currencyRate));
                    currencyRateRepository.save(currencyRate);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class CmcDTO {
        public Status status;
        public Map<String, CurrencyDTO> data;
    }

    static class CurrencyDTO {
        public Long id;
        public String name;
        public Map<String, PriceDTO> quote;
    }

    static class Status {
        public String timestamp;
    }

    static class PriceDTO {
        public String price;
    }
}
