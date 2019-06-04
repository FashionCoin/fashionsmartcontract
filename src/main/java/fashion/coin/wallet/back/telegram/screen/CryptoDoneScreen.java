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

import static fashion.coin.wallet.back.telegram.FashionBot.*;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CRYPTONAME;


public class CryptoDoneScreen implements TelegramEventHandler {
    private static CryptoDoneScreen screen = null;


    private CryptoDoneScreen() {
    }

    public static CryptoDoneScreen getInstance(){
        if(screen == null) {
            screen = new CryptoDoneScreen();
        }

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
                .setText("Поздравляем!!! Теперь у тебя есть " +
                        "имя на блокчейне. Мы пришлем тебе уникальную ссылку для завершения " +
                        "регистрации позже");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static InlineKeyboardMarkup startInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowOneInline = new ArrayList<>();
        rowOneInline.add(new InlineKeyboardButton().setText("Мой баланс").setCallbackData(MYBALANCE));
        rowsInline.add(rowOneInline);

        List<InlineKeyboardButton> rowTwoInline = new ArrayList<>();
        rowTwoInline.add(new InlineKeyboardButton().setText("вернуться к блоку “Following”").setCallbackData(GOTOCFOLLOWING));
        rowsInline.add(rowTwoInline);

        List<InlineKeyboardButton> rowThreeInline = new ArrayList<>();
        rowThreeInline.add(new InlineKeyboardButton().setText("Получить реферальную ссылку").setCallbackData(GOTOREFERALSCREEN));
        rowsInline.add(rowThreeInline);

        List<InlineKeyboardButton> rowFourInline = new ArrayList<>();
        rowFourInline.add(new InlineKeyboardButton().setText("FAQ").setUrl("http://coin.fashion/faq"));
        rowsInline.add(rowFourInline);

        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
