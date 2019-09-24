package fashion.coin.wallet.back.redirectpage.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.PhoneModelDTO;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.redirectpage.service.RefcodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectPageController {

    Logger logger = LoggerFactory.getLogger(RedirectPageController.class);

    @Autowired
    RefcodeService refcodeService;

    @Autowired
    Gson gson;

    @PostMapping("/api/v1/phonemodel")
    @ResponseBody
    public ResultDTO setPhoneModel(@RequestBody PhoneModelDTO phoneModel, HttpServletRequest request) {


        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        logger.info(ipAddress + " : " + gson.toJson(phoneModel));
        refcodeService.setPhoneModel(phoneModel, ipAddress);

        return new ResultDTO(true, "Ok", 0);
    }


    @GetMapping("/reflink/{refcode}")
    public void redirectPageExecute(HttpServletRequest request, HttpServletResponse responce,
                                    @PathVariable String refcode) {

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

            refcodeService.saveRefCode(refcode,ipAddress,toReturn.name(),userAgent);

        } catch (Exception e) {
            logger.error("Line number: "+e.getStackTrace()[0].getLineNumber());
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

