package fashion.coin.wallet.back.telegram.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramBotRepository extends JpaRepository<TelegramBotData, Long> {

    TelegramBotData findFirstByUserIdAndParamname(String userId, String paramname);

}
