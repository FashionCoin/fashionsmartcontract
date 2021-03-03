package fashion.coin.wallet.back.dto.blockchain;

/**
 * Created by JAVA-P on 25.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class BlockchainTransactionDTO {
    private Integer network_id;
    private Integer protocol_version;
    private Integer message_id;
    private Integer service_id;
    private BodyDTO body;
    private String signature;

    public Integer getNetwork_id() {
        return network_id;
    }

    public void setNetwork_id(Integer network_id) {
        this.network_id = network_id;
    }

    public Integer getProtocol_version() {
        return protocol_version;
    }

    public void setProtocol_version(Integer protocol_version) {
        this.protocol_version = protocol_version;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public Integer getService_id() {
        return service_id;
    }

    public void setService_id(Integer service_id) {
        this.service_id = service_id;
    }

    public BodyDTO getBody() {
        return body;
    }

    public void setBody(BodyDTO body) {
        this.body = body;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "BlockchainTransactionDTO{" +
                "network_id=" + network_id +
                ", protocol_version=" + protocol_version +
                ", message_id=" + message_id +
                ", service_id=" + service_id +
                ", body=" + body +
                ", signature='" + signature + '\'' +
                '}';
    }
}
