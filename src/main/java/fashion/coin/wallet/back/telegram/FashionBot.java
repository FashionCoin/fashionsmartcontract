package fashion.coin.wallet.back.telegram;


import fashion.coin.wallet.back.telegram.screen.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public class FashionBot extends TelegramLongPollingBot {

    Logger logger = LoggerFactory.getLogger(FashionBot.class);

    private String botName;


    private String botToken;

    public static final String MYBALANCE = "myBalance";
    public static final String OLDBALANCE = "oldBalance";



    public FashionBot() {
    }

    public FashionBot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info(update.getMessage().toString());
        MyBalanceScreen.getInstance().execute(this, update);
    }



    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


}
