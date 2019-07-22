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

        if (validUserId(userId)) {

            SendMessage message1 = new SendMessage()
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())

                    .setText("Your balance is " + getBalance(userId) + " FSHN \n" +
                            "Finish your Crypto Name registration in Fashion Wallet DApp:\n" +
                            "Google Play:https://play.google.com/store/apps/details?id=wallet.fashion.coin\n" +
                            "App Store: The link will be here soon, please come back to check it\n" +
                            "Copy your secret code below and paste to Fashion Wallet while Sign UP:");

            SendMessage message2 = new SendMessage()
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText(getApiKey(userId));

            try {
                bot.execute(message1);
                bot.execute(message2);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validUserId(String userId) {
        String apiKey = telegramCheckService.getApiKey(userId);
        return  (apiKey != null && apiKey.length() > 0);
    }

    private String getApiKey(String userId) {

        String apiKey = telegramCheckService.getApiKey(userId);
        if (apiKey == null || apiKey.length() == 0)
            apiKey = "You have already completed the registration of the Crypto Name";

        return apiKey;
    }

    private String getBalance(String userId) {

        String balance = telegramCheckService.getBalance(userId);
        if (balance == null || balance.length() == 0) return "0";
        else return balance;
    }


}
