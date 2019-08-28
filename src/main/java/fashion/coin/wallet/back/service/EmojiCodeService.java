package fashion.coin.wallet.back.service;

import com.vdurmont.emoji.EmojiParser;
import fashion.coin.wallet.back.entity.EmojiCode;
import fashion.coin.wallet.back.repository.EmojiCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmojiCodeService {

    @Value("${fashion.emojicode}")
    String[] emojiCodeList;

    @Autowired
    EmojiCodeRepository emojiCodeRepository;


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
        if (cryptoname.length() < 1) return false;
        char ch = ((char) 65039);
        String textWithoutEmoji = EmojiParser.removeAllEmojis(cryptoname).replace(Character.toString(ch), "");
        List<String> textOnlyEmoji = EmojiParser.extractEmojis(cryptoname);


        // Reserv:
        return textWithoutEmoji.length() == 0 && textOnlyEmoji.size() == 1;
    }


}
