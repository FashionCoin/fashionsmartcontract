package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.sun.xml.fastinfoset.Encoder;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.WrapLog;
import fashion.coin.wallet.back.entity.WrappedTokenEvents;
import fashion.coin.wallet.back.repository.TokenEventsRepository;
import fashion.coin.wallet.back.repository.WrapLogRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class WrapService {

    Logger logger = LoggerFactory.getLogger(WrapService.class);

    Gson gson;
    RestTemplate restTemplate;

    TransactionService transactionService;
    AIService aiService;
    ClientService clientService;
    WrapLogRepository wrapLogRepository;
    TokenEventsRepository tokenEventsRepository;
    CryptoWalletsService cryptoWalletsService;

    long nonce;

    public static final String MINT = "mint";
    public static final String TRANSFER = "transfer";
    public static final String BURN = "burn";
    public static final String NULL_ADDRESS = "0x0000000000000000000000000000000000000000000000000000000000000000";
    public static final String SHORT_NULL_ADDRESS = "0x0000000000000000000000000000000000000000";

    // Ethereum
    @Value("${wfshn.contract.address}")
    String ethereumContractAddress;


    @Value("${wfshn.owner.priv.key}")
    String ethereumOwnerPrivKey;

    @Value("${etherscan.api.key}")
    String ethereumApiKey;

// Binance

    @Value("${fshnb.contract.address}")
    String binanceContractAddress;


    @Value("${fshnb.owner.priv.key}")
    String binanceOwnerPrivKey;

    @Value("${bscscan.api.key}")
    String binanceApiKey;


    public ResultDTO wrap(WrappedRequestDTO request) {
        try {
            String contractAddress = request.getNetwork().equals("binance") ?
                    binanceContractAddress : ethereumContractAddress;
            String ownerPrivKey = request.getNetwork().equals("binance") ?
                    binanceOwnerPrivKey : ethereumOwnerPrivKey;

            logger.info(gson.toJson(request));
            if (!aiService.isDiamondWallet(request.getTransactionRequestDTO().getReceiverWallet(), request.getNetwork())) {
                return error207;
            }
            logger.info("receiver ok!");
            ResultDTO result = transactionService.send(request.getTransactionRequestDTO());
            logger.info(gson.toJson(result));
            if (result.isResult()) {

                long amount = Long.parseLong(request.getTransactionRequestDTO().getBlockchainTransaction().getBody().getAmount());
                logger.info("Amount: {}", amount);
                WrappedResponseDTO resp = signPayment(request.getEthereumWallet(),
                        amount,
                        getNonce(),
                        contractAddress,
                        ownerPrivKey
                );
                wrapLogRepository.save(new WrapLog(true, amount, request.getTransactionRequestDTO().getSenderWallet(),
                        request.getEthereumWallet(), request.getNetwork()));
                return new ResultDTO(true, resp, 0);

            } else {

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private Long getNonce() {
        if (nonce == 0) {
            nonce = System.currentTimeMillis();
        }
        nonce++;
        return nonce;
    }


    private WrappedResponseDTO signPayment(String recipient, long amount, long nonce,
                                           String contractAddress, String ownerPrivKey) throws UnsupportedEncodingException {

        WrappedResponseDTO resp = new WrappedResponseDTO();
        resp.setWallet(recipient);
        resp.setAmount(amount);
        resp.setNonce(nonce);
        resp.setSmartconract(contractAddress);


        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B on Rinkebuy
        Credentials ownerCred = Credentials.create(ownerPrivKey);
        logger.info("Owner Address: {}", ownerCred.getAddress());

        byte[] recipientAddress = Numeric.hexStringToByteArray(recipient);
        logger.info("Recipient Address: {}", Numeric.toHexString(recipientAddress));


        byte[] amountBytes = Numeric.toBytesPadded(BigInteger.valueOf(amount), 32);
        logger.info("Amount: {}", Numeric.toHexString(amountBytes));

        byte[] nonceBytes = Numeric.toBytesPadded(BigInteger.valueOf(nonce), 32);
        logger.info("Nonce: {}", Numeric.toHexString(nonceBytes));

        byte[] contract = Numeric.hexStringToByteArray(contractAddress);
        logger.info("Contract Address: {}", Numeric.toHexString(contract));


        byte[] concatBytes = ArrayUtils.addAll(recipientAddress, amountBytes);
        concatBytes = ArrayUtils.addAll(concatBytes, nonceBytes);
        concatBytes = ArrayUtils.addAll(concatBytes, contract);
        logger.info("Concat Bytes: {}", Numeric.toHexString(concatBytes));

        byte[] hash = Hash.sha3(concatBytes);
        logger.info("Hash: {}", Numeric.toHexString(hash));

        byte[] prefixed = ArrayUtils.addAll("\u0019Ethereum Signed Message:\n32".getBytes(Encoder.UTF_8), hash);
        logger.info("Prefixed: {}", Numeric.toHexString(prefixed));

        byte[] prefHash = Hash.sha3(prefixed);
        logger.info("Prefixed Hash: {}", Numeric.toHexString(prefHash));

        // Hash: 0xc1de7059c3c0ed7870c7ca25bcc78fce22c8f1966bba51e5b43bd4740d3dacce
        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B
        Sign.SignatureData signData = Sign.signPrefixedMessage(hash, ownerCred.getEcKeyPair());


        byte[] r = signData.getR();
        logger.info("R: {}", Numeric.toHexString(r));
        resp.setR(Numeric.toHexString(r));

        byte[] s = signData.getS();
        logger.info("S: {}", Numeric.toHexString(s));
        resp.setS(Numeric.toHexString(s));

        byte[] v = signData.getV();
        logger.info("V: {}", Numeric.toBigInt(v));
        resp.setV(Numeric.toBigInt(v).longValue());

        byte[] sig = ArrayUtils.addAll(r, s);
        sig = ArrayUtils.addAll(sig, v);

        logger.info("Signature len: {}", sig.length);
        logger.info("Sign data: {}", Numeric.toHexString(sig));
        return resp;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
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
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setWrapLogRepository(WrapLogRepository wrapLogRepository) {
        this.wrapLogRepository = wrapLogRepository;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setTokenEventsRepository(TokenEventsRepository tokenEventsRepository) {
        this.tokenEventsRepository = tokenEventsRepository;
    }

    @Autowired
    public void setCryptoWalletsService(CryptoWalletsService cryptoWalletsService) {
        this.cryptoWalletsService = cryptoWalletsService;
    }

    public ResultDTO unwrap(UnwrapRequestDTO request) {
        try {
            String network = request.getNetwork().equals("binance") ? "binance" : "ethereum";


            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) return error109;
            if (transactionExists(request.getTransactionHash())) {
                return error208;
            }

            if (!eventExists(request.getTransactionHash())) {
                return error209;
            }
            WrappedTokenEvents event = tokenEventsRepository.findById(request.getTransactionHash()).orElse(null);

            BigDecimal floatamount = new BigDecimal(request.getAmount());
            BigInteger amount = floatamount.movePointRight(3).toBigInteger();
            if (amount.longValue() != event.getAmount()) {
                logger.error("Amount {} != {}", amount.longValue(), event.getAmount());
                return error210;
            }
            if (!event.getType().equals(BURN)) {
                return error211;
            }


            logger.info("TX {} hash: {}", network, request.getTransactionHash());

            AIService.AIWallets diamondWallet = network.equals("binance") ?
                    AIService.AIWallets.BINANCE : AIService.AIWallets.DIAMOND;

            boolean result = aiService.transfer(
                    request.getAmount(),
                    client.getWalletAddress(),
                    diamondWallet).isResult();
            if (result) {

                wrapLogRepository.save(new WrapLog(false, amount.longValue(), client.getWalletAddress(),
                        request.getEthereumWallet(), network, request.getTransactionHash()));
                return new ResultDTO(true, "Unwrap ok!", 0);
            } else {
                return error205;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private boolean eventExists(String transactionHash) {

        WrappedTokenEvents event = null;
        for (int i = 1000; i < 100000; i += 500) {
            logger.info("TxHash: {}", transactionHash);
            updateEthereumEventd();
            event = tokenEventsRepository.findById(transactionHash).orElse(null);
            if (event == null) {
                try {
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return (event != null);
    }

    private boolean transactionExists(String transactionHash) {
        List<WrapLog> wrapLogList = wrapLogRepository.findByTxHash(transactionHash);
        return (wrapLogList != null && wrapLogList.size() > 0);
    }


    private WrappedTokenEvents convertToEvent(EthEventDTO result, String network) {
        WrappedTokenEvents event = new WrappedTokenEvents();
        event.setTransactionHash(result.getTransactionHash());

        logger.info("{}: {}", result.transactionHash, hexToLong(result.getBlockNumber()));
        event.setBlockNumber(hexToLong(result.getBlockNumber()));

        event.setTimeStamp(hexToLong(result.getTimeStamp()));
        event.setAmount(hexToLong(result.getData()));
        event.setAddressFrom(hexToAddress(result.getTopics().get(1)));
        event.setAddressTo(hexToAddress(result.getTopics().get(2)));
        event.setType(hexToType(result.getTopics()));
        event.setNetwork(network);
        logger.info(gson.toJson(event));
        return event;
    }

    private String hexToType(List<String> topics) {
        if (topics.get(1).equals(NULL_ADDRESS)
                && !topics.get(2).equals(NULL_ADDRESS)) {
            return MINT;
        } else if (!topics.get(1).equals(NULL_ADDRESS)
                && !topics.get(2).equals(NULL_ADDRESS)) {
            return TRANSFER;
        } else if (!topics.get(1).equals(NULL_ADDRESS)
                && topics.get(2).equals(NULL_ADDRESS)) {
            return BURN;
        } else {
            return "";
        }
    }

    private Long hexToLong(String hexString) {
        logger.info("Convert {} to Long:",hexString);

        hexString = hexString.replace("0x", "");
        long result =Long.parseLong(hexString,16);

        //        byte[] byteArray = HexUtils.fromHexString(hexString);
//        BigInteger bigInteger = new BigInteger(byteArray);
//        logger.info("Big Integer: {}",bigInteger);
//        Long result = bigInteger.longValue();
//        if (result < 0) {
//            result = 16777216L - result;
//        }
        logger.info("Long: {}",result);
        return result;
    }

    private String hexToAddress(String hexString) {
        Address address = new Address(hexString);
        return Keys.toChecksumAddress(address.toString());
    }

    //    @Scheduled(cron = "0 * * * * *")
    public void updateEthereumEventd() {
        try {
            updateWrapEvents("ethereum");
            updateWrapEvents("binance");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateWrapEvents(String network) {
        try {

            long lastBlock = network.equals("binance") ?
                    6573342 : 11866190;
            WrappedTokenEvents lastEvent = tokenEventsRepository.findByLastTransaction(network);
            if (lastEvent != null) {
                lastBlock = lastEvent.blockNumber;
            }
            logger.info("Last event block on {}: {}", network, lastBlock);

            EventsDTO responce = null;
            if (network.equals("binance")) {
                responce = restTemplate.getForObject("https://api.bscscan.com/api?module=logs&action=getLogs&" +
//                                "fromBlock=" + lastBlock + "&toBlock=latest&" +
                                "fromBlock=" + lastBlock + "&toBlock=latest&" +
                                "address=" + binanceContractAddress.toLowerCase() + "&topic0=0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef&" +
                                "apikey=" + binanceApiKey,
                        EventsDTO.class);
            } else {

                responce = restTemplate.getForObject("https://api.etherscan.io/api?module=logs&action=getLogs&" +
                                "fromBlock=" + lastBlock + "&toBlock=latest&" +
                                "address=" + ethereumContractAddress.toLowerCase() + "&topic0=0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef&" +
                                "apikey=" + ethereumApiKey,
                        EventsDTO.class);
            }
            logger.info(gson.toJson(responce));
            logger.info(String.valueOf(responce.getResult().size()));
            for (EthEventDTO result : responce.getResult()) {
                tokenEventsRepository.save(convertToEvent(result, network));
            }
            logger.info("{} Events Updated. Add {} new events", network, responce.getResult().size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultDTO getWalletHistoy(WrapHistoryRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) return error109;
            updateEthereumEventd();
            String hexAddress = cryptoWalletsService.getWalletByCryptoname(client.getCryptoname(), "ETH");

            String address = hexToAddress(hexAddress);
            List<WrappedTokenEvents> fullHistory = getHistory(address, request.getNetwork());
            Long balance = 0L;
            List<WFSHNhistory> wfshNhistoryList = new ArrayList<>();
            for (WrappedTokenEvents events : fullHistory) {
                balance += getBalance(events, address);
                wfshNhistoryList.add(convertToWalletHistory(events, address));
            }
            WFSHNwallet wfshNwallet = new WFSHNwallet();
            wfshNwallet.setBalance(balance);
            wfshNwallet.setHistory(wfshNhistoryList);
            return new ResultDTO(true, wfshNwallet, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private WFSHNhistory convertToWalletHistory(WrappedTokenEvents events, String address) {
        WFSHNhistory wfshNhistory = new WFSHNhistory();
        wfshNhistory.setTimestamp(events.getTimeStamp());
        wfshNhistory.setAmount(events.getAmount());
        if (events.getAddressTo().equals(address)) {
            wfshNhistory.setIncome(true);
        }
        if (!events.getAddressFrom().equals(SHORT_NULL_ADDRESS) && !events.getAddressTo().equals(SHORT_NULL_ADDRESS)) {
            logger.info(events.getAddressFrom());
            logger.info(events.getAddressTo());
            logger.info(SHORT_NULL_ADDRESS);
            wfshNhistory.setTransfer(true);
            String contragent = wfshNhistory.isIncome() ? events.getAddressFrom() : events.getAddressTo();
            wfshNhistory.setWallet(contragent);
            Client client = clientService.findClientByCurrencyWallet("ETH", contragent);
            if (client != null) {
                wfshNhistory.setCryptoname(client.getCryptoname());
                wfshNhistory.setFshnPubKey(client.getWalletAddress());
                wfshNhistory.setAvatar(client.getAvatar());
            }
        }
        return wfshNhistory;

    }

    private Long getBalance(WrappedTokenEvents events, String address) {
        if (events.getAddressFrom().equals(address)) {
            return 0 - events.getAmount();
        } else if (events.getAddressTo().equals(address)) {
            return events.getAmount();
        } else {
            logger.error("Addres not found");
            return 0L;
        }
    }


    private List<WrappedTokenEvents> getHistory(String address, String network) {
        List<WrappedTokenEvents> eventsList = tokenEventsRepository.findByAddressFromOrAddressTo(address, address);
        eventsList.removeIf(wrappedTokenEvents -> !wrappedTokenEvents.getNetwork().equals(network));
        eventsList.sort((o1, o2) -> (o2.getTimeStamp().compareTo(o1.getTimeStamp())));
        return eventsList;
    }


}
