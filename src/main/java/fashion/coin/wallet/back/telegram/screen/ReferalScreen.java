package fashion.coin.wallet.back.telegram.screen;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;


public class ReferalScreen implements TelegramEventHandler {
    private static ReferalScreen screen = null;

    private ReferalScreen() {
    }

    public static ReferalScreen getInstance() {
        if (screen == null) screen = new ReferalScreen();
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
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard(userId.toString()))
                .setText("Copy unique link to the bot and send it to your friend. After your friend has created his/her Crypto Name in this bot, you will receive 1,000 FSHN on your balance, and your friend 10,000 FSHN for creating Crypto Name. We do not limit you in the number of invitations - more friends, more FSHN.\n" +
                        "Your unique referral link: https://t.me/CryptoNameAirdropBot?start=" + userId.toString() + " ");

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static InlineKeyboardMarkup startInlineKeyboard(String userId) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();


        List<InlineKeyboardButton> rowOneInline = new ArrayList<>();
        rowOneInline.add(new InlineKeyboardButton().setText("MY BALANCE").setCallbackData(MYBALANCE));
        rowsInline.add(rowOneInline);

        List<InlineKeyboardButton> rowTwoInline = new ArrayList<>();
        rowTwoInline.add(new InlineKeyboardButton()
                .setText("SEND UNIQUE REFERRAL LINK")
                .setSwitchInlineQuery("Join to Crypto World with me! Create your unique Crypto Name on blockchain and get 10,000 FSHN. Use my link to join Crypto Name Bot by Fashion Coin:\n" +
                        "{https://t.me/CryptoNameAirdropBot?start=" + userId + "}")
        );
        rowsInline.add(rowTwoInline);

        List<InlineKeyboardButton> rowFourInline = new ArrayList<>();
        rowFourInline.add(new InlineKeyboardButton().setText("FAQ").setUrl("http://coin.fashion/faq"));
        rowsInline.add(rowFourInline);

        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
