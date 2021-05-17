package fashion.coin.wallet.back.messenger.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Conversation {

    @Id @GeneratedValue
    Long id;

    String type;



}
