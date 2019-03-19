package fashion.coin.wallet.back.latoken;

import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LatokenService {

    ClientRepository clientRepository;

    public LatokenResponceDTO checkEMail(LatokenRequestDTO data) {
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
}
