package fashion.coin.wallet.back.service;

import com.google.api.client.http.HttpHeaders;
import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.CurrencyDTO;
import fashion.coin.wallet.back.entity.CurrencyRate;
import fashion.coin.wallet.back.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;
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

    @Autowired
    CurrencyRateRepository currencyRateRepository;


    private static final String apiUrlBitfinex = "https://api.bitfinex.com/v1";
    private static final String apiUrlLatoken = "https://api.latoken.com/api/v1/MarketData/ticker";
    private static final String apiUrlNazbank = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    private static final String USD_PRICE = "1000";

    LocalDateTime lastUpdate = LocalDateTime.now().minusDays(1);

    public List<String> getAvailableCrypts() {
        return Stream.of("USD", "BTC", "ETH", "UAH").collect(Collectors.toList());
    }


    public BigDecimal getRateForCoinBitfinex(String coinName) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(coinName);
        BitFinexRateDTO[] result = restTemplate.getForObject(apiUrlBitfinex + "/trades/" + coinName + "usd", BitFinexRateDTO[].class);
        BitFinexRateDTO last = result[0];
        for (BitFinexRateDTO dto : result) {
            if (dto.getTimestamp() > last.getTimestamp()) {
                last = dto;
            }
        }
        return new BigDecimal(last.getPrice());
    }

    public CurrencyDTO getCurrencyRate(String currency) {

        CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyAndDateTimeIsAfter(currency, LocalDateTime.now().minusHours(1));
        if (currencyRate == null) {

            if (currency.equals("BTC") || currency.equals("ETH")) {
                BigDecimal rateLA = getRateForCoinLatoken(currency);
                BigDecimal rate = BigDecimal.ONE.divide(rateLA, 3, RoundingMode.HALF_UP);
                currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
            } else if (currency.equals("USD")) {

                BigDecimal rate = BigDecimal.ONE.divide(getRateForCoinLatoken("USDT"), 3, RoundingMode.HALF_UP);

/*  Bitfinex Version
            BigDecimal rateLA = getRateForCoinLatoken("ETH");
            BigDecimal rateETH = getRateForCoinBitfinex("ETH");
            BigDecimal rate = BigDecimal.ONE.divide(rateLA.multiply(rateETH), 3, RoundingMode.HALF_UP);
 */
                currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
            } else if (currency.equals("UAH")) {
                BigDecimal rateUSD = BigDecimal.ONE.divide(getRateForCoinLatoken("USDT"), 6, RoundingMode.HALF_UP);
                BigDecimal rate = rateUSD.divide(getRateFromNBU("USD"), 3, RoundingMode.HALF_UP);
                currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
            } else {
                System.out.println("Panic! Currency " + currency + "not found");
                currencyRate = new CurrencyRate(currency, BigDecimal.ONE, LocalDateTime.now());
            }
            currencyRateRepository.save(currencyRate);
        }
        return new CurrencyDTO(currency,
                currencyRate.getRate().setScale(3, RoundingMode.HALF_UP).toString());
    }

    private BigDecimal getRateFromNBU(String currency) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<List> responce = restTemplate.exchange(apiUrlNazbank, HttpMethod.GET, entity, List.class, "json");
            List<NazbankDTO> result = responce.getBody();
            result.removeIf(nazbankDTO -> !nazbankDTO.cc.equals(currency));
            NazbankDTO usd = result.get(0);
            return new BigDecimal(usd.rate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ONE;
    }

    public List<CurrencyDTO> getCurrencyList() {
        List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        for (String currency : getAvailableCrypts()) {
            currencyDTOList.add(getCurrencyRate(currency));
        }
        return currencyDTOList;
    }

    public BigDecimal getRateForCoinLatoken(String coinName) {

        RestTemplate restTemplate = new RestTemplate();

        LatokenRateDTO result = restTemplate.getForObject(apiUrlLatoken + "/FSHN" + coinName, LatokenRateDTO.class);

        return new BigDecimal(result.getClose());
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

class NazbankDTO {

    public Integer r030;

    public String txt;

    public Double rate;

    public String cc;

    public String exchangedate;

    public Integer getR030() {
        return r030;
    }

    public void setR030(Integer r030) {
        this.r030 = r030;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getExchangedate() {
        return exchangedate;
    }

    public void setExchangedate(String exchangedate) {
        this.exchangedate = exchangedate;
    }
}