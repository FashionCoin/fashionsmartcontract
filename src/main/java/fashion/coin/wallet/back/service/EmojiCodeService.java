package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiParser;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.entity.EmojiCode;
import fashion.coin.wallet.back.repository.EmojiCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmojiCodeService {

    Logger logger = LoggerFactory.getLogger(EmojiCodeService.class);

    @Value("${fashion.emojicode}")
    String[] emojiCodeList;

    @Autowired
    EmojiCodeRepository emojiCodeRepository;

    @Autowired
    Gson gson;


    private boolean listIsRefreshed = false;

    private void refreshEmojilist() {
        if (!listIsRefreshed) {
            for (int i = 0; i < emojiCodeList.length; i++) {
                EmojiCode emojiCode = emojiCodeRepository.findById(emojiCodeList[i]).orElse(null);
                if (emojiCode == null) {
                    emojiCode = new EmojiCode(emojiCodeList[i]);
                    emojiCodeRepository.save(emojiCode);
                }
            }
            listIsRefreshed = true;
        }
    }

    String checkEmojiCode(String codeCandidat) {
        if (codeCandidat.contains(":")) {
            logger.info("codeCandidat " + codeCandidat);
            refreshEmojilist();

            int colonePosition = codeCandidat.indexOf(":");

            String emcode = codeCandidat.substring(0, colonePosition);
            EmojiCode emojiCode = emojiCodeRepository.findById(emcode).orElse(null);
            if (emojiCode != null && emojiCode.getWallet() == null) {
                String cryptoname = codeCandidat.substring(colonePosition + 1);
                if (cryptoname != null && cryptoname.length() > 0) {
                    logger.info("checkCode: " + cryptoname);
                    emojiCode.setEmoji(cryptoname);
                    emojiCode.setUsed(LocalDateTime.now());
                    emojiCodeRepository.save(emojiCode);
                    if (checkOneEmoji(cryptoname)) return cryptoname;
                }
            }
        }
        return null;
    }

    private boolean checkOneEmoji(String cryptoname) {

        return true;
        /*
        if (cryptoname == null || cryptoname.length() < 1) return false;
        char ch = ((char) 65039);
        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname).replace(Character.toString(ch), "");
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);
        logger.info("textWithoutEmoji.length() " + textWithoutEmoji.length());
        logger.info("textOnlyEmoji.size() " + textOnlyEmoji.size());
        if (textOnlyEmoji.size() == 1) {
            logger.info(textOnlyEmoji.get(0));
        }
        // Reserv:
        return textWithoutEmoji.length() == 0 && textOnlyEmoji.size() == 1;

         */
    }


    public void registerClient(Client client) throws IllegalAccessException {
        logger.info("Client: " + gson.toJson(client));

        String cryptoname = client.getCryptoname();
        EmojiCode emojiCode = emojiCodeRepository.findByEmoji(cryptoname);
        if (emojiCode == null) return;
        if (emojiCode.getWallet() != null && emojiCode.getWallet().length() > 0) return;
        if (client.getWalletAddress() == null || client.getWalletAddress().length() == 0) return;
        emojiCode.setWallet(client.getWalletAddress());
        emojiCode.setClient(client.getId());
        emojiCodeRepository.save(emojiCode);
        logger.info("Client oneEmoji registered");
//
//        if (!client.getCryptoname().equals(codeCandidat) && checkOneEmoji(client.getCryptoname())) {
//            String oneEmoji = checkEmojiCode(codeCandidat);
//            if (oneEmoji.equals(client.getCryptoname())) {
//                int colonePosition = codeCandidat.indexOf(":");
//
//                String emcode = codeCandidat.substring(0, colonePosition);
//                EmojiCode emojiCode = emojiCodeRepository.findById(emcode).orElse(null);
//
//                emojiCode.setClient(client.getId());
//                emojiCode.setEmoji(oneEmoji);
//                emojiCode.setUsed(LocalDateTime.now());
//                emojiCodeRepository.save(emojiCode);
//            } else {
//                logger.error("codeCandidat: " + codeCandidat + "  client name: " + client.getCryptoname());
//                throw new IllegalAccessException("codeCandidat: " + codeCandidat + "  client name: " + client.getCryptoname());
//            }

    }

    public boolean emojiAvaliable(String cryptoname) {

        EmojiCode emojiCode = emojiCodeRepository.findByEmoji(cryptoname);
        if (emojiCode == null) return false;
        if (emojiCode.getWallet() != null && emojiCode.getWallet().length() > 0) return false;
        return true;
    }
}

