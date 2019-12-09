package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import fashion.coin.wallet.back.dto.CurrencyDTO;
import fashion.coin.wallet.back.entity.CurrencyRate;
import fashion.coin.wallet.back.repository.CurrencyRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    Gson gson;

    @Autowired
    CurrencyRateRepository currencyRateRepository;

    @Autowired
    SettingsService settingsService;

    @Autowired
    RestTemplate restTemplate;


    private static final String apiUrlBitfinex = "https://api.bitfinex.com/v1";
//    private static final String apiUrlLatoken = "https://api.latoken.com/api/v1/MarketData/ticker";
    private static final String apiUrlLatoken = "https://api.latoken.com/v2/ticker";
    private static final String apiUrlNazbank = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private static final String LASTRATE = "lastrate";

    DecimalFormat df = new DecimalFormat("#.00000000");

    private static final String USD_PRICE = "1000";

    LocalDateTime lastUpdate = LocalDateTime.now().minusDays(1);

    Map<String, BigDecimal> lastLatokenPrice = new HashMap<>();


    public List<String> getAvailableCrypts() {
        return Stream.of("USD", "BTC", "ETH", "UAH").collect(Collectors.toList());
    }


    public BigDecimal getRateForCoinBitfinex(String coinName) {

        logger.info(coinName);
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

        CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyAndDateTimeIsAfter(currency, LocalDateTime.now().minusMinutes(1));
        if (currencyRate == null) {
            try {
                if (currency.equals("BTC") || currency.equals("ETH")) {
                    BigDecimal rateLA = getRateForCoinLatoken(currency);
                    logger.info("LA "+currency+": "+rateLA);
                    BigDecimal rate = BigDecimal.ONE.divide(rateLA, 3, RoundingMode.HALF_UP);
                    currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
                } else if (currency.equals("USD")) {
                    BigDecimal rateLA = getRateForCoinLatoken("USDT");
                    logger.info("LA "+currency+": "+rateLA);
                    BigDecimal rate = BigDecimal.ONE.divide(rateLA, 3, RoundingMode.HALF_UP);
                    currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
                } else if (currency.equals("UAH")) {
                    BigDecimal rateUSD = BigDecimal.ONE.divide(getRateForCoinLatoken("USDT"), 6, RoundingMode.HALF_UP);
                    BigDecimal rate = rateUSD.divide(getRateFromNBU("USD"), 3, RoundingMode.HALF_UP);
                    currencyRate = new CurrencyRate(currency, rate.setScale(6, RoundingMode.HALF_UP), LocalDateTime.now());
                } else {
                    logger.error("Panic! Currency " + currency + "not found");
                    currencyRate = new CurrencyRate(currency, BigDecimal.ONE, LocalDateTime.now());
                }
                currencyRateRepository.save(currencyRate);
            } catch (Exception e) {
                logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
                logger.error(e.getMessage());
                currencyRate = currencyRateRepository.findTopByCurrencyOrderByDateTimeDesc(currency);
//                e.printStackTrace();
            }
        }
        if (currencyRate == null) return null;
        return new CurrencyDTO(currency,
                currencyRate.getRate().setScale(3, RoundingMode.HALF_UP).toString());
    }

    private BigDecimal getRateFromNBU(String currency) throws Exception {
        try {


            ArrayList<LinkedTreeMap> responce = restTemplate.getForObject(apiUrlNazbank, ArrayList.class);

            responce.removeIf(listEntity -> !listEntity.get("cc").equals(currency));
            Object usd = responce.get(0).get("rate");
            return new BigDecimal((Double) usd).setScale(6, RoundingMode.HALF_UP);

        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }

    }

    public List<CurrencyDTO> getCurrencyList() {
        List<CurrencyDTO> currencyDTOList = new ArrayList<>();
        try {
            for (String currency : getAvailableCrypts()) {
                currencyDTOList.add(getCurrencyRate(currency));
            }
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
        }
        if (currencyDTOList.size() == 0) {
            String json = settingsService.get(LASTRATE);
            currencyDTOList = gson.fromJson(json, List.class);
            logger.info("Read from base: " + gson.toJson(currencyDTOList));
        } else {
            settingsService.set(LASTRATE, gson.toJson(currencyDTOList));
        }
        return currencyDTOList;
    }

    public BigDecimal getRateForCoinLatoken(String coinName) {
        BigDecimal rate = null;

        try {
            LatokenRateDTO result = restTemplate.getForObject(apiUrlLatoken + "/FSHN/" + coinName, LatokenRateDTO.class);
//            logger.info("LA: " + gson.toJson(result));
            if (result != null) {
                rate = new BigDecimal(result.lastPrice);
            }
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
        }
        if (rate != null) {
            lastLatokenPrice.put(coinName, rate);
        } else {
            rate = lastLatokenPrice.get(coinName);
        }
        return rate;
    }

    public List<CurrencyDTO> getCurrencyHistory(LocalDateTime beforeTime) {
        List<CurrencyDTO> currencyList = new ArrayList<>();
        List<String> crypts = getAvailableCrypts();
        try {
            logger.info("Before Time: " + beforeTime);
            for (String currency : crypts) {
                CurrencyRate currencyRate =
                        currencyRateRepository.findTopByCurrencyAndDateTimeIsAfter(
                                currency, beforeTime);
                CurrencyDTO currencyDTO = new CurrencyDTO(currency, currencyRate.getRate().toString());
                currencyList.add(currencyDTO);
                logger.info(gson.toJson(currencyRate));
            }
            logger.info("currencyList.size()=" + currencyList.size());
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return currencyList;
    }



    public List<CurrencyDTO> getAverageByDate(String date) {
        List<CurrencyDTO> currencyList = new ArrayList<>();
        List<String> crypts = getAvailableCrypts();
        try {
            logger.info("Average for: " + date);
            LocalDateTime dateStart = LocalDateTime.parse(date + "T00:00:00",DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime dateEnd = LocalDateTime.parse(date + "T23:59:59",DateTimeFormatter.ISO_DATE_TIME);
            for (String currency : crypts) {
                BigDecimal rate = BigDecimal.valueOf(currencyRateRepository.getAverageCurrency(currency,
                        dateStart, dateEnd));
                CurrencyDTO currencyDTO = new CurrencyDTO(currency, df.format( rate));
                currencyList.add(currencyDTO);
                logger.info(gson.toJson(currencyDTO));
            }
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return currencyList;
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

    public String symbol;
    public String baseCurrency;
    public String quoteCurrency;
    public String volume24h;
    public String volume7d;
    public String change24h;
    public String change7d;
    public String lastPrice;

}

/*
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
*/
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