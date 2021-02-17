package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyRateService {

    Logger logger = LoggerFactory.getLogger(CurrencyRateService.class);
    @Autowired
    Gson gson;

    @Autowired
    RestTemplate restTemplate;

    @Value("${currency.coinmarketcap.api.key}")
    String cmcApiKey;

    public static final String cmcLink = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";


    @Scheduled(cron = "0 * * * * *")
    public void TestExchangeRate() {
        try {
            logger.info("TestExchangeRate");
            logger.info(String.valueOf(getUsdExchangeRate("BTC")));
            Thread.sleep(1000);
            logger.info(String.valueOf(getUsdExchangeRate("ETH")));
            Thread.sleep(1000);
            logger.info(String.valueOf(getUsdExchangeRate("FSHN")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    BigDecimal getUsdExchangeRate(String currency) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", cmcApiKey);

            HttpEntity entity = new HttpEntity(headers);

            Map<String, String> params = new HashMap<String, String>();
            params.put("symbol", currency);

            ResponseEntity<CmcDTO> result = restTemplate.exchange(cmcLink, HttpMethod.GET, entity, CmcDTO.class, params);

            if (result.getStatusCode().isError()) {
                return null;
            }

            CmcDTO rate = result.getBody();
            logger.info("Coinmarcetcap: {}", gson.toJson(rate));

            return new BigDecimal(rate.data.get(currency).quote.get("USD").price);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    class CmcDTO {

        public Status status;
        public Map<String, CurrencyDTO> data;

    }

    class CurrencyDTO {

        public Long id;
        public String name;
        public String symbol;
        public Map<String, FiatDTO> quote;

    }


    class Status {

        public String timestamp;
        public Long errorCode;
        public Object errorMessage;
        public Long elapsed;
        public Long creditCount;
        public Object notice;

    }

    class FiatDTO {

        public Double price;
        public Double volume24h;
        public Double percentChange1h;
        public Double percentChange24h;
        public Double percentChange7d;
        public Double percentChange30d;
        public Double marketCap;
        public String lastUpdated;

    }

}
