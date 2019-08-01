package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.AddWalletDTO;
import fashion.coin.wallet.back.dto.GetNameByWalletDTO;
import fashion.coin.wallet.back.dto.GetWalletsByNameDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.CryptoWallets;
import fashion.coin.wallet.back.service.CryptoWalletsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller
public class CryptoWalletsController {

    @Autowired
    CryptoWalletsService cryptoWalletsService;

    @PostMapping("/api/v1/addwallet")
    @ResponseBody
    ResultDTO addWallet(@RequestBody AddWalletDTO data) {
        try {
            return cryptoWalletsService.saveWallet(data);

        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    @PostMapping("/api/v1/getwalletsbyname")
    @ResponseBody
    List<CryptoWallets> getWallets(@RequestBody GetWalletsByNameDTO data) {
        try {
            return cryptoWalletsService.getWallets(data);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @PostMapping("/api/v1/getnamebywallet")
    @ResponseBody
    ResultDTO getCryptoname(@RequestBody GetNameByWalletDTO data) {
        try {
            return cryptoWalletsService.getCryptoname(data);

        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


}
