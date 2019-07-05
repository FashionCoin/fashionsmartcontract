package fashion.coin.wallet.back.apple.service;

import fashion.coin.wallet.back.apple.entity.AppleRefInstall;
import fashion.coin.wallet.back.apple.repository.AppleReferalRepository;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppleRefService {

    @Autowired
    AppleReferalRepository appleReferalRepository;

    @Autowired
    ClientService clientService;


    public boolean registerDevice(String api_key, String ipAddress, String userAgent) {
        try {
            Client client = clientService.findClientByApikey(api_key);

            if (client != null) {
                if (client.getWalletAddress() != null && client.getWalletAddress().length() > 0) return false;
                String cryptoName = client.getCryptoname();

                AppleRefInstall appleRefInstall = new AppleRefInstall(
                        api_key,
                        cryptoName,
                        userAgent,
                        ipAddress,
                        LocalDateTime.now()
                );
                appleReferalRepository.save(appleRefInstall);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AppleRefInstall> findByIp(String ipAddress) {

        List<AppleRefInstall> appleRefInstallList = appleReferalRepository.findAllByIpAddressAndLocalDateTimeAfter(
                ipAddress,
                LocalDateTime.now().minusMinutes(30));
        if (appleRefInstallList == null) return new ArrayList<>();
        return appleRefInstallList;
    }
}
