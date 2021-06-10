package fashion.coin.wallet.back.service;

import com.google.gson.Gson;
import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardTag;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftFile;
import fashion.coin.wallet.back.nft.repository.NftFileRepository;
import fashion.coin.wallet.back.nft.service.NftService;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.asList;

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

    @Autowired
    NftService nftService;

    @Autowired
    Gson gson;

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

            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(" ", "_");
//            String originalFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String contentType = multipartFile.getContentType();
            Long size = multipartFile.getSize();
            String fileExtension = getExtensionByStringHandling(originalFilename).orElse("");
            Path copyLocation = Paths.get(NFT_PATH + File.separator + originalFilename);
            Files.copy(multipartFile.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            String shaChecksum = getFileChecksum(shaDigest, copyLocation.toFile());
            Path newName = Paths.get(NFT_PATH + File.separator + shaChecksum + fileExtension);

            NftFile nftFile = nftFileRepository.findTopByFilename(shaChecksum + fileExtension);
            if (nftFile != null) {
                logger.error("File already exists: {}", gson.toJson(nftFile));
                return error123;
            }

            nftFile = new NftFile(shaChecksum + fileExtension, contentType, size);
            if (Files.exists(newName)) {
                logger.info("{} exists", newName.toString());
                Files.delete(copyLocation);

            } else {
                Files.move(copyLocation, copyLocation.resolveSibling(newName));
                Map<Tag, String> valueMap = getImageParams(newName);
                nftFile.setExifOrientation(valueMap.get(StandardTag.ORIENTATION));
                nftFile.setHeight(valueMap.get(StandardTag.IMAGE_HEIGHT));
                nftFile.setWidth(valueMap.get(StandardTag.IMAGE_WIDTH));

                nftFileRepository.save(nftFile);

                if (multipartFile.getContentType().toLowerCase().contains("video")) {
                    String videoName = newName.toString();
                    String imageName = NFT_PATH + File.separator + shaChecksum + ".jpeg";
                    createPreview(videoName, imageName);
                    resizePreview(shaChecksum + ".jpeg");
                } else {
                    if (multipartFile.getContentType().toLowerCase().contains("gif")) {
                        String videoName = newName.toString();
                        String imageName = NFT_PATH + File.separator + shaChecksum + ".jpeg";
                        createPreview(videoName, imageName);
                    }
                    resizePreview(shaChecksum + fileExtension);
                }
            }

            return new ResultDTO(true, nftFile, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private Map<Tag, String> getImageParams(Path path) {
        logger.info("Get Image Params");
        Map<Tag, String> valueMap = new HashMap<>();
        try {
            File image = path.toFile();
            ExifTool exifTool = new ExifToolBuilder().build();

            valueMap = exifTool.getImageMeta(image, asList(
                    StandardTag.ORIENTATION,
                    StandardTag.IMAGE_WIDTH,
                    StandardTag.IMAGE_HEIGHT,
                    StandardTag.ROTATION
            ));
        } catch (Exception e) {
            e.printStackTrace();
            valueMap.put(StandardTag.ORIENTATION, null);
            valueMap.put(StandardTag.IMAGE_HEIGHT, null);
            valueMap.put(StandardTag.IMAGE_WIDTH, null);
            valueMap.put(StandardTag.ROTATION, null);
        }
        String orientation = valueMap.get(StandardTag.ORIENTATION);
        String rotation = valueMap.get(StandardTag.ROTATION);
        String height = valueMap.get(StandardTag.IMAGE_HEIGHT);
        String width = valueMap.get(StandardTag.IMAGE_WIDTH);

        if (orientation == null && rotation != null) {
            if (rotation.trim().equals("90")) {
                orientation = "8";
                valueMap.put(StandardTag.IMAGE_HEIGHT, width);
                valueMap.put(StandardTag.IMAGE_WIDTH, height);
            } else if (rotation.trim().equals("270")) {
                orientation = "6";
                valueMap.put(StandardTag.IMAGE_HEIGHT, width);
                valueMap.put(StandardTag.IMAGE_WIDTH, height);
            } else if (rotation.trim().equals("180")) {
                orientation = "3";
            }
            valueMap.put(StandardTag.ORIENTATION, orientation);
        }

        logger.info(gson.toJson(valueMap));
        return valueMap;

    }

    private void resizePreview(String fileName) {
        try {
            String originalFile = NFT_PATH + File.separator + fileName;
            String size300 = NFT_PATH + File.separator + "300" + File.separator + fileName;
            String size600 = NFT_PATH + File.separator + "600" + File.separator + fileName;
            String size1000 = NFT_PATH + File.separator + "1000" + File.separator + fileName;

            String exif = getRotateOrientation(originalFile);
//            logger.info("EXIF: " + exif);
            String command = "ffmpeg  -y -i '" + originalFile + "' -vf \"scale=300:-1, select=eq(n\\,0)\"  '" + size300 + "'";
//            logger.info(command);
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
//            logger.info("process.isAlive() : {}", process.isAlive());
            int result = process.waitFor();
//            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size300, exif);
            }

            command = "ffmpeg -y  -i '" + originalFile + "' -vf \"scale=600:-1, select=eq(n\\,0)\"  '" + size600 + "'";
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            result = process.waitFor();
//            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size600, exif);
            }

            command = "ffmpeg  -y  -i '" + originalFile + "' -vf \"scale=1000:-1, select=eq(n\\,0)\"  '" + size1000 + "'";
            process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            result = process.waitFor();
//            logger.info("Result: {}", result);

            if (exif != null) {
                setRotateOrientation(size1000, exif);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRotateOrientation(String filename, String exif) {
        try {
            File image = new File(filename);
//            logger.info("ORIENTATION before set: {}", getRotateOrientation(filename));

            ExifTool exifTool = new ExifToolBuilder().build();

            Map<Tag, String> valueMap = new HashMap<>();
            valueMap.put(StandardTag.ORIENTATION, exif);
            exifTool.setImageMeta(image, valueMap);
//            logger.info("ORIENTATION after set: {}", getRotateOrientation(filename));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRotateOrientation(String originalFile) {
        try {
            File image = new File(originalFile);
            ExifTool exifTool = new ExifToolBuilder().build();

            Map<Tag, String> valueMap = exifTool.getImageMeta(image, asList(
                    StandardTag.ORIENTATION,
                    StandardTag.ROTATION
            ));
            String orientation = valueMap.get(StandardTag.ORIENTATION);
            String rotation = valueMap.get(StandardTag.ROTATION);
//            logger.info("ORIENTATION: {}", orientation);
//            logger.info("ROTATION: {}", rotation);
            if (orientation == null && rotation != null) {
                if (rotation.trim().equals("90")) {
                    orientation = "8";
                } else if (rotation.trim().equals("270")) {
                    orientation = "6";
                } else if (rotation.trim().equals("180")) {
                    orientation = "3";
                }
            }
            return orientation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void convertAllFiles() {
        logger.info("Convert All files START");

        List<Nft> nfts = nftService.findLast(LocalDateTime.of(2021, Month.JUNE, 1, 0, 0));

        for (Nft nft : nfts) {
            NftFile nftFile = nftFileRepository.findTopByFilename(nft.getFileName());
            if (nftFile.getContentType().toLowerCase().contains("video")) {
//                logger.info("Filename : {}",nft.getFileName());
                String shaChecksum = nft.getFileName().split("\\.")[0];
//                logger.info(shaChecksum);
                resizePreview(shaChecksum + ".jpeg");
            } else {
                resizePreview(nft.getFileName());
            }
        }


 /*
              List<NftFile> nftFileList = nftFileRepository.findAll();
        for (NftFile nftFile : nftFileList) {
     if (nftFile.getContentType().toLowerCase().contains("video")) {
//                String shaChecksum = nftFile.getFilename().replace(".gif", "");
//                String videoName = NFT_PATH + File.separator + shaChecksum + ".gif";
//                String imageName = NFT_PATH + File.separator + shaChecksum + ".jpeg";
//                createPreview(videoName, imageName);

                String orientation = getRotateOrientation(NFT_PATH + File.separator + nftFile.getFilename());
                if (orientation != null && orientation != "1") {
                    logger.info("FILE: {}", nftFile.getFilename());
                    Nft nft = nftService.findByfile(nftFile);
                    nft.setOrientation(orientation);
                    if (orientation.equals("5") || orientation.equals("6")
                            || orientation.equals("7") || orientation.equals("8")) {
                        nft.setHeight(nftFile.getWidth());
                        nft.setWidth(nftFile.getHeight());
                    }
                    nftService.save(nft);
                    nftFile.setExifOrientation(orientation);
                    nftFileRepository.save(nftFile);
                }
            }



        }
 */
        logger.info("Convert All files END");
    }


    private boolean createPreview(String videoName, String imageName) {
        try {
            String command = "ffmpeg -i '" + videoName + "' -frames 1  -f image2 '" + imageName + "'";
            if (videoName.toLowerCase().contains("gif")) {
                command = "ffmpeg -y -i '" + videoName + "' -pix_fmt yuvj420p  -vf \"select=eq(n\\,0)\" '" + imageName + "'";
            }

            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            int result = process.waitFor();
            logger.info("Result: {}", result);
            logger.info(imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    @PostConstruct
//    void getOrient() {
//        try {
//            List<NftFile> fileList = nftFileRepository.findAll();
//            for (NftFile nftFile : fileList) {
//                String orientation = nftFile.getExifOrientation();
//                if (orientation != null && orientation.length() > 0) {
//                    String fileName = nftFile.getFilename();
//                    String size300 = NFT_PATH + File.separator + "300" + File.separator + fileName;
//                    String size600 = NFT_PATH + File.separator + "600" + File.separator + fileName;
//                    String size1000 = NFT_PATH + File.separator + "1000" + File.separator + fileName;
//
//                    setRotateOrientation(size300, orientation);
//                    setRotateOrientation(size600, orientation);
//                    setRotateOrientation(size1000, orientation);
//                    logger.info("Set orientation {} to {}", orientation, fileName);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
