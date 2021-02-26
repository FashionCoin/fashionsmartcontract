package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.service.ProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProofController {

    @Autowired
    ProofService proofService;


    @PostMapping("/api/v1/nft/proof")
    @ResponseBody
    ResultDTO getNft(@RequestBody NftRequestDTO request) {
        return proofService.proofNft(request);
    }

}
