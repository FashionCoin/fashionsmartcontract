package fashion.coin.wallet.back.apple.controller;

import fashion.coin.wallet.back.apple.entity.AppleRefInstall;
import fashion.coin.wallet.back.apple.service.AppleRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class RefinstallController {

    @Autowired
    AppleRefService appleRefService;

    @GetMapping("/api/v1/google/refinstall")
    void redirectToGoogle(HttpServletRequest request, HttpServletResponse httpServletResponse,
                         @RequestParam String api_key) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");

        appleRefService.registerDevice(api_key, ipAddress, userAgent);

        httpServletResponse.setHeader("Location", "https://play.google.com/store/apps/details?id=wallet.fashion.coin");
        httpServletResponse.setStatus(302);

    }
    @GetMapping("/api/v1/apple/refinstall")
    void redirectToApple(HttpServletRequest request, HttpServletResponse httpServletResponse,
                         @RequestParam String api_key) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");

        appleRefService.registerDevice(api_key, ipAddress, userAgent);

        httpServletResponse.setHeader("Location", "https://www.apple.com/ios/app-store/");
        httpServletResponse.setStatus(302);

    }

    @PostMapping("/api/v1/apple/getrefinstall")
    @ResponseBody
    List<AppleRefInstall> getRefInstall(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        List<AppleRefInstall> appleRefInstall = appleRefService.findByIp(ipAddress);
        return appleRefInstall;
    }

}
