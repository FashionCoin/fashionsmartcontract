package fashion.coin.wallet.back.tranzzo.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.CurrencyService;
import fashion.coin.wallet.back.tranzzo.dto.*;
import fashion.coin.wallet.back.tranzzo.entity.BuyFshn;
import fashion.coin.wallet.back.tranzzo.entity.Tranzzo;
import fashion.coin.wallet.back.tranzzo.repository.BuyFshnRepository;
import fashion.coin.wallet.back.tranzzo.repository.TranzzoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error230;

// https://cdn.tranzzo.com/tranzzo-api/index.html#direct-payments-with-card-data


@Service
public class TranzzoService {

    Logger logger = LoggerFactory.getLogger(TranzzoService.class);

    @Value("${tranzzo.pos.id}")
    String posId;

    @Value("${tranzzo.api.key}")
    String apiKey;

    @Value("${tranzzo.secret.key}")
    String secretKey;

    @Value("${tranzzo.x.api.key}")
    String xApiKey;

    @Value("${fashion.host}")
    String fashinHost;

    @Value("${tranzzo.payment.url}")
    String paymentUrl;


    @Autowired
    Gson gson;

    @Autowired
    TranzzoRepository tranzzoRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    BuyFshnRepository buyFshnRepository;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    RestTemplate restTemplate;

    public Tranzzo saveRequest(TranzzoPaymentRequestDTO request) {

        Tranzzo tranzzo = new Tranzzo();
        tranzzo.setTimestamp(System.currentTimeMillis());
        tranzzo.setLocalDateTime(LocalDateTime.now());

        tranzzo.setPosId(request.getPosId());
        tranzzo.setMode(request.getMode());
        tranzzo.setMethod(request.getMethod());
        tranzzo.setAmount(new BigDecimal(request.getAmount()));
        tranzzo.setCurrency(request.getCurrency());
        tranzzo.setDescription(request.getDescription());
        tranzzo.setOrderId(request.getOrderId());
        tranzzo.setOrder3dsBypass(request.getOrder3dsBypass());
        tranzzo.setCcNumber(request.getCcNumber());
        tranzzo.setExpMonth(request.getExpMonth());
        tranzzo.setExpYear(request.getExpYear());
        tranzzo.setCardCvv(request.getCardCvv());
        tranzzo.setServerUrl(request.getServerUrl());
        tranzzo.setResultUrl(request.getResultUrl());
        tranzzo.setPayload(request.getPayload());

        BrowserFingerprintDTO fingerprint = request.getBrowserFingerprint();

        tranzzo.setBrowserColorDepth(fingerprint.getBrowserColorDepth());
        tranzzo.setBrowserScreenHeight(fingerprint.getBrowserScreenHeight());
        tranzzo.setBrowserScreenWidth(fingerprint.getBrowserScreenWidth());
        tranzzo.setBrowserJavaEnabled(fingerprint.getBrowserJavaEnabled());
        tranzzo.setBrowserLanguage(fingerprint.getBrowserLanguage());
        tranzzo.setBrowserTimeZone(fingerprint.getBrowserTimeZone());
        tranzzo.setBrowserTimeZoneOffset(fingerprint.getBrowserTimeZoneOffset());
        tranzzo.setBrowserAcceptHeader(fingerprint.getBrowserAcceptHeader());
        tranzzo.setBrowserAcceptHeader(tranzzo.getBrowserIpAddress());
        tranzzo.setBrowserUserAgent(fingerprint.getBrowserUserAgent());

        tranzzoRepository.save(tranzzo);

        return tranzzo;
    }

