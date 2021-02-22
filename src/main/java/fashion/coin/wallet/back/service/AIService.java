package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.CurrencyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;

import fashion.coin.wallet.back.dto.blockchain.FshnBalanceDTO;
import fashion.coin.wallet.back.dto.blockchain.ResponceDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.utils.SignBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class AIService {

    Logger logger = LoggerFactory.getLogger(AIService.class);

    BlockchainService blockchainService;
    ClientService clientService;
    TransactionService transactionService;
    SettingsService settingsService;
    CurrencyService currencyService;
    AESEncriptor aesEncriptor;
    MessagingService messagingService;
    Gson gson;

    Map<String, String> keyStore = new HashMap<>();

    public String getAiWallet() {
        return getPubKey(AIWallets.LEFT);
    }

    /// Temp
    @Value("${diamond.pub}")
    String pubDiamond;

    @Value("${diamond.priv}")
    String privDiamond;

    ///


    public enum AIWallets {
        LEFT,
        BTCU,
        MONEYBAG,
        DIAMOND
    }

    public AIService() {
        SignBuilder signBuilder = SignBuilder.init();
    }

    public void createNewWallet() {

        String pub_key = getPubKey(AIWallets.LEFT);
        String priv_key = getPrivKey(AIWallets.LEFT);

        try {
            String sign = SignBuilder.init()
                    .setNetworkId(0)
                    .setProtocolVersion(0)
                    .setMessageId(0)
                    .setServiceId(0)
                    .addPublicKeyOrHash(pub_key)
                    .sign(priv_key);
            String json = String.format(CREATE_WALLET, pub_key, sign);
            logger.info(json);
            BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
            String tx_hash = blockchainService.sendTransaction(blockchainTransactionDTO);
            logger.info(tx_hash);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public ResponceDTO setRoot() {

        String pub_key = getPubKey(AIWallets.LEFT);
        String priv_key = getPrivKey(AIWallets.LEFT);

        ResponceDTO responce = new ResponceDTO();
        try {
            Gson gson = new Gson();
            BigInteger left_right = new BigInteger("1");
            BigInteger seed = new BigInteger(String.valueOf(System.currentTimeMillis()));
            String sign = SignBuilder.init()
                    .setNetworkId(0)
                    .setProtocolVersion(0)
                    .setMessageId(3)
                    .setServiceId(0)
                    .addUint64(left_right)
                    .addPublicKeyOrHash(pub_key)
                    .addUint64(seed)
                    .sign(priv_key);
            String json = String.format(SET_ROOT, left_right.toString(10), pub_key, seed.toString(10), sign);
            BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
            String tx_hash = blockchainService.sendTransaction(blockchainTransactionDTO);
            logger.info(tx_hash);
            responce.setTx_hash(tx_hash);

            return responce;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return responce;
    }


    public boolean transfer(String amountStr, String receiver, AIWallets sender) {
        return transfer(amountStr, receiver, sender, System.currentTimeMillis());
    }

    public boolean transfer(String amountStr, String receiver, AIWallets sender, long seedLong) {


        String pub_key = getPubKey(sender);
        String priv_key = getPrivKey(sender);

        BigDecimal floatamount = new BigDecimal(amountStr);
//        BigInteger amount = new BigInteger(amountStr + "000", 10);
        BigInteger amount = floatamount.movePointRight(3).toBigInteger();
        BigInteger seed = new BigInteger(String.valueOf(seedLong));
        SignBuilder.init();
        try {
            String sign = SignBuilder.init()
                    .setNetworkId(0)
                    .setProtocolVersion(0)
                    .setMessageId(1)
                    .setServiceId(0)
                    .addPublicKeyOrHash(pub_key)
                    .addPublicKeyOrHash(receiver)
                    .addUint64(amount)
                    .addUint64(seed)
                    .sign(priv_key);

            String json = String.format(TRANSFER_COIN, pub_key, receiver, amount.toString(10), seed.toString(10), sign);
            BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
            TransactionRequestDTO transactionRequestDTO = createRequest(blockchainTransactionDTO);
            logger.info(gson.toJson(transactionRequestDTO));

            ResultDTO resp = transactionService.send(transactionRequestDTO);

            logger.info(gson.toJson(resp));
            return resp.isResult();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    ConcurrentHashMap<String, String> blockKey = new ConcurrentHashMap<>();

    public void cryptoname(String cryptoname, String salt, String wallet) {

        if (blockKey.containsKey(wallet)) return;
        else blockKey.put(wallet, cryptoname);
        String pub_key = getPubKey(AIWallets.LEFT);
        String priv_key = getPrivKey(AIWallets.LEFT);

        new Thread(new Runnable() {

            @Override
            public void run() {
                String name_hash = DigestUtils.sha256Hex(cryptoname + salt);
                BigInteger seed = new BigInteger(String.valueOf(System.currentTimeMillis()));
                SignBuilder.init();
                try {
                    boolean isWalletExists = false;
                    do {
                        logger.info("Sleep before register name on blockchain");
                        Thread.sleep(1000);
                        logger.info("Wake Up");
                        FshnBalanceDTO fshnBalanceDTO = blockchainService.getWalletInfo(wallet);
                        if (fshnBalanceDTO != null && fshnBalanceDTO.getPub_key() != null
                                && fshnBalanceDTO.getPub_key().equals(wallet)) {
                            isWalletExists = true;
                        }
                        logger.info("isWalletExists = " + String.valueOf(isWalletExists));
                    } while (!isWalletExists);

                    FshnBalanceDTO fshnBalanceDTO = blockchainService.getWalletInfo(wallet);
                    if (!fshnBalanceDTO.name_hash.equals("0000000000000000000000000000000000000000000000000000000000000000")) {
                        logger.info("Name " + cryptoname + " is already registered for " + wallet);
                        logger.info("Balance " + cryptoname + " is " + fshnBalanceDTO.balance);
                        if (blockKey.containsKey(wallet)) blockKey.remove(wallet);
                        return;
                    }

//                    System.out.println(priv_key);
                    String sign = SignBuilder.init()
                            .setNetworkId(0)
                            .setProtocolVersion(0)
                            .setMessageId(4)
                            .setServiceId(0)
                            .addPublicKeyOrHash(name_hash)
                            .addPublicKeyOrHash(wallet)
                            .addUint64(seed)
                            .sign(priv_key);

                    String json = String.format(CRYPTO_NAME, name_hash, wallet, seed.toString(10), sign);
                    logger.info(json);
                    BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
                    String tx_hash = blockchainService.sendTransaction(blockchainTransactionDTO);
                    logger.info(tx_hash);

//                    tenDollarBonus(wallet);
                    if (blockKey.containsKey(wallet)) blockKey.remove(wallet);
                    return;

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void tenDollarBonus(String wallet) {
        try {
            logger.info("Bonus $10");
            CurrencyDTO currencyDTO = currencyService.getCurrencyRate("USD");
            BigDecimal usdRate = new BigDecimal(currencyDTO.getRate()); // $1
            BigDecimal amount = usdRate.multiply(BigDecimal.TEN).setScale(3, RoundingMode.HALF_UP); // $10
            logger.info("Send " + amount + " FSHN to " + wallet);
            transfer(amount.toString(), wallet, AIWallets.MONEYBAG, 10000);
            Client client = clientService.findByWallet(wallet);
            clientService.updateBalance(client);
            messagingService.sendNotification("change_balance",
                    client.getWalletBalance().toString(),
                    "topic_" + client.getWalletAddress());
            if (blockKey.containsKey(wallet)) blockKey.remove(wallet);
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private TransactionRequestDTO createRequest(BlockchainTransactionDTO blockchainTransactionDTO) {

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setSenderWallet(blockchainTransactionDTO.getBody().getFrom());
        transactionRequestDTO.setReceiverWallet(blockchainTransactionDTO.getBody().getTo());
        transactionRequestDTO.setAmount(new BigDecimal(blockchainTransactionDTO.getBody().getAmount()).movePointLeft(3).toString());
        transactionRequestDTO.setBlockchainTransaction(blockchainTransactionDTO);
        return transactionRequestDTO;
    }


    public static final String CREATE_WALLET = "{\n" +
            "    \"network_id\": 0,\n" +
            "    \"protocol_version\": 0,\n" +
            "    \"message_id\": 0,\n" +
            "    \"service_id\": 0,\n" +
            "    \"body\": {\n" +
            "        \"pub_key\": \"%s\"\n" +
            "    },\n" +
            "    \"signature\": \"%s\"\n" +
            "}";

    public static final String SET_ROOT = "{\n" +
            "    \"network_id\": 0,\n" +
            "    \"protocol_version\": 0,\n" +
            "    \"message_id\": 3,\n" +
            "    \"service_id\": 0,\n" +
            "    \"body\": {\n" +
            "        \"left_right\": \"%s\",\n" +
            "        \"pub_key\": \"%s\",\n" +
            "        \"seed\": \"%s\"\n" +
            "    },\n" +
            "    \"signature\": \"%s\"\n" +
            "}";


    public static final String TRANSFER_COIN = "{\n" +
            "    \"network_id\": 0,\n" +
            "    \"protocol_version\": 0,\n" +
            "    \"message_id\": 1,\n" +
            "    \"service_id\": 0,\n" +
            "    \"body\": {\n" +
            "        \"from\": \"%s\",\n" +
            "        \"to\": \"%s\",\n" +
            "        \"amount\": \"%s\",\n" +
            "        \"seed\": \"%s\"\n" +
            "    }," +
            "    \"signature\": \"%s\"\n" +
            "}";

    public static final String CRYPTO_NAME = "{\n" +
            "    \"network_id\": 0,\n" +
            "    \"protocol_version\": 0,\n" +
            "    \"message_id\": 4,\n" +
            "    \"service_id\": 0,\n" +
            "    \"body\": {\n" +
            "        \"name_hash\": \"%s\",\n" +
            "        \"pub_key\": \"%s\",\n" +
            "        \"seed\": \"%s\"\n" +
            "    }," +
            "    \"signature\": \"%s\"\n" +
            "}";


    @Autowired
    public void setBlockchainService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Autowired
    public void setAesEncriptor(AESEncriptor aesEncriptor) {
        this.aesEncriptor = aesEncriptor;
    }

    @Autowired
    public void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public boolean isMoneyBagWallet(String walletAddress) {
        String moneyBagWallet = getPubKey(AIWallets.MONEYBAG);
        logger.info("MonneyBag Wallet: {}", moneyBagWallet);
        return moneyBagWallet.equals(walletAddress);
    }

    public String getPubKey(AIWallets wallet) {
        return getKey(false, wallet);
    }

    String getPrivKey(AIWallets wallets) {
        return getKey(true, wallets);
    }


    String getKey(boolean isPrivate, AIWallets wallet) {
        try {
            String settingsKey = wallet.name() + (isPrivate ? "_priv_key" : "_pub_key");
            if (keyStore.containsKey(settingsKey)
                    && keyStore.get(settingsKey) != null
                    && keyStore.get(settingsKey).length() > 0
            ) {
                return keyStore.get(settingsKey);
            }
            String encryptedKey = settingsService.get(settingsKey);
            if (encryptedKey == null) {
                logger.error("Key " + settingsKey + " not found in DB");
                return null;
            }
            String key = aesEncriptor.convertToEntityAttribute(encryptedKey);
            keyStore.put(settingsKey, key);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    public void saveDiamond() {
        try {
            String ecrPub = aesEncriptor.convertToDatabaseColumn(pubDiamond);
            String encPriv = aesEncriptor.convertToDatabaseColumn(privDiamond);

            settingsService.set(AIWallets.DIAMOND + "_pub_key", ecrPub);
            settingsService.set(AIWallets.DIAMOND + "_priv_key", encPriv);

            logger.info("ecrPub: {}", ecrPub);
            logger.info("encPriv: {}", encPriv);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
