package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;

import fashion.coin.wallet.back.dto.blockchain.ResponceDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.utils.SignBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;


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

    BlockchainService blockchainService;
    ClientService clientService;
    TransactionService transactionService;
    SettingsService settingsService;
    Gson gson;


    public AIService() {
        SignBuilder signBuilder = SignBuilder.init();
    }

    public void createNewWallet() {

        try {
            String sign = SignBuilder.init()
                    .setNetworkId(0)
                    .setProtocolVersion(0)
                    .setMessageId(0)
                    .setServiceId(0)
                    .addPublicKeyOrHash(pub_key)
                    .sign(priv_key);
            String json = String.format(CREATE_WALLET, pub_key, sign);
            System.out.println(json);
            BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
            String tx_hash = blockchainService.sendTransaction(blockchainTransactionDTO);
            System.out.println(tx_hash);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public ResponceDTO setRoot() {
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
            System.out.println(tx_hash);
            responce.setTx_hash(tx_hash);

            return responce;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return responce;
    }


    public boolean transfer(String amountStr, String receiver) {
        BigDecimal floatamount = new BigDecimal(amountStr);
//        BigInteger amount = new BigInteger(amountStr + "000", 10);
        BigInteger amount = floatamount.movePointRight(3).toBigInteger();
        BigInteger seed = new BigInteger(String.valueOf(System.currentTimeMillis()));
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
            System.out.println(gson.toJson(transactionRequestDTO));
            System.out.println(gson.toJson(transactionService.send(transactionRequestDTO)));
            return true;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cryptoname(String cryptoname, String salt, String wallet) {

        String name_hash = DigestUtils.sha256Hex(cryptoname + salt);
        BigInteger seed = new BigInteger(String.valueOf(System.currentTimeMillis()));
        SignBuilder.init();
        try {
            System.out.println(priv_key);
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
            System.out.println(json);
            BlockchainTransactionDTO blockchainTransactionDTO = gson.fromJson(json, BlockchainTransactionDTO.class);
            String tx_hash = blockchainService.sendTransaction(blockchainTransactionDTO);
            System.out.println(tx_hash);
        } catch (UnsupportedEncodingException e) {
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

    private static final String AI_PUB_KEY = "AI public key";
    private static final String AI_PRIV_KEY = "AI private key";

    private String priv_key = null;
    private String pub_key = null;

    public void refillWallet(String login) {
        Client client = clientService.findByLogin(login);
        if (client != null) {
            transfer("300000", client.getWalletAddress());
        }
    }

    public boolean isReady() {

        if (priv_key != null && pub_key != null) {
            return true;
        } else {
            pub_key = settingsService.get(AI_PUB_KEY);
            priv_key = settingsService.get(AI_PRIV_KEY);
            return (priv_key != null && pub_key != null);
        }
    }

    public ResponceDTO setKeys(String pub_key, String priv_key) {
        ResponceDTO result = new ResponceDTO();
        if (isReady()) {
            return result;
        } else if (pub_key.length() == 64 && priv_key.length() == 128) {
            this.pub_key = pub_key;
            this.priv_key = priv_key;
            settingsService.set(AI_PUB_KEY, pub_key);
            settingsService.set(AI_PRIV_KEY, priv_key);
            createNewWallet();
            result = setRoot();
            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    cryptoname("AI-LEFT",
                            "3265ccebf07f7417a6c9a5885cbe6e048e2a61541d66fcefccfb378404c73b90",
                            pub_key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return result;
    }

    public void printTransferTransaction(String receiver, String amountStr) {
        if (isReady()) {
            BigDecimal floatamount = new BigDecimal(amountStr);
            BigInteger amount = floatamount.movePointRight(3).toBigInteger();
            BigInteger seed = new BigInteger(String.valueOf(System.currentTimeMillis()));
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
                System.out.println(gson.toJson(transactionRequestDTO));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("AI don't ready");
        }
    }

}
