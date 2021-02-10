package fashion.coin.wallet.back.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.entity.NftFile;
import fashion.coin.wallet.back.nft.repository.NftFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Optional;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;
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

    Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${storage.avatar.path}")
    String AVATARS_PATH;

    @Value("${storage.nft.path}")
    String NFT_PATH;

    public static final String AVATARS_EXTERNAL_PATH = "/pic/avatars";

    ClientService clientService;

    NftFileRepository nftFileRepository;

    public ResultDTO uploadAvatar(MultipartFile multipartFile, String login, String apikey) {
        try {
            if (!clientService.checkApiKey(login, apikey)) return error109;
            Client client = clientService.findByCryptoname(login);
            InputStream inputStream = multipartFile.getInputStream();

            Path path = Paths.get(AVATARS_PATH + File.separator + client.getWalletAddress());
            Files.copy(inputStream, path, REPLACE_EXISTING);
            clientService.setAvatar(login, AVATARS_EXTERNAL_PATH + "/" + client.getWalletAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

        return new ResultDTO(true, "Avatar saveded", 0);
    }


    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setNftFileRepository(NftFileRepository nftFileRepository) {
        this.nftFileRepository = nftFileRepository;
    }

    public ResultDTO uploadNftPicture(MultipartFile multipartFile, String login, String apikey) {

        Long nftId = 0L;
        try {
            if (!clientService.checkApiKey(login, apikey)) return error109;
            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String contentType = multipartFile.getContentType();
            Long size = multipartFile.getSize();
            String fileExtension = getExtensionByStringHandling(originalFilename).orElse("");
            Path copyLocation = Paths.get(NFT_PATH + File.separator + originalFilename);
            Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            String shaChecksum = getFileChecksum(shaDigest, copyLocation.toFile());
            Path newName = Paths.get(NFT_PATH + File.separator + shaChecksum + fileExtension);
            if (newName.toFile().exists()) {
                logger.info("{} exists", newName.toString());
                Files.delete(copyLocation);
                return error123;
            }
            Files.move(copyLocation, copyLocation.resolveSibling(newName));
            NftFile nftFile = new NftFile(shaChecksum + fileExtension, contentType, size);
            nftFileRepository.save(nftFile);
            nftId = nftFile.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
        return new ResultDTO(true, String.valueOf(nftId), 0);
    }


    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }


    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".")));
    }
}
