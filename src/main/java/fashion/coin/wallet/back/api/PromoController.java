package fashion.coin.wallet.back.api;


import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.PromoDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.PromoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PromoController {

    Logger logger = LoggerFactory.getLogger(PromoController.class);

    @Autowired
    Gson gson;

    @Autowired
    PromoService promoService;

    @PostMapping("/api/v1/promo/setpromo")
    @ResponseBody
    ResultDTO setPromo(HttpServletRequest request, @RequestBody PromoDTO promo) {

        ResultDTO result = promoService.setPromo(request, promo);
        return result;
    }

}
