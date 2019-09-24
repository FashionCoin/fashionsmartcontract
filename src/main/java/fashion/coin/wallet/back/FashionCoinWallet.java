package fashion.coin.wallet.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static java.time.Duration.ofSeconds;

@SpringBootApplication
@EnableScheduling
public class FashionCoinWallet {

    public static void main(String[] args) {
        SpringApplication.run(FashionCoinWallet.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {

        return restTemplateBuilder
                .setConnectTimeout(500000)
                .setReadTimeout(500000)
                .build();
    }


}
