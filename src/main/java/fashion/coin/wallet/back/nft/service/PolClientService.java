package fashion.coin.wallet.back.nft.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.SocialLinkDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.PolClientRequestDTO;
import fashion.coin.wallet.back.nft.dto.PolClientResponseDTO;
import fashion.coin.wallet.back.nft.entity.FriendProof;
import fashion.coin.wallet.back.nft.entity.Nft;

import fashion.coin.wallet.back.nft.entity.NftTirage;
import fashion.coin.wallet.back.nft.repository.FriendProofRepository;

import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error127;

@Service
public class PolClientService {

    Logger logger = LoggerFactory.getLogger(PolClientService.class);

    @Autowired
    ClientService clientService;

    @Autowired
    NftService nftService;

    @Autowired
    FriendProofRepository friendProofRepository;

    @Autowired
    ProofService proofService;

    @Autowired
    Gson gson;

    @Autowired
    TirageService tirageService;


    public ResultDTO getClientInfo(PolClientRequestDTO request) {
        try {

            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
//                return error109;
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

                if(nft.isTirage()){

                    NftTirage nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(),friend.getId());

                    faceValue = faceValue.add(nft.getFaceValue()
                            .multiply(BigDecimal.valueOf(nftTirage.getTirage())));
                    creativeValue = creativeValue.add(nftTirage.getCreativeValue()
                            .multiply(BigDecimal.valueOf(nftTirage.getTirage())));

                    if (nft.getAuthorId().compareTo(friend.getId()) == 0) {
                        creation.add(nft);
                    } else {
                        collection.add(nft);
                    }
                }else {

                    faceValue = faceValue.add(nft.getFaceValue());
                    creativeValue = creativeValue.add(nft.getCreativeValue());
                    proofs = proofs.add(nft.getProofs());


                    if (nft.getAuthorId().compareTo(friend.getId()) == 0) {

                        creation.add(nft);
                    } else {

                        collection.add(nft);
                    }
                }

            }

            nftList = nftService.getCreation(friend.getId());

            for (Nft nft : nftList) {
                if(nft.isTirage()){
                    NftTirage nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(),friend.getId());
                    if (nftTirage.getTirage()==0L) {
                        creation.add(nft);
                    }
                }else{
                    logger.info(String.valueOf(nft.getAuthorId()));
                    logger.info(String.valueOf(friend.getId()));
                    logger.info(String.valueOf(nft.getOwnerId()));

                    logger.info(gson.toJson(nft));

                    if (nft.getAuthorId().compareTo(friend.getId()) == 0 &&
                            nft.getOwnerId().compareTo(friend.getId()) != 0) {
                        creation.add(nft);
                    }
                }
            }

            creation.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
            collection.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

            responseDTO.setFaceValue(faceValue);
            responseDTO.setCreativeValue(creativeValue);
            responseDTO.setProofs(proofs);
            responseDTO.setProof(proofService.allProof(friend));
            responseDTO.setCollection(collection);
            responseDTO.setCreation(creation);

            responseDTO.setAvatar(friend.getAvatar());
            responseDTO.setAvaExists(friend.getAvatar() != null && friend.getAvatar().length() > 0);

            if(client != null) {
                responseDTO.setProofReceiver(checkProof(client.getId(), friend.getId()));
                responseDTO.setProofSender(checkProof(friend.getId(), client.getId()));
            }
            responseDTO.setAbout(friend.getAbout());
            responseDTO.setSocialLinks(gson.fromJson( friend.getSocialLinks(),List.class));


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
