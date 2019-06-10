package fashion.coin.wallet.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    AIService aiService;

    @PostConstruct
    public void init() {
        emailService.sendMail("tech@coin.fashion", "FC Wallet", "Сервер FC Wallet только что запустился");
        aiService.printTransaction("54d55f28e78e2a73ef1e1084c9b800cdd1cf2b8cab0e8e019dd889952925fd7f", "10000000.000");

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
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }
}
