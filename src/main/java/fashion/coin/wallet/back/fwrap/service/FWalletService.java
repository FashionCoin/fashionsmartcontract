package fashion.coin.wallet.back.fwrap.service;

import fashion.coin.wallet.back.dto.ApiKeyDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.fwrap.dto.FWalletsResponseDTO;
import fashion.coin.wallet.back.fwrap.entity.FWallet;
import fashion.coin.wallet.back.fwrap.repository.FWalletRepository;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

@Service
public class FWalletService {

    Logger logger = LoggerFactory.getLogger(FWalletService.class);

    @Autowired
    ClientService clientService;

    @Autowired
    FWalletRepository fWalletRepository;

    @Autowired
    CurrencyService currencyService;


    List<String> currencyList = Arrays.asList("FSHN", "FUSD", "FEUR", "FGBP", "FBTC", "FETH");

    public ResultDTO getClientWallets(ApiKeyDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            List<FWallet> walletList = fWalletRepository.findByClientId(client.getId());
            List<FWalletsResponseDTO> fWalletsResponseList = new ArrayList<>();
            for (String currency : currencyList) {
                FWalletsResponseDTO fWalletsResponse = new FWalletsResponseDTO();
                fWalletsResponse.setCurrency(currency);

                if (currency.equals("FSHN")) {
                    client = clientService.updateBalance(client);
                    fWalletsResponse.setAmount(client.getWalletBalance());
                    BigDecimal lastRate = currencyService.getLastCurrencyRate("USD");
                    BigDecimal usd = client.getWalletBalance().divide(lastRate, 2, RoundingMode.HALF_UP);
                    fWalletsResponse.setApproximateСost("~ " + usd + " USD");
                } else {
                    fWalletsResponse.setAmount(BigDecimal.ZERO);
                    fWalletsResponse.setApproximateСost("~ 0 FSHN");
                    if (walletList != null || walletList.size() > 0) {

                        for (FWallet fWallet : walletList) {
                            if (fWallet.getCurrency().equals(currency)) {
                                fWalletsResponse.setAmount(fWallet.getBalance());
                                fWalletsResponse.setApproximateСost(getAproximateFSHN(fWallet));
                            }
                        }
                    }
                }
                fWalletsResponseList.add(fWalletsResponse);
            }

            return new ResultDTO(true, fWalletsResponseList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private String getAproximateFSHN(FWallet fWallet) {

        BigDecimal lastRate = currencyService.getLastCurrencyRate(fWallet.getCurrency());
        if (lastRate == null) {
            return "~ 0 FSHN";
        } else {
            BigDecimal fshn = fWallet.getBalance().multiply(lastRate).setScale(0, RoundingMode.HALF_UP);
            return "~ " + fshn + " FSHN";
        }
    }

    public ResultDTO getCurrencyList() {
        return new ResultDTO(true, currencyList, 0);
    }

    public BigDecimal getBalance(Client client, String currency) {
        return getWallet(client, currency).getBalance();
    }

    public FWallet getWallet(Client client, String currency) {
        List<FWallet> fWalletList = fWalletRepository.findByClientIdAndCurrency(client.getId(), currency);

        if (fWalletList == null || fWalletList.size() == 0) {
            FWallet fWallet = new FWallet();
            fWallet.setClientId(client.getId());
            fWallet.setCryptoname(client.getCryptoname());
            fWallet.setCurrency(currency);
            fWallet.setBalance(BigDecimal.ZERO);
            fWalletRepository.save(fWallet);
            return fWallet;
        }

        return fWalletList.get(0);
    }


    public boolean sendMoney(Client sender, Client receiver, String currency, BigDecimal amount) {

        FWallet senderWallet = getWallet(sender, currency);
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        fWalletRepository.save(senderWallet);

        FWallet receiverWallet = getWallet(receiver, currency);
        receiverWallet.setBalance(receiver.getWalletBalance().add(amount));
        fWalletRepository.save(receiverWallet);
        return true;
    }

    public boolean changeAmount(Client client, String currency, BigDecimal amount) {
        try {
            FWallet fWallet = getWallet(client, currency);
            fWallet.setBalance(fWallet.getBalance().add(amount));
            fWalletRepository.save(fWallet);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
