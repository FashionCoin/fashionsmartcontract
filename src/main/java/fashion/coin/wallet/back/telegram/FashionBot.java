package fashion.coin.wallet.back.telegram;


import fashion.coin.wallet.back.telegram.screen.*;
import fashion.coin.wallet.back.telegram.service.TelegramCheckService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


public class FashionBot extends TelegramLongPollingBot {


    private String botName;


    private String botToken;

    public static final String GOTOSECONDSCREEN = "goToSecondScreen";
    public static final String GOTOFSHNSCREEN = "goToFashionCoinScreen";
    public static final String GOTOCREATENAMESCREEN = "goToCreateNameScreen";
    public static final String GOTOCFOLLOWING = "goToFollowing";
    public static final String GOTOREFERALSCREEN = "goToReferalScreen";
    public static final String TMFSHNDONE = "telegramFashionDone";
    public static final String TMEFSHNNEXT = "telegramFashionNext";
    public static final String TMANNADONE = "telegramAnnaDone";
    public static final String TMANNANEXT = "telegramAnnaNext";
    public static final String INSTANNANEXT = "instagramAnnaNext";
    public static final String MYBALANCE = "myBalance";


    public FashionBot() {
    }

    public FashionBot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        if (update.hasCallbackQuery()) {
            System.out.println(update.getCallbackQuery().getData());
            if (update.getCallbackQuery().getData().equals(GOTOSECONDSCREEN)) {
                SecondScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(GOTOFSHNSCREEN)) {
                FashionScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(GOTOCREATENAMESCREEN)) {
                CreateNameScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(GOTOREFERALSCREEN)) {
                ReferalScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(MYBALANCE)) {
                MyBalanceScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(TMEFSHNNEXT)) {
                TAnnaScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(TMANNANEXT)) {
                InstAnnaScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(INSTANNANEXT)) {
                CryptoNameScreen.getInstance().execute(this, update);
            } else if (update.getCallbackQuery().getData().equals(TMFSHNDONE)) {
                TelegramCheckService.getInstance().checkFashion(this, update);
            } else if (update.getCallbackQuery().getData().equals(TMANNADONE)) {
                TelegramCheckService.getInstance().checkTAnna(this, update);
            } else if (update.getCallbackQuery().getData().equals(GOTOCFOLLOWING)) {
               TelegramCheckService.getInstance().goToFollowing(this, update);
            }

        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().contains("/start")) {
                FirstScreen.getInstance().execute(this, update);
            } else {
                TelegramCheckService.getInstance().checkText(this, update);
            }

        }


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
