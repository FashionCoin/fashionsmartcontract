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
public class TransactionRequestDTO {

    String senderWallet;
    String receiverWallet;
    String receiverLogin;
    String amount;

    BlockchainTransactionDTO blockchainTransaction;

    public TransactionRequestDTO() {
    }

    public String getSenderWallet() {
        return senderWallet;
    }

    public void setSenderWallet(String senderWallet) {
        this.senderWallet = senderWallet;
    }

    public String getReceiverWallet() {
        return receiverWallet;
    }

    public void setReceiverWallet(String receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

    public String getReceiverLogin() {
        return receiverLogin;
    }

    public void setReceiverLogin(String receiverLogin) {
        this.receiverLogin = receiverLogin;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public BlockchainTransactionDTO getBlockchainTransaction() {
        return blockchainTransaction;
    }

    public void setBlockchainTransaction(BlockchainTransactionDTO blockchainTransaction) {
        this.blockchainTransaction = blockchainTransaction;
    }
}
