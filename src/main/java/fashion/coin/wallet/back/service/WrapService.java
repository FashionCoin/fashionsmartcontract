package fashion.coin.wallet.back.service;

import com.sun.xml.internal.fastinfoset.Encoder;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.WrappedRequestDTO;
import fashion.coin.wallet.back.dto.WrappedResponseDTO;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error207;

@Service
public class WrapService {

    Logger logger = LoggerFactory.getLogger(WrapService.class);

    TransactionService transactionService;
    AIService aiService;

    long nonce;

    // Temporary address on Rinkebuy
    String contractAddress = "0xeE46C11660Aca35cEE8Eb889596c61D132Ef3dba";

    // Temporary owner Private Key
    String ownerPrivKey = "3fcb55529e04677892012f28fa6a4c44f7fd0d82ce054418487b9e0af820e0f9";


    public ResultDTO wrap(WrappedRequestDTO request) {
        try {
            if(!aiService.isMoneyBagWallet( request.getTransactionRequestDTO().getReceiverWallet())) {
                return error207;
            }

            ResultDTO result = transactionService.send(request.getTransactionRequestDTO());
            if (result.isResult()) {

                int amount = Integer.parseInt(request.getTransactionRequestDTO().getBlockchainTransaction().getBody().getAmount());

                WrappedResponseDTO resp = signPayment(request.getEthereumWallet(),
                        amount,
                        getNonce(),
                        contractAddress
                );

                return new ResultDTO(true,resp,0);

            } else {
                return result;
            }
        } catch (Exception e) {
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private Long getNonce() {
        if (nonce == 0) {
            nonce = System.currentTimeMillis();
        }
        nonce++;
        return nonce;
    }


    private WrappedResponseDTO signPayment(String recipient, int amount, long nonce, String contractAddress) throws UnsupportedEncodingException {

        WrappedResponseDTO resp = new WrappedResponseDTO();
        resp.setWallet(recipient);
        resp.setAmount((long) amount);
        resp.setNonce(nonce);
        resp.setSmartconract(contractAddress);


        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B on Rinkebuy
        Credentials ownerCred = Credentials.create(ownerPrivKey);
        logger.info("Owner Address: {}", ownerCred.getAddress());

        byte[] recipientAddress = Numeric.hexStringToByteArray(recipient);
        logger.info("Recipient Address: {}", Numeric.toHexString(recipientAddress));


        byte[] amountBytes = Numeric.toBytesPadded(BigInteger.valueOf(amount), 32);
        logger.info("Amount: {}", Numeric.toHexString(amountBytes));

        byte[] nonceBytes = Numeric.toBytesPadded(BigInteger.valueOf(nonce), 32);
        logger.info("Nonce: {}", Numeric.toHexString(nonceBytes));

        byte[] contract = Numeric.hexStringToByteArray(contractAddress);
        logger.info("Contract Address: {}", Numeric.toHexString(contract));


        byte[] concatBytes = ArrayUtils.addAll(recipientAddress, amountBytes);
        concatBytes = ArrayUtils.addAll(concatBytes, nonceBytes);
        concatBytes = ArrayUtils.addAll(concatBytes, contract);
        logger.info("Concat Bytes: {}", Numeric.toHexString(concatBytes));

        byte[] hash = Hash.sha3(concatBytes);
        logger.info("Hash: {}", Numeric.toHexString(hash));

        byte[] prefixed = ArrayUtils.addAll("\u0019Ethereum Signed Message:\n32".getBytes(Encoder.UTF_8), hash);
        logger.info("Prefixed: {}", Numeric.toHexString(prefixed));

        byte[] prefHash = Hash.sha3(prefixed);
        logger.info("Prefixed Hash: {}", Numeric.toHexString(prefHash));

        // Hash: 0xc1de7059c3c0ed7870c7ca25bcc78fce22c8f1966bba51e5b43bd4740d3dacce
        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B
        Sign.SignatureData signData = Sign.signPrefixedMessage(hash, ownerCred.getEcKeyPair());


        byte[] r = signData.getR();
        logger.info("R: {}", Numeric.toHexString(r));
        resp.setR(Numeric.toHexString(r));

        byte[] s = signData.getS();
        logger.info("S: {}", Numeric.toHexString(s));
        resp.setS(Numeric.toHexString(s));

        byte[] v = signData.getV();
        logger.info("V: {}", Numeric.toBigInt(v));
        resp.setV(Numeric.toBigInt(v).longValue());

        byte[] sig = ArrayUtils.addAll(r, s);
        sig = ArrayUtils.addAll(sig, v);

        logger.info("Signature len: {}", sig.length);
        logger.info("Sign data: {}", Numeric.toHexString(sig));
        return resp;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }
}
