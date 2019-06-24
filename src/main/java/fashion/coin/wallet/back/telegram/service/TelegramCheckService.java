package fashion.coin.wallet.back.telegram.service;


import fashion.coin.wallet.back.dto.CryptonameTelegramDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.telegram.ContextProvider;
import fashion.coin.wallet.back.telegram.FashionBot;
import fashion.coin.wallet.back.telegram.screen.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;


public class TelegramCheckService {

    private static TelegramCheckService service = null;
    private static TelegramDataService dataService = null;
    private static ClientService clientService = null;
    private static InstagramService instagramService = null;

    private TelegramCheckService() {
    }

    public static TelegramCheckService getInstance() {
        if (service == null) {
            service = new TelegramCheckService();
            dataService = ContextProvider.getBean(TelegramDataService.class);
            clientService = ContextProvider.getBean(ClientService.class);
            instagramService = ContextProvider.getBean(InstagramService.class);
        }
        return service;
    }


    public void checkFashion(FashionBot fashionBot, Update update) {
        Integer userId = update.getCallbackQuery().getFrom().getId();
        String isdone = dataService.getValue(userId.toString(), TELEGRAMFSHNDONE);
        if (!"done".equals(isdone)) {
            boolean isSubscribe = checkSubscribeTelegramFashon(fashionBot, update);
            if (isSubscribe) {
                increaceBalance(userId.toString(), "5000");
                dataService.setValue(userId.toString(), TELEGRAMFSHNDONE, "done");
            }
        }
        firstRouter(fashionBot, update, 2);
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

    public void checkTAnna(FashionBot fashionBot, Update update) {
        Integer userId = update.getCallbackQuery().getFrom().getId();
        String isdone = dataService.getValue(userId.toString(), TANNADONE);
        if (!"done".equals(isdone)) {
            boolean isSubscribe = checkSubscribeTelegramAnna(fashionBot, update);
            if (isSubscribe) {
                increaceBalance(userId.toString(), "5000");
                dataService.setValue(userId.toString(), TANNADONE, "done");
            }
        }
        firstRouter(fashionBot, update, 3);
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

    public void checkInstAnna(FashionBot fashionBot, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        String isdone = dataService.getValue(userId.toString(), INSTANNANAME);
        if (!"done".equals(isdone)) {
            String userAccount = update.getMessage().getText();
            boolean isSubscribe = checkSubscribeInstagram(userAccount,"annakfashion");
            if (isSubscribe) {
                increaceBalance(userId.toString(), "5000");
                dataService.setValue(userId.toString(), INSTANNANAME, userAccount);
                dataService.setValue(userId.toString(), CURRENTSTEP, "");
            } else {
                dataService.setValue(userId.toString(), CURRENTSTEP, "");
            }
        }
        TelegramCheckService.getInstance().firstRouter(fashionBot, update, 5);
    }

    public void checkInstFashion(FashionBot fashionBot, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        String isdone = dataService.getValue(userId.toString(), INSTFASHIONNAME);
        if (!"done".equals(isdone)) {
            String userAccount = update.getMessage().getText();
            boolean isSubscribe = checkSubscribeInstagram(userAccount,"fashioncoin");
            if (isSubscribe) {
                increaceBalance(userId.toString(), "5000");
                dataService.setValue(userId.toString(), INSTFASHIONNAME, userAccount);
                dataService.setValue(userId.toString(), CURRENTSTEP, "");
            } else {
                dataService.setValue(userId.toString(), CURRENTSTEP, "");
            }
        }
        TelegramCheckService.getInstance().firstRouter(fashionBot, update, 4);
    }

    private void increaceBalance(String userId, String bonus) {
        String myBalance = dataService.getValue(userId, MYBALANCE);
        if (myBalance == null || myBalance.length() == 0) myBalance = "0";
        BigDecimal balance = new BigDecimal(myBalance);
        balance = balance.add(new BigDecimal(bonus));
        dataService.setValue(userId.toString(), MYBALANCE, balance.toString());
    }


    private boolean checkSubscribeInstagram(String userAccount,String followAccaunt) {
        return instagramService.checkFollowing(userAccount,followAccaunt);
    }

    private final String EXPERIENCED = "experienced";
    private final String TELEGRAMFSHNDONE = "telegramFashionDone";
    private final String TANNADONE = "telegramAnnaDone";
    private final String INSTFASHIONNAME = "InstagramFashionDone";
    private final String INSTANNANAME = "InstagramAnnaDone";
    public static final String CRYPTONAME = "CryptoName";
    private final String REFER = "Refer";
    public static final String CURRENTSTEP = "currentStep";
    public static final String WAITINSTANNA = "waitInstagramAnna";
    public static final String WAITINSTFASHION = "waitInstagramFashion";
    public static final String WAITNAME = "waitCryptonameName";

    public void checkText(FashionBot fashionBot, Update update) {
//        System.out.println(update.getMessage().getText());
        Integer userId = update.getMessage().getFrom().getId();
        String currentStep = dataService.getValue(userId.toString(), CURRENTSTEP);
        if (WAITINSTANNA.equals(currentStep)) {
            checkInstAnna(fashionBot, update);
        }else if (WAITINSTFASHION.equals(currentStep)) {
            checkInstFashion(fashionBot, update);
        } else if (WAITNAME.equals(currentStep)) {
            checkCryptoname(fashionBot, update);
        }
    }

    private void checkCryptoname(FashionBot fashionBot, Update update) {
        Integer userId = update.getMessage().getFrom().getId();
        String cryptoname = dataService.getValue(userId.toString(), CRYPTONAME);
        if (cryptoname != null) {
            ReferalScreen.getInstance().execute(fashionBot, update);
            return;
        }
        cryptoname = update.getMessage().getText();

        ResultDTO result = clientService.registerCryptoname(new CryptonameTelegramDTO(cryptoname, userId));
        if (result.isResult()) {
            dataService.setValue(userId.toString(), CRYPTONAME, cryptoname);
            dataService.setValue(userId.toString(), WAITNAME, "");
            dataService.setValue(userId.toString(), CURRENTSTEP, "");
            increaceBalance(userId.toString(), "10000");
            referalBonus(userId.toString());
            CryptoDoneScreen.getInstance().execute(fashionBot, update);
            return;
        } else if (result.getError() == 100) {
            CryptoExistsError.getInstance().execute(fashionBot, update);
            return;
        } else {
            CryptoError.getInstance().execute(fashionBot, update);
            return;
        }
    }

//    public void goToFollowing(FashionBot fashionBot, Update update) {
//        Integer userId = update.getCallbackQuery().getFrom().getId();
//        String isdone = dataService.getValue(userId.toString(), TELEGRAMFSHNDONE);
//        if (!"done".equals(isdone)) {
//            FashionScreen.getInstance().execute(fashionBot, update);
//            return;
//        }
//        isdone = dataService.getValue(userId.toString(), TANNADONE);
//        if (!"done".equals(isdone)) {
//
//            TAnnaScreen.getInstance().execute(fashionBot, update);
//            return;
//        }
//        isdone = dataService.getValue(userId.toString(), INSTANAME);
//        if (!"done".equals(isdone)) {
//            InstAnnaScreen.getInstance().execute(fashionBot, update);
//            return;
//        }
//        SecondScreen.getInstance().execute(fashionBot, update);
//    }

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

    public void checkExistsName(FashionBot fashionBot, Update update) {

        Integer userId = update.getCallbackQuery().getFrom().getId();
        String cryptoname = dataService.getValue(userId.toString(), CRYPTONAME);
        if (cryptoname == null || cryptoname.length() == 0) {
            CreateNameScreen.getInstance().execute(fashionBot, update);
            return;
        } else {
            SkipCreationScreen.getInstance(cryptoname).execute(fashionBot, update);
            return;
        }

    }


    public void firstRouter(FashionBot fashionBot, Update update, int step) {
        Integer userId = update.hasMessage() ?
                update.getMessage().getFrom().getId() :
                update.getCallbackQuery().getFrom().getId();
        String experienced = dataService.getValue(userId.toString(), EXPERIENCED);
        if (step > 0 || (experienced != null && experienced.length() > 0)) {
            String telegramFashionDone = dataService.getValue(userId.toString(), TELEGRAMFSHNDONE);
            if (step > 1 || (telegramFashionDone != null && telegramFashionDone.length() > 0)) {
                String telegramAnnaDone = dataService.getValue(userId.toString(), TANNADONE);
                if (step > 2 || (telegramAnnaDone != null && telegramAnnaDone.length() > 0)) {
                    String InstagramFashion = dataService.getValue(userId.toString(), INSTFASHIONNAME);
                    if (step > 3 || (InstagramFashion != null && InstagramFashion.length() > 0)) {
                        String InstagramAnnaDone = dataService.getValue(userId.toString(), INSTANNANAME);
                        if (step > 4 || (InstagramAnnaDone != null && InstagramAnnaDone.length() > 0)) {
                            String CryptoName = dataService.getValue(userId.toString(), CRYPTONAME);
                            if (step > 5 || (CryptoName != null && CryptoName.length() > 0)) {
                                LastScreen.getInstance().execute(fashionBot, update);
                            } else {
                                CryptoNameScreen.getInstance().execute(fashionBot, update);
                            }
                        } else {
                            InstAnnaScreen.getInstance().execute(fashionBot, update);
                        }
                    } else {
                        InstFashionScreen.getInstance().execute(fashionBot, update);
                    }
                } else {
                    TAnnaScreen.getInstance().execute(fashionBot, update);
                }
            } else {
                FashionScreen.getInstance().execute(fashionBot, update);
            }
        } else {
            dataService.setValue(userId.toString(), EXPERIENCED, "experienced");
            SecondScreen.getInstance().execute(fashionBot, update);
        }


    }
}
