package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class HashTag {

    @Id
    String id;

    Long publications;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPublications() {
        return publications;
    }

    public void setPublications(Long publications) {
        this.publications = publications;
    }
}
