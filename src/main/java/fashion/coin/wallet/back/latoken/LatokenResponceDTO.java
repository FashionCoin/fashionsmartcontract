package fashion.coin.wallet.back.latoken;

public class LatokenResponceDTO {

    String email;

    int status;

    public LatokenResponceDTO() {
    }

    public LatokenResponceDTO(String email, int status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
