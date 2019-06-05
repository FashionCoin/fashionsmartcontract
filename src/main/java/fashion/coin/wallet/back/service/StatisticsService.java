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
    //    @Scheduled(cron = "0 */10 * * * *")  // 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.

    @Scheduled(cron = "0 0 3 * * *")
    public void sendRaport() {


        LocalDateTime now = LocalDateTime.now();

        LocalDateTime yesterday = now.minusDays(1L);

        LocalDateTime start = yesterday.minusHours(6L);
        LocalDateTime end = start.plusDays(1L);


        List<Client> clientList = clientService.findByCreateTimeBetween(start, end);

        int telegram = 0;
        int web = 0;
        int mobile = 0;

        for (Client client : clientList) {

            switch (client.getFrom()) {
                case FROMMOBILE:
                    mobile++;
                    break;
                case FROMTELEGRAMM:
                    telegram++;
                    break;
                case FROMWEB:
                    web++;
                    break;
            }
        }


        emailService.sendMail(emaiManager, "Статистика Crypto Name: за " + yesterday.format(formatter) + "  создано " + (telegram + web) + " CN ",
                "Телеграм: " + telegram + " CN <br>" +
                        "Caйт:    " + web + " CN <br>" +
                        "Mobile:   " + mobile + " CN <br>");

    }


    public static final String FROMTELEGRAMM = "Telegramm";
    public static final String FROMWEB = "Web";
    public static final String FROMMOBILE = "Mobile";
}
