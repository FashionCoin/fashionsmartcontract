package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.nft.dto.BuyNftDTO;
import fashion.coin.wallet.back.nft.dto.CommentsRequestDTO;
import fashion.coin.wallet.back.nft.dto.NewCommentNftDTO;
import fashion.coin.wallet.back.nft.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentNftController {

    @Autowired
    CommentService commentService;


    @PostMapping("/api/v1/nft/comment/new")
    @ResponseBody
    ResultDTO newComment(@RequestBody NewCommentNftDTO request) {
        return commentService.newComment(request);
    }

    @PostMapping("/api/v1/nft/comments")
    @ResponseBody
    ResultDTO newComment(@RequestBody CommentsRequestDTO request) {
        return commentService.getCommentList(request);
    }



}
