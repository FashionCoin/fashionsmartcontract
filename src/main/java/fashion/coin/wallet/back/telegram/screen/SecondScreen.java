package fashion.coin.wallet.back.telegram.screen;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.GOTOFSHNSCREEN;
import static fashion.coin.wallet.back.telegram.FashionBot.GOTOSECONDROUTER;


public class SecondScreen implements TelegramEventHandler {
    private static SecondScreen screen = null;

    private SecondScreen() {
    }

    public static SecondScreen getInstance(){
        if(screen == null) screen = new SecondScreen();
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setReplyMarkup(startInlineKeyboard())
                .setText("To create a Crypto Name and earn FSHN, you need to complete 3 blocks of tasks:\n" +
                        "1. Optionally subscribe to our Telegram and Instagram accounts, get a reward of 5,000 FSHN for each and keep up with the latest Fashion Coin news.\n" +
                        "2. Create your own unique Crypto Name! on the blockchain and get 10,000 FSHN on the balance. More about crypto names here - https://telegra.ph/CRYPTO-NAME-creation-rules-06-07\n" +
                        "3. Share this opportunity with your friends using unique referral link. You will be rewarded for each of your friends’ Crypto Name with 1,000 FSHN\n");
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
        rowInline.add(new InlineKeyboardButton().setText("START").setCallbackData(GOTOSECONDROUTER));
        rowsInline.add(rowInline);

        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
