package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.nft.service.NftService;
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
    ClientService clientService;

    AIService aiService;

    FileUploadService fileUploadService;

    NftService nftService;

    @PostConstruct
    public void init() {
        emailService.sendMail("tech@coin.fashion", "FC Wallet", "Сервер FC Wallet только что запустился");
//        aiService.printTransferTransaction("b1803b8a0c196b7f3d433c9976bb3a328982b734f4be701a12065f1e8321827b", "100.000");
//aiService.printCreateWalletTransaction();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//               fileUploadService.convertAllFiles();
//            }
//        }).start();

        nftService.init();

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

    @Autowired
    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    public void setNftService(NftService nftService) {
        this.nftService = nftService;
    }
}
