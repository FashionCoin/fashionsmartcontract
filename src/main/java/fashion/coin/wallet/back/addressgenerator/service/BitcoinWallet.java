package fashion.coin.wallet.back.addressgenerator.service;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.Address;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BitcoinWallet {

    public   Map<String, String> generateWallet(byte[] sha256) throws MnemonicException.MnemonicLengthException {

        MainNetParams MAINNET = MainNetParams.get();

        byte[] s128 = xorByte(sha256);

        MnemonicCode mnemonicCode = MnemonicCode.INSTANCE;
        List<String> wordList = mnemonicCode.toMnemonic(s128);

        System.out.println("Bitcoin "+wordList.size() + " words:");
        for (String word : wordList) {
            System.out.print(word + " ");
        }
        System.out.println();

        String passphrase = "";
        Long creationtime = 1409478661L;

        DeterministicSeed seed = new DeterministicSeed(wordList, null, passphrase, creationtime);


        Wallet wallet = Wallet.fromSeed(MAINNET, seed, Script.ScriptType.P2PKH, ImmutableList.of(new ChildNumber(44, true),
                ChildNumber.ZERO_HARDENED, ChildNumber.ZERO_HARDENED));
        Address firstAddressKey = wallet.currentReceiveAddress();
//        wallet.freshAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS);
//        System.out.println(wallet.toString());

        DeterministicKey masterPrivateKey = wallet.getWatchingKey();


        String address = firstAddressKey.toString();
        String publicKey = masterPrivateKey.serializePubB58(MAINNET);
        String privateKey = masterPrivateKey.serializePrivB58(MAINNET);

        System.out.println("Bitcoin address: "+address);
        System.out.println("Bitcoin public key: "+publicKey);
        System.out.println("Bitcoin private key: "+privateKey);


        Map<String, String> result = new HashMap<>();
        result.put("address", address);
        result.put("mnemonic_words", String.join(" ",wordList));
        result.put("pub_key", publicKey);
        result.put("priv_key", privateKey);
        return result;

    }



    private byte[] xorByte(byte[] s256) {

        byte[] s128 = new byte[16];
        for (int i = 0; i < 16; i++) {
            s128[i] = (byte) (s256[i] ^ s256[i + 16]);
        }
        return s128;
    }


}
