package fashion.coin.wallet.back.nft.service;

import com.google.inject.internal.asm.$ClassTooLargeException;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.HashTagFindAllDTO;
import fashion.coin.wallet.back.nft.entity.HashTag;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.repository.HashTagRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagService {

    @Autowired
    NftRepository nftRepository;

    @Autowired
    HashTagRepository hashTagRepository;

    public static final String REG_EX_TAG = ".*?\\s(#\\w+).*?";
    Pattern tagMatcher = Pattern.compile(REG_EX_TAG);

    List<HashTag> findAllTags(String hashtag) {
        try {
            List<HashTag> hashTagList = hashTagRepository.findByIdContains(hashtag);
            if (hashTagList == null) {
                hashTagList = new ArrayList<>();
            }

            hashTagList.removeIf(hashTag -> hashTag.getId().equals(hashtag));
            hashTagList.sort((o1, o2) -> o2.getPublications().compareTo(o1.getPublications()));

            HashTag hashTag = hashTagRepository.findById(hashtag).orElse(null);
            if (hashTag != null) {
                hashTagList.add(0, hashTag);
            }

            return hashTagList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    boolean checkTags(String description) {
        try {
            Matcher m = tagMatcher.matcher(description);

            Set<String> alreadyUsed = new HashSet<>();

            while (m.find()) {
                String tag = m.group(1);
                if (!alreadyUsed.contains(tag)) {
                    alreadyUsed.add(tag);
                    HashTag hashTag = hashTagRepository.findById(tag).orElse(null);
                    if (hashTag == null) {
                        hashTag = new HashTag();
                        hashTag.setId(tag);
                        hashTag.setPublications(0L);
                    }
                    hashTag.setPublications(hashTag.getPublications() + 1);
                    hashTagRepository.save(hashTag);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public ResultDTO findTags(HashTagFindAllDTO request) {
        try {
            return new ResultDTO(true, findAllTags(request.getHashtag()), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO findNft(HashTagFindAllDTO request) {
        try {
            List<Nft> nftList = nftRepository.findHashTags(request.getHashtag());
            if (nftList == null) {
                nftList = new ArrayList<>();
            }
            return new ResultDTO(true, nftList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
