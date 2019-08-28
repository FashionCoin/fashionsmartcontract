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
        }
    }

    String checkEmojiCode(String codeCandidat) {
        if (codeCandidat.contains(":")) {

            refreshEmojilist();

            int colonePosition = codeCandidat.indexOf(":");

            String emcode = codeCandidat.substring(0, colonePosition);
            EmojiCode emojiCode = emojiCodeRepository.findById(emcode).orElse(null);
            if (emojiCode != null && emojiCode.getUsed() == null) {
                String cryptoname = codeCandidat.substring(colonePosition + 1);
                if (checkOneEmoji(cryptoname)) return cryptoname;
            }
        }
        return null;
    }

    private boolean checkOneEmoji(String cryptoname) {
        if (cryptoname==null ||  cryptoname.length() < 1) return false;
        char ch = ((char) 65039);
        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname).replace(Character.toString(ch), "");
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);


        // Reserv:
        return textWithoutEmoji.length() == 0 && textOnlyEmoji.size() == 1;
    }


    public void registerClient(Client client, String codeCandidat) throws IllegalAccessException {
        logger.info("Client: "+gson.toJson(client));
        if (checkOneEmoji(client.getCryptoname())) {
            String oneEmoji = checkEmojiCode(codeCandidat);
            if (oneEmoji.equals(client.getCryptoname())) {
                int colonePosition = codeCandidat.indexOf(":");

                String emcode = codeCandidat.substring(0, colonePosition);
                EmojiCode emojiCode = emojiCodeRepository.findById(emcode).orElse(null);

                    emojiCode.setClient(client.getId());
                    emojiCode.setEmoji(oneEmoji);
                    emojiCode.setUsed(LocalDateTime.now());
                    emojiCodeRepository.save(emojiCode);
            } else {
                logger.error("codeCandidat: " + codeCandidat + "  client name: " + client.getCryptoname());
                throw new IllegalAccessException("codeCandidat: " + codeCandidat + "  client name: " + client.getCryptoname());
            }

        }
    }
}
