package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.dto.TransactionRequestDTO;

public class TransactionsRequestMapDTO {
    TransactionRequestDTO seller;
    TransactionRequestDTO author;
    TransactionRequestDTO taxAndProofs;

    public TransactionRequestDTO getSeller() {
        return seller;
    }

    public void setSeller(TransactionRequestDTO seller) {
        this.seller = seller;
    }

    public TransactionRequestDTO getAuthor() {
        return author;
    }

    public void setAuthor(TransactionRequestDTO author) {
        this.author = author;
    }

    public TransactionRequestDTO getTaxAndProofs() {
        return taxAndProofs;
    }

    public void setTaxAndProofs(TransactionRequestDTO taxAndProofs) {
        this.taxAndProofs = taxAndProofs;
    }
}
