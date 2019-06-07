package fashion.coin.wallet.back.telegram.screen;


import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CURRENTSTEP;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.WAITNAME;


public class CreateNameScreen implements TelegramEventHandler {

    private static CreateNameScreen screen = null;
    TelegramDataService telegramDataService = null;

    private CreateNameScreen() {
    }

    public static CreateNameScreen getInstance() {
        if (screen == null) {
            screen = new CreateNameScreen();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        Integer userId = update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.getCallbackQuery().getFrom().getId();
        telegramDataService.setValue(userId.toString(), CURRENTSTEP, WAITNAME);
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setText("Please, enter and send your Crypto Name");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
