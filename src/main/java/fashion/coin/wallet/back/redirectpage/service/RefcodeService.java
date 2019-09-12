package fashion.coin.wallet.back.redirectpage.service;

import fashion.coin.wallet.back.dto.PhoneModelDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.redirectpage.RefcodeRepository;
import fashion.coin.wallet.back.redirectpage.entity.Refcode;

import fashion.coin.wallet.back.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RefcodeService {

    Logger logger = LoggerFactory.getLogger(RefcodeService.class);


    @Autowired
    RefcodeRepository refcodeRepository;

    @Autowired
    ClientService clientService;


    Refcode saveRefCode(String code, String ip, String os, String useragent) {
        Refcode refcode = new Refcode(code, ip, os, useragent);
        refcodeRepository.save(refcode);
        return refcode;
    }


    Refcode getRefCode(String ip, String model) {
        try {

            List<Refcode> refcodeList = refcodeRepository.findByIpadress(ip);
            if (refcodeList != null && refcodeList.size() > 0) {
                if (refcodeList.size() == 1) return refcodeList.get(0);
                logger.info("Found " + refcodeList.size() + " refcodes for ip: " + ip);
                refcodeList.sort(Refcode::compareTo);
                List<Refcode> newList = new ArrayList<>();
                for (Refcode code : refcodeList) {
                    if (code.getUseragent().toLowerCase().contains(model.toLowerCase())) {
                        newList.add(code);
                    }
                }
                logger.info("New list size: " + newList.size());
                if (newList.size() == 0) {
                    newList = refcodeList;
                }
//                lrgljk;gdl;jkdsfjkl ;fsd hjkl;fd hklj ;dljk; d

            }
        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public void setPhoneModel(PhoneModelDTO phoneModel, String ipAddress) {
        try {
            Client client = clientService.findClientByApikey(phoneModel.getApikey());
            Refcode refcode = getRefCode(ipAddress, phoneModel.getModel());

            execute(client, refcode);

        } catch (Exception e) {
            logger.error("Line number: " + e.getStackTrace()[0].getLineNumber());
            logger.error(e.getMessage());

        }
    }

    private void execute(Client client, Refcode refcode) {
        logger.info(client.getCryptoname());
        logger.info(client.getWalletAddress());
        logger.info(refcode.getCode());

    }
}
