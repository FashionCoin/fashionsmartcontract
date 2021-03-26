package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.ReportRequestDTO;
import fashion.coin.wallet.back.nft.service.ReportNftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportNftController {


    @Autowired
    ReportNftService reportNftService;


    @PostMapping("/api/v1/nft/report")
    @ResponseBody
    ResultDTO reportNft(@RequestBody ReportRequestDTO request) {
        return reportNftService.report(request);
    }

}
