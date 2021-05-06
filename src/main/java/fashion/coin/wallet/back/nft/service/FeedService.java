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

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error124;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error125;


@Service
public class FeedService {

    Logger logger = LoggerFactory.getLogger(FeedService.class);

    public static final String MAIN_FEED = "main";
    public static final String PROOF_FEED = "proof";
    public static final String PROOFS_FEED = "proofs";

    ClientService clientService;
    NftRepository nftRepository;
    FriendProofRepository friendProofRepository;
    TirageService tirageService;


    public ResultDTO getFeed(FeedNftRequestDTO request) {
//        logger.info("Cryptoname: {} \t ApiKey: {}", request.getCryptoname(), request.getApikey());
        Client client = clientService.findByCryptonameAndApiKey(request.getCryptoname(), request.getApikey());
//        logger.info("Feed Type: {}", request.getFeedType());
        if (client == null && !request.getFeedType().equals(MAIN_FEED)) {
            logger.error("Client: {}", client);
            logger.error("Feed type: {}", request.getFeedType());
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
                    nftList.addAll(tirageService.tirageFindByOwnerId(fp.getProofReceiverId()));
                    if (nftList != null && nftList.size() > 0) {
                        nftList.removeIf(nft -> nft.getOwnerId() != null && nft.getOwnerId().equals(client.getId()));

                        for (Nft nft : nftList) {
                            boolean isDouble = false;
                            for (Nft feedNft : feed) {
                                if (feedNft.getId().equals(nft.getId())) {
                                    isDouble = true;
                                    break;
                                }
                            }
                            if (!isDouble) {
                                feed.add(nft);
                            }
                        }

//                        feed.addAll(nftList);
                    }
                }
                feed.removeIf(nft -> nft.isBurned() || nft.isBanned());
                feed.forEach(nft -> {
                    if (nft.isTirage()) {
                        nft.setOwnerId(nft.getAuthorId());
                        nft.setOwnerName(nft.getAuthorName());
                    }
                });
                feed.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));


            } else if (request.getFeedType().equals(PROOFS_FEED)) {
//                logger.info(request.getFeedType());
                List<FriendProof> friendList = friendProofRepository.findByProofReceiverId(client.getId());
//                logger.info("Frienf List size: {}", friendList.size());
                for (FriendProof fp : friendList) {


                    List<Nft> nftList = nftRepository.findByOwnerId(fp.getProofSenderId());
                    nftList.addAll(tirageService.tirageFindByOwnerId(fp.getProofReceiverId()));

                    if (nftList != null && nftList.size() > 0) {
                        nftList.removeIf(nft -> nft.getOwnerId() != null && nft.getOwnerId().equals(client.getId()));

                        for (Nft nft : nftList) {
                            boolean isDouble = false;
                            for (Nft feedNft : feed) {
                                if (feedNft.getId().equals(nft.getId())) {
                                    isDouble = true;
                                    break;
                                }
                            }
                            if (!isDouble) {
                                feed.add(nft);
                            }
                        }

//                        feed.addAll(nftList);
                    }
                }
//                logger.info("NFT list size: {}", feed.size());
                feed.removeIf(nft -> nft.isBurned() || nft.isBanned());
                feed.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            } else {
                logger.error("Feed type: {}",request.getFeedType());

                return error125;
            }
//            logger.info("FromIndex: {}", fromIndex);
//            logger.info("ToIndex: {}", toIndex);

            if (fromIndex >= feed.size()) {
                return new ResultDTO(true, new ArrayList<>(), 0);
            }

            if (toIndex >= feed.size()) {
                toIndex = feed.size();
            }

            List<Nft> subList = feed.subList(fromIndex, toIndex);
            for(Nft nft : subList){
                logger.info(nft.getTitle());
            }
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

    @Autowired
    public void setTirageService(TirageService tirageService) {
        this.tirageService = tirageService;
    }
}
