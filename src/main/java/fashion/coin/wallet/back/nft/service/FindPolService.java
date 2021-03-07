package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.FindNameRequestDTO;
import fashion.coin.wallet.back.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error125;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error127;

@Service
public class FindPolService {

    @Autowired
    ClientRepository clientRepository;


    public ResultDTO byName(FindNameRequestDTO request) {
        try {

            if (request == null || request.getName() == null) {
                return error127;
            }
            List<Client> clientList = clientRepository.findNameContains(request.getName());
            if (clientList == null || clientList.size() == 0) {
                return new ResultDTO(true,new ArrayList<>(),0);
            }

            List<Client> result = new ArrayList<>();
            for (Client c : clientList) {
                Client cl = new Client();
                cl.setId(c.getId());
                cl.setCryptoname(c.getCryptoname());
                if (c.avaExists()) {
                    cl.setAvatar(c.getAvatar());
                }
                cl.setWalletBalance(c.getWalletBalance());
                result.add(cl);
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
