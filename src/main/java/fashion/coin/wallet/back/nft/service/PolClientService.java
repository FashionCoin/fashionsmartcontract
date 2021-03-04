package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.PolClientRequestDTO;
import fashion.coin.wallet.back.nft.dto.PolClientResponseDTO;
import fashion.coin.wallet.back.nft.entity.FriendProof;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.PolClient;
import fashion.coin.wallet.back.nft.repository.FriendProofRepository;
import fashion.coin.wallet.back.nft.repository.PolClientRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error127;

@Service
public class PolClientService {

    @Autowired
    PolClientRepository polClientRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    NftService nftService;

    @Autowired
    FriendProofRepository friendProofRepository;


    public PolClient getClient(Long id) {
        PolClient polClient = polClientRepository.findById(id).orElse(null);
        if (polClient == null) {
            Client client = clientService.getClient(id);
            polClient = new PolClient();
            polClient.setId(client.getId());
            polClient.setCryptoname(client.getCryptoname());
            polClient.setCreativeValue(BigDecimal.ZERO);
            polClient.setFaceValue(BigDecimal.ZERO);
            polClient.setProofs(BigDecimal.ZERO);

            polClientRepository.save(polClient);
        }
        return polClient;
    }

    public void addNft(Nft nft) {
        PolClient polClient = getClient(nft.getOwnerId());
        polClient.setFaceValue(polClient.getFaceValue().add(nft.getFaceValue()));
        polClient.setCreativeValue(polClient.getCreativeValue().add(nft.getCreativeValue()));
        polClientRepository.save(polClient);
    }

    public PolClient increaseValue(Client client, BigDecimal incFace, BigDecimal incCreative) {
        PolClient polClient = getClient(client.getId());
        polClient.setFaceValue(polClient.getFaceValue().add(incFace));
        polClient.setCreativeValue(polClient.getCreativeValue().add(incCreative));
        polClientRepository.save(polClient);
        return polClient;
    }

    public ResultDTO getClientInfo(PolClientRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }


            Client friend = clientService.getClient(request.getId());
            if (friend == null) {
                friend = clientService.findByCryptoname(request.getCryptoname());
            }
            if (friend == null) {
                return error127;
            }


            PolClient polClient = getClient(friend.getId());
            PolClientResponseDTO responseDTO = new PolClientResponseDTO();
            responseDTO.setId(polClient.getId());
            responseDTO.setCryptoname(polClient.getCryptoname());
            responseDTO.setFaceValue(polClient.getFaceValue());
            responseDTO.setCreativeValue(polClient.getCreativeValue());
            responseDTO.setProofs(polClient.getProofs());

            responseDTO.setCollection(nftService.getCollection(friend.getId()));
            responseDTO.setCreation(nftService.getCreation(friend.getId()));

            responseDTO.setAvatar(friend.getAvatar());
            responseDTO.setAvaExists(friend.getAvatar() != null && friend.getAvatar().length() > 0);

            responseDTO.setProofReceiver(checkProof(client.getId(), friend.getId()));
            responseDTO.setProofSender(checkProof(friend.getId(), client.getId()));

            return new ResultDTO(true, responseDTO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private boolean checkProof(Long proofSenderId, Long proofReceiverId) {
        List<FriendProof> friendProofList =
                friendProofRepository.findByProofSenderIdAndProofReceiverId(proofSenderId, proofReceiverId);
        return friendProofList != null && friendProofList.size() != 0;
    }
}
