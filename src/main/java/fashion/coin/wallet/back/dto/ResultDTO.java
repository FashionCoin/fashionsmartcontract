package fashion.coin.wallet.back.dto;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class ResultDTO {
   boolean result;
   String message;
   Object data;
   String cryptoname;
   int error;

    public ResultDTO() {
    }

    public ResultDTO(boolean result, String message, int error) {
        this.result = result;
        this.message = message;
        this.error = error;
    }

    public ResultDTO(boolean result, Object data, int error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }
}
