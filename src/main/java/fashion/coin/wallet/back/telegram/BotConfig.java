package fashion.coin.wallet.back.telegram;

import fashion.coin.wallet.back.telegram.service.TelegramCheckService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Component
public class BotConfig {

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

//



    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {



        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {

                ApiContextInitializer.init();

                TelegramBotsApi botsApi = new TelegramBotsApi();

                try {

                    FashionBot fashionBot = new FashionBot(botName, botToken);

                    botsApi.registerBot(fashionBot);

                    TelegramCheckService telegramCheckService = TelegramCheckService.getInstance();
                    telegramCheckService.setBot(fashionBot);

                } catch (TelegramApiRequestException e) {
                    e.printStackTrace();
                }
            }
        };
    }


}
