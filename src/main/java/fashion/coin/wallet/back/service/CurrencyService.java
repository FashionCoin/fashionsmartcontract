package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.CurrencyDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by JAVA-P on 24.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class CurrencyService {
    private static final String apiUrl = "https://api.bitfinex.com/v1";
    private static final String USD_PRICE = "1000";

    Map<String, LocalDateTime> lastUpdateMap = new HashMap<>();
    Map<String, BigDecimal> lastRateMap = new HashMap();


    public List<String> getAvailableCrypts() {
        return Stream.of("USD", "BTC", "ETH").collect(Collectors.toList());
    }


    public BigDecimal getRateForCoin(String coinName) {

        if (!lastUpdateMap.containsKey(coinName)) {
            lastUpdateMap.put(coinName, LocalDateTime.now().minusMinutes(60));
        }
        LocalDateTime lastUpdate = lastUpdateMap.get(coinName);

        if (LocalDateTime.now().minusMinutes(5).compareTo(lastUpdate) > 0) {

            RestTemplate restTemplate = new RestTemplate();
            System.out.println(coinName);
            BitFinexRateDTO[] result = restTemplate.getForObject(apiUrl + "/trades/" + coinName + "usd", BitFinexRateDTO[].class);
            BitFinexRateDTO last = result[0];
            for (BitFinexRateDTO dto : result) {
                if (dto.getTimestamp() > last.getTimestamp()) {
                    last = dto;
                }
            }
//        System.out.println("LAST PRICE FOR "+coinName+" AT BITFINEX -> "+last.getPrice()+" USD");
            lastUpdateMap.put(coinName,LocalDateTime.now());
            lastRateMap.put(coinName, new BigDecimal(last.getPrice()));
        }
        return lastRateMap.get(coinName);
    }

    public CurrencyDTO getCurrencyRate(String currency) {
        if (currency.equals("USD")) {
            return new CurrencyDTO("USD", USD_PRICE);
        } else {
            BigDecimal rate = getRateForCoin(currency);
            rate = rate.multiply(new BigDecimal(USD_PRICE));
            return new CurrencyDTO(currency, rate.toString());
        }
    }

    public List<CurrencyDTO> getCurrencyList() {
        List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        for (String currency : getAvailableCrypts()) {
            currencyDTOList.add(getCurrencyRate(currency));
        }
        return currencyDTOList;
    }
}

class BitFinexRateDTO {
    Long timestamp;
    Long tid;
    String price;
    String amount;
    String exchange;
    String type;

    @Override
    public String toString() {
        return "BitFinexRateDTO{" +
                "timestamp=" + timestamp +
                ", tid=" + tid +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                ", exchange='" + exchange + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTib() {
        return tid;
    }

    public void setTib(Long tid) {
        this.tid = tid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}