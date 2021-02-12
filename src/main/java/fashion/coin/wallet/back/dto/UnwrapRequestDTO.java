package fashion.coin.wallet.back.dto;

public class UnwrapRequestDTO {

    String apikey;
    String ethereumWallet;
    String transactionHash;
    String amount;

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getEthereumWallet() {
        return ethereumWallet;
    }

    public void setEthereumWallet(String ethereumWallet) {
        this.ethereumWallet = ethereumWallet;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
