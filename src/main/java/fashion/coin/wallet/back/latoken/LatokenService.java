package fashion.coin.wallet.back.latoken;

import com.google.gson.Gson;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.SetEmailRequest;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.repository.SetEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LatokenService {

    ClientRepository clientRepository;
    SetEmailRepository setEmailRepository;

    Gson gson;

    public LatokenResponceDTO checkEMail(LatokenRequestDTO data) {

        List<Client> clientList = clientRepository.findAll();
        for(Client cl : clientList){
            System.out.println(gson.toJson(cl));
        }

        List<SetEmailRequest> setEmailRequestList = setEmailRepository.findAll();
        for(SetEmailRequest setEmailRequest : setEmailRequestList){
            System.out.println(gson.toJson(setEmailRequest));
        }

        Client client = clientRepository.findClientByEmail(data.getEmail().toLowerCase());
        if(client != null ){
            return new LatokenResponceDTO(client.getEmail(),1);
        }else{
            return new LatokenResponceDTO(data.getEmail(),0);
        }
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setSetEmailRepository(SetEmailRepository setEmailRepository) {
        this.setEmailRepository = setEmailRepository;
    }
}
