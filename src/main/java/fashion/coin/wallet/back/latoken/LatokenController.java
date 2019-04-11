package fashion.coin.wallet.back.latoken;

import com.google.gson.Gson;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
public class LatokenController {

    private LatokenService latokenService;
    private Gson gson;


    @PostMapping("/api/latoken/checkemail")
    @ResponseBody
    LatokenResponceDTO registration(@RequestBody LatokenRequestDTO data,
                                    HttpServletRequest request) throws Exception {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        if (!ipAddress.equals(latokenIp)) {
            System.out.println("Access Denied from IP: " + ipAddress);
            throw new Exception("Access Denied");
        }
        System.out.println("Request from Latoken:" + gson.toJson(data));
        LatokenResponceDTO result = latokenService.checkEMail(data);
        System.out.println("Responce to Latoken:" + gson.toJson(result));
        return result;
    }


    @Autowired
    public void setLatokenService(LatokenService latokenService) {
        this.latokenService = latokenService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Value("${latoken.ip}")
    private String latokenIp;
}
