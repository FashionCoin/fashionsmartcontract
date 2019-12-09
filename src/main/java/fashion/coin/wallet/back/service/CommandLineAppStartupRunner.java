package fashion.coin.wallet.back.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(CommandLineAppStartupRunner.class);

    private AESEncriptor aesEncriptor;
//    private AIService aiService;


    @Override
    public void run(String... args) throws Exception {

        try {
            String key = args[0];
            String vi = args[1];
            logger.info("aesEncriptor: " + aesEncriptor);
            aesEncriptor.setKey(key, vi);
//            aiService.isReady();
            logger.info("System started. Key setted");
        } catch (Exception e) {
            logger.error("You need to specify the key and initialization vector in the command line parameters");
            throw e;
        }
    }

    @Autowired
    public void setAesEncriptor(AESEncriptor aesEncriptor) {
        System.out.println("aesEncriptor autowired");
        this.aesEncriptor = aesEncriptor;
    }
//
//    @Autowired
//    public void setAiService(AIService aiService) {
//        this.aiService = aiService;
//    }
}
