package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.HistoryNftRequestDTO;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftFile;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.repository.NftHistoryRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.service.AIService;
import fashion.coin.wallet.back.service.ClientService;
import fashion.coin.wallet.back.service.FileUploadService;
import fashion.coin.wallet.back.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fashion.coin.wallet.back.constants.ErrorDictionary.*;

@Service
public class NftService {

    Logger logger = LoggerFactory.getLogger(NftService.class);


    NftRepository nftRepository;

    FileUploadService fileUploadService;

    ClientService clientService;

    AIService aiService;

    TransactionService transactionService;

    NftHistoryRepository nftHistoryRepository;

    @Autowired
    public void setNftRepository(NftRepository nftRepository) {
        this.nftRepository = nftRepository;
    }

    @Autowired
    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setNftHistoryRepository(NftHistoryRepository nftHistoryRepository) {
        this.nftHistoryRepository = nftHistoryRepository;
    }

    public ResultDTO mint(MultipartFile multipartFile, String apikey, String login,
                          String title, String description, BigDecimal faceValue, BigDecimal creativeValue,
                          BlockchainTransactionDTO blockchainTransaction) {

        if (!clientService.checkApiKey(login, apikey)) return error109;
        ResultDTO resultDTO = fileUploadService.saveNft(multipartFile);
        if (!resultDTO.isResult()) return resultDTO;
        NftFile nftFile = (NftFile) resultDTO.getData();

        List<Nft> nftList = nftRepository.findByFileName(nftFile.getFilename());
        if (nftList != null && nftList.size() > 0) {
            return error123;
        }

        Client client = clientService.findByCryptoname(login);

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(faceValue.toString());
        transactionRequestDTO.setBlockchainTransaction(blockchainTransaction);
        transactionRequestDTO.setReceiverLogin(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
        transactionRequestDTO.setSenderWallet(client.getWalletAddress());
        resultDTO = transactionService.send(transactionRequestDTO);
        if (!resultDTO.isResult()) return resultDTO;

        Nft nft = new Nft();
        nft.setAuthorId(client.getId());
        nft.setAuthorName(client.getCryptoname());
        nft.setOwnerId(client.getId());
        nft.setOwnerName(client.getCryptoname());
        nft.setTitle(title);
        nft.setDescription(description);
        nft.setFaceValue(faceValue);
        nft.setCreativeValue(creativeValue);
        nft.setFileName(nftFile.getFilename());
        nft.setTimestamp(System.currentTimeMillis());
        nft.setProofs(0L);
        nftRepository.save(nft);
        return new ResultDTO(true, nft, 0);
    }

    public ResultDTO buy(Long nftId, TransactionRequestDTO transactionRequest) {

        try {

            Client clientFrom = clientService.findByWallet(transactionRequest.getBlockchainTransaction().getBody().getTo());
            Client clientTo = clientService.findByWallet(transactionRequest.getBlockchainTransaction().getBody().getFrom());
            Nft nft = nftRepository.getOne(nftId);

            BigDecimal amount = new BigDecimal(transactionRequest.getBlockchainTransaction().getBody().getAmount());
            amount = amount.movePointLeft(3);

            logger.info("Client From: {}", clientFrom);
            logger.info("Client: To {}", clientTo);
            logger.info("Nft: {}", nft);
            logger.info("Amount: {}", amount);

            if (nft.getCreativeValue().compareTo(amount) != 0) {
                return error212;
            }

            NftHistory nftHistory = new NftHistory();
            nftHistory.setCryptonameFrom(clientFrom.getCryptoname());
            nftHistory.setCryptonameTo(clientTo.getCryptoname());
            nftHistory.setNftId(nft.getId());
            nftHistory.setAmount(amount);

            ResultDTO result = transactionService.send(transactionRequest);
            if (!result.isResult()) {
                return result;
            }
            nftHistory.setTimestamp(System.currentTimeMillis());
            nft.setOwnerId(clientTo.getId());
            nft.setOwnerName(clientTo.getCryptoname());
            nftRepository.save(nft);
            nftHistoryRepository.save(nftHistory);

            return new ResultDTO(true, nftHistory, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getHistory(HistoryNftRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            List<NftHistory> nftHistoryList = nftHistoryRepository.findByNftIdOrderByTimestampDesc(request.getNftId());
            if (nftHistoryList == null || nftHistoryList.size() == 0) {
                return new ResultDTO(true, new ArrayList<NftHistory>(), 0);
            }
            return new ResultDTO(true, nftHistoryList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Nft findNft(Long nftId) {

        return nftRepository.getOne(nftId);
    }
}
