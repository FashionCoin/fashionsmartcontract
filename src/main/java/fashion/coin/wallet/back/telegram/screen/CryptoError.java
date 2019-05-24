package fashion.coin.wallet.back.telegram.screen;


import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CURRENTSTEP;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.WAITNAME;


public class CryptoError implements TelegramEventHandler {

    private static CryptoError screen = null;
    TelegramDataService telegramDataService = null;

    private CryptoError() {
    }

    public static CryptoError getInstance() {
        if (screen == null) {
            screen = new CryptoError();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        telegramDataService.setValue(userId.toString(), CURRENTSTEP, WAITNAME);

        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setText("“Ваше имя не соответствует правилам, попробуйте еще раз");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
