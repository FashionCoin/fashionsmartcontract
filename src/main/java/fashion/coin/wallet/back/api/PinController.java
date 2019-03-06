package fashion.coin.wallet.back.api;

import fashion.coin.wallet.back.dto.ChangePinDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.PinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.management.modelmbean.ModelMBean;

/**
 * Created by JAVA-P on 01.11.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class PinController {

    private PinService pinService;


    @PostMapping("/api/v1/restorepin")
    @ResponseBody
    ResultDTO restorePin(@RequestBody ChangePinDTO changePinDTO) {
        return pinService.restorePin(changePinDTO);
    }

    @GetMapping("/restorepin")
    String restorePinRedirect(ModelMap modelMap, @RequestParam String recovery) {
        modelMap.addAttribute("recovery", recovery);
        return "restorepin";
    }


    @Autowired
    public void setPinService(PinService pinService) {
        this.pinService = pinService;
    }
}
