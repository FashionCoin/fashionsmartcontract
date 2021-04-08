package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.ReportRequestDTO;
import fashion.coin.wallet.back.nft.entity.ReportNft;
import fashion.coin.wallet.back.nft.repository.ReportNftRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;

@Service
public class ReportNftService {

    @Autowired
    ReportNftRepository reportNftRepository;

    @Autowired
    ClientService clientService;

    Logger logger = LoggerFactory.getLogger(ReportNftService.class);


    public ResultDTO report(ReportRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
//                return error109;
            }

            ReportNft reportNft = new ReportNft();
            if (client != null) {
                reportNft.setClientId(client.getId());
            }
            reportNft.setTimestamp(System.currentTimeMillis());
            reportNft.setNftId(request.getNftId());
            reportNft.setText(request.getText());
            reportNft.setDetails(request.getDetails());

            reportNftRepository.save(reportNft);

            return new ResultDTO(true, reportNft, 0);


        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
