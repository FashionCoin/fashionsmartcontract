package fashion.coin.wallet.back.dto;

import java.util.List;

public class EventsDTO {
    public String status;
    public String message;
    public List<EthEventDTO> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EthEventDTO> getResult() {
        return result;
    }

    public void setResult(List<EthEventDTO> result) {
        this.result = result;
    }
}
