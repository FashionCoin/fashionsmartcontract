package fashion.coin.wallet.back.redirectpage.service;

import fashion.coin.wallet.back.redirectpage.RefcodeRepository;
import fashion.coin.wallet.back.redirectpage.entity.Refcode;

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
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


}
