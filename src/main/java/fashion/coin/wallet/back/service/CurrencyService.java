package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.CurrencyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    Gson gson;


    private static final String apiUrlBitfinex = "https://api.bitfinex.com/v1";
    private static final String apiUrlLatoken = "https://api.latoken.com/api/v1/MarketData/ticker";

    private static final String USD_PRICE = "1000";

    Map<String, LocalDateTime> lastUpdateBfMap = new HashMap<>();
    Map<String, BigDecimal> lastBfRateMap = new HashMap();
    Map<String, LocalDateTime> lastUpdateLaMap = new HashMap<>();
    Map<String, BigDecimal> lastLaRateMap = new HashMap();


    public List<String> getAvailableCrypts() {
        return Stream.of("USD", "BTC", "ETH").collect(Collectors.toList());
    }


    public BigDecimal getRateForCoinBitfinex(String coinName) {

        if (!lastUpdateBfMap.containsKey(coinName)) {
            lastUpdateBfMap.put(coinName, LocalDateTime.now().minusMinutes(60));
        }
        LocalDateTime lastUpdate = lastUpdateBfMap.get(coinName);

        if (LocalDateTime.now().minusMinutes(5).compareTo(lastUpdate) > 0) {

            RestTemplate restTemplate = new RestTemplate();
            System.out.println(coinName);
            BitFinexRateDTO[] result = restTemplate.getForObject(apiUrlBitfinex + "/trades/" + coinName + "usd", BitFinexRateDTO[].class);
            BitFinexRateDTO last = result[0];
            for (BitFinexRateDTO dto : result) {
                if (dto.getTimestamp() > last.getTimestamp()) {
                    last = dto;
                }
            }
//        System.out.println("LAST PRICE FOR "+coinName+" AT BITFINEX -> "+last.getPrice()+" USD");
            lastUpdateBfMap.put(coinName, LocalDateTime.now());
            lastBfRateMap.put(coinName, new BigDecimal(last.getPrice()));
        }
        return lastBfRateMap.get(coinName);
    }

    public CurrencyDTO getCurrencyRate(String currency) {

        if (currency.equals("BTC") || currency.equals("ETH")) {
            BigDecimal rateLA = getRateForCoinLatoken(currency);
            BigDecimal rate = BigDecimal.ONE.divide(rateLA, 3, RoundingMode.HALF_UP);
            return new CurrencyDTO(currency, rate.setScale(3, RoundingMode.HALF_UP).toString());
        } else if (currency.equals("USD")) {
            BigDecimal rateLA = getRateForCoinLatoken("ETH");
            BigDecimal rateETH = getRateForCoinBitfinex("ETH");

            System.out.println("rateLA " + rateLA);
            System.out.println("rateETH " + rateETH);

            BigDecimal rate = BigDecimal.ONE.divide(rateLA.multiply(rateETH), 3, RoundingMode.HALF_UP);
            return new CurrencyDTO(currency, rate.setScale(3, RoundingMode.HALF_UP).toString());
        } else {
            System.out.println("Panic! Currency " + currency + "not found");
            return new CurrencyDTO(currency, "1");
        }
/*
        if (currency.equals("USD")) {
            return new CurrencyDTO("USD", USD_PRICE);
        } else {
            BigDecimal rate = getRateForCoinBitfinex(currency);
            rate = rate.multiply(new BigDecimal(USD_PRICE));
            return new CurrencyDTO(currency, rate.toString());
        }
 */
    }

    public List<CurrencyDTO> getCurrencyList() {
        List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        for (String currency : getAvailableCrypts()) {
            currencyDTOList.add(getCurrencyRate(currency));
        }
        return currencyDTOList;
    }

    public BigDecimal getRateForCoinLatoken(String coinName) {
        if (!lastUpdateLaMap.containsKey(coinName)) {
            lastUpdateLaMap.put(coinName, LocalDateTime.now().minusMinutes(60));
        }
        LocalDateTime lastUpdate = lastUpdateLaMap.get(coinName);

        if (LocalDateTime.now().minusMinutes(5).compareTo(lastUpdate) > 0) {


            RestTemplate restTemplate = new RestTemplate();
            System.out.println(coinName);
            LatokenRateDTO result = restTemplate.getForObject(apiUrlLatoken + "/FSHN" + coinName, LatokenRateDTO.class);
            System.out.println(gson.toJson(result));
            System.out.println(gson.toJson(result.getClose()));
            lastUpdateLaMap.put(coinName, LocalDateTime.now());
            lastLaRateMap.put(coinName, new BigDecimal(result.getClose()));
        }
        System.out.println(lastBfRateMap.get(coinName));
        return lastLaRateMap.get(coinName);
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

class LatokenRateDTO {

    public Integer pairId;

    public String symbol;

    public String volume;

    public String open;

    public String low;

    public String high;

    public String close;

    public String priceChange;

    public Integer getPairId() {
        return pairId;
    }

    public void setPairId(Integer pairId) {
        this.pairId = pairId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }
}