package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.SetEmailRequest;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.repository.SetEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

import static fashion.coin.wallet.back.FashionCoinWallet.HOST_NAME;
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

    Random random = new Random();

    public ResultDTO trySignUp(RegistrationRequestDTO data) {
        try {
            System.out.println(gson.toJson(data));
            Client client = clientRepository.findClientByLogin(data.getLogin().toLowerCase());
            if (client != null) return error100;
            if (data.getApikey() == null) return error107;
            BlockchainTransactionDTO jsonTransaction = data.getBlockchainTransaction();
            String pub_key = jsonTransaction.getBody().getPub_key();
            if (pub_key == null) return error106;
            if (!pub_key.equals(data.getWalletAddress())) return error103;
            if (!checkValidCryptoname(data.getLogin().toLowerCase())) return error105;
            if (blockchainService.sendTransaction(data.getBlockchainTransaction()).length() == 0)
                return error101;
//            aiService.cryptoname(data.getLogin().toLowerCase(), "", data.getWalletAddress());
            clientRepository.save(new Client(data.getLogin().toLowerCase(), data.getApikey(), data.getWalletAddress()));
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO checkLogin(CheckLoginDTO data) {
        try {
            Client client = clientRepository.findClientByLogin(data.getLogin().toLowerCase());
            if (client != null) return error100;
            if (!data.getLogin().toLowerCase().equals(data.getLogin())) return error104;
            if (!checkValidCryptoname(data.getLogin())) return error105;
            return validLogin;
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private boolean checkValidCryptoname(String cryptoname) {

        System.out.println(EmojiParser.parseToAliases(cryptoname, EmojiParser.FitzpatrickAction.REMOVE));

        System.out.println("cryptoname: "+cryptoname);
        System.out.println("cryptoname.length(): "+cryptoname.length());
        if(cryptoname.length()<1) return false;

        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname);
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);

        System.out.println("textWithoutEmoji: "+textWithoutEmoji);
        System.out.println("textOnlyEmoji: "+ gson.toJson(textOnlyEmoji));
        System.out.println("textWithoutEmoji.length()+textOnlyEmoji.size(): "+ textWithoutEmoji.length()+textOnlyEmoji.size());

        if(textWithoutEmoji.length()+textOnlyEmoji.size()>25) return false;
        System.out.println("textWithoutEmoji.length() textOnlyEmoji.size():"+textWithoutEmoji.length()+" "+textOnlyEmoji.size());
        // Reserv:
        if(textWithoutEmoji.length()==0 && textOnlyEmoji.size()==1) return false;

        char[] charArray = textWithoutEmoji.toCharArray();
        int charArrayLength = charArray.length;
        for (int i = 0; i < charArrayLength; i++) {
            char symbol = charArray[i];
            System.out.println(symbol);
            System.out.println("Code: "+ (int)symbol);
            if (!(Character.isLetter(symbol) && Character.isLowerCase(symbol)) && symbol != '-') return false;

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
        return clientRepository.findClientByWalletAddress(walletAddress);
    }

    public Client findByLogin(String login) {
        return clientRepository.findClientByLogin(login.toLowerCase());
    }

    public ResultDTO trySetEmail(SetEmailRequestDTO data) {
        try {
            Client client = findByLogin(data.getLogin());
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

    private static final ResultDTO created = new ResultDTO(true, "Account created", 0);
    private static final ResultDTO validLogin = new ResultDTO(true, "Login is valid", 0);
    private static final ResultDTO mailSended = new ResultDTO(true, "Confirm mail sended", 0);
    private static final ResultDTO error100 = new ResultDTO(false, "Client with this login exists", 100);
    private static final ResultDTO error101 = new ResultDTO(false, "Wallet does not created", 101);
    private static final ResultDTO error102 = new ResultDTO(false, "This is brand name", 102);
    private static final ResultDTO error103 = new ResultDTO(false, "Public Key in transaction not equal wallet", 103);
    private static final ResultDTO error104 = new ResultDTO(false, "Login must be lower case", 104);
    private static final ResultDTO error105 = new ResultDTO(false, "Emoji error, or short login", 105);
    private static final ResultDTO error106 = new ResultDTO(false, "Can't find pub_key param", 106);
    private static final ResultDTO error107 = new ResultDTO(false, "Can't find apikey param", 107);
    private static final ResultDTO error108 = new ResultDTO(false, "Client not found", 108);
    private static final ResultDTO error109 = new ResultDTO(false, "Not valid apikey", 109);
    private static final ResultDTO error113 = new ResultDTO(false, "Login has been changed once", 113);
    private static final ResultDTO error114 = new ResultDTO(false, "This Email is already use", 114);


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
            Client client = findByLogin(data.getLogin());
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
        Client client = clientRepository.findClientByLogin(data.getLogin().toLowerCase());
        if (client == null) return error108;
        return new ResultDTO(true, client.getWalletAddress(), 0);
    }

    public ResultDTO getLogin(GetLoginDTO data) {
        Client client = clientRepository.findClientByWalletAddress(data.getWallet());
        if (client == null) return error108;
        return new ResultDTO(true, client.getLogin(), 0);
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
        Client client = clientRepository.findClientByLogin(data.getLogin().toLowerCase());
        if (client == null) return error108;
        if (client.getApikey() == null) return error107;
        if (!client.getApikey().equals(data.getApikey())) return 109;
        return client;
    }

    public Object changeClientInfo(ClientInfoDTO data) {
        Client client = clientRepository.findClientByApikey(data.getApikey());
        if (client == null) return error109;

        if (data.getLogin() != null) {
            if (!data.getLogin().toLowerCase().equals(data.getLogin())) return error104;
            if (!checkValidCryptoname(data.getLogin())) return error105;

            if (!client.getLogin().toLowerCase().equals(data.getLogin().toLowerCase())) {
                if (client.isLoginChanged()) return error113;
                client.setLogin(data.getLogin().toLowerCase());
                client.setLoginChanged(true);
            }
        }

        if (data.getPhone() != null) {
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
        return client;
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
        if (!client.getLogin().equals(login)) return false;
        return true;
    }

    public List<Client> findByPhone(String phone) {
        return clientRepository.findClientsByPhone(phone);
    }

    public List<Client> findByPhoneEndingWith(String phone) {
        return clientRepository.findClientsByPhoneEndingWith(phone);
    }


    public void setAvatar(String login, String path) {
        Client client = clientRepository.findClientByLogin(login);
        client.setAvatar(path);
        clientRepository.save(client);
    }

    public ResultDTO registerCryptoname(CryptonameEmailDTO data) {
        try {
            System.out.println(gson.toJson(data));
            Client client = clientRepository.findClientByLogin(data.getCryptoname().toLowerCase());
            if (client != null) return error100;
            List<Client> clientList = clientRepository.findClientsByEmail(data.getEmail());
            if (clientList != null && clientList.size() > 0) return error114;
            if (!checkValidCryptoname(data.getCryptoname().toLowerCase())) return error105;

            clientRepository.save(new Client(data.getCryptoname().toLowerCase(),
                    data.getEmail()));
            emailService.sendMail(data.getEmail(), "Fashion Coin: Congratulations!", "You created cryptoname: " + data.getCryptoname().toLowerCase() + " ");
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
