package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.FeedNftRequestDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

//    private List<Nft> mainFeed = new ArrayList<>();
//    Long lastUpdateFeed = 0L;
//    Nft lastActualNft = null;

    public ResultDTO getFeed(FeedNftRequestDTO request) {
        logger.info("Cryptoname: {} \t ApiKey: {}", request.getCryptoname(), request.getApikey());
        Client client = clientService.findByCryptonameAndApiKey(request.getCryptoname(), request.getApikey());

        if (client == null && !request.getFeedType().equals(MAIN_FEED)) {
            return error124;
        }

        if (request.getFeedType().equals(MAIN_FEED)) {
            try {
                int fromIndex = (request.getPage() - 1) * request.getPerPage();
                int toIndex = fromIndex + request.getPerPage();

                List<Nft> mainFeed = nftRepository.findLastTenThousand();
                /*
                if (toIndex > mainFeed.size()) {
                    getFeedTail(toIndex - mainFeed.size());

                    if (mainFeed.size() == 0) {
                        return new ResultDTO(true, mainFeed, 0);
                    }

                    toIndex = mainFeed.size();
                }
                */

                List<Nft> subList = mainFeed.subList(fromIndex, toIndex);
//                cleanFeed(toIndex);
                return new ResultDTO(true, subList, 0);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return error126;
            }
        } else if (request.getFeedType().equals(PROOF_FEED)) {
            logger.info("Proof feed does not create");
            return new ResultDTO(true, new ArrayList<>(), 0);
        } else if (request.getFeedType().equals(PROOFS_FEED)) {
            logger.info("Proofs feed does not create");
            return new ResultDTO(true, new ArrayList<>(), 0);
        } else {
            return error125;
        }

    }

    /*
    private void cleanFeed(int toIndex) {
        try {
            if (mainFeed.size() >= toIndex) {
                Nft last = mainFeed.get(toIndex - 1);
                if (lastActualNft == null || lastActualNft.getTimestamp() > last.getTimestamp()) {
                    lastActualNft = last;
                }
            }

            if (lastUpdateFeed + 100000L < System.currentTimeMillis()
                    && lastActualNft != null) {
                List<Nft> newList = mainFeed.subList(0, mainFeed.indexOf(lastActualNft) + 1);
                mainFeed = newList;
                lastActualNft = null;
                lastUpdateFeed = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFeedTail(int elements) {
        Long lastNftTime = System.currentTimeMillis();
        if (mainFeed.size() > 1) {
            lastNftTime = mainFeed.get(mainFeed.size() - 1).getTimestamp();
        }

        logger.info("Last Time: {}",lastNftTime);
        logger.info("Elements: {}",elements);

        List<Nft> oldNfts = nftRepository.findByLocalDateTimeBeforeAndOrderByLocalDateTimeDescLimitedTo(lastNftTime, elements);
        logger.info("Old Nfts: {}",oldNfts);
        if (oldNfts != null && oldNfts.size() > 0) {
            mainFeed.addAll(oldNfts);
        }
    }
*/
    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setNftRepository(NftRepository nftRepository) {
        this.nftRepository = nftRepository;
    }
/*
    public void addNewNft(Nft nft) {
        mainFeed.add(0, nft);
    }

 */
}
