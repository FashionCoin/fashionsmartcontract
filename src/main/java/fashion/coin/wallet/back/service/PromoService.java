package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.PromoDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Bloggers;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.Relationship;
import fashion.coin.wallet.back.repository.BloggerRepository;
import fashion.coin.wallet.back.repository.RelationshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class PromoService {

    Logger logger = LoggerFactory.getLogger(PromoService.class);

    @Autowired
    Gson gson;

    @Autowired
    RelationshipRepository relationshipRepository;

    @Autowired
    BloggerRepository bloggerRepository;

    @Autowired
    ClientService clientService;

    public ResultDTO setPromo(HttpServletRequest request, PromoDTO promo) {
        try {

            String ip = getIpAddress(request);
            String useragent = getUserAgent(request);

//            if (promo.getNameList() != null && promo.getNameList().size() > 0) {
//                for (String cname : promo.getNameList()) {
//                    for (String alter : promo.getNameList()) {
//                        if (!cname.equals(alter)) {
//                            Relationship relationship = relationshipRepository.findTopByCryptonameAndAlterego(cname, alter);
//                            if (relationship == null) {
//                                relationship = new Relationship();
//                                relationship.setIp(ip);
//                                relationship.setUseragent(useragent);
//                                relationship.setCryptoname(cname);
//                                relationship.setAlterego(alter);
//                                relationship.setLocalDateTime(LocalDateTime.now());
//                                relationship.setTimestamp(System.currentTimeMillis());
//                                relationshipRepository.save(relationship);
//                            }
//                        }
//                    }
//                }
//            }
            logger.info(gson.toJson(promo));
            if (promo.getPromocode() != null && promo.getPromocode().equals("Proof-of-Love")) {
                logger.info("Valid Code");
                Client client = clientService.findClientByApikey(promo.getApikey());
                if (client != null) {

                    Bloggers bloggers = bloggerRepository.findTopByCryptoname(client.getCryptoname());
                    if (bloggers == null) {
                        bloggers = new Bloggers();
                        bloggers.setLocalDateTime(LocalDateTime.now());
                        bloggers.setTimestamp(System.currentTimeMillis());
                        bloggers.setCryptoname(client.getCryptoname());
                        bloggerRepository.save(bloggers);
                    } else {
                        logger.error("Blogger: {}", gson.toJson(bloggers));
                    }
                } else {
                    logger.error("ApiKey: {}",promo.getApikey());
                    logger.error("Client: {}", client);
                }

            } else if (promo.getPromocode() != null && !promo.getPromocode().equals("Proof-of-Love")) {
                logger.error("Invalid Code");
                logger.error("Promocode: {}", promo.getPromocode());
            } else {
                logger.info("Void code");
            }
            return new ResultDTO(true, "OK", 0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(gson.toJson(promo));
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private String getUserAgent(HttpServletRequest servletRequest) {
        return servletRequest.getHeader("User-Agent");
    }

    private String getIpAddress(HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = servletRequest.getRemoteAddr();
        }
        return ipAddress;
    }
}
