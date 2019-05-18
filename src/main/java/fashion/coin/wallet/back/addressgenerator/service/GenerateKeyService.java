package fashion.coin.wallet.back.addressgenerator.service;


import fashion.coin.wallet.back.utils.SignBuilder;
import org.bitcoinj.crypto.MnemonicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenerateKeyService {

    @Autowired
    BitcoinWallet bitcoinWallet;

    @Autowired
    EthereumWallet ethereumWallet;


    private byte[] getSHA256(InputStream fileInputStream) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(fileInputStream);
        while ((count = bis.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }
        bis.close();

        byte[] hash = digest.digest();

        return hash;
    }

    public Map<String, Map<String, String>> getKeyPairs(InputStream fileInputStream) {
        try {
            byte[] seed = getSHA256(fileInputStream);

            Map<String, Map<String, String>> keyPairs = new HashMap<>();

            SignBuilder signBuilder = SignBuilder.init();
            Map<String, String> fashionKeys = signBuilder.createKeyPayr(seed);
            keyPairs.put("FSHN", fashionKeys);
            keyPairs.put("BTC",bitcoinWallet.generateWallet(seed));
            keyPairs.put("ETH",ethereumWallet.generateWallet(seed));

            return keyPairs;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
        return null;
    }


}
