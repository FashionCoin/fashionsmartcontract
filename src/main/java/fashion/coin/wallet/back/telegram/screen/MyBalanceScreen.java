package fashion.coin.wallet.back.telegram.screen;


import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramCheckService;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;


public class MyBalanceScreen implements TelegramEventHandler {


    TelegramDataService telegramDataService;

    private static MyBalanceScreen screen = null;
    TelegramCheckService telegramCheckService = null;

    private MyBalanceScreen() {
    }

    public static MyBalanceScreen getInstance() {
        if (screen == null) {
            screen = new MyBalanceScreen();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
            screen.telegramCheckService = TelegramCheckService.getInstance();
        }
        return screen;
    }


    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        
        String userId = update.getCallbackQuery().getFrom().getId().toString();
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())

                .setText("Your balance is " + getBalance(userId) + " FSHN \n") ;
//                        "Finish your Crypto Name registration in Fashion Wallet DApp:\n" +
//                        "Google Play: https://play.google.com/store/apps/details?id=wallet.fashion.coin&referrer=api_key%3D"+getApiKey(userId)+"\n" +
//                        "App Store: The link will be here soon, please come back to check it\n");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getApiKey(String userId) {
        return telegramCheckService.getApiKey(userId);
    }

    private String getBalance(String userId) {

//        String balance = telegramDataService.getValue(userId, MYBALANCE);
        String balance = telegramCheckService.getBalance(userId);
        if (balance == null || balance.length() == 0) return "0";
        else return balance;
    }
    
    

}
