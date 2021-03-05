package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.FeedNftRequestDTO;
import fashion.coin.wallet.back.nft.entity.FriendProof;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.repository.FriendProofRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;


@Service
public class FeedService {

    Logger logger = LoggerFactory.getLogger(FeedService.class);

    public static final String MAIN_FEED = "main";
    public static final String PROOF_FEED = "proof";
    public static final String PROOFS_FEED = "proofs";

    ClientService clientService;
    NftRepository nftRepository;
    FriendProofRepository friendProofRepository;


    public ResultDTO getFeed(FeedNftRequestDTO request) {
        logger.info("Cryptoname: {} \t ApiKey: {}", request.getCryptoname(), request.getApikey());
        Client client = clientService.findByCryptonameAndApiKey(request.getCryptoname(), request.getApikey());
        logger.info("Feed Type: {}", request.getFeedType());
        if (client == null && !request.getFeedType().equals(MAIN_FEED)) {
            return error124;
        }


        int fromIndex = (request.getPage() - 1) * request.getPerPage();
        int toIndex = fromIndex + request.getPerPage();

        try {
            List<Nft> feed = new ArrayList<>();
            if (request.getFeedType().equals(MAIN_FEED)) {

                feed = nftRepository.findLastTenThousand();

            } else if (request.getFeedType().equals(PROOF_FEED)) {
                List<FriendProof> friendList = friendProofRepository.findByProofSenderId(client.getId());

                for (FriendProof fp : friendList) {
                    List<Nft> nftList = nftRepository.findByOwnerId(fp.getProofReceiverId());
                    if (nftList != null && nftList.size() > 0) {
                        feed.addAll(nftList);
                    }
                }
                feed.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

            } else if (request.getFeedType().equals(PROOFS_FEED)) {
                logger.info(request.getFeedType());
                List<FriendProof> friendList = friendProofRepository.findByProofReceiverId(client.getId());
                logger.info("Frienf List size: {}", friendList.size());
                for (FriendProof fp : friendList) {

                    logger.info("FP: {}", fp.getProofSenderId());
                    List<Nft> nftList = nftRepository.findByOwnerId(fp.getProofSenderId());
                    logger.info("Size: {}", nftList.size());
                    if (nftList != null && nftList.size() > 0) {
                        feed.addAll(nftList);
                    }
                }
                logger.info("NFT list size: {}", feed.size());
                feed.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            } else {
                return error125;
            }
            logger.info("FromIndex: {}", fromIndex);
            logger.info("ToIndex: {}", toIndex);

            if (fromIndex >= feed.size()) {
                return new ResultDTO(true, new ArrayList<>(), 0);
            }

            if (toIndex >= feed.size()) {
                toIndex = feed.size();
            }

            List<Nft> subList = feed.subList(fromIndex, toIndex);
            return new ResultDTO(true, subList, 0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResultDTO(true, new ArrayList<>(), 0);
        }
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setNftRepository(NftRepository nftRepository) {
        this.nftRepository = nftRepository;
    }

    @Autowired
    public void setFriendProofRepository(FriendProofRepository friendProofRepository) {
        this.friendProofRepository = friendProofRepository;
    }
}