    public Tranzzo saveResponse(Tranzzo tranzzo, TranzzoPaymentResponseDTO response) {

        tranzzo.setPaymentId(response.getPaymentId());
        tranzzo.setOrderId(response.getOrderId());
        tranzzo.setGatewayOrderId(response.getGatewayOrderId());
        tranzzo.setBillingOrderId(response.getBillingOrderId());
        tranzzo.setTransactionId(response.getTransactionId());
        tranzzo.setPosId(response.getPosId());
        tranzzo.setMode(response.getMode());
        tranzzo.setMethod(response.getMethod());
        tranzzo.setAmount(response.getAmount());
        tranzzo.setCurrency(response.getCurrency());
        tranzzo.setDescription(response.getDescription());
        tranzzo.setStatus(response.getStatus());
        tranzzo.setStatusCode(response.getStatusCode());
        tranzzo.setStatusDescription(response.getStatusDescription());
        tranzzo.setUserActionRequired(response.getUserActionRequired());
        tranzzo.setUserActionUrl(response.getUserActionUrl());
        tranzzo.setEci(response.getEci());
        tranzzo.setMcc(response.getMcc());
        tranzzo.setOptions3ds(response.getOptions3ds());
        tranzzo.setCcMask(response.getCcMask());
        tranzzo.setCcToken(response.getCcToken());
        tranzzo.setCcTokenExpiration(response.getCcTokenExpiration());
        tranzzo.setCustomerId(response.getCustomerId());
        tranzzo.setCustomerIp(response.getCustomerIp());
        tranzzo.setCustomerFname(response.getCustomerFname());
        tranzzo.setCustomerLname(response.getCustomerLname());
        tranzzo.setCustomerEmail(response.getCustomerEmail());
        tranzzo.setCustomerPhone(response.getCustomerPhone());
        tranzzo.setCustomerCountry(response.getCustomerCountry());
        tranzzo.setResultUrl(response.getResultUrl());
        tranzzo.setCreatedAt(response.getCreatedAt());
        tranzzo.setProcessingTime(response.getProcessingTime());
        tranzzo.setPayload(response.getPayload());
        tranzzo.setBankShortName(response.getBankShortName());

        tranzzoRepository.save(tranzzo);

        return tranzzo;
    }

    public Tranzzo saveError(Tranzzo tranzzo, String error) {
        tranzzo.setError(error);
        tranzzoRepository.save(tranzzo);
        return tranzzo;
    }

    public ResultDTO createPayment(CreateTranzzoPaymentDTO request, HttpServletRequest servletRequest) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            if (!checkCurrency(request)) {
                return error230;
            }

            BuyFshn buyFshn = new BuyFshn();
            buyFshn.setTimestamp(System.currentTimeMillis());
            buyFshn.setLocalDateTime(LocalDateTime.now());
            buyFshn.setClientId(client.getId());
            buyFshn.setCryptoname(client.getCryptoname());
            buyFshn.setFshnAmount(request.getFshnAmount());
            buyFshn.setUsdAmount(request.getUsdAmount());
            buyFshn.setUahAmount(request.getUahAmount());
            buyFshn.setEmail(request.getEmail());
            buyFshn.setPhone(request.getPhone());

            buyFshn.setIpAddress(getIpAddress(servletRequest));
            buyFshn.setUserAgent(getUserAgent(servletRequest));

            buyFshnRepository.save(buyFshn);

