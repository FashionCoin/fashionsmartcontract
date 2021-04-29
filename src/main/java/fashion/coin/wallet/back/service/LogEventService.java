package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.LogEvent;
import fashion.coin.wallet.back.repository.LogEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class LogEventService {

    @Autowired
    LogEventRepository logEventRepository;

    @Autowired
    ClientService clientService;

    public void save(HttpServletRequest request, String apikey, String cryptoname) {
        try {
            LogEvent logEvent = new LogEvent();
            logEvent.setTimestamp(System.currentTimeMillis());
            logEvent.setLocalDateTime(LocalDateTime.now());
            logEvent.setApiKey(apikey);
            logEvent.setCryptoname(cryptoname);
            logEvent.setIp(getIpAddress(request));
            logEvent.setUserAgent(getUserAgent(request));

            logEventRepository.save(logEvent);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void saveTrans(HttpServletRequest httpServletRequest, String senderWallet, String receiverWallet) {
        try {
            Client client = clientService.findByWallet(senderWallet);

            save(httpServletRequest, client.getApikey(), client.getCryptoname());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
