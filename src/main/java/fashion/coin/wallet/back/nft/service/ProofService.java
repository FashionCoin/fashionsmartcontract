package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.entity.DividendHistory;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.ProofHistory;
import fashion.coin.wallet.back.nft.repository.DividendHistoryRepository;
import fashion.coin.wallet.back.nft.repository.ProofHistoryRepository;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;
import static fashion.coin.wallet.back.nft.service.NftService.BASE_WAY;

@Service
public class ProofService {

    Logger logger = LoggerFactory.getLogger(ProofService.class);

    @Autowired
    NftService nftService;

    @Autowired
    ClientService clientService;

    @Autowired
    AIService aiService;

    @Autowired
    ProofHistoryRepository proofHistoryRepository;

    @Autowired
    DividendHistoryRepository dividendHistoryRepository;

    static final long DAY = 24 * 60 * 60 * 1000;

    public ResultDTO proofNft(NftRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            if (overdraftProof(client.getId())) {
                return error217;
            }
            Nft nft = nftService.findNft(request.getNftId());
            if (nft == null) {
                return error213;
            }
            int clientRate = getOwnerRate(client.getId());
            int nftRate = getNftRate(nft.getId());
            BigDecimal proofValue = new BigDecimal(clientRate * nftRate);

            boolean result = aiService.transfer(proofValue.toString(), nft.getOwnerWallet(), AIService.AIWallets.MONEYBAG);

            if (!result) {
                return error205;
            }

            ProofHistory proofHistory = new ProofHistory();
            proofHistory.setTimestamp(System.currentTimeMillis());
            proofHistory.setClientId(client.getId());
            proofHistory.setNftId(nft.getId());
            proofHistory.setOwnerId(nft.getOwnerId());
            proofHistory.setClientRate(clientRate);
            proofHistory.setNftRate(nftRate);
            proofHistory.setProofValue(proofValue);

            proofHistoryRepository.save(proofHistory);

            nft.setProofs(nft.getProofs().add(proofValue));
            nftService.save(nft);


            return new ResultDTO(true, nft, 0);


        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private boolean overdraftProof(Long clientId) {

        long yesterday = System.currentTimeMillis() - DAY;
        List<ProofHistory> proofHistoryList =
                proofHistoryRepository.findByClientIdAndAndTimestampGreaterThan(clientId, yesterday);
        return proofHistoryList != null && proofHistoryList.size() >= 100;
    }


    int getOwnerRate(Long ownerId) {
        List<Nft> nftList = nftService.getNftByOwner(ownerId);

        Long totalValue = 0L;
        for (Nft nft : nftList) {
            totalValue += nft.getFaceValue().movePointRight(3).longValue();
        }

        String totalStr = String.valueOf(totalValue / 1000);

        return totalStr.length();
    }

    Integer getNftRate(Long nftId) {
        Nft nft = nftService.findNft(nftId);
        if (nft == null) {
            logger.error("Nft {} not found", nftId);
            return null;
        }
        String totalStr = String.valueOf(nft.getFaceValue().movePointRight(3).longValue() / 1000);
        return totalStr.length();
    }


    public ResultDTO dividendPayment(Nft nft, Client seller) {
        if (nft.getWayOfAllocatingFunds().equals(BASE_WAY)) {
            BigDecimal amountToDistribute = nft.getCreativeValue().divide(BigDecimal.TEN, 6, RoundingMode.HALF_UP);

            List<ProofHistory> proofHistoryList = proofHistoryRepository.findByNftId(nft.getId());
            BigDecimal totalAmount = BigDecimal.ZERO;

            if (proofHistoryList != null && proofHistoryList.size() > 0) {

                BigDecimal oneProofRate = amountToDistribute.divide(nft.getProofs(), 6, RoundingMode.HALF_UP);

                for (ProofHistory pfh : proofHistoryList) {

                    BigDecimal amount = pfh.getProofValue().multiply(oneProofRate).setScale(3, RoundingMode.HALF_UP);
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        Client client = clientService.getClient(pfh.getClientId());
                        totalAmount = totalAmount.add(amount);
                        if (!aiService.transfer(amount.toString(), client.getWalletAddress(), AIService.AIWallets.MONEYBAG)) {
                            return error205;
                        }
                        DividendHistory dividendHistory = new DividendHistory();
                        dividendHistory.setTimestamp(System.currentTimeMillis());
                        dividendHistory.setClientId(client.getId());
                        dividendHistory.setNftId(nft.getId());
                        dividendHistory.setAmount(amount);
                        dividendHistoryRepository.save(dividendHistory);
                    }
                }
            }
            amountToDistribute = amountToDistribute.subtract(totalAmount).setScale(3, RoundingMode.HALF_UP);
            if (amountToDistribute.compareTo(BigDecimal.ZERO) > 0) {
                if (!aiService.transfer(amountToDistribute.toString(), seller.getWalletAddress(), AIService.AIWallets.MONEYBAG)) {
                    return error205;
                }
            }
        } else {
            return error220;
        }
        return new ResultDTO(true, "Ok", 0);
    }

}
