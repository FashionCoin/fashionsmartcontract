package fashion.coin.wallet.back.dto;

import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;

/**
 * Created by JAVA-P on 24.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class WrappedRequestDTO {

    String ethereumWallet;

    TransactionRequestDTO transactionRequestDTO;

    String network = "ethereum";

    public WrappedRequestDTO() {
        this.network = "ethereum";
    }

    public String getEthereumWallet() {
        return ethereumWallet;
    }

    public void setEthereumWallet(String ethereumWallet) {
        this.ethereumWallet = ethereumWallet;
    }

    public TransactionRequestDTO getTransactionRequestDTO() {
        return transactionRequestDTO;
    }

    public void setTransactionRequestDTO(TransactionRequestDTO transactionRequestDTO) {
        this.transactionRequestDTO = transactionRequestDTO;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
