package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.NftRequestDTO;
import fashion.coin.wallet.back.nft.dto.NewCommentNftDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftComment;
import fashion.coin.wallet.back.nft.repository.NftCommentRepository;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.error109;
import static fashion.coin.wallet.back.constants.ErrorDictionary.error213;

@Service
public class CommentService {

    @Autowired
    NftCommentRepository commentRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    NftService nftService;


    public ResultDTO newComment(NewCommentNftDTO request) {
        try {

            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            Nft nft = nftService.findNft(request.getNftId());
            if (nft == null) {
                return error213;
            }

            NftComment nftComment = new NftComment();
            nftComment.setAuthor(client.getCryptoname());
            nftComment.setTimestamp(System.currentTimeMillis());
            nftComment.setNftId(nft.getId());
            nftComment.setText(request.getText());
            commentRepository.save(nftComment);
            return new ResultDTO(true, nftComment, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getCommentList(NftRequestDTO request) {
        try {
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
            List<NftComment> commentList = commentRepository.findByNftIdOrderByTimestampDesc(request.getNftId());
            if (commentList == null || commentList.size() == 0) {
                return new ResultDTO(true, new ArrayList<NftComment>(), 0);
            }
            return new ResultDTO(true, commentList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
