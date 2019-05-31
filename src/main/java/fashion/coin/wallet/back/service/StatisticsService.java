package fashion.coin.wallet.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    @Autowired
    EmailService emailService;

    @Value("${fashion.manager.email}")
    String emaiManager;


    //    @Scheduled(cron = "0 0 6 * * *")
    @Scheduled(cron = "0 30 * * * *")  // 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
    public void sendMail() {


        emailService.sendMail(emaiManager, "Статистика Crypto Name: за 28.05.19  создано {250}  CN ",
                "Телеграм: 200 CN \n" +
                        "Caйт:     50 CN ");

    }

}
