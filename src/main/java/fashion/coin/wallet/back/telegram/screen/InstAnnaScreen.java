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
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.CURRENTSTEP;
import static fashion.coin.wallet.back.telegram.service.TelegramCheckService.WAITINST;


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
        Integer userId = update.getCallbackQuery().getFrom().getId();
        telegramDataService.setValue(userId.toString(),CURRENTSTEP,WAITINST);
        SendMessage message = new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setReplyMarkup(startInlineKeyboard())
                .setText("Зайди в Instagram на аккаунт Anna K https://www.instagram.com/annakfashion/ и нажми “Follow”, чтобы " +
                        "получить 5,000 FSHN. После этого вернись в бот и введи и отправь свой " +
                        "Instagram account name (без @) в бот");
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
        rowInline.add(new InlineKeyboardButton().setText("Пропустить").setCallbackData(GOTOFIFTHROUTER));
        rowInline.add(new InlineKeyboardButton().setText("Мой баланс").setCallbackData(MYBALANCE));
        rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

}
