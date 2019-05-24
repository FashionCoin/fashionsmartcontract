package fashion.coin.wallet.back.telegram.service;


import fashion.coin.wallet.back.telegram.entity.TelegramBotData;
import fashion.coin.wallet.back.telegram.entity.TelegramBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramDataService {

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
