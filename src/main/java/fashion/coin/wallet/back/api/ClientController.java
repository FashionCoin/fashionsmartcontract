package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class ClientController {

    ClientService clientService;

    @PostMapping("/api/v1/signup")
    @ResponseBody
    ResultDTO registration(@RequestBody RegistrationRequestDTO data){
       return clientService.trySignUp(data);
    }

    @PostMapping("/api/v1/checklogin")
    @ResponseBody
    ResultDTO checkLogin(@RequestBody CheckLoginDTO data){
        return clientService.checkLogin(data);
    }


    @PostMapping("/api/v1/getwallet")
    @ResponseBody
    ResultDTO getWallet(@RequestBody GetWalletDTO data){
        return clientService.getWallet(data);
    }



    @PostMapping("/api/v1/getlogin")
    @ResponseBody
    ResultDTO getWallet(@RequestBody GetLoginDTO data){
        return clientService.getLogin(data);
    }


    @PostMapping("/api/v1/clientinfo")
    @ResponseBody
    Object getСlientInfo(@RequestBody CheckEmailDTO data){
        return clientService.getClientInfo(data);
    }


    @PostMapping("/api/v1/changeinfo")
    @ResponseBody
    Object changeInfo(@RequestBody ClientInfoDTO clientInfo){
        return clientService.changeClientInfo(clientInfo);
    }

    @PostMapping("/api/v1/registercryptoname")
    @ResponseBody
    ResultDTO registerCryptoname(@RequestBody  CryptonameEmailDTO data){
        return clientService.registerCryptoname( data);
    }


    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
