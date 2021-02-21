package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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
import java.time.temporal.Temporal;
import java.util.ArrayList;
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

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyRateRepository currencyRateRepository;

    @Value("${currency.coinmarketcap.api.key}")
    String cmcApiKey;

    private static final String cmcLink = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
    private static final String apiUrlNazbank = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    static LocalDateTime lastUpdate = LocalDateTime.now();
    Map<String, BigDecimal> lastExchangeRate = new HashMap<>();

    @Scheduled(cron = "0 */5 * * * *")
    public void updateExchangeRate() {
        try {
            List<String> crypts = currencyService.getAvailableCrypts();
            for(String currency : crypts){
                CurrencyRate currencyRate = new CurrencyRate();
                currencyRate.setCurrency(currency);
                currencyRate.setRate(getFshnExchangeRate(currency).setScale(6));
                currencyRate.setDateTime(LocalDateTime.now());
                if(!currencyRate.getRate().equals(BigDecimal.ONE)){
                    currencyRateRepository.save(currencyRate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    BigDecimal getFshnExchangeRate(String currency) {
        BigDecimal fshnUsd = getUsdExchangeRate("FSHN");
        if (currency.equals("USD")) {
            return BigDecimal.ONE.divide(fshnUsd, 3, RoundingMode.HALF_UP);
        } else if (currency.equals("ETH") || currency.equals("BTC")) {
            BigDecimal curUsd = getUsdExchangeRate(currency);
            return curUsd.divide(fshnUsd, 3, RoundingMode.HALF_UP);
        } else if (currency.equals("UAH")) {
            BigDecimal usdUah = getRateFromNBU(currency);
            return BigDecimal.ONE.divide(usdUah.multiply(fshnUsd), 3, RoundingMode.HALF_UP);
        } else {
            logger.error("Currency: {}", currency);
        }
        return BigDecimal.ONE;
    }

    BigDecimal getUsdExchangeRate(String currency) {
        try {
            if (!lastExchangeRate.containsKey(currency)) {
                lastExchangeRate.put(currency, BigDecimal.ONE);
            }

            if (lastUpdate.plusSeconds(600).isAfter(LocalDateTime.now())) {
                logger.info("Last update: {}", lastUpdate);
//                return lastExchangeRate.get(currency);
                Thread.sleep(600000);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", cmcApiKey);

            HttpEntity entity = new HttpEntity(headers);

            Map<String, String> params = new HashMap<String, String>();
            params.put("symbol", currency);

            lastUpdate = LocalDateTime.now();
            ResponseEntity<CmcDTO> result = restTemplate.exchange(cmcLink + "?symbol=" + currency, HttpMethod.GET, entity, CmcDTO.class, params);

            if (result.getStatusCode().isError()) {
                logger.error(result.getStatusCode().toString());
                return lastExchangeRate.get(currency);
            }

            CmcDTO rate = result.getBody();
            logger.info("Coinmarcetcap: {}", gson.toJson(rate));

            BigDecimal usdRate = new BigDecimal(rate.data.get(currency).quote.get("USD").price);
            lastExchangeRate.put(currency, usdRate);
            return usdRate;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return lastExchangeRate.get(currency);
    }

    private BigDecimal getRateFromNBU(String currency) {
        try {
            if (!lastExchangeRate.containsKey(currency)) {
                lastExchangeRate.put(currency, BigDecimal.ONE);
            }

            if (lastUpdate.plusSeconds(600).isAfter(LocalDateTime.now())) {
                logger.info("Last update: {}", lastUpdate);
//                return lastExchangeRate.get(currency);
                Thread.sleep(600000);
            }

            lastUpdate = LocalDateTime.now();
            ArrayList<LinkedTreeMap> responce = restTemplate.getForObject(apiUrlNazbank, ArrayList.class);

            responce.removeIf(listEntity -> !listEntity.get("cc").equals("USD"));
            logger.info("Nazbank: {}", gson.toJson(responce));
            String usdUah = String.valueOf(responce.get(0).get("rate"));
            BigDecimal rate = new BigDecimal(usdUah);
            lastExchangeRate.put(currency, rate);
            return rate;

        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
        }
        return lastExchangeRate.get(currency);
    }


    static class CmcDTO {

        public Status status;
        public Map<String, CurrencyDTO> data;

    }

    static class CurrencyDTO {

        public Long id;
        public String name;
        public String symbol;
        public Map<String, FiatDTO> quote;

    }


    static class Status {

        public String timestamp;
        public Long errorCode;
        public Object errorMessage;
        public Long elapsed;
        public Long creditCount;
        public Object notice;

    }

    static class FiatDTO {

        public String price;
        public Double volume24h;
        public Double percentChange1h;
        public Double percentChange24h;
        public Double percentChange7d;
        public Double percentChange30d;
        public Double marketCap;
        public String lastUpdated;

    }

}
