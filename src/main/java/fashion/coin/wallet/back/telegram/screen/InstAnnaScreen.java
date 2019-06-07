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
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.*;


public class InstAnnaScreen implements TelegramEventHandler {

   private static InstAnnaScreen screen = null;
    TelegramDataService telegramDataService = null;
    private InstAnnaScreen() {
    }

    public static InstAnnaScreen getInstance(){
        if(screen == null) {
            screen = new InstAnnaScreen();
            screen.telegramDataService = ContextProvider.getBean(TelegramDataService.class);
        }
        return screen;
    }

    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {
        Integer userId = update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.getCallbackQuery().getFrom().getId();

        Long chatId = update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();

        telegramDataService.setValue(userId.toString(),CURRENTSTEP,WAITINSTANNA);
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setReplyMarkup(startInlineKeyboard())
                .setText("Follow Instagram account Anna K https://www.instagram.com/annakfashion and click “Follow” to get 5,000 FSHN. After that, go back to the bot and enter your Instagram account name (without @) to the bot");
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
        rowInline.add(new InlineKeyboardButton().setText("SKIP").setCallbackData(GOTOSIXROUTER));
        rowInline.add(new InlineKeyboardButton().setText("MY BALANCE").setCallbackData(MYBALANCE));
        rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

}
