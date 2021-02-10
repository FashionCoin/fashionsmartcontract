//package fashion.coin.wallet.back.service;
//
//import com.sun.xml.internal.fastinfoset.Encoder;
//import fashion.coin.wallet.back.dto.ResultDTO;
//import fashion.coin.wallet.back.dto.WrappedRequestDTO;
//import org.apache.commons.lang3.ArrayUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.Hash;
//import org.web3j.crypto.Sign;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.utils.Numeric;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
//import java.time.LocalDateTime;
//
//@Service
//public class WrapService {
//
//    Logger logger = LoggerFactory.getLogger(WrapService.class);
//
//    TransactionService transactionService;
//
//    long nonce;
//
//    public ResultDTO wrap(WrappedRequestDTO request) {
//     ResultDTO result =   transactionService.send(request.getTransactionRequestDTO());
//     if(result.isResult()){
//
//         signPayment(request.getEthereumWallet(),
//                 request.getTransactionRequestDTO().getBlockchainTransaction().getBody().getAmount(),
//                 getNonce(),
//
//                 );
//
//     }else{
//
//     }
//    }
//
//    private Long getNonce() {
//        if(nonce==0){
//            nonce = System.currentTimeMillis();
//        }
//        nonce++;
//        return nonce;
//    }
//
//
//    public void signPayment(String recipient, int amount, long nonce, String contractAddress) throws UnsupportedEncodingException {
//        Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/cb40c32f3e9840cdaf2cc3b2854eb14c"));
//
//        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B
//        Credentials ownerCred = Credentials.create("3fcb55529e04677892012f28fa6a4c44f7fd0d82ce054418487b9e0af820e0f9");
//        logger.info("Owner Address: {}", ownerCred.getAddress());
//
//        byte[] recipientAddress = Numeric.hexStringToByteArray(recipient);
//        logger.info("Recipient Address: {}", Numeric.toHexString(recipientAddress));
//
//        byte[] amountBytes = Numeric.toBytesPadded(BigInteger.valueOf(amount), 32);
//        logger.info("Amount: {}", Numeric.toHexString(amountBytes));
//
//        byte[] nonceBytes = Numeric.toBytesPadded(BigInteger.valueOf(nonce), 32);
//        logger.info("Nonce: {}", Numeric.toHexString(nonceBytes));
//
//        byte[] contract = Numeric.hexStringToByteArray(contractAddress);
//        logger.info("Contract Address: {}", Numeric.toHexString(contract));
//
//
//        byte[] concatBytes = ArrayUtils.addAll(recipientAddress, amountBytes);
//        concatBytes = ArrayUtils.addAll(concatBytes, nonceBytes);
//        concatBytes = ArrayUtils.addAll(concatBytes, contract);
//        logger.info("Concat Bytes: {}", Numeric.toHexString(concatBytes));
//
//        byte[] hash = Hash.sha3(concatBytes);
//        logger.info("Hash: {}", Numeric.toHexString(hash));
//
//        byte[] prefixed = ArrayUtils.addAll("\u0019Ethereum Signed Message:\n32".getBytes(Encoder.UTF_8),hash);
//        logger.info("Prefixed: {}", Numeric.toHexString(prefixed));
//
//        byte[] prefHash = Hash.sha3(prefixed);
//        logger.info("Prefixed Hash: {}", Numeric.toHexString(prefHash));
//
//        // Hash: 0xc1de7059c3c0ed7870c7ca25bcc78fce22c8f1966bba51e5b43bd4740d3dacce
//        // Owner: 0x2E960FF80fCD4C7C31a18f62E78db89AD99fF56B
//        Sign.SignatureData signData = Sign.signPrefixedMessage(hash, ownerCred.getEcKeyPair());
//
//
//// Check signature:
//// String pubKey = Sign.signedMessageToKey(messageBytes, sig).toString(16);
//
//
//        byte[] r = signData.getR();
//        logger.info("R: {}", Numeric.toHexString(r));
//        byte[] s = signData.getS();
//        logger.info("S: {}", Numeric.toHexString(s));
//        byte[] v = signData.getV();
//        logger.info("V: {}", Numeric.toBigInt(v));
//        byte[] sig = ArrayUtils.addAll(r, s);
//        sig = ArrayUtils.addAll(sig, v);
//
//        logger.info("Signature len: {}", sig.length);
//        logger.info("Sign data: {}", Numeric.toHexString(sig));
//
//
//
//        web3j.shutdown();
//    }
//
//    @Autowired
//    public void setTransactionService(TransactionService transactionService) {
//        this.transactionService = transactionService;
//    }
//}
