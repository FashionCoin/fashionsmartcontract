package fashion.coin.wallet.back.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by JAVA-P on 23.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Entity
public class Contact {

    @Id @GeneratedValue
    Long id;

    @ManyToOne
    Client listOwner;

    @ManyToOne
    Client friend;

    boolean deleted;

    public Contact() {
    }

    public Contact(Client listOwner, Client friend) {
        this.listOwner = listOwner;
        this.friend = friend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getListOwner() {
        return listOwner;
    }

    public void setListOwner(Client listOwner) {
        this.listOwner = listOwner;
    }

    public Client getFriend() {
        return friend;
    }

    public void setFriend(Client friend) {
        this.friend = friend;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
