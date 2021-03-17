package fashion.coin.wallet.back.fwrap.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.fwrap.dto.FWrapRequestDTO;
import fashion.coin.wallet.back.fwrap.entity.FExchange;
import fashion.coin.wallet.back.fwrap.entity.FWallet;
import fashion.coin.wallet.back.fwrap.repository.FExchangeRepository;
import fashion.coin.wallet.back.fwrap.repository.FTransactionRepository;
import fashion.coin.wallet.back.fwrap.repository.FWalletRepository;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.CurrencyService;
import fashion.coin.wallet.back.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class FExchangeService {

    Logger logger = LoggerFactory.getLogger(FExchangeService.class);

    @Autowired
    FTransactionRepository fTransactionRepository;

    @Autowired
    FWalletService fWalletService;

    @Autowired
    ClientService clientService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    AIService aiService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    FExchangeRepository fExchangeRepository;

    public static final String FEE = "0.02";

    public ResultDTO wrapCurrency(FWrapRequestDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            ResultDTO resultDTO = checkTransaction(request.getTransactionRequest());
            if (!resultDTO.isResult()) {
                return resultDTO;
            }
            BigDecimal fshn = BigDecimal.ZERO;
            if (resultDTO.getData() instanceof BigDecimal) {
                fshn = (BigDecimal) resultDTO.getData();
            } else {
                return error109;
            }
            BigDecimal rate = currencyService.getLastCurrencyRate(request.getCurrency());
            logger.info("Rate: {}", rate);

            BigDecimal totalAmount = fshn.divide(rate, 2, RoundingMode.HALF_UP);

            BigDecimal receive = totalAmount.multiply(BigDecimal.ONE.subtract(new BigDecimal(FEE))).setScale(2);
            BigDecimal fee = totalAmount.subtract(receive);

            resultDTO = transactionService.send(request.getTransactionRequest());
            if (!resultDTO.isResult()) {
                return resultDTO;
            }

            FExchange fExchange = new FExchange();
            fExchange.setTimstamp(System.currentTimeMillis());
            fExchange.setClientId(client.getId());
            fExchange.setCryptoname(client.getCryptoname());
            fExchange.setCurrencyFrom("FSHN");
            fExchange.setCurrencyTo(request.getCurrency());
            fExchange.setTxHash(resultDTO.getMessage());
            fExchange.setValueFrom(fshn);
            fExchange.setValueTo(receive);
            fExchange.setFee(fee);

            fExchangeRepository.save(fExchange);


            fWalletService.changeAmount(client, request.getCurrency(), receive);

            return new ResultDTO(true, fExchange, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private ResultDTO checkTransaction(TransactionRequestDTO request) {

        BigDecimal transactionAmount = new BigDecimal(request.getBlockchainTransaction()
                .getBody().getAmount()).movePointLeft(3);

        if (transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
            return error205;
        }

        if (!request.getBlockchainTransaction().getBody()
                .getTo().equals(aiService.getPubKey(AIService.AIWallets.MONEYBAG))) {
            return error205;
        }

        return new ResultDTO(true, transactionAmount, 0);
    }
}
