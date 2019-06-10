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
        aiService.printTransaction("ec4fb739d9f85bf66765a9009cacb3b32bd0f8b037b6ecca498939341898d36b", "248810000.000");

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
