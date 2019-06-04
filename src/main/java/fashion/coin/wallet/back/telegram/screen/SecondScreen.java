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
                .setText("Дорогой друг мы тебе расскажем подробнее о том, " +
                        "как ты можешь создать Crypto Name и заработать Fashion Coin. " +
                        "В боте есть 3 блока заданий (возможностей): \n\n" +
                        "a.Following: подпишись на наши ресурсы в Telegram и Instagram и получи за это вознаграждение " +
                        "в FSHN и будь в курсе последних новостей Fashion Coin. \n\n" +
                        "b. создание Crypto Name: создай свое уникальное Crypto Name на блокчейне и получи " +
                        "на баланс 10,000 FSHN. Мы придумали способ предоставить любому человеку или бренду в мире " +
                        "уникальное, защищенное, удобное для чтения и произношения имя. " +
                        "Ты никогда не столкнешься с проблемой, что твои имя занято кем-то другим, " +
                        "потому что мы даем возможность использовать буквы всех алфавитов мира и эмотиконы, " +
                        "которые позволяют генерировать миллионы уникальных комбинаций. \n\n" +
                        "c. реферальная программа: Получи свою уникальную реферальную ссылку " +
                        "и приглашай своих друзей в наш бот, делись с ними возможностью создать Crypto Name " +
                        "и получай за каждого друга по 1,000 FSHN НЕОГРАНИЧЕННО ");
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
        rowInline.add(new InlineKeyboardButton().setText("Продолжить").setCallbackData(GOTOSECONDROUTER));
        rowsInline.add(rowInline);

        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }


}
