package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.WrappedTokenEvents;
import fashion.coin.wallet.back.nft.entity.Nft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TokenEventsRepository extends JpaRepository<WrappedTokenEvents, String> {

    @Query(value = "SELECT * FROM wrapped_token_events WHERE network=?1 ORDER BY block_number DESC LIMIT 1", nativeQuery = true)
    WrappedTokenEvents findByLastTransaction(String network);

    List<WrappedTokenEvents> findByAddressFromOrAddressTo(String from, String to);

    WrappedTokenEvents findByTransactionHashAndNetwork(String txHash,String network);

}
