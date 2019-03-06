package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.CheckEmailDTO;
import fashion.coin.wallet.back.dto.RegistrationRequestDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.SetEmailRequestDTO;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class EmailController {

    ClientService clientService;

    @PostMapping("/api/v1/setemail")
    @ResponseBody
    ResultDTO setEmail(@RequestBody SetEmailRequestDTO data) {
        return clientService.trySetEmail(data);
    }

    @PostMapping("/api/v1/checkemail")
    @ResponseBody
    ResultDTO checkEmail(@RequestBody CheckEmailDTO data) {
        return clientService.checkEmail(data);
    }

    @GetMapping("/confirmemail")
    String confirmMail(@RequestParam String token) {
        if (clientService.checkEmailToken(token)) return "redirect:/emailconfirmed";
        else return "redirect:/emailerror";
    }

    @GetMapping("/emailconfirmed")
    String emailConfirmed() {
        return "emailconfirmed";

    }

    @GetMapping("/emailerror")
    String emailError() {
        return "emailerror";
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
