package fashion.coin.wallet.back.fiat.dto;

public class PayResponceDTO {
    Boolean result;
    String msg;

    public PayResponceDTO() {
    }

    public PayResponceDTO(Boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
