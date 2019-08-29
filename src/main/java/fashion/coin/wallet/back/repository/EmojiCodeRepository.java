package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.EmojiCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiCodeRepository extends JpaRepository<EmojiCode, String> {
    EmojiCode findByEmoji(String emoji);
}
