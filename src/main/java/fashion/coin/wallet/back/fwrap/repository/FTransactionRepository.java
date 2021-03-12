package fashion.coin.wallet.back.fwrap.repository;

import fashion.coin.wallet.back.fwrap.entity.FTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FTransactionRepository extends JpaRepository<FTransaction,Long> {

    List<FTransaction> findByFromIdOrToId(Long fromId, Long toId);
}
