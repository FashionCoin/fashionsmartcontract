package fashion.coin.wallet.back.telegram.screen;


import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CURRENTSTEP;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.WAITNAME;


public class CryptoExistsError implements TelegramEventHandler {

    private static CryptoExistsError screen = null;
    TelegramDataService telegramDataService = null;

    private CryptoExistsError() {
    }

    public static CryptoExistsError getInstance() {
        if (screen == null) {
            screen = new CryptoExistsError();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        telegramDataService.setValue(userId.toString(), CURRENTSTEP, WAITNAME);

        String cryptoname = update.getMessage().getText();

        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Жаль, но похоже имя " + cryptoname + " занято");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
