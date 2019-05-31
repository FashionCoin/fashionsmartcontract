package fashion.coin.wallet.back.entity;

import org.graalvm.compiler.lir.CompositeValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Entity
public class Client implements Comparable<Client> {

    @Id
    @GeneratedValue
    Long id;
    @Column(unique = true)
    String login;
    @Column(unique = true)
    String apikey;

    boolean loginChanged;

    String email;
    Integer telegramId;
    String pin;

    String phone;
    boolean showPhone;
    String realname;

    @Column(unique = true)
    String walletAddress;

    @Column(precision = 30, scale = 3)
    BigDecimal walletBalance;

    String avatar;

    LocalDateTime createTime;

    public Client() {
        this.walletBalance = BigDecimal.ZERO;
    }

    public Client(String login, String email) {
        this.login = login;
        this.email = email;
        createTime = LocalDateTime.now();
    }

    public Client(String login, Integer telegramId) {
        this.login = login;
        this.telegramId = telegramId;
        createTime = LocalDateTime.now();
    }

    public Client(String login, String apikey, String walletAddress) {
        this.login = login;
        this.apikey = apikey;
        this.walletAddress = walletAddress;
        this.walletBalance = BigDecimal.ZERO;
        createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isLoginChanged() {
        return loginChanged;
    }

    public void setLoginChanged(boolean loginChanged) {
        this.loginChanged = loginChanged;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isShowPhone() {
        return showPhone;
    }

    public void setShowPhone(boolean showPhone) {
        this.showPhone = showPhone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Integer telegramId) {
        this.telegramId = telegramId;
    }

    @Override
    public int compareTo(Client o) {
        return this.id.compareTo(o.id);
    }

    public LocalDateTime getCreateTime() {

        if(createTime==null) createTime = LocalDateTime.of(2019,05,30,00,00,00,00);

        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
