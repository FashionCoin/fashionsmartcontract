package fashion.coin.wallet.back.fwrap.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionDTO;
import fashion.coin.wallet.back.dto.TransactionListRequestDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.fwrap.dto.FCurrencyRequestDTO;
import fashion.coin.wallet.back.fwrap.dto.FSendMoneyRequestDTO;
import fashion.coin.wallet.back.fwrap.dto.FTransactionResponseDTO;
import fashion.coin.wallet.back.fwrap.dto.FWalletsResponseDTO;
import fashion.coin.wallet.back.fwrap.entity.FExchange;
import fashion.coin.wallet.back.fwrap.entity.FTransaction;
import fashion.coin.wallet.back.fwrap.entity.FWallet;
import fashion.coin.wallet.back.fwrap.repository.FExchangeRepository;
import fashion.coin.wallet.back.fwrap.repository.FTransactionRepository;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class FTransactionService {

    @Autowired
    FTransactionRepository fTransactionRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    FWalletService fWalletService;

    @Autowired
    FExchangeService fExchangeService;

    public ResultDTO getByCurrency(FCurrencyRequestDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            if (request.getCurrency().equals("FSHN")) {
                TransactionListRequestDTO txRequest = new TransactionListRequestDTO();
                txRequest.setApikey(client.getApikey());
                txRequest.setLogin(client.getCryptoname());
                List<TransactionDTO> txList = transactionService.getList(txRequest);
                List<FTransactionResponseDTO> fTransactionList = new ArrayList<>();
                if (txList == null || txList.size() == 0) {
                    return new ResultDTO(true, fTransactionList, 0);
                }

                for (TransactionDTO transaction : txList) {
                    FTransactionResponseDTO transactionRespons = new FTransactionResponseDTO();
                    transactionRespons.setId(transaction.getTimestamp());
                    transactionRespons.setTimestamp(transaction.getTimestamp());
                    if (transaction.getReceiver().equals(client.getCryptoname())) {
                        transactionRespons.setIncome(true);
                        transactionRespons.setAmount(transaction.getAmount());
                        transactionRespons.setCryptoname(transaction.getSender());
                    } else {
                        transactionRespons.setIncome(false);
                        transactionRespons.setAmount(transaction.getAmount().negate());
                        transactionRespons.setCryptoname(transaction.getReceiver());
                    }
                    fTransactionList.add(transactionRespons);
                }
                return new ResultDTO(true, fTransactionList, 0);

            } else {
                List<FTransaction> transactionList = fTransactionRepository.findByFromIdOrToId(client.getId(), client.getId());

                List<FTransactionResponseDTO> fTransactionList = new ArrayList<>();
                if (transactionList == null || transactionList.size() == 0) {
                    return new ResultDTO(true, fTransactionList, 0);
                }

                for (FTransaction transaction : transactionList) {
                    if (transaction.getCurrency().equals(request.getCurrency())) {
                        FTransactionResponseDTO transactionRespons = new FTransactionResponseDTO();
                        transactionRespons.setId(transaction.getId());
                        transactionRespons.setTimestamp(transaction.getTimestamp());
                        if (transaction.getToId().equals(client.getId())) {
                            transactionRespons.setIncome(true);
                            transactionRespons.setAmount(transaction.getAmount());
                            transactionRespons.setCryptoname(transaction.getFromCryptoname());
                        } else {
                            transactionRespons.setIncome(false);
                            transactionRespons.setAmount(transaction.getAmount().negate());
                            transactionRespons.setCryptoname(transaction.getToCryptoname());
                        }
                        fTransactionList.add(transactionRespons);
                    }
                }

                List<FExchange> exchangeList = fExchangeService.findByClientIdAndCurrency(client.getId(),request.getCurrency());
                if(exchangeList!= null && exchangeList.size()>0){
                    for(FExchange fExchange : exchangeList){
                        FTransactionResponseDTO transactionRespons = new FTransactionResponseDTO();
                        transactionRespons.setId(fExchange.getId());
                        transactionRespons.setTimestamp(fExchange.getTimstamp());
                        if (fExchange.getCurrencyTo().equals(request.getCurrency())) {
                            transactionRespons.setIncome(true);
                            transactionRespons.setAmount(fExchange.getValueTo());
                            transactionRespons.setCryptoname(fExchange.getCurrencyFrom());
                        } else {
                            transactionRespons.setIncome(false);
                            transactionRespons.setAmount(fExchange.getValueFrom().negate());
                            transactionRespons.setCryptoname(fExchange.getCurrencyTo());
                        }
                        fTransactionList.add(transactionRespons);
                    }
                }

                fTransactionList.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
                return new ResultDTO(true, fTransactionList, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO sendMoney(FSendMoneyRequestDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            if (request.getCurrency().equals("FSHN")) {
                return new ResultDTO(false, "This API not for FSHN. Use /api/v1/send with blockchain transaction", -1);
            }

            Client receiver = clientService.findByCryptonameOrWallet(request.getReceiver());
            if (receiver == null) {
                return error112;
            }

            if (fWalletService.getBalance(client, request.getCurrency()).compareTo(request.getAmount()) < 0) {
                return error202;
            }
           fWalletService.sendMoney(client, receiver, request.getCurrency(), request.getAmount()) ;

                FTransaction fTransaction = new FTransaction();
                fTransaction.setTimestamp(System.currentTimeMillis());
                fTransaction.setCurrency(request.getCurrency());
                fTransaction.setFromId(client.getId());
                fTransaction.setFromCryptoname(client.getCryptoname());
                fTransaction.setToId(receiver.getId());
                fTransaction.setToCryptoname(receiver.getCryptoname());
                fTransaction.setAmount(request.getAmount());

                fTransactionRepository.save(fTransaction);


                return new ResultDTO(true, fTransaction, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
