package fashion.coin.wallet.back.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Entity
public class SetEmailRequest {

    @Id @GeneratedValue
    Long id;

    LocalDateTime dataTime;

    @ManyToOne
    Client client;

    String email;

    @Column(unique = true)
    String emailVerificationCode;

    boolean status;

    public SetEmailRequest() {
    }

    public SetEmailRequest(Client client, String email, String emailVerificationCode) {
        this.dataTime = LocalDateTime.now();
        this.client = client;
        this.email = email;
        this.emailVerificationCode = emailVerificationCode;
        this.status = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataTime() {
        return dataTime;
    }

    public void setDataTime(LocalDateTime dataTime) {
        this.dataTime = dataTime;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerificationCode() {
        return emailVerificationCode;
    }

    public void setEmailVerificationCode(String emailVerificationCode) {
        this.emailVerificationCode = emailVerificationCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
