package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.PolClientRequestDTO;
import fashion.coin.wallet.back.nft.dto.PolClientResponseDTO;
import fashion.coin.wallet.back.nft.entity.FriendProof;
import fashion.coin.wallet.back.nft.entity.Nft;

import fashion.coin.wallet.back.nft.repository.FriendProofRepository;

import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error127;

@Service
public class PolClientService {


    @Autowired
    ClientService clientService;

    @Autowired
    NftService nftService;

    @Autowired
    FriendProofRepository friendProofRepository;



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

            PolClientResponseDTO responseDTO = new PolClientResponseDTO();
            responseDTO.setId(friend.getId());
            responseDTO.setCryptoname(friend.getCryptoname());

            List<Nft> nftList = nftService.getCollection(friend.getId());
            BigDecimal faceValue = BigDecimal.ZERO;
            BigDecimal creativeValue = BigDecimal.ZERO;
            BigDecimal proofs = BigDecimal.ZERO;

            List<Nft> collection = new ArrayList<>();
            List<Nft> creation = new ArrayList<>();

            for (Nft nft : nftList) {
                faceValue = faceValue.add(nft.getFaceValue());
                creativeValue = creativeValue.add(nft.getCreativeValue());
                proofs = proofs.add(nft.getProofs());

                if (nft.getAuthorId() == client.getId()) {
                    creation.add(nft);
                } else {
                    collection.add(nft);
                }


            }
            creation.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            collection.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

            responseDTO.setFaceValue(faceValue);
            responseDTO.setCreativeValue(creativeValue);
            responseDTO.setProofs(proofs);

            responseDTO.setCollection(collection);
            responseDTO.setCreation(creation);

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
