package fashion.coin.wallet.back.telegram;


import fashion.coin.wallet.back.telegram.screen.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public class FashionBot extends TelegramLongPollingBot {


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
