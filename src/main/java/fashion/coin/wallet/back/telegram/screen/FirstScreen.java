package fashion.coin.wallet.back.telegram.screen;

import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.service.TelegramCheckService;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.telegram.FashionBot.GOTOSECONDSCREEN;


public class FirstScreen implements TelegramEventHandler {

    private static FirstScreen screen = null;
    TelegramCheckService telegramCheckService = null;

    private FirstScreen() {
    }

    public static FirstScreen getInstance(){
        if(screen == null) screen = new FirstScreen();
        screen.telegramCheckService = ContextProvider.getBean(TelegramCheckService.class);
        return screen;
    }


    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {

        telegramCheckService.checkReferal(update);


        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
//                .setReplyMarkup(new ReplyKeyboardRemove())
                .setReplyMarkup(startInlineKeyboard())
                .setText("Мы рады тебя приветствовать в telegram-боте Fashion Coin. " +
                        "Наша миссия дать каждому человеку или бренду в мире уникальное, защищенное, " +
                        "удобное для чтения и произношения имя на блокчейне - Crypto Name, " +
                        "которое сделает использование криптовалюты FSHN удобным и интуитивным. " +
                        "Создав свое Crypto Name в нашем боте ты автоматически получишь 10,000 FSHN (10$) на баланс. " +
                        "Также в этом боте ты сможешь заработать дополнительные FSHN за участие в реферальной программе " +
                        "(1,000 FSHN за каждое новое созданное Crypto Name по твоей уникальной ссылке) " +
                        "и 5,000 FSHN за фолловинг наших ресурсов " +
                        "(подписывайся на нас и будь в курсе последних новостей Fashion Coin). " +
                        "Больше деталей на сайте - https://coin.fashion ");

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
            rowInline.add(new InlineKeyboardButton().setText("Start").setCallbackData(GOTOSECONDSCREEN));
            rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }



}
