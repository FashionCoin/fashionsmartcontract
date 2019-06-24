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
    String cryptoname;
    String apikey;
    String encryptedhash;
    String walletAddress;
    BlockchainTransactionDTO blockchainTransaction;

    public String getCryptoname() {
        return cryptoname;
    }

    public void setCryptoname(String cryptoname) {
        this.cryptoname = cryptoname;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getEncryptedhash() {
        return encryptedhash;
    }

    public void setEncryptedhash(String encryptedhash) {
        this.encryptedhash = encryptedhash;
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
