package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.dto.blockchain.FshnHistoryTxDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.TransactionCoins;
import fashion.coin.wallet.back.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class TransactionService {

    Logger logger = LoggerFactory.getLogger(TransactionService.class);

    TransactionRepository transactionRepository;
    BlockchainService blockchainService;
    ClientService clientService;
    ContactService contactService;
    AIService aiService;
    Gson gson;

    @Value("${fashion.anonimous}")
    Boolean anonimousSending;

    public String createTransaction(Client sender, Client receiver, BigDecimal amount, BlockchainTransactionDTO blockchainTransaction) {
        TransactionCoins transactionCoins = null;
        if (!anonimousSending || receiver != null) {
            transactionCoins = new TransactionCoins(sender, receiver, amount);
        }

        String txhash = blockchainService.sendTransaction(blockchainTransaction);
        if ( transactionCoins != null && txhash != null && txhash.length() > 0) {
            transactionCoins.setTxhash(txhash);
            List<TransactionCoins> list = transactionRepository.findAllByTxhash(txhash);
            if (list != null && list.size() > 0) return txhash;
            transactionRepository.save(transactionCoins);
        }

        return txhash;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

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
    public void setContactService(ContactService contactService) {
        this.contactService = contactService;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    public ResultDTO send(TransactionRequestDTO request) {
        try {
            if (request.getSenderWallet() == null) {
                logger.error("request.getSenderWallet():{}",request.getSenderWallet());
                return error200;
            }
            Client sender = clientService.findByWallet(request.getSenderWallet());
            if (sender == null){
                logger.error("Sender: ",sender);
                return error201;
            }
            if(sender.isBanned()){
                logger.error("Sender banned: {}",sender.isBanned());
                return error122;
            }
            BigDecimal amount = new BigDecimal(request.getAmount());
            clientService.updateBalance(sender);
            if (sender.getWalletBalance().compareTo(amount) < 0){
                logger.error("sender.getWalletBalance(): {}",sender.getWalletBalance());
                logger.error("Amount: {}",amount);
                return error202;
            }

            Client receiver = null;
            String receiverWallet = request.getReceiverWallet();
            if (request.getReceiverWallet() != null) {
                receiver = clientService.findByWallet(request.getReceiverWallet());
            } else if (request.getReceiverLogin() != null) {
                receiver = clientService.findByCryptoname(request.getReceiverLogin());
            } else {
                logger.error("request.getReceiverWallet(): {}",request.getReceiverWallet());
                logger.error("request.getReceiverLogin(): {}",request.getReceiverLogin());
                return error203;
            }

            /// For anonimous wallet disable:
            if (!anonimousSending && receiver == null) {
                logger.error("anonimousSending: {}",anonimousSending);
                logger.error("receiver: {}",receiver);
                return error203;
            }
            ///


            if (receiver != null && !receiver.getWalletAddress().equals(receiverWallet)) {
                logger.error("Receiver: {}",gson.toJson( receiver));
//                logger.error("receiver wallet: " + receiver.getWalletAddress());
//                logger.error("request wallet: " + request.getReceiverWallet());
                return error203;
            }

            if(receiver != null && receiver.isBanned()) {
                logger.error("Receiver: {}",gson.toJson(receiver));
                return error122;
            }

            if (request.getBlockchainTransaction() == null){
                logger.error("request.getBlockchainTransaction(): {}",request.getBlockchainTransaction());
                return error204;
            }
            if (!checkTransaction(sender.getWalletAddress(), request.getReceiverWallet(), amount, request.getBlockchainTransaction())){
                return error206;
            }

            String txhash = createTransaction(sender, receiver, amount, request.getBlockchainTransaction());
            if (txhash == null || txhash.length() == 0) {
                logger.error("TxHash: {}",txhash);
                return error205;
            }
            clientService.addAmountToWallet(sender, amount.negate());
            if (receiver != null) {
                clientService.addAmountToWallet(receiver, amount);
                contactService.connectFriends(sender, receiver);
            }
//            return created;
            return new ResultDTO(true,txhash,0);
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private boolean checkTransaction(String senderWallet, String receiverWallet, BigDecimal amount, BlockchainTransactionDTO blockchainTransaction) {

    logger.info(senderWallet);
    logger.info(receiverWallet);
    logger.info(amount.toString());
    logger.info(gson.toJson(blockchainTransaction));

        if (blockchainTransaction.getSignature() == null){
            logger.error("blockchainTransaction.getSignature() == null");
            return false;
        }
        if (!blockchainTransaction.getBody().getFrom().equals(senderWallet)){
            logger.error("!blockchainTransaction.getBody().getFrom().equals(senderWallet)");
            return false;
        }
        if (receiverWallet != null) {
            logger.info("receiverWallet != null");
            if (!blockchainTransaction.getBody().getTo().equals(receiverWallet)){
                logger.error("!blockchainTransaction.getBody().getTo().equals(receiverWallet)");
                return false;
            }
        }
        if (new BigDecimal(blockchainTransaction.getBody().getAmount()).compareTo(amount.movePointRight(3)) != 0){
            logger.error("new BigDecimal(blockchainTransaction.getBody().getAmount()).compareTo(amount.movePointRight(3)) != 0");
            return false;
        }

        return true;
    }


    private static final ResultDTO created = new ResultDTO(true, "Transaction sended", 0);

    public static final String COIN_SCALE = "1000";

    public List<TransactionDTO> getList(TransactionListRequestDTO request) {
        try {
            logger.info(gson.toJson(request));
            Client client = null;
            if (request.getLogin() != null) {
                client = clientService.findByCryptoname(request.getLogin().trim());
                if (client == null) return new ArrayList<>();
            } else {
                logger.error("Login is empty: " + gson.toJson(request));
                return new ArrayList<>();
            }

            if (!client.getApikey().equals(request.getApikey())) return new ArrayList<>();
            List<TransactionDTO> allTransaction = new ArrayList<>();

            // TODO: rewrite
        /*
        List<TransactionCoins> sended = transactionRepository.findAllBySender(client);
        if (sended != null && !sended.isEmpty()) allTransaction.addAll(adaptToDTO(sended, true));
        List<TransactionCoins> received = transactionRepository.findAllByReceiver(client);
        if (received != null && !received.isEmpty()) allTransaction.addAll(adaptToDTO(received, false));
        //
         */

            List<FshnHistoryTxDTO> fshnHistoryTxList = blockchainService.getHistory(client.getWalletAddress());
            for (FshnHistoryTxDTO fshnHistoryTx : fshnHistoryTxList) {
                TransactionDTO transactionDTO = new TransactionDTO();
                if (fshnHistoryTx.from.equals(client.getWalletAddress())) {
                    transactionDTO.setSender(client.getCryptoname());
                    String contragent = clientService.getClientByWallet(fshnHistoryTx.to);
                    if (contragent == null || contragent.length() == 0) {
                        contragent = fshnHistoryTx.to;
                    }
                    transactionDTO.setReceiver(contragent);
                } else {
                    transactionDTO.setReceiver(client.getCryptoname());
                    String contragent = clientService.getClientByWallet(fshnHistoryTx.from);
                    if (contragent == null || contragent.length() == 0) {
                        contragent = fshnHistoryTx.from;
                    }
                    transactionDTO.setSender(contragent);
                }
                BigDecimal amount = (new BigDecimal(fshnHistoryTx.amount)).movePointLeft(3);
                transactionDTO.setAmount(amount);
                Long timestamp = Long.parseLong(fshnHistoryTx.time.secs) * 1000 + (fshnHistoryTx.time.nanos / 100000);
                transactionDTO.setTimestamp(timestamp);
                transactionDTO.setTxhash("");
                allTransaction.add(transactionDTO);
            }
            Collections.sort(allTransaction);
            return allTransaction;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());

        }
        return new ArrayList<>();
    }

    private List<TransactionDTO> adaptToDTO(List<TransactionCoins> transactionList, boolean isSended) {
        List<TransactionDTO> result = new ArrayList<>();
        for (TransactionCoins tx : transactionList) {
            result.add(new TransactionDTO(tx.getTimestamp(), tx.getSender().getCryptoname(), tx.getReceiver().getCryptoname(),
                    isSended ? tx.getAmount().negate() : tx.getAmount(),
                    tx.getTxhash()));
        }
        return result;
    }


    List<AILefttransactionDTO> resultHistory = new ArrayList<>();

    public List<AILefttransactionDTO> getAiTransactions() {
        return resultHistory;
    }


    public String prepareAiTransactions(AiPrepareDTO params) {
        logger.info("Prepare in");
        logger.info(gson.toJson(params));
        new Thread(() -> {
            try {

                String wallet = params.getWallet();
                if (wallet == null || wallet.length() == 0) {
                    Client client = clientService.findByCryptoname(params.getCryptoname());
                    if (client != null) {
                        wallet = client.getWalletAddress();
                    } else {
                        return;
                    }
                }

                resultHistory = new ArrayList<>();
                logger.info("Prepare start new Thread");
                List<FshnHistoryTxDTO> history = blockchainService.getHistory(wallet);
                logger.info("Geted history. Size: " + history.size());
                for (FshnHistoryTxDTO fshnHistoryTx : history) {

                    Long txTime = Long.parseLong(fshnHistoryTx.time.secs);

                    if (txTime > params.getStart() && txTime < params.getEnd()) {
                        AILefttransactionDTO aiLefttransactionDTO = new AILefttransactionDTO();
                        BigDecimal amount = new BigDecimal(fshnHistoryTx.amount).movePointLeft(3);
                        LocalDateTime time = LocalDateTime.ofEpochSecond(txTime, 0, ZoneOffset.UTC);
                        String from = clientService.getClientByWallet(fshnHistoryTx.from);
                        if (from == null) from = fshnHistoryTx.from;
                        String to = clientService.getClientByWallet(fshnHistoryTx.to);
                        if (to == null) to = fshnHistoryTx.to;
                        aiLefttransactionDTO.setAmount(amount);
                        aiLefttransactionDTO.setFrom(from);
                        aiLefttransactionDTO.setTo(to);
                        aiLefttransactionDTO.setTime(time);
                        resultHistory.add(aiLefttransactionDTO);
                    }
                }
                logger.info("Result size: " + resultHistory.size());

                logger.info("Prepare end");
            } catch (Exception e) {
                logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }).start();

        return "Ok";
    }

    public TransactionCoins getTransactionCoins(String txhash) {
        TransactionCoins transactionCoins = transactionRepository.findTopByTxhash(txhash);
        if(transactionCoins==null){
            logger.error("Tx Hash: {}",txhash);
            logger.error("Transaction: {}",transactionCoins);
        }
        return transactionCoins;
    }
}
