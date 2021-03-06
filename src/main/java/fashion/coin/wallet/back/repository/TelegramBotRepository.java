package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.TelegramBotData;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramBotRepository extends JpaRepository<TelegramBotData, Long> {

    TelegramBotData findFirstByUserIdAndParamname(String userId, String paramname);

}
