package fashion.coin.wallet.back.fiat.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.fiat.dto.*;
import fashion.coin.wallet.back.fiat.entity.FiatPayment;
import fashion.coin.wallet.back.fiat.service.FiatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FiatController {

    private FiatService fiatService;
    private Gson gson;

    @PostMapping("/api/v1/fiat/checkphone")
    @ResponseBody
    CheckPhoneResponceDTO checkPhone(@RequestBody CheckPhoneRequestDTO data,
                                     HttpServletRequest request) throws Exception {
        checkIp(request);
        System.out.println("Request from Fiat:" + gson.toJson(data));
        CheckPhoneResponceDTO result = fiatService.checkPhone(data);
        System.out.println("Responce to Fiat:" + gson.toJson(result));
        return result;
    }

    @PostMapping("/api/v1/fiat/pay")
    @ResponseBody
    PayResponceDTO createPayment(@RequestBody PayRequestDTO data,
                                 HttpServletRequest request) throws Exception {
        checkIp(request);
        System.out.println("Request from Fiat:" + gson.toJson(data));
        PayResponceDTO result = fiatService.createPayment(data);
        System.out.println("Responce to Fiat:" + gson.toJson(result));
        return result;
    }

    @PostMapping("/api/v1/fiat/logs")
    @ResponseBody
    PaymentHistoryDTO getHistory(@RequestBody LogsRequestDTO data,
                                 HttpServletRequest request) throws Exception {
        checkIp(request);
        System.out.println("Request from Fiat:" + gson.toJson(data));
        PaymentHistoryDTO result = fiatService.getHistory(data);
        System.out.println("Responce to Fiat:" + gson.toJson(result));
        return result;
    }

    @PostMapping("/api/v1/fiat/status")
    @ResponseBody
    FiatPayment getPaymentStatus(@RequestBody PaymentStatusRequestDTO data,
                                 HttpServletRequest request) throws Exception {
        checkIp(request);
        System.out.println("Request from Fiat:" + gson.toJson(data));
        FiatPayment result = fiatService.getPaymentStatus(data);
        System.out.println("Responce to Fiat:" + gson.toJson(result));
        return result;
    }


    private void checkIp(HttpServletRequest request) throws Exception {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        if (!ipAddress.equals(fiatIp)) {
            System.out.println("Access Denied from IP: " + ipAddress);
            throw new Exception("Access Denied");
        }
    }

    @Autowired
    public void setFiatService(FiatService fiatService) {
        this.fiatService = fiatService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Value("${fiat.ip}")
    private String fiatIp;
}
