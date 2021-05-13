package fashion.coin.wallet.back.repository;

import fashion.coin.wallet.back.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship,Long> {

    List<Relationship> findByCryptoname(String cryptoname);
    Relationship findTopByCryptonameAndAlterego(String cryptoname, String alterego);

}
