package fashion.coin.wallet.back.latoken;

import com.google.gson.Gson;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.SetEmailRequest;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.repository.SetEmailRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LatokenService {

    ClientService clientService;


    Gson gson;

    public LatokenResponceDTO checkEMail(LatokenRequestDTO data) {

        Client client = clientService.findClientByEmail(data.getEmail().toLowerCase());
        if(client != null ){
            return new LatokenResponceDTO(client.getEmail(),1);
        }else{
            return new LatokenResponceDTO(data.getEmail(),0);
        }
    }


    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

}
