package fashion.coin.wallet.back.telegram.screen;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.GOTOCREATENAMESCREEN;


public class CryptoNameScreen implements TelegramEventHandler {
    private static CryptoNameScreen screen = null;

    private CryptoNameScreen() {
    }

    public static CryptoNameScreen getInstance(){
        if(screen == null) screen = new CryptoNameScreen();
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {

        Long chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard())
                .setText("Crypto Name позволяет каждому осуществлять платежи, участвовать в " +
                        "голосовании, производить эмиссию и распределение криптографических " +
                        "коинов - с самой широкой базой применения. Для создания Crypto Name ты " +
                        "можешь буквы всех алфавитов мира, эмотиконы и дефис. Crypto Name " +
                        "записывается на блокчейн и его нельзя изменить, поэтому прочитай " +
                        "правила создания имен и выбери себе самое лучшее имя! ");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static InlineKeyboardMarkup startInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
        firstRowInline.add(new InlineKeyboardButton().setText("Правила создания Crypto Name").setUrl("https://coin.fashion/single-2.html#section6"));
        rowsInline.add(firstRowInline);
        List<InlineKeyboardButton> secondRowInline = new ArrayList<>();
        secondRowInline.add(new InlineKeyboardButton().setText("Создать Crypto Name").setCallbackData(GOTOCREATENAMESCREEN));
        rowsInline.add(secondRowInline);

        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