            return new ResultDTO(true, buyFshn, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private boolean checkCurrency(CreateTranzzoPaymentDTO request) {

        String uahStringRate = currencyService.getCurrencyRate("UAH").getRate();
        BigDecimal uahRate = new BigDecimal(uahStringRate);

        BigDecimal uah = request.getFshnAmount().divide(uahRate, 3, RoundingMode.HALF_UP);

        BigDecimal difference = uah.divide(request.getUahAmount(), 6, RoundingMode.HALF_UP);
        Double diff = difference.doubleValue();
        if (diff < 0.99 || diff > 1.01) {
            logger.error("diff: {}", diff);
            logger.error("uahRate: {}", uahRate);
            logger.error("uah: {}", uah);
            logger.error("difference: {}", difference);
            logger.error("request: {}", gson.toJson(request));
            return false;
        } else {
            return true;
        }

    }

    public ResultDTO paymentPay(BuyFshnTranzzoDTO request, HttpServletRequest servletRequest) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            BuyFshn buyFshn = buyFshnRepository.findById(request.getPaymentId()).orElse(null);

            buyFshn.setCardNumberMask(maskPAN(request.getCardNumber()));
            buyFshn.setIpAddress(getIpAddress(servletRequest));
            buyFshn.setUserAgent(getUserAgent(servletRequest));
            buyFshnRepository.save(buyFshn);

            TranzzoPaymentRequestDTO paymentRequest = new TranzzoPaymentRequestDTO();
            paymentRequest.setPosId(posId);
            paymentRequest.setMode("direct");
            paymentRequest.setMethod("purchase");
//            paymentRequest.setAmount(buyFshn.getUahAmount().doubleValue());
            paymentRequest.setAmount(1.0); // TODO: sandbox
            paymentRequest.setCurrency("UAH");
            paymentRequest.setDescription("Buying FSHN for Ukrainian Hryvna");
            paymentRequest.setOrderId(String.valueOf(buyFshn.getPaymentId()));
            paymentRequest.setOrder3dsBypass("always");
            paymentRequest.setCcNumber(request.getCardNumber());
            paymentRequest.setExpMonth(Integer.parseInt(request.getExpMonth()));
            paymentRequest.setExpYear(Integer.parseInt(request.getExpYear()));
            paymentRequest.setCardCvv(request.getCvv());
            paymentRequest.setServerUrl(fashinHost + "/api/v1/tranzzo/callback");
            paymentRequest.setResultUrl(request.getResultUrl());

            BrowserFingerprintDTO fingerprint = new BrowserFingerprintDTO();
            fingerprint.setBrowserColorDepth(request.getBrowserColorDepth());
            fingerprint.setBrowserScreenHeight(request.getBrowserScreenHeight());
            fingerprint.setBrowserScreenWidth(request.getBrowserScreenWidth());
            fingerprint.setBrowserJavaEnabled(request.getBrowserJavaEnabled());
            fingerprint.setBrowserLanguage(request.getBrowserLanguage());
            fingerprint.setBrowserTimeZone(request.getBrowserTimeZone());
            fingerprint.setBrowserTimeZoneOffset(request.getBrowserTimeZoneOffset());
            fingerprint.setBrowserAcceptHeader(request.getBrowserAcceptHeader());
            fingerprint.setBrowserIpAddress(request.getBrowserIpAddress());
            fingerprint.setBrowserUserAgent(request.getBrowserUserAgent());

            paymentRequest.setBrowserFingerprint(fingerprint);

            logger.info("Tranzzo sandbox request: {}", gson.toJson(paymentRequest));


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-AUTH", "CPAY " + apiKey + ":" + secretKey);
            headers.set("X-API-KEY", xApiKey);
            headers.set("X-Requested-With", "XMLHttpRequest");

            HttpEntity entity = new HttpEntity(paymentRequest, headers);

            logger.info(gson.toJson(entity));
            logger.info(paymentUrl);

//            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, entity, String.class);

            ResponseEntity<String> response = restTemplate.postForEntity(paymentUrl, entity, String.class);


            paymentRequest.setCcNumber(maskPAN(paymentRequest.getCcNumber()));

            Tranzzo tranzzo = saveRequest(paymentRequest);

            logger.info("Tranzzo response: {}", gson.toJson(response));

            if (response.getStatusCode().isError()) {
                if (response.hasBody()) {
                    logger.error(response.getBody());
                    saveError(tranzzo, response.getBody());
                } else {
                    logger.error(gson.toJson(response.getStatusCode()));
                    saveError(tranzzo, gson.toJson(response.getStatusCode()));
                }

                return new ResultDTO(false, tranzzo.getError(), -1);
            } else {
                logger.info(response.getBody());
                TranzzoPaymentResponseDTO tranzzoResponse = gson.fromJson(response.getBody(), TranzzoPaymentResponseDTO.class);
                saveResponse(tranzzo, tranzzoResponse);

                return new ResultDTO(true, tranzzoResponse.getUserActionUrl(), 0);
            }

        } catch (RestClientException re) {
            re.printStackTrace();
            logger.info(gson.toJson(re));
            return new ResultDTO(false, re.getMessage(), -1);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private String getUserAgent(HttpServletRequest servletRequest) {
        return servletRequest.getHeader("User-Agent");
    }

    private String getIpAddress(HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = servletRequest.getRemoteAddr();
        }
        return ipAddress;
    }

    private String maskPAN(String cardNumber) {
        return
                cardNumber.substring(0, 6)
                        + "******"
                        + cardNumber.substring(12, 16);
    }

    public String interaction(String request) {

        logger.info("Tranzzo callback: {}", request);

        return "Ok";
    }
}
