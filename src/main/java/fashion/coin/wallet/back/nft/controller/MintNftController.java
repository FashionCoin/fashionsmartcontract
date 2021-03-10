package fashion.coin.wallet.back.nft.controller;

import com.google.gson.Gson;
import com.google.inject.internal.asm.$ClassTooLargeException;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.nft.service.NftService;
import fashion.coin.wallet.back.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Controller
public class MintNftController {

    Logger logger = LoggerFactory.getLogger(MintNftController.class);

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    NftService nftService;

    @Autowired
    Gson gson;

    @PostMapping("/api/v1/nft/mint")
    @ResponseBody
    ResultDTO uploadFile(@RequestParam MultipartFile multipartFile,
                         @RequestParam String apikey, @RequestParam String login,
                         @RequestParam String title,
                         @RequestParam String description,
                         @RequestParam BigDecimal faceValue,
                         @RequestParam BigDecimal creativeValue,
                         @RequestParam String stringTransaction) {
        logger.info(multipartFile.getOriginalFilename());
        try {
            logger.info("Type: {}", multipartFile.getContentType());

            BlockchainTransactionDTO blockchainTransaction = gson.fromJson(stringTransaction, BlockchainTransactionDTO.class);

            return nftService.mint(multipartFile, apikey, login, title, description, faceValue, creativeValue,
                    blockchainTransaction);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

}
