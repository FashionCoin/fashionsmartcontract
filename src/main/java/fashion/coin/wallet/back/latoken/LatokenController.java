package fashion.coin.wallet.back.latoken;

import com.google.gson.Gson;

import jdk.internal.jline.internal.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
public class LatokenController {

    private static final String LATOKEN_IP = "104.20.245.57"; // TODO: set real address

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
        if (!ipAddress.equals(LATOKEN_IP)) {
            Log.error("Access Denied from IP: " + ipAddress);
            throw new Exception("Access Denied");
        }
        Log.info("Request from Latoken:" + gson.toJson(data));
        LatokenResponceDTO result = latokenService.checkEMail(data);
        Log.info("Responce to Latoken:" + gson.toJson(result));
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
}
