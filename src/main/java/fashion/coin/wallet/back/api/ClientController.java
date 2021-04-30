package fashion.coin.wallet.back.api;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.*;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.LogEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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

    @Autowired
    Gson gson;

    Logger logger = LoggerFactory.getLogger(ClientController.class);

    ClientService clientService;

    @Autowired
    LogEventService logEventService;

    @PostMapping("/api/v1/signup")
    @ResponseBody
    ResultDTO registration(@RequestBody RegistrationRequestDTO data, HttpServletRequest request){

        logEventService.save(request, data.getApikey(),data.getCryptoname());

       return clientService.trySignUp(data);
    }


    @PostMapping("/api/v1/signin")
    @ResponseBody
    ResultDTO signIn(@RequestBody SignInDTO data, HttpServletRequest request){

        logEventService.save(request, data.getApikey(),data.getCryptoname());
        return clientService.trySignIn(data);
    }

    @PostMapping("/api/v1/permanent/signin")
    @ResponseBody
    ResultDTO permanentSignIn(@RequestBody SignInDTO data, HttpServletRequest request){

        logEventService.save(request, data.getApikey(),data.getCryptoname());
        return clientService.permanentSignIn(data);
    }


    @PostMapping("/api/v1/checkname")
    @ResponseBody
    ResultDTO checkName(@RequestBody CheckCryptoNameDTO data){
        return clientService.checkName(data);
    }


    @PostMapping("/api/v1/checkclient")
    @ResponseBody
    ResultDTO checkClient(@RequestBody SignInDTO data){
        return clientService.checkClient(data);
    }


    @PostMapping("/api/v1/reservename")
    @ResponseBody
    ResultDTO reserveName(@RequestBody ReserveCryptoNameDTO data){
        return clientService.reserveName(data);
    }


    @PostMapping("/api/v1/getwallet")
    @ResponseBody
    ResultDTO getWallet(@RequestBody GetWalletDTO data){
        return clientService.getWallet(data);
    }


    @PostMapping("/api/v1/getwallets")
    @ResponseBody
    ResultDTO getWallet(@RequestBody Object json){
        GetWalletsDTO data = gson.fromJson(String.valueOf(json),GetWalletsDTO.class);
//        logger.info(gson.toJson(data));
        return clientService.getWallets(data);
    }



    @PostMapping("/api/v1/getlogin")
    @ResponseBody
    ResultDTO getWallet(@RequestBody GetLoginDTO data){
        return clientService.getCryptoname(data);
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
    ResultDTO registerCryptoname(@RequestBody  CryptonameEmailDTO data, HttpServletRequest request){

        logEventService.save(request, null,data.getCryptoname());

        return clientService.registerCryptoname( data);
    }


    @PostMapping("/api/v1/client/about")
    @ResponseBody
    ResultDTO clientAbout(@RequestBody  AboutClientRequestDTO request){
        return clientService.clientAbout( request);
    }




    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
