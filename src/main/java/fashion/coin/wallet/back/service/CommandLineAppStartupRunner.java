package fashion.coin.wallet.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private AESEncriptor aesEncriptor;


    @Override
    public void run(String... args) throws Exception {

        try {
            String key = args[0];
            String vi = args[1];
            aesEncriptor.setKey(key,vi);
        }catch (Exception e){
            System.out.println("You need to specify the key and initialization vector in the command line parameters");
            throw e;
        }
    }

    @Autowired
    public void setAesEncriptor(AESEncriptor aesEncriptor) {
        System.out.println("aesEncriptor autowired");
        this.aesEncriptor = aesEncriptor;
    }
}
