package fashion.coin.wallet.back.addressgenerator.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.addressgenerator.service.GenerateKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class KeyGenController {

    @Autowired
    GenerateKeyService generateKeyService;

    @GetMapping("/keygen")
    public String getPage(){
        return "keygen";
    }

    @PostMapping("/")
    public String handleFileUpload(ModelMap modelMap, @RequestParam("file") MultipartFile file) {

        try {
            modelMap.addAttribute("keypairs",generateKeyService.getKeyPairs(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "keylist";
    }
}
