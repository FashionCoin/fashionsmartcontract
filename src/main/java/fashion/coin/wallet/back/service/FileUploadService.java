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

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.List;
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
        if (!clientService.checkApiKey(login, apikey)) return error109;
        return saveNft(multipartFile);
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

    public ResultDTO saveNft(MultipartFile multipartFile) {
        try {
            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String contentType = multipartFile.getContentType();
            Long size = multipartFile.getSize();
            String fileExtension = getExtensionByStringHandling(originalFilename).orElse("");
            Path copyLocation = Paths.get(NFT_PATH + File.separator + originalFilename);
            Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            String shaChecksum = getFileChecksum(shaDigest, copyLocation.toFile());
            Path newName = Paths.get(NFT_PATH + File.separator + shaChecksum + fileExtension);
            NftFile nftFile = new NftFile(shaChecksum + fileExtension, contentType, size);
            if (Files.exists(newName)) {
                logger.info("{} exists", newName.toString());
                Files.delete(copyLocation);
                /// TODO: Временно
//                Files.move(copyLocation, copyLocation.resolveSibling(newName));
//                nftFileRepository.save(nftFile);
//                if (multipartFile.getContentType().toLowerCase().contains("video")) {
//                    String videoName = newName.toString();
//                    String imageName = NFT_PATH + File.separator + shaChecksum + ".jpeg";
//                    createPreview(videoName, imageName);
//                    resizePreview(shaChecksum + ".jpeg");
//                } else {
//                    resizePreview(shaChecksum + fileExtension);
//                }

                ///
            } else {
                Files.move(copyLocation, copyLocation.resolveSibling(newName));
                nftFileRepository.save(nftFile);

                if (multipartFile.getContentType().toLowerCase().contains("video")) {
                    String videoName = newName.toString();
                    String imageName = NFT_PATH + File.separator + shaChecksum + ".jpeg";
                    createPreview(videoName, imageName);
                    resizePreview(shaChecksum + ".jpeg");
                } else {
                    resizePreview(shaChecksum + fileExtension);
                }
            }

            return new ResultDTO(true, nftFile, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private void resizePreview(String fileName) {
        try {
            String originalFile = NFT_PATH + File.separator + fileName;
            String size300 = NFT_PATH + File.separator + "300" + File.separator + fileName;
            String size600 = NFT_PATH + File.separator + "600" + File.separator + fileName;
            String size1000 = NFT_PATH + File.separator + "1000" + File.separator + fileName;

            String exif = getRotateOrientation(originalFile);
            logger.info("EXIF: " + exif);
            String command = "ffmpeg -i '" + originalFile + "' -vf scale=300:-1  '" + size300 + "'";
            logger.info(command);
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            logger.info("process.isAlive() : {}", process.isAlive());
            int result = process.waitFor();
            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size300, exif);
            }

            command = "ffmpeg -i '" + originalFile + "' -vf scale=600:-1  '" + size600 + "'";
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            result = process.waitFor();
            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size600, exif);
            }

            command = "ffmpeg -i '" + originalFile + "' -vf scale=1000:-1  '" + size1000 + "'";
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            result = process.waitFor();
            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size1000, exif);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRotateOrientation(String filename, String exif) {
        try {
            String command = "exiftool -Orientation=" + exif + " -n " + filename;
            logger.info(command);
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            int result = process.waitFor();
            logger.info("Result: {}", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRotateOrientation(String originalFile) {
        try {
//            if (originalFile.equals("/var/fashion/pic/nft/846c6f309eaf0462cba6ea57e0f869ce8828486b640c845fb993df195ecac7cf.jpeg")) {
//                return "6";
//            } else {
//                logger.info(originalFile);
//            }



            logger.info("Exif: {}",originalFile);
            Process process = Runtime.getRuntime().exec("exiftool -Orientation -n -S " + originalFile);

//            String command = "exiftool -Orientation -n -S " + originalFile;
//            logger.info(command);
//            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String firstLine = reader.readLine();
            logger.info("First Line: {}", firstLine);

            int result = process.waitFor();
            logger.info("Result: {}", result);

            if (firstLine != null && firstLine.contains("Orientation: ")) {
                return firstLine.replace("Orientation: ", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void convertAllFiles() {

        List<NftFile> nftFileList = nftFileRepository.findAll();
        for (NftFile nftFile : nftFileList) {

            if (nftFile.getContentType().toLowerCase().contains("image")) {
                resizePreview(nftFile.getFilename());
            } else if (nftFile.getContentType().toLowerCase().contains("video")) {
                resizePreview(nftFile.getFilename().substring(0, 64) + ".jpeg");
            }
        }
    }


    private boolean createPreview(String videoName, String imageName) {
        try {
            String command = "ffmpeg -i '" + videoName + "' -frames 1  -f image2 '" + imageName + "'";
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            int result = process.waitFor();
            logger.info("Result: {}", result);
            logger.info(imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @PostConstruct
    void getOrient(){
        try {
            String exif = getRotateOrientation("/var/fashion/pic/nft/846c6f309eaf0462cba6ea57e0f869ce8828486b640c845fb993df195ecac7cf.jpeg");
            logger.info(exif);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
