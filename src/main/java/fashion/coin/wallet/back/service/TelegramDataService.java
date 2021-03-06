package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.entity.TelegramBotData;
import fashion.coin.wallet.back.repository.TelegramBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramDataService {


    public static final String MYBALANCE = "myBalance";
    public static final String OLDBALANCE = "oldBalance";


    @Autowired
    TelegramBotRepository telegramBotRepository;


    public String getValue(String userId, String paramName) {
        TelegramBotData botData = telegramBotRepository.findFirstByUserIdAndParamname(userId, paramName);
        if (botData != null) return botData.getParamvalue();
        return null;
    }

    public boolean hasValue(String userId, String paramName) {
        TelegramBotData botData = telegramBotRepository.findFirstByUserIdAndParamname(userId, paramName);
        return (botData != null && botData.getParamvalue().length() > 0);
    }

    public void setValue(String userId, String paramName, String paramValue) {
        TelegramBotData botData = telegramBotRepository.findFirstByUserIdAndParamname(userId, paramName);
        if (botData == null) botData = new TelegramBotData(userId, paramName);
        if (!paramValue.equals(botData.getParamvalue())) {
            botData.setParamvalue(paramValue);
            telegramBotRepository.save(botData);
        }
    }

}
