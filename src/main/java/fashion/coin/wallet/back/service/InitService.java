package fashion.coin.wallet.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class InitService {

    EmailService emailService;
    BlockchainService blockchainService;
    ClientService clientService;

    AIService aiService;

    @PostConstruct
    public void init() {
        emailService.sendMail("tech@coin.fashion", "FC Wallet", "Сервер FC Wallet только что запустился");
//        aiService.printTransaction("c2f7a3c86bb2bb818e6d1e938f60ddd5dadfceaa0c1cd9429884b5e1c5fb9636", "100.000");
        clientService.reserveNames(Arrays.asList("alandamir", "alan", "zyxit", "genesis", "Genesys", "dot", "bot", "good", "site", "dating", "betting", "world", "home", "mars", "war", "helloworld", "code", "blog", "myblog", "health", "www", "wtf", "red", "codemaster", "key", "keymaster", "matrix", "crypto", "coin", "coinmaster", "host", "gate", "law", "love", "exchange", "logic", "global", "one"));
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setBlockchainService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }
}
