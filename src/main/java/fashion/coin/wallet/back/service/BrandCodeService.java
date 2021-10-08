package fashion.coin.wallet.back.service;

import com.google.gson.Gson;

import fashion.coin.wallet.back.entity.BrandCode;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.repository.BrandCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;


@Service
public class BrandCodeService {

    Logger logger = LoggerFactory.getLogger(BrandCodeService.class);

    @Value("${fashion.brandcode}")
    String[] brandCodeList;

    @Autowired
    BrandCodeRepository brandCodeRepository;

    @Autowired
    Gson gson;


    private boolean listIsRefreshed = false;

    private void refreshBrandlist() {
        if (!listIsRefreshed) {
            for (int i = 0; i < brandCodeList.length; i++) {
                BrandCode brandCode = brandCodeRepository.findById(brandCodeList[i]).orElse(null);
                if (brandCode == null) {
                    brandCode = new BrandCode(brandCodeList[i]);
                    brandCodeRepository.save(brandCode);
                }
            }

//            try {
//                MessageDigest digest = MessageDigest.getInstance("SHA-256");
//                for (int i = 0; i < 98; i++) {
//                    byte[] encodedhash = digest.digest(("brand" + i).getBytes());
//
//                    String brnd = "CAPS" + bytesToHex(encodedhash).substring(0, 8);
//
//                    BrandCode brandCode = brandCodeRepository.findById(brnd).orElse(null);
//                    if (brandCode == null) {
//                        brandCode = new BrandCode(brnd);
//                        brandCodeRepository.save(brandCode);
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }


            listIsRefreshed = true;
        }
    }

    String checkBrandCode(String codeCandidat) {
        if (codeCandidat.contains(":")) {
            logger.info("codeCandidat " + codeCandidat);
            refreshBrandlist();

            int colonePosition = codeCandidat.indexOf(":");

            String brcode = codeCandidat.substring(0, colonePosition).replace("case", "CASE");
            logger.info("brcode: " + brcode);

            BrandCode brandCode = brandCodeRepository.findById(brcode).orElse(null);
            logger.info("brandCode: {}", gson.toJson(brandCode));
            if (brandCode != null && brandCode.getWallet() == null) {
                String cryptoname = codeCandidat.substring(colonePosition + 1);
                logger.info("cryptoname: {}", cryptoname);
                if (cryptoname.length() > 0) {
                    logger.info("checkCode: " + cryptoname);
                    brandCode.setBrand(cryptoname);
                    brandCode.setUsed(LocalDateTime.now());
                    brandCodeRepository.save(brandCode);
                    return cryptoname;
                }
            }
        }
        return null;
    }


    public void registerClient(Client client) throws IllegalAccessException {
        logger.info("Client: " + gson.toJson(client));

        String cryptoname = client.getCryptoname();
        BrandCode brandCode = brandCodeRepository.findByBrand(cryptoname);
        if (brandCode == null) return;
        if (brandCode.getWallet() != null && brandCode.getWallet().length() > 0) return;
        if (client.getWalletAddress() == null || client.getWalletAddress().length() == 0) return;
        brandCode.setWallet(client.getWalletAddress());
        brandCode.setClient(client.getId());
        brandCodeRepository.save(brandCode);
        logger.info("Client Brand " + cryptoname + " registered");


    }

    // TODO: Check logic!!
    public boolean brandAvaliable(String cryptoname) {
        try {
            BrandCode brandCode = brandCodeRepository.findByBrand(cryptoname);
            if (brandCode == null) return false;
            logger.info("brandcode: " + gson.toJson(brandCode));
            if (brandCode.getWallet() != null && brandCode.getWallet().length() > 0) return false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getBrandFromCode(String codeCandidat) {
        if (codeCandidat.contains(":")) {
            logger.info("codeCandidat " + codeCandidat);
            refreshBrandlist();

            int colonePosition = codeCandidat.indexOf(":");

            String brcode = codeCandidat.substring(0, colonePosition).replace("case", "CASE");
            logger.info("brcode: " + brcode);

            BrandCode brandCode = brandCodeRepository.findById(brcode).orElse(null);
            logger.info("brandCode: {}", gson.toJson(brandCode));
            if (brandCode != null) {
                String cryptoname = codeCandidat.substring(colonePosition + 1);
                logger.info("cryptoname: {}", cryptoname);
                if (cryptoname.length() > 0) {
                    logger.info("checkCode: " + cryptoname);
                    return cryptoname;
                }
            }
        }
        return null;

    }
}

