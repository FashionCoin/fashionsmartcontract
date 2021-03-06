package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.entity.DividendHistory;
import fashion.coin.wallet.back.nft.entity.FriendProof;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.ProofHistory;
import fashion.coin.wallet.back.nft.repository.DividendHistoryRepository;
import fashion.coin.wallet.back.nft.repository.FriendProofRepository;
import fashion.coin.wallet.back.nft.repository.ProofHistoryRepository;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    FriendProofRepository friendProofRepository;

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

            if (nft.isTirage()) {
                return error227;
            }

            if (!checkOneProof(nft, client)) {
                return error222;
            }
            int clientRate = getOwnerRate(client.getId());
            int nftRate = getNftRate(nft.getId());
            BigDecimal proofValue = new BigDecimal(clientRate * nftRate);

            boolean result = aiService.transfer(proofValue.toString(),
                    nft.getOwnerWallet(),
                    AIService.AIWallets.MONEYBAG).isResult();

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

            updateFriends(client.getId(), nft.getOwnerId());

            return new ResultDTO(true, nft, 0);


        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private boolean checkOneProof(Nft nft, Client client) {
        List<ProofHistory> proofHistoryList = proofHistoryRepository.findByClientIdAndNftId(client.getId(), nft.getId());
        return proofHistoryList == null || proofHistoryList.size() == 0;
    }

    private void updateFriends(Long proofSenderId, Long proofReceiverId) {
        List<FriendProof> friendList = friendProofRepository.findByProofSenderIdAndProofReceiverId(proofSenderId, proofReceiverId);

        if (friendList == null || friendList.size() == 0) {
            FriendProof friendProof = new FriendProof();
            friendProof.setTimestamp(System.currentTimeMillis());
            friendProof.setProofSenderId(proofSenderId);
            friendProof.setProofReceiverId(proofReceiverId);
            friendProofRepository.save(friendProof);
        } else if (friendList.size() > 1) {
            logger.error("FriendList > 1");
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


            for (int i = 1; i < 10000; i += 1000) {

                if (!aiService.isEnoughMoney(AIService.AIWallets.MONEYBAG, amountToDistribute)) {
                    try {
                        Thread.sleep(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }

            }
            /// TODO: временно отключаем пруфы

            List<ProofHistory> proofHistoryList = proofHistoryRepository.findByNftId(nft.getId());
// TODO: раскомментировать когда включим пруфы
            BigDecimal totalAmount = BigDecimal.ZERO;
// TODO: закомментирвоать когда включим пруфы
//            BigDecimal totalAmount = amountToDistribute.divide(BigDecimal.TEN, 3, RoundingMode.HALF_UP);

//            /*
            if (proofHistoryList != null && proofHistoryList.size() > 0) {

                BigDecimal oneProofRate = amountToDistribute.divide(nft.getProofs(), 6, RoundingMode.HALF_UP);

                for (ProofHistory pfh : proofHistoryList) {

                    BigDecimal amount = pfh.getProofValue().multiply(oneProofRate).setScale(3, RoundingMode.HALF_UP);
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        Client client = clientService.getClient(pfh.getClientId());
                        totalAmount = totalAmount.add(amount);
                        if (!aiService.transfer(amount.toString(),
                                client.getWalletAddress(),
                                AIService.AIWallets.MONEYBAG).isResult()) {
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
                if (!aiService.transfer(amountToDistribute.toString(),
                        seller.getWalletAddress(),
                        AIService.AIWallets.MONEYBAG).isResult()) {
                    return error205;
                }
            }
        } else {
            return error220;
        }
        return new ResultDTO(true, "Ok", 0);
    }

    public BigDecimal allProof(Client client) {

        BigDecimal proof = BigDecimal.ZERO;

        List<ProofHistory> proofHistoryList = proofHistoryRepository.findByClientId(client.getId());
        if (proofHistoryList != null && proofHistoryList.size() > 0) {
            for (ProofHistory ph : proofHistoryList) {
                proof = proof.add(ph.getProofValue());
            }
        }
        return proof;
    }

    public List<Long> mutual(Client client) {
        List<FriendProof> proofMe = friendProofRepository.findByProofReceiverId(client.getId());
        List<FriendProof> myProof = friendProofRepository.findByProofSenderId(client.getId());

        Set<Long> friedsId = new HashSet<>();

        for (FriendProof sender : proofMe) {
            for (FriendProof receiver : myProof) {
                if (sender.getProofSenderId().equals(receiver.getProofReceiverId())
                        && !sender.getProofSenderId().equals(client.getId())) {
                    friedsId.add(sender.getProofSenderId());
                }
            }
        }
        return new ArrayList<>(friedsId);
    }
}
