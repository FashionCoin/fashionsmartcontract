package fashion.coin.wallet.back.telegram.screen;


import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;


public class MyBalanceScreen implements TelegramEventHandler {


    TelegramDataService telegramDataService;

    private static MyBalanceScreen screen = null;

    private MyBalanceScreen() {
    }

    public static MyBalanceScreen getInstance(){
        if(screen == null){
            screen = new MyBalanceScreen();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }
        return screen;
    }


    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())

                .setText("Your balance is " + getBalance(update.getCallbackQuery().getFrom().getId().toString()) + " FSHN");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getBalance(String userId) {
        System.out.println("UserID = " + userId);
        String balance = telegramDataService.getValue(userId, MYBALANCE);
        if (balance == null || balance.length() == 0) return "0";
        else return balance;
    }

}
