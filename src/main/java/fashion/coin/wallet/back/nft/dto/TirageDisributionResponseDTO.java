package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftTirage;

import java.util.List;

public class TirageDisributionResponseDTO {

  Nft nft;

    List<DistributionNftDTO>distribution;

    public Nft getNft() {
        return nft;
    }

    public void setNft(Nft nft) {
        this.nft = nft;
    }

    public List<DistributionNftDTO> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<DistributionNftDTO> distribution) {
        this.distribution = distribution;
    }
}
