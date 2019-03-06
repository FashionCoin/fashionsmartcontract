package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.SetEmailRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public interface SetEmailRepository extends JpaRepository<SetEmailRequest,Long> {
    SetEmailRequest findByEmailVerificationCode(String emailVerificationCode);

    List<SetEmailRequest> findAllByEmail(String email);
}
