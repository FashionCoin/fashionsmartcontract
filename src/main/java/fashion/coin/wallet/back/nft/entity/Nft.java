package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Nft {
    @Id
    @GeneratedValue
    Long id;

    String fileName;
    String title;
    String description;
    @Column(precision = 30, scale = 3)
    BigDecimal faceValue;
    @Column(precision = 30, scale = 3)
    BigDecimal creativeValue;
    Long authorId;
    String authorName;
    Long ownerId;
    String ownerName;
    String ownerWallet;
    Long timestamp;
    Long proofs;
    @Column(name ="can_change_value", columnDefinition="boolean default false",nullable = false)
    boolean canChangeValue;

    @Column(name ="burned", columnDefinition="boolean default false",nullable = false)
    boolean isBurned;

    public Nft() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getCreativeValue() {
        return creativeValue;
    }

    public void setCreativeValue(BigDecimal creativeValue) {
        this.creativeValue = creativeValue;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerWallet() {
        return ownerWallet;
    }

    public void setOwnerWallet(String ownerWallet) {
        this.ownerWallet = ownerWallet;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getProofs() {
        return proofs;
    }

    public void setProofs(Long proofs) {
        this.proofs = proofs;
    }

    public boolean isCanChangeValue() {
        return canChangeValue;
    }

    public void setCanChangeValue(boolean canChangeValue) {
        this.canChangeValue = canChangeValue;
    }

    public boolean isBurned() {
        return isBurned;
    }

    public void setBurned(boolean burned) {
        isBurned = burned;
    }
}
