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


public class SkipCreationScreen implements TelegramEventHandler {
    private static SkipCreationScreen screen = null;
    TelegramDataService telegramDataService = null;

    private SkipCreationScreen() {
    }

    public static SkipCreationScreen getInstance(String name){
        if(screen == null) screen = new SkipCreationScreen();
        screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        Long chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        Integer userId = update.getCallbackQuery().getFrom().getId();

        String cryptoname = telegramDataService.getValue(userId.toString(),CRYPTONAME);

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard())
                .setText("Ваше имя "+cryptoname+" уже зарезервировано для вас");
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


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
