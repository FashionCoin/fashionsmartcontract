package fashion.coin.wallet.back.nft.dto;

import fashion.coin.wallet.back.nft.entity.Nft;

import java.util.List;

public class TopTenDTO {
    List<TopClientDTO> topCollectors;
    List<TopClientDTO> topCreators;
    List<Nft> topProofs;
    List<Nft> recentlySold;

     public List<TopClientDTO> getTopCollectors() {
          return topCollectors;
     }

     public void setTopCollectors(List<TopClientDTO> topCollectors) {
          this.topCollectors = topCollectors;
     }

     public List<TopClientDTO> getTopCreators() {
          return topCreators;
     }

     public void setTopCreators(List<TopClientDTO> topCreators) {
          this.topCreators = topCreators;
     }

     public List<Nft> getTopProofs() {
          return topProofs;
     }

     public void setTopProofs(List<Nft> topProofs) {
          this.topProofs = topProofs;
     }

     public List<Nft> getRecentlySold() {
          return recentlySold;
     }

     public void setRecentlySold(List<Nft> recentlySold) {
          this.recentlySold = recentlySold;
     }
}
