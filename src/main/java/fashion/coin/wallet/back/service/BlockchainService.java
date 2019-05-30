package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.blockchain.ResponceDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class BlockchainService {

    private RestTemplate restTemplate;
    private Gson gson;

    @Value("${fashion.blockchain.server}")
    public static String BLOCKCHAIN_API_URI;

    public String sendTransaction(BlockchainTransactionDTO blockchainTransaction) {
        try {

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BlockchainTransactionDTO> request = new HttpEntity<>(blockchainTransaction, headers);
            System.out.println(gson.toJson( blockchainTransaction));
            ResponseEntity<ResponceDTO> responce = restTemplate.postForEntity(BLOCKCHAIN_API_URI, request, ResponceDTO.class);
            if (!responce.hasBody()) return "";
            ResponceDTO responceBody = responce.getBody();
            System.out.println(responceBody);
            return responceBody.getTx_hash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
