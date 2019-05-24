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
        Integer userId = update.getCallbackQuery().getFrom().getId();
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard(userId.toString()))
                .setText("Скопируй свою уникальную ссылку на бот и отправь ее своему другу. После " +
                        "того, как твой друг заведет себе Crypto Name в нашем боте, ты получишь " +
                        "1,000 FSHN на свой баланс, а твой друг 20,000 FSHN за создание СN. Мы не " +
                        "ограничиваем тебя в количестве приглашений - больше друзей, больше " +
                        "FSHN.\n" +
                        "Твоя уникальная реферальная ссылка: {https://t.me/CryptoNameAirdropBot?start=" + userId.toString() + "} ");

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
        rowOneInline.add(new InlineKeyboardButton().setText("Мой баланс").setCallbackData(MYBALANCE));
        rowsInline.add(rowOneInline);

        List<InlineKeyboardButton> rowTwoInline = new ArrayList<>();
        rowTwoInline.add(new InlineKeyboardButton()
                .setText("отправить реферальную ссылку")
                .setSwitchInlineQuery("Привет, я зарегистрировал себе Crypto Name и получил 10 000 FSHN.\n" +
                        "Рекомендую тебе сделать то же самое, по ссылке:\n" +
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
