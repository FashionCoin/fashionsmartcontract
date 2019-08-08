package fashion.coin.wallet.back.telegram.service;


import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.FashionBot;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

import java.util.List;


import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;



public class TelegramCheckService {

    private static TelegramCheckService service = null;
    private static TelegramDataService dataService = null;
    private static ClientService clientService = null;

    private static FashionBot bot = null;

    private TelegramCheckService() {
    }

    public static TelegramCheckService getInstance() {
        if (service == null) {
            service = new TelegramCheckService();
            dataService = ContextProvider.getBean(TelegramDataService.class);
            clientService = ContextProvider.getBean(ClientService.class);
        }
        return service;
    }



    private boolean checkSubscribeTelegramFashon(FashionBot fashionBot, Update update) {
        try {
            Integer userId = update.getCallbackQuery().getFrom().getId();
            GetChatMember getChatMember = new GetChatMember()
                    .setChatId("@Fashioncoin")
                    .setUserId(userId);
            ChatMember member = fashionBot.execute(getChatMember);

            if (member != null && member.getUser() != null && member.getUser().getId() != null
                    && member.getStatus().equals("member")) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;


    }


    private boolean checkSubscribeTelegramAnna(FashionBot fashionBot, Update update) {
        try {
            Integer userId = update.getCallbackQuery().getFrom().getId();
            GetChatMember getChatMember = new GetChatMember()
                    .setChatId("@annakfashion")
                    .setUserId(userId);
            ChatMember member = fashionBot.execute(getChatMember);
//            System.out.println("@annakfashion"+member);
            if (member != null && member.getUser() != null && member.getUser().getId() != null
                    && member.getStatus().equals("member")
            ) {
//                System.out.println(member.getStatus());
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;

    }



    private void increaceBalance(String userId, String bonus) {
        String myBalance = dataService.getValue(userId, MYBALANCE);
        if (myBalance == null || myBalance.length() == 0) myBalance = "0";
        BigDecimal balance = new BigDecimal(myBalance);
        balance = balance.add(new BigDecimal(bonus));
        dataService.setValue(userId.toString(), MYBALANCE, balance.toString());
    }



    public final String EXPERIENCED = "experienced";
    private final String TELEGRAMFSHNDONE = "telegramFashionDone";
    private final String TANNADONE = "telegramAnnaDone";

    public static final String CRYPTONAME = "CryptoName";
    private final String REFER = "Refer";
    public static final String CURRENTSTEP = "currentStep";

    public static final String WAITNAME = "waitCryptonameName";
    public static final String BULKSENDED = "bulkSended";





    public void checkReferal(Update update) {

        String message = update.getMessage().getText();
        if (message.contains("/start") && message.length() > 7) {

            Integer userId = update.getMessage().getFrom().getId();

            String refer = message.substring(7);
            if (!refer.equals(userId)) {
                System.out.println("Refer: " + refer);
                dataService.setValue(userId.toString(), REFER, refer);
            }
        }
    }


    private void referalBonus(String userId) {
        String refer = dataService.getValue(userId, REFER);
        if (refer != null && refer.length() > 0) {
            increaceBalance(refer, "1000");
        }
    }



    public String getBalance(String userId) {
        BigDecimal telegramBalance = clientService.getTelegramBalance(userId);
        BigDecimal clientBalance = clientService.getClientBalanceByTelegram(userId);

        System.out.println(clientBalance);
        System.out.println(telegramBalance);

        if (clientBalance.equals(BigDecimal.ZERO)) return telegramBalance.toString();
        else return clientBalance.toString();
//        return (clientBalance.add(telegramBalance)).toString();
    }

    public String getApiKey(String userId) {
        return clientService.getApiKeyByTelegram(userId);
    }


    boolean bulkStarted = false;

    public void startBulk() {
        if (!bulkStarted) {
            bulkStarted = true;
            LinkSender linkSender = new LinkSender(this);
            new Thread(linkSender).start();
            System.out.println("Bulk started...");
        }
    }

    public void setBot(FashionBot fashionBot) {
        this.bot = fashionBot;
    }

    public static ClientService getClientService() {
        return clientService;
    }

    public static FashionBot getBot() {
        return bot;
    }

    public static TelegramDataService getDataService() {
        return dataService;
    }
}

class LinkSender implements Runnable {

    private final ClientService clientService;
    private final FashionBot bot;
    private final TelegramCheckService telegramCheckService;
    private final TelegramDataService telegramDataService;


    public LinkSender(TelegramCheckService telegramCheckService) {
        this.telegramCheckService = telegramCheckService;
        this.clientService = TelegramCheckService.getClientService();
        this.bot = TelegramCheckService.getBot();
        this.telegramDataService = TelegramCheckService.getDataService();
    }

    @Override
    public void run() {

        List<Client> clientList = clientService.findClientsForBulk();

        for (Client client : clientList) {
            try {
                String chatId = String.valueOf(client.getTelegramId());
                String cryptoName = client.getCryptoname();
                String balance = telegramCheckService.getBalance(chatId);
                String apiKey = clientService.getApiKeyByTelegram(chatId);
                String bulkSended = telegramDataService.getValue(chatId, TelegramCheckService.BULKSENDED);
                String expirience = telegramDataService.getValue(chatId, telegramCheckService.EXPERIENCED);
                if (expirience != null && expirience.length() > 0 && (bulkSended == null || !bulkSended.equals("true"))) {
                    SendMessage message = new SendMessage()
                            .setChatId(chatId)
                            .setText("Dear " + cryptoName + ", your balance on Crypto Name is " + balance + " FSHN. \n" +
                                    "\n" +
                                    "If you are an Android user, you can finish your Crypto Name registration and get your money right now:\n" +
                                    "1. Install the latest of Fashion Wallet from here https://play.google.com/store/apps/details?id=wallet.fashion.coin&referrer=api_key%3D" + apiKey + "\n" +
                                    "2. When you open the app, you will see your Crypto Name " + cryptoName + ".\n" +
                                    "3. Go ahead and choose any picture from your device as Mnemonic Pic to finish the registration. Mnemonic Pic is important, so please remember it.\n" +
                                    "4. Set Pin code for your Fashion Wallet \n" +
                                    "5. Congratulations! Your money is your wallet!\n" +
                                    "\n" +
                                    "If you are an iOS user, please wait for a little. When the app will appear in the App Store, we will send you a message with the right link.\n" +
                                    "\n" +
                                    "We are here to help you: support@coin.fashion\n");

                    bot.execute(message);
                    telegramDataService.setValue(chatId, TelegramCheckService.BULKSENDED, "true");
                    System.out.println(cryptoName + " bulk sended");
                    Thread.sleep(3000);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Bulk ended.");
    }
}