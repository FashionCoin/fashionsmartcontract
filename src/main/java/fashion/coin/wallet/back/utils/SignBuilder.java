package fashion.coin.wallet.back.utils;

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class SignBuilder {
    private int networkId;
    private int protocolVersion;
    private int serviceId;
    private int messageId;
    private int payloadLength = 10;
    private ArrayList<Byte> body = new ArrayList<>();
    private Map<Integer, String> strings = new HashMap<>();

    public String sign(String privateKey) throws UnsupportedEncodingException {
        checkHexString(privateKey);
        byte[] key = hexStringToByteArray(privateKey);

        int pointer = 0;
        int initLength = body.size()+10;
        for (Map.Entry<Integer,String> entry: strings.entrySet()){
            initLength = initLength+8 + entry.getValue().getBytes("UTF-8").length;
        }
        byte[] data = new byte[initLength];
        data[pointer++]= (byte) networkId;
        data[pointer++]= (byte) protocolVersion;
        for (byte i : integerToByteArray(BigInteger.valueOf(messageId), 2)) {
            data[pointer++]= i;
        }
        for (byte i : integerToByteArray(BigInteger.valueOf(serviceId), 2)) {
            data[pointer++]= i;
        }
        pointer = pointer + 4;
        for (Byte i: body){
            if (strings.containsKey(pointer)){
                System.out.println("STRING!");
                String text = strings.remove(pointer);
                for (byte j : integerToByteArray(BigInteger.valueOf(payloadLength), 4)) {
                    data[pointer++] = j;
                }
                for (byte j : integerToByteArray(BigInteger.valueOf(text.length()), 4)) {
                    data[pointer++] = j;
                }
                for (byte j : text.getBytes("UTF-8")) {
                    data[payloadLength++] = j;
                }
                data[pointer++] = i;
            } else {
                data[pointer++] = i;
            }
        }
        if (strings.size()>0){
            for (Map.Entry<Integer,String> entry: strings.entrySet()){
                String text = entry.getValue();
                for (byte j : integerToByteArray(BigInteger.valueOf(payloadLength), 4)) {
                    data[pointer++] = j;
                }
                for (byte j : integerToByteArray(BigInteger.valueOf(text.length()), 4)) {
                    data[pointer++] = j;
                }
                for (byte j : text.getBytes("UTF-8")) {
                    data[payloadLength++] = j;
                }
            }
        }
        byte[] length = integerToByteArray(BigInteger.valueOf(payloadLength+64), 4);
        System.arraycopy(length, 0, data, 6, 4);
        byte[] signature = TweetNaCl.crypto_sign(data, key);
        byte[] sign = new byte[64];
        System.arraycopy(signature, 0, sign, 0, 64);
        return bytesToHex(sign);
    }

    public SignBuilder addString(String string) {
        strings.put(payloadLength, string);
        payloadLength = payloadLength + 8;
        return this;
    }

    public SignBuilder addPublicKeyOrHash(String hex) {
        checkHexString(hex);
        for (byte i : hexStringToByteArray(hex)) {
            body.add(i);
            payloadLength++;
        }
        return this;
    }

    public SignBuilder addUint8(int uint8) {
        checkUint8(uint8);
        for (byte i : integerToByteArray(new BigInteger(String.valueOf(uint8)), 1)) {
            body.add(i);
            payloadLength++;
        }
        return this;
    }

    public SignBuilder addUint16(int uint16) {
        checkUint16(uint16);
        for (byte i : integerToByteArray(new BigInteger(String.valueOf(uint16)), 2)) {
            body.add(i);
            payloadLength++;
        }
        return this;
    }

    public SignBuilder addUint32(BigInteger uint32) {
        checkUint32(uint32);
        for (byte i : integerToByteArray(uint32, 4)) {
            body.add(i);
            payloadLength++;
        }
        return this;
    }

    public SignBuilder addUint64(BigInteger uint64) {
        checkUint64(uint64);
        for (byte i : integerToByteArray(uint64, 8)) {
            body.add(i);
            payloadLength++;
        }
        return this;
    }

    public SignBuilder setMessageId(int messageId) {
        checkUint16(messageId);
        this.messageId = messageId;
        return this;
    }

    public SignBuilder setServiceId(int serviceId) {
        checkUint16(serviceId);
        this.serviceId = serviceId;
        return this;
    }

    public SignBuilder setProtocolVersion(int protocolVersion) {
        checkUint8(protocolVersion);
        this.protocolVersion = protocolVersion;
        return this;
    }

    public SignBuilder setNetworkId(int networkId) {
        checkUint8(networkId);
        this.networkId = networkId;
        return this;
    }

    public Map<String,String> createKeyPayr(){
        byte[] secretKey = new byte[64];
        byte[] publicKey = new byte[32];
        TweetNaCl.crypto_sign_keypair(publicKey,secretKey,false);

        Map<String,String> result = new HashMap<>();
        result.put("pub_key",bytesToHex(publicKey));
        result.put("priv_key",bytesToHex(secretKey));
        return result;
    }

    public Map<String, String> createKeyPayr(byte[] seed) {
        byte[] secretKey = Arrays.copyOf(seed, 64);
        byte[] publicKey = new byte[32];
        TweetNaCl.crypto_sign_keypair(publicKey, secretKey, true);

        Map<String, String> result = new HashMap<>();
        result.put("pub_key", bytesToHex(publicKey));
        result.put("priv_key", bytesToHex(secretKey));
        return result;
    }

    private void checkUint8(int number) {
        if (number < 0 || number > 255) {
            throw new IllegalArgumentException("Wrong number range, 0..255 expected");
        }
    }

    private void checkUint16(int number) {
        if (number < 0 || number > 65_535) {
            throw new IllegalArgumentException("Wrong number range, 0..65535 expected");
        }
    }

    private void checkUint32(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) < 0 || number.compareTo(BigInteger.valueOf(4_294_967_295L)) > 0) {
            throw new IllegalArgumentException("Wrong number range, 0..4 294 967 295 expected");
        }
    }

    private void checkUint64(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) < 0 || number.compareTo(new BigInteger("18446744073709551615")) > 0) {
            throw new IllegalArgumentException("Wrong number range, 0..18 446 744 073 709 551 615 expected");
        }
    }

    private void checkHexString(String hex) {
        if (hex == null || hex.length() != 64 && hex.length()!=128) {
            throw new IllegalArgumentException("HexDecimal string expected");
        }
    }

    public static SignBuilder init() {
        return new SignBuilder();
    }

    private SignBuilder() {
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    private byte[] integerToByteArray(BigInteger number, int size) {
        byte[] buffer = new byte[size];
        for (int i = 0; i < size; i++) {
            BigInteger bg[] = number.divideAndRemainder(new BigInteger("256"));
            buffer[i] = bg[1].byteValue();
            number = bg[0];
        }
        return buffer;
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}


