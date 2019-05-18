package fashion.coin.wallet.back.addressgenerator.service;


import org.bitcoinj.crypto.MnemonicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class InitService {

    @Autowired
    BitcoinWallet bitcoinWallet;

    @Autowired
    EthereumWallet ethereumWallet;



    @PostConstruct
    public void init(){


        try {
            bitcoinWallet.generateWallet(getExampleHash());
//            ethereumWallet.generateWallet(getExampleHash());
            ethereumWallet.generateWallet(getExampleHash());
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
    }



    public byte[] getExampleHash() {
        try {
            String test = "Test1";

            MessageDigest sha = MessageDigest.getInstance("SHA-256");


            byte[] s256 = sha.digest(test.getBytes("UTF-8"));
            return s256;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[256];
    }
}
