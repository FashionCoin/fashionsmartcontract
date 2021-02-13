package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.WrappedTokenEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenEventsRepository extends JpaRepository<WrappedTokenEvents,String> {

    WrappedTokenEvents findFirstOrderByBlockNumber();

}
