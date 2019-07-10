package fashion.coin.wallet.back.service;


import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;

import com.vdurmont.emoji.EmojiParser;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.dto.blockchain.FshnBalanceDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.SetEmailRequest;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.repository.SetEmailRepository;
import fashion.coin.wallet.back.telegram.service.TelegramDataService;
import fashion.coin.wallet.back.utils.SignBuilder;
import fashion.coin.wallet.back.utils.TweetNaCl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;


import static fashion.coin.wallet.back.service.StatisticsService.*;
import static fashion.coin.wallet.back.telegram.FashionBot.MYBALANCE;
import static fashion.coin.wallet.back.telegram.FashionBot.OLDBALANCE;
import static java.lang.Character.isLetter;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class ClientService {

    ClientRepository clientRepository;
    BlockchainService blockchainService;
    SetEmailRepository setEmailRepository;
    EmailService emailService;
    ContactService contactService;
    MessagingService messagingService;
    AIService aiService;
    Gson gson;
    TelegramDataService telegramDataService;

    Random random = new Random();


    @Value("${fashion.host}")
    String HOST_NAME;

    public ResultDTO trySignUp(RegistrationRequestDTO data) {
        try {
            System.out.println(gson.toJson(data));

            if (data.getWalletAddress() == null || data.getWalletAddress().equals("0000000000000000000000000000000000000000000000000000000000000000")) {
                return error101;
            }

            Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (data.getApikey() == null) return error107;
            if (client != null) {
                if (client.getApikey() != null && !client.getApikey().equals(data.getApikey()))
                    return error100;
            }

            BlockchainTransactionDTO jsonTransaction = data.getBlockchainTransaction();
            String pub_key = jsonTransaction.getBody().getPub_key();
            if (pub_key == null) return error106;
            if (!pub_key.equals(data.getWalletAddress())) return error103;
            if (!checkValidCryptoname(data.getCryptoname())) return error105;

            if (!checkNewWallet(data.getWalletAddress())) return error119;

            if (blockchainService.sendTransaction(data.getBlockchainTransaction()).length() == 0)
                return error101;
            aiService.cryptoname(data.getCryptoname().toLowerCase(), "", data.getWalletAddress());

            if (client == null) {
                client = new Client(data.getCryptoname().toLowerCase(), data.getApikey(), data.getWalletAddress());
            } else {
                client.setWalletAddress(data.getWalletAddress());
            }

            if (data.getEncryptedhash() != null) {
                client.setEncryptedhash(data.getEncryptedhash());
            }

            client.setRegisteredFrom(FROMMOBILE);
            clientRepository.save(client);
            //// FOR TESTING
            System.out.println("10 000: " + HOST_NAME);
            if (!HOST_NAME.contains("api.coin.fashion")) {
                aiService.transfer("10000.00", client.getWalletAddress());
                System.out.println("10 000: sended");
            }
            //// END FOR TESTING

            if (client.getTelegramId() != null && client.getTelegramId() > 0) {
                String userId = String.valueOf(client.getTelegramId());
                BigDecimal balance = getTelegramBalance(userId);
                if (!balance.equals(BigDecimal.ZERO) && client.getWalletAddress() != null) {
                    boolean result = aiService.transfer(balance.toString(), client.getWalletAddress());
                    if (!result) {
                        System.out.println("Error sending telegram money to client: \n" +
                                gson.toJson(client));
                    } else {
                        resetTelegramBalance(userId);
                    }
                }
            }
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void resetTelegramBalance(String userId) {


        String myBalance = telegramDataService.getValue(userId, MYBALANCE);
        if (myBalance != null && myBalance.length() > 0) {
            BigDecimal balance = new BigDecimal(myBalance);
            if (!balance.equals(BigDecimal.ZERO)) {
                telegramDataService.setValue(userId, OLDBALANCE, balance.toString());
                telegramDataService.setValue(userId, MYBALANCE, "0");
            }
        }

    }

    public BigDecimal getTelegramBalance(String userId) {
        String myBalance = telegramDataService.getValue(userId, MYBALANCE);
        if (myBalance == null || myBalance.length() == 0) return BigDecimal.ZERO;
        return new BigDecimal(myBalance);
    }

    private boolean checkNewWallet(String walletAddress) {
        Client client = clientRepository.findClientByWalletAddress(walletAddress);
        if (client == null || client.getId() == null) return true;
        else return false;
    }

    public ResultDTO trySignIn(SignInDTO data) {
        try {
            System.out.println(gson.toJson(data));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().toLowerCase());
            if (client == null) return error108;
            if (data.getApikey() == null) return error107;

            if (!checkUsingApiKey(data.getApikey())) return error117;

            String apiKeyInSignature = data.getSignature().substring(128);
            String apiKeyInData = SignBuilder.bytesToHex(data.getApikey().getBytes());


            if (!apiKeyInData.equals(apiKeyInSignature)) return error109;
// 3b722fc4cbcdbe05fa33e740d2e6c25bce557969503af0de15130d9034766a1b5d745f5050d72220986dc76c860a165a36e9e794f57c12632a664994b8023909
            if (!checkSignature(data.getSignature(), client.getWalletAddress())) return error115;
            client.setApikey(data.getApikey());

            clientRepository.save(client);

            if (client.getEncryptedhash() != null) {
                return new ResultDTO(true, "{\"encryptedhash\" : \"" + client.getEncryptedhash() + "\" }", 0);
            }
            return validLogin;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private boolean checkSignature(String signedData, String publicKey) {
        try {
            byte[] signed = SignBuilder.hexStringToByteArray(signedData);
            byte[] publicSigningKey = SignBuilder.hexStringToByteArray(publicKey);

            byte[] result = TweetNaCl.crypto_sign_open(signed, publicSigningKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public ResultDTO reserveName(ReserveCryptoNameDTO data) {
        try {
            System.out.println(gson.toJson(data));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().toLowerCase());
            if (client != null) return error100;
            if (data.getApikey() == null) return error107;


            if (!checkUsingApiKey(data.getApikey())) return error117;

            if (!checkValidCryptoname(data.getCryptoname())) return error105;

            clientRepository.save(new Client(data.getCryptoname().toLowerCase(), data.getApikey(), null));
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private boolean checkUsingApiKey(String apikey) {

        Client client = clientRepository.findClientByApikey(apikey);
        if (client == null || client.getId() == null) return true;
        else return false;
    }


    public ResultDTO checkName(CheckCryptoNameDTO data) {
        try {
            Client client = clientRepository.findClientByApikey(data.getCryptoname());
            if(client!= null) {
                if(client.getWalletAddress()!=null) return error117;
                ResultDTO result = new ResultDTO(true,null , 0);
                result.setCryptoname(client.getCryptoname());
                return result;
            }

            client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (client != null) return error100;
            if (!data.getCryptoname().toLowerCase().equals(data.getCryptoname())) return error104;
            if (!checkValidCryptoname(data.getCryptoname())) return error105;
            return validLogin;
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private boolean checkValidCryptoname(String cryptoname) {

        if (cryptoname.length() < 1) return false;
        Character ch = ((char) 65039);
        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname).replace(ch.toString(), "");
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);

        if (textWithoutEmoji.length() + textOnlyEmoji.size() > 25) return false;

        // Reserv:
        if (textWithoutEmoji.length() == 0 && textOnlyEmoji.size() == 1) return false;

        int codePointCount = textWithoutEmoji.codePointCount(0, textWithoutEmoji.length());
        if (codePointCount == 1 && codePointCount < textWithoutEmoji.length()) return false;


        char[] charArray = textWithoutEmoji.toCharArray();
        int charArrayLength = charArray.length;
        for (int i = 0; i < charArrayLength; i++) {
            char symbol = charArray[i];
//            System.out.println(symbol + " " + (int) symbol);
            if (!Character.isHighSurrogate(symbol) && !Character.isLowSurrogate(symbol) &&
                    !(Character.isLetter(symbol) && Character.isLowerCase(symbol)) &&
                    !(Character.isAlphabetic(symbol) && Character.toLowerCase(symbol) == symbol) &&
                    symbol != '-') return false;

        }
        return true;
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'a' && c <= 'z');
    }

    public static void main(String[] args) {
        ClientService clientService = new ClientService();
        List<String> tests = Arrays.asList("normallogin", "nor-malLogin", "norm-allogin", "norma.llogin", "normallo_gin",
                "norm allogin", "norm\uD83D\uDE09\uD83D\uDE09allogin", "nor\uD83D\uDE03mallogin", "norm\uD83D\uDE09-\uD83D\uDE09allogin", "nor\uD83D\uDE03mallo\ngin");
        for (String test : tests) {
            System.out.println(test + " " + clientService.checkValidCryptoname(test));
        }
    }

    public Client findByWallet(String walletAddress) {
        try {
            return clientRepository.findClientByWalletAddress(walletAddress);
        } catch (Exception e) {
            return null;
        }
    }

    public Client findByCryptoname(String cryptoname) {
        if (cryptoname == null) return null;
        try {
            return clientRepository.findClientByCryptoname(cryptoname);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public ResultDTO trySetEmail(SetEmailRequestDTO data) {
        try {
            Client client = findByCryptoname(data.getLogin());
            if (client == null) return error108;
            if (!checkUnic(data.getEmail().toLowerCase())) return error114;
            if (!data.getApikey().equals(client.getApikey())) return error109;
            String randomToken = getRandomToken(16);
            setEmailRepository.save(new SetEmailRequest(client, data.getEmail().toLowerCase(), randomToken));
            emailService.sendMail(data.getEmail(), "Fashion Coin: Confirm email", "For confirm email click this link:<br/>" +
                    "<a href='" + HOST_NAME + "/confirmemail?token=" + randomToken + "' >" + HOST_NAME + "/confirmemail?token=" + randomToken + "</a>");
            return mailSended;
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private boolean checkUnic(String email) {
        Client client = findClientByEmail(email.toLowerCase());
        return client == null;
    }

    public Client findClientByEmail(String email) {
        List<Client> clientList = clientRepository.findClientsByEmail(email);
        if (clientList == null || clientList.size() == 0) return null;
        else return clientList.get(0);
//        return clientRepository.findClientByEmail(email);
    }

    private String getRandomToken(int size) {
        BigInteger randomNumber = new BigInteger(size * 8, random);
        return randomNumber.toString(16);
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setBlockchainService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Autowired
    public void setSetEmailRepository(SetEmailRepository setEmailRepository) {
        this.setEmailRepository = setEmailRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }

    @Autowired
    public void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setTelegramDataService(TelegramDataService telegramDataService) {
        this.telegramDataService = telegramDataService;
    }

    private static final ResultDTO created = new ResultDTO(true, "Account created", 0);
    private static final ResultDTO validLogin = new ResultDTO(true, "Login is valid", 0);
    private static final ResultDTO mailSended = new ResultDTO(true, "Confirm mail sended", 0);
    private static final ResultDTO error100 = new ResultDTO(false, "Client with this login exists", 100);
    private static final ResultDTO error101 = new ResultDTO(false, "Wallet does not created", 101);
    private static final ResultDTO error102 = new ResultDTO(false, "This is brand name", 102);
    private static final ResultDTO error103 = new ResultDTO(false, "Public Key in transaction not equal wallet", 103);
    private static final ResultDTO error104 = new ResultDTO(false, "Cryptoname must be lower case", 104);
    private static final ResultDTO error105 = new ResultDTO(false, "Emoji error, or short login", 105);
    private static final ResultDTO error106 = new ResultDTO(false, "Can't find pub_key param", 106);
    private static final ResultDTO error107 = new ResultDTO(false, "Can't find apikey param", 107);
    private static final ResultDTO error108 = new ResultDTO(false, "Client not found", 108);
    private static final ResultDTO error109 = new ResultDTO(false, "Not valid apikey", 109);
    private static final ResultDTO error113 = new ResultDTO(false, "Login has been changed once", 113);
    private static final ResultDTO error114 = new ResultDTO(false, "This Email is already use", 114);
    private static final ResultDTO error115 = new ResultDTO(false, "Not valid Signature", 115);
    private static final ResultDTO error116 = new ResultDTO(false, "This wallet already exists", 116);
    private static final ResultDTO error117 = new ResultDTO(false, "This ApiKey has already been used", 117);
    private static final ResultDTO error118 = new ResultDTO(false, "Can't find client param", 118);
    private static final ResultDTO error119 = new ResultDTO(false, "This photo has already been used as a Mnemonic Pic for another Crypto Name. Please choose another photo.", 119);
    private static final ResultDTO error120 = new ResultDTO(false, "This phone already using", 120);


    public void addAmountToWallet(Client client, BigDecimal amount) {
        client.setWalletBalance(client.getWalletBalance().add(amount));
        clientRepository.save(client);
        try {
            messagingService.sendNotification("change_balance",
                    client.getWalletBalance().toString(),
                    "topic_" + client.getWalletAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkEmailToken(String token) {
        SetEmailRequest request = setEmailRepository.findByEmailVerificationCode(token);
        if (request == null) return false;
        System.out.println(request.getDataTime());
        System.out.println(LocalDateTime.now().minusDays(1));
        System.out.println(request.getDataTime().compareTo(LocalDateTime.now().minusDays(1)));
        if (!request.isStatus() && request.getDataTime().compareTo(LocalDateTime.now().minusDays(1)) > 0) {
            Client client = request.getClient();
            client.setEmail(request.getEmail());
            clientRepository.save(client);
            request.setStatus(true);
            setEmailRepository.save(request);
        }
        return true;
    }

    public ResultDTO checkEmail(CheckEmailDTO data) {
        try {
            Client client = findByCryptoname(data.getCryptoname());
            if (client == null) return error108;
            if (!data.getApikey().equals(client.getApikey())) return error109;
            String email = client.getEmail();
            if (email == null) return new ResultDTO(false, "Email does not set", 10);
            else return new ResultDTO(false, email, 0);
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getWallet(GetWalletDTO data) {
        try {
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (client == null) {
                // May be it is wallet:
                client = clientRepository.findClientByWalletAddress(data.getCryptoname());
                if (client == null) {
                    // May be anonimous wallet:
                    String walletAddress = checkAnonimousWallet(data.getCryptoname());
                    if (walletAddress != null && walletAddress.length() == 64) {
                        return new ResultDTO(true, walletAddress, 0);
                    }
                    return error108;
                }
            }
            String walletAddress = client.getWalletAddress();
            if (walletAddress == null || walletAddress.length() == 0) {
                return error101;
            }
            return new ResultDTO(true, client.getWalletAddress(), 0);
        } catch (Exception e) {
            return error108;
        }
    }

    private String checkAnonimousWallet(String address) {

        FshnBalanceDTO walletInfo = blockchainService.getWalletInfo(address);
        if (walletInfo != null && walletInfo.pubKey != null &&
                !walletInfo.pubKey.equals("0000000000000000000000000000000000000000000000000000000000000000")) {
            return walletInfo.pubKey;
        } else {
            return null;
        }

    }

    public ResultDTO getCryptoname(GetLoginDTO data) {
        try {
            Client client = clientRepository.findClientByWalletAddress(data.getWallet());
            if (client == null) return error108;
            return new ResultDTO(true, client.getCryptoname(), 0);
        } catch (Exception e) {
            return error108;
        }
    }

    public List<Client> findAll() {
        List<Client> clientList = clientRepository.findAll();
        if (clientList == null) return new ArrayList<>();
        else {
            Collections.sort(clientList);
            return clientList;
        }
    }

    public Object getClientInfo(CheckEmailDTO data) {
        System.out.println(gson.toJson(data));
        if (data.getApikey() == null) return error107;
        if (data.getCryptoname() == null) {
            Client client = findClientByApikey(data.getApikey());
            if (client == null) return error108;

            if (client.getWalletAddress() == null || client.getWalletAddress().length() == 0) {
                return client;
            } else {
                return error118;
            }
        }

        Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
        if (client == null) return error108;
        if (client.getApikey() == null) return error107;
        if (!client.getApikey().equals(data.getApikey())) return error109;
        updateBalance(client);
        System.out.println(gson.toJson(client));
        return client;
    }

    public Client updateBalance(Client client) {
        BigDecimal balanceFromBlockchain =
                blockchainService.getBalance(client.getWalletAddress());
        if (balanceFromBlockchain.equals(client.getWalletBalance())) return client;
        client.setWalletBalance(balanceFromBlockchain);
        clientRepository.save(client);
//        try {
//            messagingService.sendNotification("change_balance",
//                    client.getWalletBalance().toString(),
//                    "topic_" + client.getWalletAddress());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return client;
    }

    public Object changeClientInfo(ClientInfoDTO data) {
        Client client = clientRepository.findClientByApikey(data.getApikey());
        if (client == null) return error109;

        if (data.getCryptoname() != null) {
            if (!data.getCryptoname().toLowerCase().equals(data.getCryptoname())) return error104;
            if (!checkValidCryptoname(data.getCryptoname())) return error105;

            if (!client.getCryptoname().toLowerCase().equals(data.getCryptoname().toLowerCase())) {
                if (client.isLoginChanged()) return error113;
                client.setCryptoname(data.getCryptoname().toLowerCase());
                client.setLoginChanged(true);
            }
        }

        if (data.getPhone() != null) {
            if (!checkUnicPhone(data.getPhone())) return error120;
            client.setPhone(data.getPhone());
        }

        if (!data.isShowPhone() && client.isShowPhone()) {
            contactService.hidePhone(client);
        }

        client.setShowPhone(data.isShowPhone());


        if (data.getRealname() != null) {
            client.setRealname(data.getRealname());
        }
        clientRepository.save(client);
        updateBalance(client);
        return client;
    }

    private boolean checkUnicPhone(String phone) {
        List<Client> clientList = findByPhone(phone);
        return (clientList == null || clientList.size() == 0);
    }


    public Client findClientByApikey(String apikey) {

        return clientRepository.findClientByApikey(apikey);
    }


    public boolean checkRequest(String email) {
        List<SetEmailRequest> emailRequestList = setEmailRepository.findAllByEmail(email);
        return (emailRequestList != null && !emailRequestList.isEmpty() && emailRequestList.size() > 0);
    }

    public boolean checkApiKey(String login, String apikey) {
        Client client = clientRepository.findClientByApikey(apikey);
        if (client == null) return false;
        if (!client.getCryptoname().equals(login)) return false;
        return true;
    }

    public List<Client> findByPhone(String phone) {
        return clientRepository.findClientsByPhone(phone);
    }

    public List<Client> findByPhoneEndingWith(String phone) {
        return clientRepository.findClientsByPhoneEndingWith(phone);
    }


    public void setAvatar(String cryptoname, String path) {
        Client client = clientRepository.findClientByCryptoname(cryptoname);
        client.setAvatar(path);
        clientRepository.save(client);
    }

    public ResultDTO registerCryptoname(CryptonameEmailDTO data) {
        try {
            System.out.println(gson.toJson(data));
            System.out.println(Bytes.asList(data.getCryptoname().getBytes()));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().toLowerCase());
            if (client != null) {
                System.out.println(gson.toJson(error100));
                return error100;
            }
            List<Client> clientList = clientRepository.findClientsByEmail(data.getEmail());
            if (clientList != null && clientList.size() > 0) {
                System.out.println(gson.toJson(error114));
                return error114;
            }
            if (!checkValidCryptoname(data.getCryptoname())) {
                System.out.println(gson.toJson(error105));
                return error105;
            }
            client = new Client(data.getCryptoname().toLowerCase(),
                    data.getEmail());
            client.setRegisteredFrom(FROMWEB);
            clientRepository.save(client);
            emailService.sendMail(data.getEmail(), "Fashion Coin: Congratulations!", "You created cryptoname: " + data.getCryptoname().toLowerCase() + " ");
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO registerCryptoname(CryptonameTelegramDTO data) {
        try {
            System.out.println(gson.toJson(data));
            System.out.println(Bytes.asList(data.getCryptoname().getBytes()));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().toLowerCase());
            if (client != null) {
                System.out.println(gson.toJson(error100));
                return error100;
            }
            List<Client> clientList = clientRepository.findClientsByTelegramId(data.getTelegramId());
            if (clientList != null && clientList.size() > 0) {
                System.out.println(gson.toJson(error114));
                return error114;
            }
            if (!checkValidCryptoname(data.getCryptoname())) {
                System.out.println(gson.toJson(error105));
                return error105;
            }
            client = new Client(data.getCryptoname().toLowerCase(),
                    data.getTelegramId());
            client.setRegisteredFrom(FROMTELEGRAMM);
            clientRepository.save(client);
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public List<Client> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end) {
        List<Client> clientList = clientRepository.findByCreateTimeBetween(start, end);
        if (clientList == null) clientList = new ArrayList<>();
        return clientList;
    }

    public void reserveNames(List<String> names) {
        System.out.println("Reserve names");
        for (String name : names) {
            try {
                ReserveCryptoNameDTO data = new ReserveCryptoNameDTO();
                data.setCryptoname(name);
                String randomToken = getRandomToken(16);
                data.setApikey(randomToken);
                reserveName(data);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("End reserv");
    }

    public BigDecimal getClientBalanceByTelegram(String userId) {
        try {
            List<Client> clientList = clientRepository.findClientsByTelegramId(Integer.parseInt(userId));
            if (clientList != null && clientList.size() == 1) {
                Client client = clientList.get(0);
                BigDecimal walletBalance = client.getWalletBalance();
                if (walletBalance == null) return BigDecimal.ZERO;
                return walletBalance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public String getApiKeyByTelegram(String userId) {
        try {
            List<Client> clientList = clientRepository.findClientsByTelegramId(Integer.parseInt(userId));
            if (clientList != null && clientList.size() == 1) {
                Client client = clientList.get(0);
                if (client.getApikey() == null || client.getApikey().length() == 0) {
                    client.setApikey(getRandomToken(16));
                    clientRepository.save(client);
                }
                return client.getApikey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getApiKeyByCryptoname(String cryptoname) {
        try {
            Client client = clientRepository.findClientByCryptoname(cryptoname);
            if (client != null) {
                if (client.getApikey() == null || client.getApikey().length() == 0) {
                    client.setApikey(getRandomToken(16));
                    clientRepository.save(client);
                }
                return client.getApikey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<Client> findClientsForBulk() {
        List<Client> clientList = clientRepository.findAllByRegisteredFrom("Telegramm");
        clientList.removeIf(client -> (client.getWalletAddress() != null ||
                client.getTelegramId() == null));
        return clientList;
    }

    public String getClientByWallet(String walletAddress) {
        try {
            Client client = clientRepository.findClientByWalletAddress(walletAddress);
            if (client == null || client.getCryptoname() == null) return null;

            return client.getCryptoname();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
