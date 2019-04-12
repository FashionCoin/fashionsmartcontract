package fashion.coin.wallet.back.fiat.dto;

public class PaymentStatusRequestDTO {
    String id;

    public PaymentStatusRequestDTO() {
    }

    public PaymentStatusRequestDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
