package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FriendProof {
    @Id
    @GeneratedValue
    Long id;

    Long proofSenderId;
    Long proofReceiverId;
    Long timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProofSenderId() {
        return proofSenderId;
    }

    public void setProofSenderId(Long proofSenderId) {
        this.proofSenderId = proofSenderId;
    }

    public Long getProofReceiverId() {
        return proofReceiverId;
    }

    public void setProofReceiverId(Long proofReceiverId) {
        this.proofReceiverId = proofReceiverId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
