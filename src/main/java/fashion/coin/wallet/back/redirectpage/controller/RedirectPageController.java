package fashion.coin.wallet.back.redirectpage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectPageController {

    Logger logger = LoggerFactory.getLogger(RedirectPageController.class);


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

