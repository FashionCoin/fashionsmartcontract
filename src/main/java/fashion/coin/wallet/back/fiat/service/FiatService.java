package fashion.coin.wallet.back.fiat.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.fiat.dto.*;
import fashion.coin.wallet.back.fiat.entity.FiatPayment;
import fashion.coin.wallet.back.fiat.repository.FiatPaymentRepository;
import fashion.coin.wallet.back.repository.ClientRepository;
import fashion.coin.wallet.back.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FiatService {

    ClientRepository clientRepository;
    FiatPaymentRepository fiatPaymentRepository;
    AIService aiService;

    Gson gson;


    @Value("${fiat.secretkey}")
    private String fiatSecretKey;

    public CheckPhoneResponceDTO checkPhone(CheckPhoneRequestDTO data) {
        try {
            if (data == null) return new CheckPhoneResponceDTO(false, null);
            if (data.getPhone().charAt(0) != '+') return new CheckPhoneResponceDTO(false, null);

            List<Client> clientList = clientRepository.findClientsByPhoneEndingWith(data.getPhone().substring(1));
            if (clientList != null && clientList.size() == 1) {
                return new CheckPhoneResponceDTO(true, clientList.get(0).getLogin());
            } else if (clientList != null) {
                System.out.println("clientList.size() = " + clientList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CheckPhoneResponceDTO(false, null);
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setFiatPaymentRepository(FiatPaymentRepository fiatPaymentRepository) {
        this.fiatPaymentRepository = fiatPaymentRepository;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public PayResponceDTO createPayment(PayRequestDTO data) {
        try {
            if (!checkSignature(data)) return new PayResponceDTO(false, "Bad signature");
            FiatPayment payment = fiatPaymentRepository.getOne(data.getId());
            System.out.println("payment = " + payment);
            if (payment == null) payment = new FiatPayment(
                    data.getId(),
                    data.getTimestamp(),
                    data.getAmount(),
                    data.getFee(),
                    data.getCurrency(),
                    data.getFshn(),
                    data.getPhone(),
                    data.getCryptoname(),
                    false,
                    "Payment not sent"
            );

            if (!payment.getResult()) {
                if (data.getPhone().charAt(0) != '+') payment.setMsg("Invalid phone. ITU-T E.164: +380501234567 ");

                List<Client> clientList = clientRepository.findClientsByPhoneEndingWith(data.getPhone().substring(1));
                if (clientList == null) {
                    payment.setMsg("Phone " + data.getPhone() + " not found");
                } else if (clientList.size() > 1) {
                    payment.setMsg("Several users found with the same number");
                } else {
                    Client client = clientList.get(0);
                    if (aiService.transfer(data.getFshn().toString(), client.getWalletAddress())) {
                        payment.setResult(true);
                        payment.setMsg("Ok");
                    } else {
                        payment.setMsg("Failed to send funds. See the error in the logs on the server. " + LocalDateTime.now());
                    }
                }
            }

            fiatPaymentRepository.save(payment);
            if (payment.getResult()) {
                return new PayResponceDTO(true, null);
            } else {
                return new PayResponceDTO(false, payment.getMsg());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new PayResponceDTO(false, e.getMessage());
        }
    }

    private boolean checkSignature(PayRequestDTO data) {
        try {
            String concatenatedString =
                    data.getId() + ":" +
                            data.getAmount().setScale(2, RoundingMode.HALF_EVEN) + ":" +
                            data.getFshn().setScale(3, RoundingMode.HALF_EVEN) + ":" +
                            fiatSecretKey;


            System.out.println(concatenatedString);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedhash = digest.digest(
                    concatenatedString.getBytes(StandardCharsets.UTF_8));
            String signature = new BigInteger(1, encodedhash).toString(16);
            System.out.println(signature);
            return signature.equals(data.getSignature());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }


    public PaymentHistoryDTO getHistory(LogsRequestDTO data) {
        try{
            List<FiatPayment> payments;
            if(data.getOnlysuccessful()){
             payments = fiatPaymentRepository.findAllByTimestampBetweenAndResult(
                     data.getStart(),
                     data.getEnd(),
                     true);
            }else{
                payments = fiatPaymentRepository.findAllByTimestampBetween(
                        data.getStart(),
                        data.getEnd());
            }
            return new PaymentHistoryDTO(payments);
        }catch (Exception e){
            e.printStackTrace();
            return new PaymentHistoryDTO();
        }
    }


}
