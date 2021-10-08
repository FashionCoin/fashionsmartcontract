package fashion.coin.wallet.back.service;


import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.dto.blockchain.FshnBalanceDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.Contact;
import fashion.coin.wallet.back.entity.SetEmailRequest;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.repository.SetEmailRepository;

import fashion.coin.wallet.back.utils.SignBuilder;
import fashion.coin.wallet.back.utils.TweetNaCl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;
import static fashion.coin.wallet.back.service.StatisticsService.*;
import static fashion.coin.wallet.back.service.TelegramDataService.MYBALANCE;
import static fashion.coin.wallet.back.service.TelegramDataService.OLDBALANCE;


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

    Logger logger = LoggerFactory.getLogger(ClientService.class);

    ClientRepository clientRepository;
    BlockchainService blockchainService;
    SetEmailRepository setEmailRepository;
    EmailService emailService;
    ContactService contactService;
    MessagingService messagingService;
    AIService aiService;
    Gson gson;
    TelegramDataService telegramDataService;
    EmojiCodeService emojiCodeService;
    BrandCodeService brandCodeService;
    CryptoWalletsService cryptoWalletsService;

    Random random = new Random();


    @Value("${fashion.host}")
    String HOST_NAME;

    @Value("${fashion.anonimous}")
    Boolean anonimousSending;

    public ResultDTO trySignUp(RegistrationRequestDTO data) {
        try {
            logger.info(gson.toJson(data));

            if (data.getWalletAddress() == null || data.getWalletAddress().equals("0000000000000000000000000000000000000000000000000000000000000000")) {
                logger.error("Wallet: ", data.getWalletAddress());
                return error101;
            }
            String cryptoname = null;
            Client client;
// Вход из ботов по apiKey
            client = clientRepository.findClientByApikey(data.getCryptoname());
            if (client != null && (client.getWalletAddress() == null || client.getWalletAddress().length() == 0)) {
                cryptoname = client.getCryptoname();
            }
            //
            if (cryptoname == null) {
                cryptoname = emojiCodeService.checkEmojiCode(data.getCryptoname());
                if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode((data.getCryptoname()));
                if (cryptoname == null) cryptoname = data.getCryptoname().trim();
            }
            if (client == null) {
                client = clientRepository.findClientByCryptoname(cryptoname);
                if (data.getApikey() == null) return error107;
                if (client != null) {
                    if (client.getApikey() != null && !client.getApikey().equals(data.getApikey()))
                        return error100;
                }
            }
            if (client == null) {
                client = clientRepository.findClientByApikey(data.getApikey());
                if (client != null)
                    return error117;
            }

            BlockchainTransactionDTO jsonTransaction = data.getBlockchainTransaction();
            String pub_key = jsonTransaction.getBody().getPub_key();
            if (pub_key == null) return error106;
            if (!pub_key.equals(data.getWalletAddress())) return error103;
            if (!checkValidCryptoname(data.getCryptoname())) {
                logger.error("Sign Up");
                return error105;
            }

            if (!checkNewWallet(data.getWalletAddress())) return error119;

            if (blockchainService.sendTransaction(data.getBlockchainTransaction()).length() == 0)
                return error101;
            aiService.cryptoname(cryptoname, "", data.getWalletAddress());

            if (client == null) {
                client = new Client(cryptoname, data.getApikey(), data.getWalletAddress());
            } else {
                client.setWalletAddress(data.getWalletAddress());
            }

            if (data.getEncryptedhash() != null) {
                client.setEncryptedhash(data.getEncryptedhash());
            }

/*          IMPORTANT! Do not delete!

            //generating encrypted seed
            String secretKey = result.get("priv_key");
            byte[] secretKeyBytes = SignBuilder.hexStringToByteArray(secretKey);
            byte[] key = Arrays.copyOfRange(secretKeyBytes, 0, 16);
            byte[] vi = Arrays.copyOfRange(secretKeyBytes, 16, 32);

            String sha256Encrypted = AESEncriptor.encrypt(sha256, key, vi);
*/

            client.setRegisteredFrom(FROMMOBILE);

            clientRepository.save(client);
            emojiCodeService.registerClient(client);
            brandCodeService.registerClient(client);


            if (client.getTelegramId() != null && client.getTelegramId() > 0) {
                String userId = String.valueOf(client.getTelegramId());
                BigDecimal balance = getTelegramBalance(userId);
                if (!balance.equals(BigDecimal.ZERO) && client.getWalletAddress() != null) {
                    final String clientWallet = client.getWalletAddress();
                    final String clientName = client.getCryptoname();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                boolean isWalletExists = false;
                                do {
//                                    logger.info("Sleep before telegramm bonus");
                                    Thread.sleep(1000);
//                                    logger.info("Wake Up");
                                    FshnBalanceDTO fshnBalanceDTO = blockchainService.getWalletInfo(clientWallet);
                                    if (fshnBalanceDTO != null && fshnBalanceDTO.getPub_key() != null
                                            && fshnBalanceDTO.getPub_key().equals(clientWallet)) {
                                        isWalletExists = true;
                                    }
//                                    logger.info("isWalletExists = " + String.valueOf(isWalletExists));
                                } while (!isWalletExists);

                                boolean result = aiService.transfer(balance.toString(),
                                        clientWallet, AIService.AIWallets.MONEYBAG).isResult();
                                if (!result) {
                                    logger.error("Error sending telegram money to client: \n" +
                                            gson.toJson(clientName));
                                } else {
                                    resetTelegramBalance(userId);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
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


    public ResultDTO checkClient(SignInDTO data) {
        try {
//            logger.info("Chek client " + gson.toJson(data));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().trim());
            if (client == null) {
                logger.error("Client: {}", client);
                return error108;
            }
            if (data.getApikey() == null) {
                logger.error("Apikey: {}", data.getApikey());
                return error107;
            }

            String apiKeyInSignature = data.getSignature().substring(128);
            String apiKeyInData = SignBuilder.bytesToHex(data.getApikey().getBytes());


            if (!apiKeyInData.equals(apiKeyInSignature)) {
                logger.error("ApiKey in Data: {}", apiKeyInData);
                logger.error("ApiKey in signature: {}", apiKeyInSignature);
                return error109;
            }

            if (!checkSignature(data.getSignature(), client.getWalletAddress())) {
                logger.error("Signature error");
                return error115;
            }

            return validLogin;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO trySignIn(SignInDTO data) {
        try {
            logger.info("Sign in " + gson.toJson(data));

            String cryptoname = emojiCodeService.checkEmojiCode(data.getCryptoname()); // ???
            if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode(data.getCryptoname()); // ???
            if (cryptoname == null) cryptoname = data.getCryptoname().trim();

            Client client = clientRepository.findClientByCryptoname(cryptoname);
            if (client == null) {
                logger.error("Client: {}", client);
                return error108;
            }
            if (client.isBanned()) {
                logger.error("Client in ban: {}", client.isBanned());
                return error122;
            }
            if (data.getApikey() == null) {
                logger.error("ApiKey: {}", data.getApikey());
                return error107;
            }

            if (!checkUsingApiKey(data.getApikey())) {
                logger.error("ApiKey is alredy in using");
                return error117;
            }

            String apiKeyInSignature = data.getSignature().substring(128);
            String apiKeyInData = SignBuilder.bytesToHex(data.getApikey().getBytes());


            if (!apiKeyInData.equals(apiKeyInSignature)) {
                logger.error("apiKeyInSignature: {}", apiKeyInSignature);
                logger.error("apiKeyInData: {}", apiKeyInData);

                return error109;
            }

            if (!checkSignature(data.getSignature(), client.getWalletAddress())) {
                logger.error("Signature error");
                return error115;
            }
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


    public ResultDTO permanentSignIn(SignInDTO data) {
        try {
            logger.info("Sign in " + gson.toJson(data));

            String cryptoname = emojiCodeService.checkEmojiCode(data.getCryptoname()); /// ???
            if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode(data.getCryptoname()); /// ???
            if (cryptoname == null) cryptoname = data.getCryptoname().trim();

            Client client = clientRepository.findClientByCryptoname(cryptoname);
            if (client == null) {
                return error108;
            }
            if (client.isBanned()) return error122;
            if (data.getApikey() == null) return error107;


            String apiKeyInSignature = data.getSignature().substring(128);
            String apiKeyInData = SignBuilder.bytesToHex(data.getApikey().getBytes());


            if (!apiKeyInData.equals(apiKeyInSignature)) {
                logger.error("apiKeyInSignature: {}", apiKeyInSignature);
                logger.error("apiKeyInData: {}", apiKeyInData);
                return error109;
            }

            if (!checkSignature(data.getSignature(), client.getWalletAddress())) return error115;
            if (client.getApikey() == null) {
                client.setApikey(data.getApikey());
                clientRepository.save(client);
            }
            ApiKeyDTO apiKeyDTO = new ApiKeyDTO();
            apiKeyDTO.setApikey(client.getApikey());

            return new ResultDTO(true, apiKeyDTO, 0);

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
//            logger.info("Reserv Name " + gson.toJson(data));


            Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (client != null) return error100;
            if (data.getApikey() == null) return error107;


            if (!checkUsingApiKey(data.getApikey())) return error117;

            if (!checkValidCryptoname(data.getCryptoname())) {
                logger.error("Reserv name");
                return error105;
            }

            String cryptoname = emojiCodeService.checkEmojiCode(data.getCryptoname());
            if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode(data.getCryptoname());
            if (cryptoname == null) cryptoname = data.getCryptoname();


            client = new Client(cryptoname, data.getApikey(), null);
            clientRepository.save(client);
            logger.info("Register: " + data.getCryptoname());
//            emojiCodeService.registerClient(client);
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
            logger.info("Check Name: " + gson.toJson(data));
            logger.info("data.getCryptoname().trim(): " + data.getCryptoname().trim());
            Client client = clientRepository.findClientByApikey(data.getCryptoname().trim());
            logger.info("client: " + gson.toJson(client));
            if (client != null) {
                if (client.getWalletAddress() != null) return error121;
                ResultDTO result = new ResultDTO(true, null, 0);
                result.setCryptoname(client.getCryptoname());
                logger.info(gson.toJson(result));
                return result;
            }

            String oneEmojiName = emojiCodeService.checkEmojiCode(data.getCryptoname().trim());
            logger.info("one: " + oneEmojiName);
            if (oneEmojiName != null && oneEmojiName.length() > 0) {

//                client = new Client(oneEmojiName, data.getCryptoname(), null);
//                clientRepository.save(client);
                ResultDTO result = new ResultDTO(true, null, 0);
                result.setCryptoname(oneEmojiName);
                logger.info(gson.toJson(result));
                return result;
//                return validLogin;
            }

            String brand = brandCodeService.checkBrandCode(data.getCryptoname().trim());
            logger.info("Brand: " + brand);
            if (brand != null && brand.length() > 0) {
                ResultDTO result = new ResultDTO(true, null, 0);
                result.setCryptoname(brand);
                logger.info(gson.toJson(result));
                return result;
            }

            client = clientRepository.findClientByCryptoname(data.getCryptoname().trim());
            if (client != null) return error100;
            if (!data.getCryptoname().trim().equals(data.getCryptoname().trim())) return error104;
            if (!checkValidCryptoname(data.getCryptoname().trim())) {
                logger.error("Check name");
                return error105;
            }
            return validLogin;
        } catch (Exception e) {
            logger.error("checkName: "+gson.toJson( data));
            logger.error(e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private boolean checkValidCryptoname(String cryptoname) {

        if (cryptoname.length() <= 1) {
            logger.error("So short");
            return false;
        }
        logger.info(cryptoname);
        if (emojiCodeService.checkEmojiCode(cryptoname) != null) return true;
        if (brandCodeService.checkBrandCode(cryptoname) != null) return true;
        if (emojiCodeService.emojiAvaliable(cryptoname)) return true;
        if (brandCodeService.brandAvaliable(cryptoname)) return true;

        char ch = ((char) 65039);
        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname).replace(Character.toString(ch), "");
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);

        if (textWithoutEmoji.length() + textOnlyEmoji.size() > 25) {
            logger.error("So long");
            return false;
        }

        // Reserv:
        if (textWithoutEmoji.length() == 0 && textOnlyEmoji.size() == 1) {
            logger.error("textOnlyEmoji == 1");
            return false;
        }

        int codePointCount = textWithoutEmoji.codePointCount(0, textWithoutEmoji.length());
        if (codePointCount == 1 && codePointCount < textWithoutEmoji.length()) {
            logger.error("One letter");
            return false;
        }


        char[] charArray = textWithoutEmoji.toCharArray();
        int charArrayLength = charArray.length;
        for (int i = 0; i < charArrayLength; i++) {
            char symbol = charArray[i];

            if (!Character.isHighSurrogate(symbol) && !Character.isLowSurrogate(symbol) &&
                    !(Character.isLetter(symbol) && Character.isLowerCase(symbol)) &&
                    !(Character.isAlphabetic(symbol) && Character.toLowerCase(symbol) == symbol) &&
                    symbol != '-') {
                logger.error("Eroor rullers");
                return false;
            }

        }
        return true;
    }

    private static boolean isLatinLetter(char c) {
        return (c >= 'a' && c <= 'z');
    }

//    public static void main(String[] args) {
//        ClientService clientService = new ClientService();
//        List<String> tests = Arrays.asList("normallogin", "nor-malLogin", "norm-allogin", "norma.llogin", "normallo_gin",
//                "norm allogin", "norm\uD83D\uDE09\uD83D\uDE09allogin", "nor\uD83D\uDE03mallogin", "norm\uD83D\uDE09-\uD83D\uDE09allogin", "nor\uD83D\uDE03mallo\ngin");
//        for (String test : tests) {
//            System.out.println(test + " " + clientService.checkValidCryptoname(test));
//        }
//    }

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
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
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

    @Autowired
    public void setEmojiCodeService(EmojiCodeService emojiCodeService) {
        this.emojiCodeService = emojiCodeService;
    }

    @Autowired
    public void setBrandCodeService(BrandCodeService brandCodeService) {
        this.brandCodeService = brandCodeService;
    }

    @Autowired
    public void setCryptoWalletsService(CryptoWalletsService cryptoWalletsService) {
        this.cryptoWalletsService = cryptoWalletsService;
    }


    public static final ResultDTO created = new ResultDTO(true, "Account created", 0);
    public static final ResultDTO validLogin = new ResultDTO(true, "Login is valid", 0);
    public static final ResultDTO mailSended = new ResultDTO(true, "Confirm mail sended", 0);


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
//        logger.info(String.valueOf(request.getDataTime()));
//        logger.info(String.valueOf(LocalDateTime.now().minusDays(1)));
//        logger.info(String.valueOf(request.getDataTime().compareTo(LocalDateTime.now().minusDays(1))));
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
            logger.info("Data: "+gson.toJson(data));
            String brand = brandCodeService.getBrandFromCode(data.getCryptoname());
            logger.info("Brand: " + brand);
            if (brand != null) {
                data.setCryptoname(brand);
            }

            Client client = clientRepository.findClientByCryptoname(data.getCryptoname().trim());
            if (client == null) {
//                if(!anonimousSending){
//                    return error108;
//                }
                // May be it is wallet:
                client = clientRepository.findClientByWalletAddress(data.getCryptoname());
                if (client == null) {
                    // May be anonimous wallet:
                    String walletAddress = checkAnonimousWallet(data.getCryptoname());
                    if (walletAddress != null && walletAddress.length() == 64) {
                        return new ResultDTO(true, walletAddress, 0);
                    } else if (anonimousSending && data.getCryptoname().length() == 64) {
                        return new ResultDTO(true, data.getCryptoname(), 0);
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

    public ResultDTO getWallets(GetWalletsDTO data) {
        try {
            Client quested = clientRepository.findClientByApikey(data.getApikey());
            if (quested == null) {
                return error109;
            }

            Map<String, ContactDTO> clients = new HashMap<>();
            for (String cryptoname : data.getCryptonames()) {
                Client client = clientRepository.findClientByCryptoname(cryptoname.trim());
                if (client != null && client.getWalletAddress() != null) {


                    clients.put(cryptoname, toContact(client));
                }
            }

            return new ResultDTO(true, clients, 0);
        } catch (Exception e) {
            return error108;
        }
    }

    private ContactDTO toContact(Client client) {

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setLogin(client.getCryptoname());
        contactDTO.setWalletAddress(client.getWalletAddress());
        contactDTO.setAvatar(client.getAvatar());
        contactDTO.setAvaExists(client.avaExists());
        contactDTO.setPhone(client.getPhone());
        return contactDTO;
    }

    private String checkAnonimousWallet(String address) {
        if (address.length() < 60) {
            logger.error(address + "is not wallet");
            return null;
        }
        FshnBalanceDTO walletInfo = blockchainService.getWalletInfo(address);
        if (walletInfo != null && walletInfo.getPub_key() != null &&
                !walletInfo.getPub_key().equals("0000000000000000000000000000000000000000000000000000000000000000")) {
            return walletInfo.getPub_key();
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
//        logger.info(gson.toJson(data));
        if (data.getApikey() == null) {
            logger.error("ApiKey: {]", data.getApikey());
            return error107;
        }
        if (data.getCryptoname() == null) {

            Client clientByApikey = findClientByApikey(data.getApikey());
            if (clientByApikey == null) {
                logger.error(gson.toJson(data));
                return error108;
            }

            if (clientByApikey.isBanned()) {
                logger.error("Cliend is in bann: {}", clientByApikey.isBanned());
                return error122;
            }

            if (clientByApikey.getWalletAddress() == null || clientByApikey.getWalletAddress().length() == 0) {
                return clientByApikey;
            } else {
                logger.error("Wallet Address: {}", clientByApikey.getWalletAddress());
                return error118;
            }
        }

        String cryptoname = emojiCodeService.checkEmojiCode(data.getCryptoname());
        if (cryptoname == null) cryptoname = emojiCodeService.checkEmojiCode(data.getApikey());
        if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode(data.getCryptoname());
        if (cryptoname == null) cryptoname = brandCodeService.checkBrandCode(data.getApikey());
        if (cryptoname == null) cryptoname = data.getCryptoname().trim();

        Client client = clientRepository.findClientByCryptoname(cryptoname);
//        logger.info("Client info: " + client);
        if (client == null) {
            logger.error("Client: {}", client);
            return error108;
        }
        if (client.getApikey() == null) {
            logger.error("ApiKey: {}", client.getApikey());
            return error107;
        }
        if (!client.getApikey().equals(data.getApikey())) {
            logger.error("Apikey: {}", client.getApikey());
            logger.error("ApiKey from data: {}", data.getApikey());
            return error109;
        }
        updateBalance(client);
//        logger.info(gson.toJson(client));
        return client;
    }

    public Client updateBalance(Client client) {
        BigDecimal balanceFromBlockchain =
                blockchainService.getBalance(client.getWalletAddress(), client.getCryptoname());
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
            if (!data.getCryptoname().equals(data.getCryptoname())) return error104;
            if (!checkValidCryptoname(data.getCryptoname())) {
                logger.error("Client Info");
                return error105;
            }

            if (!client.getCryptoname().equals(data.getCryptoname())) {
                if (client.isLoginChanged()) return error113;
                client.setCryptoname(data.getCryptoname());
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
        if (apikey == null) return null;
        List<Client> clientList = clientRepository.findByApikey(apikey);
        if (clientList == null || clientList.size() == 0) return null;
        return clientList.get(0);
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

    public Client findByCryptonameAndApiKey(String cryptoname, String apikey) {
        List<Client> clientList = clientRepository.findByApikey(apikey);

        if (clientList == null || clientList.size() == 0) return null;
        if (!clientList.get(0).getCryptoname().equals(cryptoname)) return null;
        return clientList.get(0);
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
            logger.info(gson.toJson(data));
            logger.info(String.valueOf(Bytes.asList(data.getCryptoname().getBytes())));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (client != null) {
                logger.error(gson.toJson(error100));
                return error100;
            }
            List<Client> clientList = clientRepository.findClientsByEmail(data.getEmail());
            if (clientList != null && clientList.size() > 0) {
                logger.error(gson.toJson(error114));
                return error114;
            }
            if (!checkValidCryptoname(data.getCryptoname())) {
                logger.error("register Cryptoname" + gson.toJson(error105));
                return error105;
            }
            client = new Client(data.getCryptoname(),
                    data.getEmail());
            client.setRegisteredFrom(FROMWEB);
            clientRepository.save(client);
            emailService.sendMail(data.getEmail(), "Fashion Coin: Congratulations!", "You created cryptoname: " + data.getCryptoname() + " ");
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO registerCryptoname(CryptonameTelegramDTO data) {
        try {
            logger.info(gson.toJson(data));
            logger.info(String.valueOf(Bytes.asList(data.getCryptoname().getBytes())));
            Client client = clientRepository.findClientByCryptoname(data.getCryptoname());
            if (client != null) {
                logger.info(gson.toJson(error100));
                return error100;
            }
            List<Client> clientList = clientRepository.findClientsByTelegramId(data.getTelegramId());
            if (clientList != null && clientList.size() > 0) {
                logger.info(gson.toJson(error114));
                return error114;
            }
            if (!checkValidCryptoname(data.getCryptoname())) {
                logger.info("Register Name " + gson.toJson(error105));
                return error105;
            }
            client = new Client(data.getCryptoname(),
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
        logger.info("Reserve names");
        for (String name : names) {
            try {
                ReserveCryptoNameDTO data = new ReserveCryptoNameDTO();
                data.setCryptoname(name);
                String randomToken = getRandomToken(16);
                data.setApikey(randomToken);
                reserveName(data);
            } catch (Exception e) {
                logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
                logger.error(e.getMessage());
            }
        }
        logger.info("End reserv");
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
            logger.info("Get API from Telegramm: " + userId);

            List<Client> clientList = clientRepository.findClientsByTelegramId(Integer.parseInt(userId));

            if (clientList != null && clientList.size() == 1) {
                logger.info("ClientList size: " + clientList.size());
                Client client = clientList.get(0);
                logger.info(client.getCryptoname());
                if (client.getApikey() == null || client.getApikey().length() == 0) {
                    client.setApikey(getRandomToken(16));
                    clientRepository.save(client);
                }
                return client.getApikey();
            } else {
                logger.info("ClientList size: 0");
            }
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        logger.error("API key for Telegram not found");
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
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            return null;
        }
    }

    public Client findClientByCurrencyWallet(String currency, String address) {
        String cryptoname = cryptoWalletsService.getCryptoname(currency, address);
        if (cryptoname == null) return null;
        Client client = findByCryptoname(cryptoname);
        return client;
    }

    public Client getClient(Long id) {
        if (id == null) {
            logger.error("Client ID: {}", id);
        }
        return clientRepository.findById(id).orElse(null);
    }

    public Client findByCryptonameOrWallet(String receiver) {
        Client result = findByCryptoname(receiver);
        if (result != null) {
            return result;
        } else {
            return findByWallet(receiver);
        }
    }

    public ResultDTO clientAbout(AboutClientRequestDTO request) {

        try {

            Client client = findClientByApikey(request.getApikey());
            if (client == null) {
                return error127;
            }

            client.setAbout(request.getAbout());
            client.setSocialLinks(gson.toJson(request.getSocialLinks()));

            clientRepository.save(client);

            return new ResultDTO(true, client, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }


    }
}
