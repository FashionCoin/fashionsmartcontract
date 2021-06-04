package fashion.coin.wallet.back.nft.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ClientInfoDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.FindByDurationRequestDTO;
import fashion.coin.wallet.back.nft.dto.FindNameRequestDTO;
import fashion.coin.wallet.back.nft.dto.TopClientDTO;
import fashion.coin.wallet.back.nft.dto.TopTenDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.entity.ProofHistory;
import fashion.coin.wallet.back.nft.repository.NftHistoryRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.nft.repository.ProofHistoryRepository;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error127;
import static fashion.coin.wallet.back.nft.service.ProofService.DAY;

@Service
public class FindPolService {

    Logger logger = LoggerFactory.getLogger(FindPolService.class);

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    NftRepository nftRepository;

    @Autowired
    NftHistoryRepository nftHistoryRepository;

    @Autowired
    ProofHistoryRepository proofHistoryRepository;

    @Autowired
    Gson gson;


    public ResultDTO byName(FindNameRequestDTO request) {
        try {

            logger.info(gson.toJson(request));
            if (request == null || request.getName() == null) {
                logger.error(gson.toJson(request));
                return error127;
            }
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
            Client friend = clientRepository.findClientByCryptoname(request.getName());


            List<Client> clientList = clientRepository.findNameContains(request.getName().toLowerCase());
//            logger.info(String.valueOf(clientList));
            if (clientList == null || clientList.size() == 0) {
                return new ResultDTO(true, new ArrayList<>(), 0);
            }
//            logger.info("Friend: {}", friend);
            if (friend != null) {
                clientList.removeIf(cl -> cl.getId().equals(friend.getId()));
                clientList.add(friend);
            }


            List<Client> result = new ArrayList<>();

            for (Client c : clientList) {

                Client cl = new Client();
                cl.setId(c.getId());
                cl.setCryptoname(c.getCryptoname());
                if (c.avaExists()) {
                    cl.setAvatar(c.getAvatar());
                }
                cl.setWalletBalance(c.getWalletBalance());


                if (c.getCryptoname().equals(request.getName())) {
                    result.add(0, cl);
                } else {
                    result.add(cl);
                }
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO creators(FindByDurationRequestDTO request) {
        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
            List<TopClientDTO> clientList = topCreators(request.getDuration());
            return new ResultDTO(true, clientList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    List<TopClientDTO> topCreators(long duration) {

        long durationStart = 0;
        if (duration > 0) {
            durationStart = System.currentTimeMillis() - duration * DAY;
        }

        List<Nft> nftList = nftRepository.findByTimestampIsGreaterThan(durationStart);

        Map<Long, TopClientDTO> topClientMap = new HashMap<>();
        for (Nft nft : nftList) {

            Long clientId = nft.getAuthorId();
            if (!topClientMap.containsKey(clientId)) {
                Client client = clientService.getClient(clientId);
                TopClientDTO topClient = new TopClientDTO();
                topClient.setId(clientId);
                topClient.setCryptoname(nft.getAuthorName());
                topClient.setAmount(BigDecimal.ZERO);
                topClientMap.put(clientId, topClient);
                topClient.setAvatar(client.getAvatar());
                topClient.setAvaExists(client.avaExists());
            }
            TopClientDTO topClient = topClientMap.get(clientId);
            topClient.setAmount(topClient.getAmount().add(nft.getFaceValue()));
        }
        List<TopClientDTO> clientList = new ArrayList<>(topClientMap.values());
        clientList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
        return clientList;

    }


    public ResultDTO collectors(FindByDurationRequestDTO request) {
        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }

            List<TopClientDTO> clientList = topCollectors(request.getDuration());
            return new ResultDTO(true, clientList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public List<TopClientDTO> topCollectors(long duration) {


        long durationStart = 0;
        if (duration > 0) {
            durationStart = System.currentTimeMillis() - duration * DAY;
        }

        List<NftHistory> nftHistoryList = nftHistoryRepository.findByTimestampIsGreaterThanOrderByTimestampDesc(durationStart);

        Map<String, TopClientDTO> topClientMap = new HashMap<>();
        for (NftHistory nftHistory : nftHistoryList) {

            String cryptoname = nftHistory.getCryptonameTo();
            if (!topClientMap.containsKey(cryptoname)) {
                Client clientEntity = clientService.findByCryptoname(cryptoname);
                TopClientDTO topClient = new TopClientDTO();
                topClient.setId(clientEntity.getId());
                topClient.setCryptoname(nftHistory.getCryptonameTo());
                topClient.setAmount(BigDecimal.ZERO);
                topClientMap.put(cryptoname, topClient);
                topClient.setAvatar(clientEntity.getAvatar());
                topClient.setAvaExists(clientEntity.avaExists());
            }
            TopClientDTO topClient = topClientMap.get(cryptoname);
            topClient.setAmount(topClient.getAmount().add(nftHistory.getAmount()));
        }
        List<TopClientDTO> clientList = new ArrayList<>(topClientMap.values());
        clientList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
        return clientList;
    }


    public ResultDTO topProofs(FindByDurationRequestDTO request) {

        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }

            List<Nft> nftList = getTopProofs(request.getDuration());
            return new ResultDTO(true, nftList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    List<Nft> getTopProofs(long duration) {
        long durationStart = 0;
        if (duration > 0) {
            durationStart = System.currentTimeMillis() - duration * DAY;
        }

        List<ProofHistory> proofHistoryList = proofHistoryRepository.findByTimestampIsGreaterThan(durationStart);

        Set<Long> nftSet = new HashSet<>();
        List<Nft> nftList = new ArrayList<>();
        for (ProofHistory proofHistory : proofHistoryList) {
            long nftId = proofHistory.getNftId();
            if (!nftSet.contains(nftId)) {
                Nft nft = nftRepository.findById(nftId).orElse(null);
                if (!nft.isBanned() && !nft.isBurned()) {
                    nftList.add(nft);
                    nftSet.add(nftId);
                }
            }
        }

        nftList.sort((o1, o2) -> o2.getProofs().compareTo(o1.getProofs()));
        return nftList;
    }

    public ResultDTO recentlySold(FindByDurationRequestDTO request) {

        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }

            List<Nft> soldNft = getRecentlySold(request.getDuration());
            return new ResultDTO(true, soldNft, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    List<Nft> getRecentlySold(long duration) {
        long durationStart = 0;
        if (duration > 0) {
            durationStart = System.currentTimeMillis() - duration * DAY;
        }

        List<NftHistory> nftHistoryList = nftHistoryRepository.findByTimestampIsGreaterThanOrderByTimestampDesc(durationStart);

        List<Nft> soldNft = new ArrayList<>();
        Set<Long> nftIdSet = new HashSet<>();
        for (NftHistory nftHistory : nftHistoryList) {
            Long nftId = nftHistory.getNftId();
            if (!nftIdSet.contains(nftId)) {
                Nft nft = nftRepository.findById(nftId).orElse(null);
                if (!nft.isBanned() && !nft.isBurned()) {

                    if (nft.isTirage()) {
                        nft.setCreativeValue(nftHistory.getAmount());
                    }

                    soldNft.add(nft);
                    nftIdSet.add(nftId);
                }
            }
        }
        return soldNft;
    }

    public ResultDTO mostExpensiveFaceValue(FindByDurationRequestDTO request) {
        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
            long durationStart = 0;
            if (request.getDuration() > 0) {
                durationStart = System.currentTimeMillis() - request.getDuration() * DAY;
            }

            List<Nft> nftList = nftRepository.findByTimestampIsGreaterThan(durationStart);
            nftList.removeIf(nft -> nft.isBurned() || nft.isBanned());
            nftList.sort((o1, o2) -> o2.getFaceValue().compareTo(o1.getFaceValue()));
            return new ResultDTO(true, nftList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO mostExpensiveCreativeValue(FindByDurationRequestDTO request) {
        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
            long durationStart = 0;
            if (request.getDuration() > 0) {
                durationStart = System.currentTimeMillis() - request.getDuration() * DAY;
            }

            List<Nft> nftList = nftRepository.findByTimestampIsGreaterThan(durationStart);
            nftList.removeIf(nft -> nft.isBurned() || nft.isBanned() || nft.isTirage());
            nftList.sort((o1, o2) -> o2.getCreativeValue().compareTo(o1.getCreativeValue()));
            return new ResultDTO(true, nftList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO topTen(FindByDurationRequestDTO request) {
        try {
            TopTenDTO result = new TopTenDTO();

            List<TopClientDTO> topCollectors = topCollectors(request.getDuration());
            if (topCollectors.size() > 10) {
                topCollectors = topCollectors.subList(0, 10);
            }
            result.setTopCollectors(topCollectors);


            List<TopClientDTO> topCreators = topCreators(request.getDuration());
            if (topCreators.size() > 10) {
                topCreators = topCreators.subList(0, 10);
            }
            result.setTopCreators(topCreators);

            List<Nft> topProofs = getTopProofs(request.getDuration());
            if (topProofs.size() > 10) {
                topProofs = topProofs.subList(0, 10);
            }
            result.setTopProofs(topProofs);

            List<Nft> recentlySold = getRecentlySold(request.getDuration());
            if (recentlySold.size() > 10) {
                recentlySold = recentlySold.subList(0, 10);
            }
            result.setRecentlySold(recentlySold);

            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}

