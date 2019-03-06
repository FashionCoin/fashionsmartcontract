package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by JAVA-P on 02.11.2018.
 */

/* Создано в www.gateon.net
 * Все права на данный программный код принадлежат компании GateOn
 * Created by www.gateon.net
 * All rights to the software code are owned by GateOn
 */
@Service
public class FileUploadService {

    public static final String AVATARS_PATH = "/var/fashion/pic/avatars";

    public static final String AVATARS_EXTERNAL_PATH = "/pic/avatars";

    ClientService clientService;

    public ResultDTO uploadFile(MultipartFile multipartFile, String login, String apikey) {
        try {
            if(!clientService.checkApiKey(login,apikey)) return error109;
            Client client = clientService.findByLogin(login);
            InputStream inputStream = multipartFile.getInputStream();

            Path path = Paths.get(AVATARS_PATH + "/" + client.getWalletAddress() );
            Files.copy(inputStream, path, REPLACE_EXISTING);
            clientService.setAvatar(login,AVATARS_EXTERNAL_PATH+ "/" + client.getWalletAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

        return new ResultDTO(true, "Avatar saveded", 0);
    }

    private static final ResultDTO error109 = new ResultDTO(false, "Not valid apikey", 109);

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
