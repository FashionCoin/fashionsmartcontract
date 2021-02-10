package fashion.coin.wallet.back.nft.controller;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileStorageController {

    Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    @Autowired
    FileUploadService fileUploadService;

    @PostMapping("/api/v1/file/nft/upload")
    @ResponseBody
    ResultDTO uploadFile(@RequestParam MultipartFile multipartFile, @RequestParam String apikey, @RequestParam String login) {
        logger.info(multipartFile.getOriginalFilename());
        return fileUploadService.uploadNftPicture(multipartFile, login, apikey);
    }

}
