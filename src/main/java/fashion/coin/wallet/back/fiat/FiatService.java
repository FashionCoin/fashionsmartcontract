package fashion.coin.wallet.back.fiat;

import com.google.gson.Gson;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiatService {

    ClientRepository clientRepository;

    Gson gson;

    public CheckPhoneResponceDTO checkPhone(CheckPhoneRequestDTO data) {
        try {
            if (data == null) return new CheckPhoneResponceDTO(false, null);
            if (data.getPhone().charAt(0) != '+') return new CheckPhoneResponceDTO(false, null);

            List<Client> clientList = clientRepository.findClientsByPhoneEndingWith(data.getPhone().substring(1));
            if (clientList != null && clientList.size() == 1) {
                return new CheckPhoneResponceDTO(true, clientList.get(0).getLogin());
            }else if (clientList != null){
                System.out.println("clientList.size() = " + clientList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CheckPhoneResponceDTO(false, null);
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
