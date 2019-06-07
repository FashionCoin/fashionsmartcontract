package fashion.coin.wallet.back.telegram.screen;

import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CRYPTONAME;


public class LastScreen implements TelegramEventHandler {
    private static LastScreen screen = null;
    private TelegramDataService telegramDataService = null;

    private LastScreen() {
    }

    public static LastScreen getInstance() {
        if (screen == null) {
            screen = new LastScreen();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }

        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {


        Long chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
        Integer userId = update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.getCallbackQuery().getFrom().getId();

        String cryptoname = telegramDataService.getValue(userId.toString(), CRYPTONAME);
        String balance = telegramDataService.getValue(userId.toString(), MYBALANCE);
        if (balance == null || balance.length() == 0) balance = "0";

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard(userId.toString()))
                .setText("Your Crypto name is "+ cryptoname + ". Your balance is "+ balance + " FSHN. Invite friends and get more coins within your unique referral link: " +
                        "https://t.me/CryptoNameAirdropBot?start=" + userId.toString() + " ");

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static InlineKeyboardMarkup startInlineKeyboard(String userId) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowTwoInline = new ArrayList<>();
        rowTwoInline.add(new InlineKeyboardButton()
                .setText("SEND UNIQUE REFERRAL LINK")
                .setSwitchInlineQuery("Join to Crypto World with me! Create your unique Crypto Name on blockchain and get 10,000 FSHN. Use my link to join Crypto Name Bot by Fashion Coin:\n" +
                        "{https://t.me/CryptoNameAirdropBot?start=" + userId + "}")
        );
        rowsInline.add(rowTwoInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
