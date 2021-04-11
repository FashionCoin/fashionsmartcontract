package fashion.coin.wallet.back.nft.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftTirage;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.nft.repository.NftTirageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TirageService {

    Logger logger = LoggerFactory.getLogger(TirageService.class);
    @Autowired
    Gson gson;

    @Autowired
    NftTirageRepository nftTirageRepository;

    @Autowired
    NftRepository nftRepository;


    public List<Nft> tirageFindByOwnerId(Long ownerId) {
        try {
            if (ownerId == null) return new ArrayList<>();
            List<NftTirage> nftTirageList = nftTirageRepository.findByOwnerId(ownerId);
            if (nftTirageList == null) return new ArrayList<>();
            List<Nft> result = new ArrayList<>();
            for(NftTirage tirage : nftTirageList){
                Optional<Nft> nftOptional = nftRepository.findById(tirage.getNftId());
                if(nftOptional.isPresent()){
                    result.add(nftOptional.get());
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public NftTirage save(NftTirage nftTirage) {
        nftTirageRepository.save(nftTirage);
        return nftTirage;
    }

    public NftTirage tirageFindByNftAndOwnerId(Long nftId, Long ownerId) {
       NftTirage nftTirage = nftTirageRepository.findTopByNftIdAndOwnerId(nftId,ownerId);
        return nftTirage;
    }
}
