package fashion.coin.wallet.back.tranzzo.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.tranzzo.dto.BuyFshnTranzzoDTO;
import fashion.coin.wallet.back.tranzzo.dto.CreateTranzzoPaymentDTO;
import fashion.coin.wallet.back.tranzzo.dto.GetTanzzoStatusDTO;
import fashion.coin.wallet.back.tranzzo.service.TranzzoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data

@Controller
public class TranzzoController {

    Logger logger = LoggerFactory.getLogger(TranzzoController.class);

    @Autowired
    Gson gson;

    @Autowired
    TranzzoService tranzzoService;

    @PostMapping("/api/v1/tranzzo/payment/create")
    @ResponseBody
    ResultDTO createPayment(@RequestBody CreateTranzzoPaymentDTO request, HttpServletRequest servletRequest) {
        logger.info("Create payment");
        logger.info(gson.toJson(request));
        return tranzzoService.createPayment(request, servletRequest);
    }


    @PostMapping("/api/v1/tranzzo/payment/pay")
    @ResponseBody
    ResultDTO paymentPay(@RequestBody BuyFshnTranzzoDTO request, HttpServletRequest servletRequest) {
        logger.info("Payment pay");
        return tranzzoService.paymentPay(request, servletRequest);
    }
    @PostMapping("/api/v1/tranzzo/payment/status")
    @ResponseBody
    ResultDTO paymentStatus(@RequestBody GetTanzzoStatusDTO request, HttpServletRequest servletRequest) {
        logger.info("Payment status");
        return tranzzoService.paymentStatus(request, servletRequest);
    }

    @PostMapping("/api/v1/tranzzo/callback")
    @ResponseBody
//    String interaction(@RequestBody String request) {
    String interaction(@RequestParam String data, @RequestParam String signature) {
        logger.info("Interaction Tanzzo");
        logger.info("Data: {}", data);
        logger.info("Signature: {}", signature);
        return tranzzoService.interaction(data,signature);
    }
}
