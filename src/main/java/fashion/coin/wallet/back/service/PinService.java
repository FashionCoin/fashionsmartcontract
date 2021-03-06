package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.ChangePinDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;


/**
 * Created by JAVA-P on 01.11.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class PinService {

    ClientService clientService;
    EmailService emailService;

    @Value("${fashion.host}")
    String HOST;

    public ResultDTO restorePin(ChangePinDTO data) {
        try {
            Client client = clientService.findClientByApikey(data.getApikey());
            if (client == null) return error109;
            if (!client.getCryptoname().equals(data.getCryptoname())) return error109;
            if (client.getEmail() == null) {
                return error110;
            }
            if (!client.getEmail().equals(data.getEmail())) {
                if (clientService.checkRequest(data.getEmail())) return error111;
                return error110;
            }
            emailService.sendMail(data.getEmail(), "Fashion Coin: Restore PIN", "For restore PIN click this link:<br/>" +
                            "<a href='"+HOST+"/restorepin?recovery=" + data.getRecovery() + "' >"+HOST+"/restorepin?recovery=" + data.getRecovery() + "</a><br/>"
//                    +"<br/>" +
//                    "Or copy this secret code: "+ data.getRecovery()
            );
            return mailSended;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.toString(), -1);
        }
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    private static final ResultDTO mailSended = new ResultDTO(true, "Mail sended", 0);

}
