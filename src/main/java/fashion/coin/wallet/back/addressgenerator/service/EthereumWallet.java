package fashion.coin.wallet.back.addressgenerator.service;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EthereumWallet {

    public   Map<String, String> generateWallet(byte[] sha256) throws MnemonicException.MnemonicLengthException {

        MnemonicCode mnemonicCode = MnemonicCode.INSTANCE;
        List<String> wordList = mnemonicCode.toMnemonic(sha256);

        System.out.println("Ethereum "+wordList.size() + " words:");
        for (String word : wordList) {
            System.out.print(word + " ");
        }
        System.out.println();

        String passphrase = "";
        Long creationtime = 1409478661L;

        DeterministicSeed seed = new DeterministicSeed(wordList, null, passphrase, creationtime);

        MainNetParams MAINNET = MainNetParams.get();
        Wallet wallet = Wallet.fromSeed(MAINNET, seed, Script.ScriptType.P2PKH, ImmutableList.of(new ChildNumber(44, true),
                new ChildNumber(60, true),
                ChildNumber.ZERO_HARDENED));
        DeterministicKey key = wallet.currentReceiveKey();

//        String publicKey = key.getPublicKeyAsHex();
        String PK = key.getPrivateKeyAsHex();


        Credentials cred = Credentials.create(PK.toUpperCase());

        String address = cred.getAddress();
        BigInteger publicKey = cred.getEcKeyPair().getPublicKey();
        BigInteger privateKey = cred.getEcKeyPair().getPrivateKey();

        System.out.println("Ethereum address: "+address);
        System.out.println("Ethereum public key: "+publicKey.toString(16));
        System.out.println("Ethereum private key: "+privateKey.toString(16));

        Map<String, String> result = new HashMap<>();
        result.put("address", address);
        result.put("mnemonic_words", String.join(" ",wordList));
        result.put("pub_key", publicKey.toString(16));
        result.put("priv_key", privateKey.toString(16));
        return result;
    }

    public static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }

}
