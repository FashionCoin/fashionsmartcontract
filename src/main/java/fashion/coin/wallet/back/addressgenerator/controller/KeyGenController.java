package fashion.coin.wallet.back.addressgenerator.controller;

import com.google.gson.Gson;
import fashion.coin.wallet.back.addressgenerator.service.GenerateKeyService;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class KeyGenController {

    @Autowired
    GenerateKeyService generateKeyService;

    @Autowired
    ClientService clientService;

    @GetMapping("/keygen")
    public String getPage() {
        return "keygen";
    }

    @PostMapping("/keygen")
    public String handleFileUpload(ModelMap modelMap, @RequestParam("file") MultipartFile file) {

        try {
            modelMap.addAttribute("keypairs", generateKeyService.getKeyPairs(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "keylist";
    }

    @GetMapping("/service/api/getlinks")
    @ResponseBody
    String getLinks(@RequestParam String cryptoname) {
        Client client = clientService.findByCryptoname(cryptoname);
        if (client == null) return "Cryptoname " + cryptoname + " not found";
        if (client.getWalletAddress() != null && client.getWalletAddress().length() > 0)
            return "This cryptoname already have wallet";

        String apiKey = clientService.getApiKeyByCryptoname(client.getCryptoname());

        String page = String.format("<h1>Android</h1>" +
                        "<p><a href='https://play.google.com/store/apps/details?id=wallet.fashion.coin&referrer=utm_source=reserve-cryptoname&utm_content=%s&utm_campaign=1'>" +
                        "https://play.google.com/store/apps/details?id=wallet.fashion.coin&referrer=utm_source=reserve-cryptoname&utm_content=%s&utm_campaign=1</a><p>" +
                        "<br/>" +
                        "<hr>" +
                        "<h1>Plan B:</h1><br/><hr>" +
                        "<h1>Google</h1>" +
                        "<p><a href='https://api.coin.fashion/api/v1/google/refinstall?api_key=%s'>" +
                        "https://api.coin.fashion/api/v1/google/refinstall?api_key=%s</a><p>" +
                        "<h1>Apple</h1>" +
                        "<p><a href='https://api.coin.fashion/api/v1/apple/refinstall?api_key=%s'>" +
                        "https://api.coin.fashion/api/v1/apple/refinstall?api_key=%s</a><p>",
                apiKey, apiKey, apiKey,  apiKey, apiKey, apiKey);

        return page;
    }
}
