package fashion.coin.wallet.back.nft.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.dto.OneNftResponceDTO;
import fashion.coin.wallet.back.nft.dto.TirageDisributionResponseDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftTirage;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.nft.repository.NftTirageRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class TirageService {

    Logger logger = LoggerFactory.getLogger(TirageService.class);
    @Autowired
    Gson gson;

    @Autowired
    NftTirageRepository nftTirageRepository;

    @Autowired
    NftRepository nftRepository;

    @Autowired
    ClientService clientService;


    public List<Nft> tirageFindByOwnerId(Long ownerId) {
        try {
            if (ownerId == null) return new ArrayList<>();
            List<NftTirage> nftTirageList = nftTirageRepository.findByOwnerId(ownerId);
            if (nftTirageList == null) return new ArrayList<>();
            List<Nft> result = new ArrayList<>();
            for (NftTirage tirage : nftTirageList) {
                Optional<Nft> nftOptional = nftRepository.findById(tirage.getNftId());
                if (nftOptional.isPresent()) {
                    result.add(nftOptional.get());
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public NftTirage save(NftTirage nftTirage) {
        nftTirageRepository.save(nftTirage);
        return nftTirage;
    }

    public NftTirage tirageFindByNftAndOwnerId(Long nftId, Long ownerId) {
        NftTirage nftTirage = nftTirageRepository.findTopByNftIdAndOwnerId(nftId, ownerId);
        if(nftTirage==null){
            Client owner = clientService.getClient(ownerId);
            nftTirage = new NftTirage();
            nftTirage.setNftId(nftId);
            nftTirage.setTirage(0L);
            nftTirage.setOwnerWallet(owner.getWalletAddress());
            nftTirage.setOwnerName(owner.getCryptoname());
            nftTirage.setInsale(false);
            nftTirage.setCanChangeValue(false);
            nftTirage.setTimestamp(System.currentTimeMillis());
            nftTirage.setOwnerId(ownerId);
        }


        return nftTirage;
    }

    public ResultDTO distribution(NftRequestDTO request) {
        try {
            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }

            List<NftTirage> nftTirageList = nftTirageRepository.findByNftId(request.getNftId());
            if (nftTirageList == null) {
                nftTirageList = new ArrayList<>();
            }

            TirageDisributionResponseDTO response = new TirageDisributionResponseDTO();
            response.setNft(nft);
            response.setDistribution(nftTirageList);

            return new ResultDTO(true, response, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO oneHolder(NftRequestDTO request) {
        try {
            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            Client owner = clientService.getClient(request.getOwnerId());
            if (owner == null) {
                return error127;
            }

            NftTirage nftTirage = nftTirageRepository.findTopByNftIdAndOwnerId(request.getNftId(), request.getOwnerId());
            if (nftTirage == null) {
                return error228;
            }


            OneNftResponceDTO oneNft = new OneNftResponceDTO();
            oneNft.setPieces(nftTirage.getTirage());
            oneNft.setWayOfAllocatingFunds(nft.getWayOfAllocatingFunds());
            oneNft.setTxhash(nftTirage.getTxhash());
            oneNft.setTitle(nft.getTitle());
            oneNft.setTimestamp(nftTirage.getTimestamp());
            oneNft.setOwnerWallet(nftTirage.getOwnerWallet());
            oneNft.setOwnerName(nftTirage.getOwnerName());
            oneNft.setOwnerId(nftTirage.getOwnerId());
            oneNft.setInsale(nftTirage.isInsale());
            oneNft.setFileName(nft.getFileName());
            oneNft.setFaceValue(nft.getFaceValue());
            oneNft.setDescription(nft.getDescription());
            oneNft.setCreativeValue(nftTirage.getCreativeValue());
            oneNft.setCanChangeValue(nftTirage.isCanChangeValue());
            oneNft.setBurned(nft.isBurned());
            oneNft.setBanned(nft.isBanned());
            oneNft.setAuthorName(nft.getAuthorName());
            oneNft.setAuthorId(nft.getAuthorId());
            oneNft.setAvatar(owner.getAvatar());
            oneNft.setAvaExists(owner.avaExists());
            oneNft.setId(nft.getId());
            oneNft.setCurrency(nft.getCurrency());
            oneNft.setFree(nft.isFree());
            oneNft.setTirage(nft.isTirage());

            return new ResultDTO(true, oneNft, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public boolean setPieces(NftTirage nftTirage, long pieces) {
        Long total = nftTirage.getTirage()+pieces;
        if(total<0){
            return false;
        }else if(total==0){
            nftTirageRepository.delete(nftTirage);
        }else{
            nftTirage.setTirage(total);
            nftTirageRepository.save(nftTirage);
        }
        return true;
    }
}
