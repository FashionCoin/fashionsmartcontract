package fashion.coin.wallet.back.dto;

import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;

/**
 * Created by JAVA-P on 22.10.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
public class RegistrationRequestDTO {
    String login;
    String apikey;
    String walletAddress;
    BlockchainTransactionDTO blockchainTransaction;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BlockchainTransactionDTO getBlockchainTransaction() {
        return blockchainTransaction;
    }

    public void setBlockchainTransaction(BlockchainTransactionDTO blockchainTransaction) {
        this.blockchainTransaction = blockchainTransaction;
    }
}
