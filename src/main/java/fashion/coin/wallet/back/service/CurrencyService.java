package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.inject.internal.cglib.core.$ClassInfo;
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


    private static final String LASTRATE = "lastrate";

    DecimalFormat df = new DecimalFormat("#.00000000");


    public List<String> getAvailableCrypts() {
        return Stream.of("USD", "EUR", "GBP", "BTC", "ETH", "BNB", "UAH", "RUB", "XAU", "XAG").collect(Collectors.toList());
    }


    public CurrencyDTO getCurrencyRate(String currency) {
        CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyOrderByDateTimeDesc(currency);
        return new CurrencyDTO(currency,
                currencyRate.getRate().setScale(3, RoundingMode.HALF_UP).toString());

    }

    public BigDecimal getLastCurrencyRate(String currency) {
        logger.info(currency);

        currency = checkFashionCurrency(currency);

        CurrencyRate currencyRate = currencyRateRepository.findTopByCurrencyOrderByDateTimeDesc(currency);
        logger.info("Currency Rate: ", gson.toJson(currencyRate));
        if (currencyRate != null) {
            return currencyRate.getRate();
        } else {
            return null;
        }
    }

    private String checkFashionCurrency(String currency) {

        if (currency.equals("FUSD") || currency.equals("FDEM")) {
            return "USD";
        }
        if (currency.equals("FEUR")) {
            return "EUR";
        }
        if (currency.equals("FGBP")) {
            return "GBP";
        }
        if (currency.equals("FUAH")) {
            return "UAH";
        }
        if (currency.equals("FBTC")) {
            return "BTC";
        }
        if (currency.equals("FETH")) {
            return "ETH";
        }
        if (currency.equals("FAU")) {
            return "XAU";
        }
        if (currency.equals("FAG")) {
            return "XAG";
        }
        return currency;
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
            LocalDateTime dateStart = LocalDateTime.parse(date + "T00:00:00", DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime dateEnd = LocalDateTime.parse(date + "T23:59:59", DateTimeFormatter.ISO_DATE_TIME);
            for (String currency : crypts) {
                logger.info("{} {} {}", currency, dateStart, dateEnd);
                Double avg = currencyRateRepository.getAverageCurrency(currency,
                        dateStart, dateEnd);
                logger.info("AVG: {}", avg);
                if (avg == null) {
                    avg = 1.;
                    logger.error("AVG == null for {} {}", currency, date);
                }
                BigDecimal rate = BigDecimal.valueOf(avg);
                CurrencyDTO currencyDTO = new CurrencyDTO(currency, df.format(rate));
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
