package fashion.coin.wallet.back.nft.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NftFile {

    @Id
    @GeneratedValue
    Long id;

    String filename;
    String contentType;
    Long size;
    String exifOrientation;
    String height;
    String width;

    public NftFile() {
    }

    public NftFile(String filename, String contentType, Long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExifOrientation() {
        return exifOrientation;
    }

    public void setExifOrientation(String exifOrientation) {
        this.exifOrientation = exifOrientation;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
