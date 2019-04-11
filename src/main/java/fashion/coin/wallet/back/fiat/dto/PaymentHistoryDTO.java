package fashion.coin.wallet.back.fiat.dto;

import fashion.coin.wallet.back.fiat.entity.FiatPayment;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryDTO {

    List<FiatPayment> payments = new ArrayList<>();

    public PaymentHistoryDTO() {
    }

    public PaymentHistoryDTO(List<FiatPayment> payments) {
        this.payments = payments;
    }

    public List<FiatPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<FiatPayment> payments) {
        this.payments = payments;
    }
}
