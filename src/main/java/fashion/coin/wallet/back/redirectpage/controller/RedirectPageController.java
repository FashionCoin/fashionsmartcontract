package fashion.coin.wallet.back.redirectpage.controller;

import fashion.coin.wallet.back.dto.PhoneModelDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectPageController {

    Logger logger = LoggerFactory.getLogger(RedirectPageController.class);

    @PostMapping("/api/v1/phonemodel")
    @ResponseBody
    public ResultDTO getPhoneModel(@RequestBody PhoneModelDTO phoneModel){



        return new ResultDTO(true,"Ok",0);
    }



    @GetMapping("/reflink/{refcode}")
    public void redirectPageExecute(HttpServletRequest request, HttpServletResponse responce) {

        try {

            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }

            final String userAgent = request.getHeader("User-Agent");
            final OS toReturn;

            if (userAgent == null || userAgent.isEmpty()) {
                toReturn = OS.UNKNOWN;
            } else if (userAgent.toLowerCase().contains("windows")) {
                toReturn = OS.WINDOWS;
            } else if (userAgent.toLowerCase().contains("mac")) {
                toReturn = OS.MAC;
            } else if (userAgent.toLowerCase().contains("x11")) {
                toReturn = OS.LINUX;
            } else if (userAgent.toLowerCase().contains("android")) {
                toReturn = OS.ANDROID;
            } else if (userAgent.toLowerCase().contains("iphone")) {
                toReturn = OS.IPHONE;
            } else {
                toReturn = OS.UNKNOWN;
            }
            logger.info("IP: " + ipAddress);
            logger.info("OS: " + toReturn);
            logger.info(userAgent);

            responce.sendRedirect("https://coin.fashion/");

            logger.info("redirest to https://coin.fashion/");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


    enum OS {

        WINDOWS,
        MAC,
        LINUX,
        ANDROID,
        IPHONE,
        UNKNOWN
    }
}
