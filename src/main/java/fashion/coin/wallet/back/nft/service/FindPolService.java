package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.FindByDurationRequestDTO;
import fashion.coin.wallet.back.nft.dto.FindNameRequestDTO;
import fashion.coin.wallet.back.nft.dto.TopClientDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.entity.ProofHistory;
import fashion.coin.wallet.back.nft.repository.NftHistoryRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.nft.repository.ProofHistoryRepository;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.glassfish.grizzly.utils.ArraySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;
import static fashion.coin.wallet.back.nft.service.ProofService.DAY;

@Service
public class FindPolService {

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


    public ResultDTO byName(FindNameRequestDTO request) {
        try {
            if (request == null || request.getName() == null) {
                return error127;
            }
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            List<Client> clientList = clientRepository.findNameContains(request.getName());
            if (clientList == null || clientList.size() == 0) {
                return new ResultDTO(true, new ArrayList<>(), 0);
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
                result.add(cl);
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO creators(FindByDurationRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            long durationStart = 0;
            if (request.getDuration() > 0) {
                durationStart = System.currentTimeMillis() - request.getDuration() * DAY;
            }

            List<Nft> nftList = nftRepository.findByTimestampIsGreaterThan(durationStart);

            Map<Long, TopClientDTO> topClientMap = new HashMap<>();
            for (Nft nft : nftList) {

                Long clientId = nft.getAuthorId();
                if (!topClientMap.containsKey(clientId)) {
                    TopClientDTO topClient = new TopClientDTO();
                    topClient.setId(clientId);
                    topClient.setCryptoname(nft.getAuthorName());
                    topClient.setAmount(BigDecimal.ZERO);
                    topClientMap.put(clientId, topClient);
                }
                TopClientDTO topClient = topClientMap.get(clientId);
                topClient.setAmount(topClient.getAmount().add(nft.getFaceValue()));
            }
            List<TopClientDTO> clientList = new ArrayList<>(topClientMap.values());
            clientList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
            return new ResultDTO(true, clientList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO collectors(FindByDurationRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            long durationStart = 0;
            if (request.getDuration() > 0) {
                durationStart = System.currentTimeMillis() - request.getDuration() * DAY;
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
                }
                TopClientDTO topClient = topClientMap.get(cryptoname);
                topClient.setAmount(topClient.getAmount().add(nftHistory.getAmount()));
            }
            List<TopClientDTO> clientList = new ArrayList<>(topClientMap.values());
            clientList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
            return new ResultDTO(true, clientList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO topProofs(FindByDurationRequestDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            long durationStart = 0;
            if (request.getDuration() > 0) {
                durationStart = System.currentTimeMillis() - request.getDuration() * DAY;
            }

            List<ProofHistory> proofHistoryList = proofHistoryRepository.findByTimestampIsGreaterThan(durationStart);

            Set<Nft> nftSet = new HashSet<>();

            for (ProofHistory proofHistory : proofHistoryList) {
                Nft nft = nftRepository.findById(proofHistory.getNftId()).orElse(null);
                nftSet.add(nft);
            }

            List<Nft> nftList = new ArrayList<>(nftSet);

            nftList.sort((o1, o2) -> o2.getProofs().compareTo(o1.getProofs()));
            return new ResultDTO(true, nftList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO recentlySold(FindByDurationRequestDTO request) {

        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            long durationStart = System.currentTimeMillis() - 100 * DAY;

            List<NftHistory> nftHistoryList = nftHistoryRepository.findByTimestampIsGreaterThanOrderByTimestampDesc(durationStart);

            List<Nft> soldNft = new ArrayList<>();
            Set<Long> nftIdSet = new HashSet<>();
            for (NftHistory nftHistory : nftHistoryList) {
                Long nftId = nftHistory.getNftId();
                if (!nftIdSet.contains(nftId)) {
                    Nft nft = nftRepository.findById(nftId).orElse(null);
                    soldNft.add(nft);
                    nftIdSet.add(nftId);
                }
            }
            return new ResultDTO(true, soldNft, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
