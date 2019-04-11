package fashion.coin.wallet.back.fiat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FiatPaymentRepository extends JpaRepository<FiatPayment, String> {

}
