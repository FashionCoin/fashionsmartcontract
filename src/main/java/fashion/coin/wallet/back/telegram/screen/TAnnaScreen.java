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


public class TAnnaScreen implements TelegramEventHandler {

   private static TAnnaScreen screen = null;

    private TAnnaScreen() {
    }

    public static TAnnaScreen getInstance(){
        if(screen == null) screen = new TAnnaScreen();
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setReplyMarkup(startInlineKeyboard())
                .setText("Зайди на телеграм аккаунт Анна К https://t.me/annakfashion и нажми " +
                        "Присоединиться”, чтобы получить 5,000 FSHN");
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
        rowInline.add(new InlineKeyboardButton().setText("Готово").setCallbackData(TMANNADONE));
        rowInline.add(new InlineKeyboardButton().setText("Пропустить").setCallbackData(GOTOFOURTHROUTER));
        rowInline.add(new InlineKeyboardButton().setText("Мой баланс").setCallbackData(MYBALANCE));
        rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

}
