package fashion.coin.wallet.back.fiat;

import com.google.gson.Gson;
import fashion.coin.wallet.back.latoken.LatokenRequestDTO;
import fashion.coin.wallet.back.latoken.LatokenResponceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FiatController {


    private static final String FIAT_IP = "193.99.999.99";

    private FiatService fiatService;
    private Gson gson;

    @PostMapping("/api/v1/fiat/checkphone")
    @ResponseBody
    CheckPhoneResponceDTO checkPhone(@RequestBody CheckPhoneRequestDTO data,
                                    HttpServletRequest request) throws Exception {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        if (!ipAddress.equals(FIAT_IP)) {
            System.out.println("Access Denied from IP: " + ipAddress);
            throw new Exception("Access Denied");
        }
        System.out.println("Request from Fiat:" + gson.toJson(data));
        CheckPhoneResponceDTO result = fiatService.checkPhone(data);
        System.out.println("Responce to Fiat:" + gson.toJson(result));
        return result;
    }

    @Autowired
    public void setFiatService(FiatService fiatService) {
        this.fiatService = fiatService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
