package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.AddWalletDTO;
import fashion.coin.wallet.back.dto.GetNameByWalletDTO;
import fashion.coin.wallet.back.dto.GetWalletsByNameDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.CryptoWallets;
import fashion.coin.wallet.back.repository.CryptoWalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JAVA-P on 24.07.2019.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class CryptoWalletsService {

    @Autowired
    CryptoWalletsRepository cryptoWalletsRepository;

    @Autowired
    ClientService clientService;

    public String getWalletByCryptoname(String cryptoname, String currency) {
        CryptoWallets cryptoWallets = cryptoWalletsRepository.findTopByCryptonameAndCurrency(cryptoname, currency);
        if (cryptoWallets == null) return "";
        else return cryptoWallets.getWallet();
    }

    public ResultDTO saveWallet(AddWalletDTO data) {

        if (data.getApikey() == null) return ClientService.error107;
        Client client = clientService.findClientByApikey(data.getApikey());
        if (client == null) return ClientService.created;

        cryptoWalletsRepository.save(new CryptoWallets(client.getCryptoname(), data.getCurrency(), data.getWallet()));
        return null;
    }


    public List<CryptoWallets> getWallets(GetWalletsByNameDTO data) {
        List<CryptoWallets> cryptoWallets = cryptoWalletsRepository.findAllByCryptoname(data.getCryptoname());
        if (cryptoWallets == null) return new ArrayList<>();
        return cryptoWallets;

    }

    public ResultDTO getCryptoname(GetNameByWalletDTO data) {

        CryptoWallets cryptoWallets = cryptoWalletsRepository.findTopByCurrencyAndWallet(data.getCurrency(), data.getWallet());
        if (cryptoWallets == null) return new ResultDTO(false, "Cryptoname not found", 523);

        return new ResultDTO(true, cryptoWallets.getCryptoname(), 0);
    }
}
