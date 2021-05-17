package fashion.coin.wallet.back.messenger.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.messenger.dto.ChatListRequestDTO;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

@Service
public class ChatListService {

    Logger logger = LoggerFactory.getLogger(ChatListService.class);

    @Autowired
    Gson gson;

    @Autowired
    ClientService clientService;

    public ResultDTO chatList(ChatListRequestDTO request) {
        try {

            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            return new ResultDTO();

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
