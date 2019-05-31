package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    EmailService emailService;

    @Autowired
    ClientService clientService;

    @Value("${fashion.manager.email}")
    String emaiManager;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");

    //    @Scheduled(cron = "0 0 6 * * *")
    @Scheduled(cron = "0 */10 * * * *")  // 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
    public void sendRaport() {


        LocalDateTime now = LocalDateTime.now();

        LocalDateTime yesterday = now.minusDays(1L);

        LocalDateTime start = yesterday.minusHours(6L);
        LocalDateTime end = start.plusDays(1L);


        List<Client> clientList = clientService.findByCreateTimeBetween(start, end);

        int telegram = 0;
        int web = 0;

        for (Client client : clientList) {
            if (client.getTelegramId() != null && client.getTelegramId() != 0) telegram++;
            else web++;
        }


        emailService.sendMail(emaiManager, "Статистика Crypto Name: за "+yesterday.format(formatter)+"  создано " + telegram + web + " CN ",
                "Телеграм: " + telegram + " CN <br>" +
                        "Caйт:    " + web + " CN ");

    }

}
