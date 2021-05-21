package fashion.coin.wallet.back.tranzzo.service;

import com.google.gson.Gson;
import com.google.inject.internal.asm.$ClassTooLargeException;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.BuyNftDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.service.NftService;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.CurrencyService;
import fashion.coin.wallet.back.tranzzo.dto.*;
import fashion.coin.wallet.back.tranzzo.entity.BuyFshn;
import fashion.coin.wallet.back.tranzzo.entity.BuyNft;
import fashion.coin.wallet.back.tranzzo.entity.Tranzzo;
import fashion.coin.wallet.back.tranzzo.repository.BuyFshnRepository;
import fashion.coin.wallet.back.tranzzo.repository.BuyNftRepository;
import fashion.coin.wallet.back.tranzzo.repository.TranzzoRepository;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

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
    BuyNftRepository buyNftRepository;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AIService aiService;

    @Autowired
    NftService nftService;

    public Tranzzo saveRequest(TranzzoPaymentRequestDTO request) {

        Tranzzo tranzzo = new Tranzzo();
        tranzzo.setTimestamp(System.currentTimeMillis());
        tranzzo.setLocalDateTime(LocalDateTime.now());

        tranzzo.setPosId(request.getPos_id());
        tranzzo.setMode(request.getMode());
        tranzzo.setMethod(request.getMethod());
        tranzzo.setAmount(new BigDecimal(request.getAmount()));
        tranzzo.setCurrency(request.getCurrency());
        tranzzo.setDescription(request.getDescription());
        tranzzo.setOrderId(request.getOrder_id());
        tranzzo.setOrder3dsBypass(request.getOrder_3ds_bypass());
        tranzzo.setCcNumber(maskPAN(request.getCc_number()));
        tranzzo.setExpMonth(request.getExp_month());
        tranzzo.setExpYear(request.getExp_year());
        tranzzo.setCardCvv(request.getCard_cvv());
        tranzzo.setServerUrl(request.getServer_url());
        tranzzo.setResultUrl(request.getResult_url());
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

        tranzzo.setPaymentId(response.getPayment_id());
        tranzzo.setOrderId(response.getOrder_id());
        tranzzo.setGatewayOrderId(response.getGateway_order_id());
        tranzzo.setBillingOrderId(response.getBilling_order_id());
        tranzzo.setTransactionId(response.getTransaction_id());
        tranzzo.setPosId(response.getPos_id());
        tranzzo.setMode(response.getMode());
        tranzzo.setMethod(response.getMethod());
        tranzzo.setAmount(response.getAmount());
        tranzzo.setCurrency(response.getCurrency());
        tranzzo.setDescription(response.getDescription());
        tranzzo.setStatus(response.getStatus());
        tranzzo.setStatusCode(response.getStatus_code());
        tranzzo.setStatusDescription(response.getStatus_description());
        tranzzo.setUserActionRequired(response.getUser_action_required());
        tranzzo.setUserActionUrl(response.getUser_action_url());
        tranzzo.setEci(response.getEci());
        tranzzo.setMcc(response.getMcc());
        tranzzo.setOptions3ds(response.getOptions3ds());
        tranzzo.setCcMask(response.getCc_mask());
        tranzzo.setCcToken(response.getCc_token());
        tranzzo.setCcTokenExpiration(response.getCc_token_expiration());
        tranzzo.setCustomerId(response.getCustomer_id());
        tranzzo.setCustomerIp(response.getCustomer_ip());
        tranzzo.setCustomerFname(response.getCustomer_fname());
        tranzzo.setCustomerLname(response.getCustomer_lname());
        tranzzo.setCustomerEmail(response.getCustomer_email());
        tranzzo.setCustomerPhone(response.getCustomer_phone());
        tranzzo.setCustomerCountry(response.getCustomer_country());
        tranzzo.setResultUrl(response.getResult_url());
        tranzzo.setCreatedAt(response.getCreated_at());
        tranzzo.setProcessingTime(response.getProcessing_time());
        tranzzo.setPayload(response.getPayload());
        tranzzo.setBankShortName(response.getBank_short_name());

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
            buyFshn.setAcceptHeader(getUserAcceptHeader(servletRequest));

            buyFshn.setWallet(client.getWalletAddress());

            buyFshnRepository.save(buyFshn);

            return new ResultDTO(true, buyFshn, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private String getUserAcceptHeader(HttpServletRequest servletRequest) {

        return servletRequest.getHeader("Accept");

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
        Tranzzo tranzzo = null;
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                logger.error(request.getApikey());
                logger.error("Client: {}", client);
                return error109;
            }

//            if(!checkCardNumber(request.getCardNumber())){
//                return error232;
//            }

            BuyFshn buyFshn = buyFshnRepository.findById(request.getPaymentId()).orElse(null);
            if (buyFshn == null) {
                return error231;
            }

            buyFshn.setCardNumberMask(maskPAN(request.getCardNumber()));
            buyFshn.setIpAddress(getIpAddress(servletRequest));
            buyFshn.setUserAgent(getUserAgent(servletRequest));
            buyFshnRepository.save(buyFshn);

            TranzzoPaymentRequestDTO paymentRequest = new TranzzoPaymentRequestDTO();
            paymentRequest.setPos_id(posId);
            paymentRequest.setMode("direct");
            paymentRequest.setMethod("purchase");
            paymentRequest.setAmount(buyFshn.getUahAmount().doubleValue());
//            paymentRequest.setAmount(1.0); // TODO: sandbox
            paymentRequest.setCurrency("UAH");
            paymentRequest.setDescription("Buying " + buyFshn.getFshnAmount() + " FSHN ");
            paymentRequest.setOrder_id(String.valueOf(buyFshn.getPaymentId()));
            paymentRequest.setOrder_3ds_bypass("always");
            paymentRequest.setCc_number(request.getCardNumber());
            paymentRequest.setExp_month(Integer.parseInt(request.getExpMonth()));
            paymentRequest.setExp_year(Integer.parseInt(request.getExpYear()));
            paymentRequest.setCard_cvv(request.getCvv());
            paymentRequest.setServer_url(fashinHost + "/api/v1/tranzzo/callback");
            paymentRequest.setResult_url(request.getResultUrl());

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

//            logger.info("Tranzzo sandbox request: {}", gson.toJson(paymentRequest));


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-AUTH", "CPAY " + apiKey + ":" + secretKey);
            headers.set("X-API-KEY", xApiKey);
            headers.set("X-Requested-With", "XMLHttpRequest");

            HttpEntity entity = new HttpEntity(paymentRequest, headers);

//            logger.info(gson.toJson(entity));
            logger.info(paymentUrl);

//            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, entity, String.class);

            tranzzo = saveRequest(paymentRequest);

            ResponseEntity<TranzzoPaymentResponseDTO> responseEntity = restTemplate.postForEntity(paymentUrl, entity, TranzzoPaymentResponseDTO.class);

            TranzzoPaymentResponseDTO tranzzoResponse = responseEntity.getBody();
            paymentRequest.setCc_number(maskPAN(paymentRequest.getCc_number()));


            saveResponse(tranzzo, tranzzoResponse);

            return new ResultDTO(true, tranzzoResponse.getUser_action_url(), 0);


        } catch (RestClientException re) {
            logger.error(re.getLocalizedMessage());

            String json = gson.toJson(re);
            Map<String, Object> errorMap = gson.fromJson(json, HashMap.class);

            List<Double> doubleResponse = (List<Double>) errorMap.get("responseBody");
            logger.error("Double Response: {}", gson.toJson(doubleResponse));
//            logger.error("Double Response: {}",gson.toJson(doubleResponse.getClass()));

            char[] respMessage = new char[doubleResponse.size()];
            for (int i = 0; i < doubleResponse.size(); i++) {
                Integer in = doubleResponse.get(i).intValue();
                respMessage[i] = Character.toChars(in)[0];
            }

            String error = new String(respMessage);
            logger.error("Tanzzo object: {}", tranzzo);
            logger.error("Error text: {}", error);
            saveError(tranzzo, error);

            return new ResultDTO(false, error, -1);

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

    public String interaction(String data, String signature) {
        logger.info("Tranzzo Callback Processed");
        String decodedData = new String(Base64.getUrlDecoder().decode(data));
        String decodedSignature = Hex.encodeHexString(Base64.getUrlDecoder().decode(signature));

        String sha1 = DigestUtils.sha1Hex(secretKey + data + secretKey);


        if (sha1.equals(decodedSignature)) {

            TranzzoPaymentResponseDTO response = gson.fromJson(decodedData, TranzzoPaymentResponseDTO.class);


            String orderId = response.getOrder_id();
            logger.info("Order ID: {}", orderId);
            Tranzzo tranzzo = tranzzoRepository.findTopByOrderId(orderId);
            if (tranzzo == null) {
                logger.error("Tranzzo not found by Order ID: {}", decodedData);
                return "FAIL";
            }
            tranzzo.setStatus(response.getStatus());
            tranzzo.setStatusCode(response.getStatus_code());
            tranzzo.setStatusDescription(response.getStatus_description());

            tranzzoRepository.save(tranzzo);

            BuyFshn buyFshn = buyFshnRepository.findById(Long.parseLong(orderId)).orElse(null);
            if (buyFshn == null) {
                logger.error("BuyFshn not found by Order ID: {}", decodedData);
                return "FAIL";
            }


            if (tranzzo.getStatus().equals("success")) {
                logger.info("Tranzzo payment status is {}", tranzzo.getStatus());
                if(!buyFshn.isLock()) {
                    buyLock(buyFshn);
                }else{
                    return "PROCESSED";
                }
                if (buyFshn.getTxHash() == null || buyFshn.getTxHash().length() == 0) {

                    ResultDTO resultDTO = aiService.transfer(buyFshn.getFshnAmount().toString(), buyFshn.getWallet(), AIService.AIWallets.MONEYBAG);
                    if (resultDTO.isResult()) {
                        logger.info("Tx Hash: {}", resultDTO.getMessage());
                        buyFshn.setTxHash(resultDTO.getMessage());
                        buyFshnRepository.save(buyFshn);
                        tryHardBuyNftForFiat(buyFshn);
                    } else {
                        logger.error(resultDTO.getMessage());
                        return "FAIL";
                    }
                } else {
                    logger.info("TxHash already exists: {}", buyFshn.getTxHash());
                }
                buyUnLock(buyFshn);
            } else {
                logger.info("Status: {}", response.getStatus());
                logger.info("Status code: {}", response.getStatus_code());
                logger.info("Status description: {}", response.getStatus_description());
                return "PROCEED";
            }
        } else {
            logger.error("Data: {}", data);
            logger.error("Signature: {}", signature);
            logger.error("Decoded data: {}", decodedData);
            logger.error("Decoded signature: {}", decodedSignature);
            logger.error("SHA1: {}", sha1);

            return "FAIL";
        }

        return "PROCEED";
    }


    private void buyUnLock(BuyFshn buyFshn) {
        buyFshn.setLock(false);
        buyFshnRepository.save(buyFshn);
    }

    private void buyLock(BuyFshn buyFshn) {
        buyFshn.setLock(true);
        buyFshnRepository.save(buyFshn);
    }

    private void tryHardBuyNftForFiat(BuyFshn buyFshn) {
        new Thread(new BuyProcess(buyFshn)).start();
    }

    class BuyProcess implements Runnable {
        BuyFshn buyFshn;

        public BuyProcess(BuyFshn buyFshn) {
            this.buyFshn = buyFshn;
        }


        @Override
        public void run() {
            for (int i = 1000; i < 100000; i += 1000) {
                if (tryBuyNftForFiat(buyFshn)) {
                    return;
                } else {
                    try {
                        Thread.sleep(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean tryBuyNftForFiat(BuyFshn buyFshn) {
        try {
            BuyNft buyNft = buyNftRepository.findById(buyFshn.getPaymentId()).orElse(null);
            logger.info("Buy NFT: {}",gson.toJson( buyNft));
            if (buyNft == null) {
                logger.error("BuyNft: {}",buyNft);
                return false;
            }
            if (buyNft.isComplited()) {
                logger.info("NFT is already buy");
                return true;
            }
            BuyNftDTO buyNftDTO = gson.fromJson(buyNft.getBuyNftRequest(), BuyNftDTO.class);
            Client client = clientService.getClient(buyNft.getClientId());
            BigDecimal nftTotalPrice = nftService.getTotalPrice(buyNftDTO);
            BigDecimal clientBalance = clientService.updateBalance(client).getWalletBalance();
            buyNftDTO.setApikey(client.getApikey());
// TODO: можем отключить, чтоб деньги не списывались так резко :)
            if (clientBalance.compareTo(nftTotalPrice) >= 0) {
                ResultDTO result = nftService.buy(buyNftDTO);
                if (!result.isResult()) {
                    logger.error(gson.toJson(result));
                } else {
                    buyNft.setComplited(true);
                    buyNftRepository.save(buyNft);
                    return true;
                }
            } else {
                logger.info("Client balance: {}", clientBalance);
                logger.info("NFT total price: {}", nftTotalPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultDTO paymentStatus(GetTanzzoStatusDTO request, HttpServletRequest servletRequest) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            BuyFshn buyFshn = buyFshnRepository.findById(request.getPaymentId()).orElse(null);
            if (buyFshn == null) {
                return error231;
            }

            Tranzzo tranzzo = tranzzoRepository.findTopByOrderId(String.valueOf(request.getPaymentId()));

            if (tranzzo != null) {
                TranzzoStatusDTO statusDTO = new TranzzoStatusDTO();
                statusDTO.setFshnAmount(buyFshn.getFshnAmount());
                statusDTO.setPaymentId(request.getPaymentId());
                statusDTO.setStatus(tranzzo.getStatus());
                statusDTO.setStatusCode(tranzzo.getStatusCode());
                statusDTO.setStatusDescription(tranzzo.getStatusDescription());
                statusDTO.setTxHash(buyFshn.getTxHash());
                statusDTO.setUahAmount(buyFshn.getUahAmount());
                statusDTO.setUsdAmount(buyFshn.getUsdAmount());
                statusDTO.setWallet(buyFshn.getWallet());
                return new ResultDTO(true, statusDTO, 0);
            } else {
                logger.error("Tranzzo object: {}", tranzzo);
                logger.error("Payment ID: {}", request.getPaymentId());
                return new ResultDTO(false, "Payment ID not found", -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }


    boolean checkCardNumber(String value) {
        int sum1 = 0;
        int sum2 = 0;
        final int nDigits = value.length();
        for (int i = nDigits; i > 0; i--) {
            int digit = Character.getNumericValue(value.charAt(i - 1));
            int z = digit;
            int y = digit;
            if (i % 2 == 0) {
                z *= 2;
                if (z > 9) {
                    z -= 9;
                }
                sum1 += z;
            } else sum2 += y;
        }
        int sum = sum1 + sum2;
        if (value.length() != 16) sum = 1;
        logger.info("ccnumber sum: {}", sum);
        if (sum % 10 == 0) {
            logger.info("Card Valid");
            return true;
        } else {
            logger.error("Card not Valid");
            return true;
        }
    }

    public ResultDTO createNftPayment(CreateTranzzoPaymentDTO request, HttpServletRequest servletRequest) {
        try {

            Client client = clientService.findClientByApikey(request.getApikey());

            ResultDTO resultDTO = createPayment(request, servletRequest);
            if (!resultDTO.isResult()) {
                logger.error(gson.toJson(resultDTO));
                return resultDTO;
            }
            if (resultDTO.getData() instanceof BuyFshn) {
                BuyFshn buyFshn = (BuyFshn) resultDTO.getData();

                BuyNft buyNft = new BuyNft();
                buyNft.setPaymentId(buyFshn.getPaymentId());
                buyNft.setTimestamp(System.currentTimeMillis());
                buyNft.setLocalDateTime(LocalDateTime.now());
                buyNft.setNftId(request.getBuyNft().getNftId());
                buyNft.setBuyNftRequest(gson.toJson(request.getBuyNft()));
                buyNft.setClientId(client.getId());
                buyNftRepository.save(buyNft);
                tryBuyNftForFiat(buyFshn);
                return resultDTO;

            } else {
                logger.error("ResultDTO: {}", gson.toJson(resultDTO));
                return new ResultDTO(false, resultDTO.getData(), -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), 0);
        }
    }
}
