package fashion.coin.wallet.back.telegram.screen;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface TelegramEventHandler {
    void execute(TelegramLongPollingBot bot, Update update);
}
