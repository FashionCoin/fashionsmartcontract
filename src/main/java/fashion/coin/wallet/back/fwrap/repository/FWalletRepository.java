package fashion.coin.wallet.back.fwrap.repository;

import fashion.coin.wallet.back.fwrap.entity.FWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FWalletRepository extends JpaRepository<FWallet, Long> {
    List<FWallet> findByClientId(Long clientId);

    List<FWallet> findByClientIdAndCurrency(Long clientId, String currency);
}
