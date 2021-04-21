package fashion.coin.wallet.back.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.security.Key;
import java.util.Base64;
import java.util.Random;


/**
 * Created by JAVA-P on 27.09.2017.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Component
public class AESEncriptor implements AttributeConverter<String, String> {

    Random secureRandom = new Random();

    private SettingsService settingsService;
    private final String TEST_PHRASE_KEY = "Test phrase";
    private final String TEST_PHRASE_VALUE = "測試短語";

    private String KEY_128 = null;// 128 bit key

    byte[] initVector = null;
    IvParameterSpec iv = null;  // 16 bytes key


    @Override
    public String convertToDatabaseColumn(String privateKey) {
        String encryptKey = "";
        try {

            Key aesKey = new SecretKeySpec(KEY_128.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
            byte[] encrypted = cipher.doFinal(privateKey.getBytes());


            encryptKey = new String(Base64.getEncoder().encodeToString(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptKey;
    }

    @Override
    public String convertToEntityAttribute(String encryptedKey) {
        String decryptKey = "";
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedKey);

            Key aesKey = new SecretKeySpec(KEY_128.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
            decryptKey = new String(cipher.doFinal(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptKey;
    }

    public void setKey(String key, String vi) throws Exception {
        if (key.length() != 16 || vi.length() != 16) throw new Exception("key.length() != 16 || vi.length() != 16");

        KEY_128 = key;
        initVector = vi.getBytes();
        iv = new IvParameterSpec(initVector);

        if (settingsService.isEmpty()) {
            settingsService.set(TEST_PHRASE_KEY, TEST_PHRASE_VALUE);
        } else {
            String value = settingsService.get(TEST_PHRASE_KEY);
            if (value != null && !value.equals(TEST_PHRASE_VALUE)) {
                KEY_128 = null;
                initVector = null;
                throw new Exception("Invalid key or IV");
            }
        }
        System.out.println("Key and IV seted OK");
        aiService.printAIwallets();
    }

    @Autowired
    AIService aiService;

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
