package fashion.coin.wallet.back.controller;


import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by JAVA-P on 02.11.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Controller
public class FileUploadController {

    FileUploadService fileUploadService;

    @PostMapping("/upload/avatar")
    @ResponseBody
    ResultDTO uploadFile(@RequestParam MultipartFile multipartFile, @RequestParam String apikey, @RequestParam String login) {
        System.out.println(multipartFile.getContentType());
        System.out.println(multipartFile.getName());
        System.out.println(multipartFile.getOriginalFilename());
        System.out.println(multipartFile.getSize());
        System.out.println(login);
        System.out.println(apikey);

        return fileUploadService.uploadFile(multipartFile, login, apikey);
    }


    @Autowired
    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
