package fashion.coin.wallet.back.fwrap.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionDTO;
import fashion.coin.wallet.back.dto.TransactionListRequestDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.fwrap.dto.FCurrencyRequestDTO;
import fashion.coin.wallet.back.fwrap.dto.FTransactionResponseDTO;
import fashion.coin.wallet.back.fwrap.dto.FWalletsResponseDTO;
import fashion.coin.wallet.back.fwrap.entity.FTransaction;
import fashion.coin.wallet.back.fwrap.entity.FWallet;
import fashion.coin.wallet.back.fwrap.repository.FTransactionRepository;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

@Service
public class FTransactionService {

    @Autowired
    FTransactionRepository fTransactionRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    TransactionService transactionService;

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
                    } else {
                        transactionRespons.setIncome(false);
                        transactionRespons.setAmount(transaction.getAmount().negate());
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
                        } else {
                            transactionRespons.setIncome(false);
                            transactionRespons.setAmount(transaction.getAmount().negate());
                        }
                        fTransactionList.add(transactionRespons);
                    }
                }

                return new ResultDTO(true, fTransactionList, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
