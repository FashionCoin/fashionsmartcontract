package fashion.coin.wallet.back.dto;

/**
 * Created by JAVA-P on 01.11.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class ContactDTO {

    String login;
    String phone;
    String walletAddress;
    String avatar;

    public ContactDTO() {
    }

    public ContactDTO(String login, String phone, String walletAddress, String avatar) {
        this.login = login;
        this.phone = phone;
        this.walletAddress = walletAddress;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
