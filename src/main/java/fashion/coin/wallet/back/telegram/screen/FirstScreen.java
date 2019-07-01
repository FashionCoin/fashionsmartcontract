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

import static fashion.coin.wallet.back.telegram.FashionBot.GOTOFIRSTROUTER;
import static fashion.coin.wallet.back.telegram.FashionBot.GOTOSECONDSCREEN;


public class FirstScreen implements TelegramEventHandler {

    private static FirstScreen screen = null;
    TelegramCheckService telegramCheckService = null;

    private FirstScreen() {
    }

    public static FirstScreen getInstance(){
        if(screen == null) screen = new FirstScreen();
        screen.telegramCheckService = TelegramCheckService.getInstance();
        return screen;
    }


    @Override
    public void execute(TelegramLongPollingBot bot, Update update) {

        System.out.println("From: "+ update.getMessage().getFrom());
        System.out.println("ChatId: "+ update.getMessage().getChatId());
        System.out.println("Chat.id: "+ update.getMessage().getChat().getId());
        System.out.println("UserId: "+ update.getMessage().getFrom().getId());


        telegramCheckService.checkReferal(update);


        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
//                .setReplyMarkup(new ReplyKeyboardRemove())
                .setReplyMarkup(startInlineKeyboard())
                .setText("Hi! You are first to create your Crypto Name - real Decentralized ID in Crypto World! Your Crypto name is real, you own and control it. Nobody can’t steal, fake or delete it. \n" +
                        "By creating your unique Crypto Name in this bot, you will be rewarded by FSHN on Fashion Wallet at the moment of its launch quite soon. More info about Fashion Coin here - http://bit.ly/31fMvRy\n");

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
            rowInline.add(new InlineKeyboardButton().setText("NEXT").setCallbackData(GOTOFIRSTROUTER));
            rowsInline.add(rowInline);


        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }



}
