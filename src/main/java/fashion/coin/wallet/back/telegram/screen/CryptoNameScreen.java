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
                .setText("Choose your unique Crypto Name (feel free to read the Crypto Name native rules - https://telegra.ph/CRYPTO-NAME-creation-rules-06-07) ");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static InlineKeyboardMarkup startInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> secondRowInline = new ArrayList<>();
        secondRowInline.add(new InlineKeyboardButton().setText("CREATE CN").setCallbackData(GOTOCREATENAMESCREEN));
        rowsInline.add(secondRowInline);

        List<InlineKeyboardButton> firstRowInline = new ArrayList<>();
        firstRowInline.add(new InlineKeyboardButton().setText("CN RULES").setUrl("https://telegra.ph/CRYPTO-NAME-creation-rules-06-07"));
        rowsInline.add(firstRowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
