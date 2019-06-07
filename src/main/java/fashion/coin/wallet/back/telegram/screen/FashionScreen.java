package fashion.coin.wallet.back.telegram.screen;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.*;


public class FashionScreen implements TelegramEventHandler {

    private static FashionScreen screen = null;

    private FashionScreen() {
    }

    public static FashionScreen getInstance(){
        if(screen == null) screen = new FashionScreen();
        return screen;
    }


    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setReplyMarkup(startInlineKeyboard())
                .setText("Join Fashion Coin Telegram channel https://t.me/Fashioncoin to be the first to know latest news about Fashion Coin and - get 5,000 FSHN");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public static InlineKeyboardMarkup startInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();


        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("DONE").setCallbackData(TMFSHNDONE));
        rowInline.add(new InlineKeyboardButton().setText("SKIP").setCallbackData(GOTOTHIRDROUTER));
        rowInline.add(new InlineKeyboardButton().setText("MY BALANCE").setCallbackData(MYBALANCE));
        rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

}
