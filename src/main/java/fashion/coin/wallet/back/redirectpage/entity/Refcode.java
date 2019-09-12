package fashion.coin.wallet.back.redirectpage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Refcode implements Comparable<Refcode>{
    @Id @GeneratedValue
    Long id;

    String code;

    String ipadress;
    String os;
    String useragent;

    LocalDateTime localDateTime;

    boolean used;


    public Refcode() {
    }

    public Refcode(String code, String ipadress, String os, String useragent) {
        this.code = code;
        this.ipadress = ipadress;
        this.os = os;
        this.useragent = useragent;
        this.localDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIpadress() {
        return ipadress;
    }

    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public int compareTo(Refcode o) {
        return this.localDateTime.compareTo(o.localDateTime);
    }
}

