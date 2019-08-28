package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.blockchain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    private RestTemplate restTemplate;
    private Gson gson;

    private AIService aiService;

    @Value("${fashion.blockchain.server}")
    public String BLOCKCHAIN_API_URI;

    public String sendTransaction(BlockchainTransactionDTO blockchainTransaction) {
        try {
            System.out.println("url: " + BLOCKCHAIN_API_URI);
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<BlockchainTransactionDTO> request = new HttpEntity<>(blockchainTransaction, headers);
            System.out.println(gson.toJson(blockchainTransaction));
            ResponseEntity<ResponceDTO> responce = restTemplate.postForEntity(BLOCKCHAIN_API_URI + "/wallets/transaction", request, ResponceDTO.class);
            if (!responce.hasBody()) return "";
            ResponceDTO responceBody = responce.getBody();
            System.out.println(responceBody);
            return responceBody.getTx_hash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<FshnHistoryTxDTO> getHistory(String walletAddress) {
        try {
            ResponseEntity<FshnHistoryDTO> responce = restTemplate.getForEntity(BLOCKCHAIN_API_URI + "/wallets/history?pub_key=" + walletAddress,
                    FshnHistoryDTO.class);
            if (responce == null || !responce.hasBody()) return new ArrayList<>();
            FshnHistoryDTO fshnHistory = responce.getBody();
            if (fshnHistory == null || fshnHistory.getResult() == null) return new ArrayList<>();
            List<FshnHistoryTxDTO> result = fshnHistory.getResult();
            return result;
        } catch (Exception e) {
            System.out.println("Don't get history for " + walletAddress);
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }

    public BigDecimal getBalance(String walletAddress, String cryptoname) {
        try {
            logger.info("walletAddress: :"+walletAddress+ "   cryptoname: "+ cryptoname);
            FshnBalanceDTO balanceDTO = getWalletInfo(walletAddress);
            logger.info("getWallet: "+ gson.toJson(balanceDTO));
            if (balanceDTO == null) return BigDecimal.ZERO;
            if(balanceDTO.getName_hash().equals("0000000000000000000000000000000000000000000000000000000000000000")){
                logger.info(gson.toJson(balanceDTO));
                aiService.cryptoname(cryptoname,"",walletAddress);
            }
            logger.info(gson.toJson(balanceDTO));
            String balanceString = balanceDTO.balance;
            return (new BigDecimal(balanceString)).movePointLeft(3);
        } catch (Exception e) {
            logger.error("Don't get balance for " + walletAddress);
            logger.error(e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    public FshnBalanceDTO getWalletInfo(String walletAddress) {
        try {
            logger.info(walletAddress);
            ResponseEntity<FshnBalanceInfoDTO> responce = restTemplate.getForEntity(BLOCKCHAIN_API_URI + "/wallets/info?pub_key=" + walletAddress,
                    FshnBalanceInfoDTO.class);
            if (responce == null || !responce.hasBody()) return null;
            logger.info(gson.toJson(responce));

            FshnBalanceInfoDTO balanceInfo = responce.getBody();
            FshnBalanceDTO balanceDTO = balanceInfo.getResult();
            return balanceDTO;
        } catch (Exception e) {
            System.out.println("Don't get wallet info for " + walletAddress);
            System.out.println(e.getMessage());
        }
        return null;
    }
}
