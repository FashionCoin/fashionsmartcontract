package fashion.coin.wallet.back.nft.service;

import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.*;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftFile;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.entity.ProofHistory;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    FeedService feedService;

    PolClientService polClientService;

    ProofService proofService;

    // Way of allocated funds
    public static final String BASE_WAY = "base";

    // Purpose of payments
    public static final String AUTHOR = "author";
    public static final String SELLER = "seller";
    public static final String TAX_AND_PROOFS = "taxAndProofs";


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

    @Autowired
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    @Autowired
    public void setPolClientService(PolClientService polClientService) {
        this.polClientService = polClientService;
    }

    @Autowired
    public void setProofService(ProofService proofService) {
        this.proofService = proofService;
    }

    public ResultDTO mint(MultipartFile multipartFile, String apikey, String login,
                          String title, String description, BigDecimal faceValue, BigDecimal creativeValue,
                          BlockchainTransactionDTO blockchainTransaction) {

        if (!clientService.checkApiKey(login, apikey)) return error109;

        if (creativeValue.compareTo(BigDecimal.ZERO) <= 0 || faceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return error218;
        }

        if (!checkCreativeValueLimit(faceValue, creativeValue, new BigDecimal(100))) {
            return error219;
        }

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
        nft.setOwnerWallet(client.getWalletAddress());
        nft.setTitle(title);
        nft.setDescription(description);
        nft.setFaceValue(faceValue);
        nft.setCreativeValue(creativeValue);
        nft.setFileName(nftFile.getFilename());
        nft.setTimestamp(System.currentTimeMillis());
        nft.setProofs(BigDecimal.ZERO);
        nftRepository.save(nft);

//        feedService.addNewNft(nft);
        polClientService.addNft(nft);
        return new ResultDTO(true, nft, 0);
    }

    private boolean checkCreativeValueLimit(BigDecimal faceValue, BigDecimal creativeValue, BigDecimal maxRate) {
        BigDecimal rate = creativeValue.divide(faceValue, 1, RoundingMode.HALF_UP);
        return rate.compareTo(maxRate) <= 0;
    }

    public ResultDTO buy(BuyNftDTO buyNftDTO) {

        try {

            Client clientFrom = clientService.findByWallet(buyNftDTO.getTransactionsRequestMap().get(SELLER)
                    .getBlockchainTransaction().getBody().getTo());
            Client clientTo = clientService.findByWallet(buyNftDTO.getTransactionsRequestMap().get(SELLER)
                    .getBlockchainTransaction().getBody().getFrom());
            Nft nft = nftRepository.findById(buyNftDTO.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            if (!checkBuyAmounts(nft, buyNftDTO.getTransactionsRequestMap())) {
                return error212;
            }


            BigDecimal amount = nft.getCreativeValue();
            NftHistory nftHistory = new NftHistory();
            nftHistory.setCryptonameFrom(clientFrom.getCryptoname());
            nftHistory.setCryptonameTo(clientTo.getCryptoname());
            nftHistory.setNftId(nft.getId());
            nftHistory.setAmount(amount);

            ResultDTO result = sendAllTransactions(buyNftDTO.getTransactionsRequestMap());
            if (!result.isResult()) {
                return result;
            }

            result = proofService.dividendPayment(nft,clientFrom);
            if (!result.isResult()) {
                return result;
            }


            nftHistory.setTimestamp(System.currentTimeMillis());
            nft.setOwnerId(clientTo.getId());
            nft.setOwnerName(clientTo.getCryptoname());
            nft.setOwnerWallet(clientTo.getWalletAddress());
            nft.setCanChangeValue(true);
            nftRepository.save(nft);
            nftHistoryRepository.save(nftHistory);

            return new ResultDTO(true, nftHistory, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }



    private ResultDTO sendAllTransactions(Map<String, TransactionRequestDTO> transactionsRequestMap) {
        for (Map.Entry<String, TransactionRequestDTO> transactionRequestDTOEntry : transactionsRequestMap.entrySet()) {
            ResultDTO resultDTO = transactionService.send(transactionRequestDTOEntry.getValue());
            if (!resultDTO.isResult()) {
                return resultDTO;
            }
        }
        return new ResultDTO(true, "Transactions sended", 0);
    }

    private boolean checkBuyAmounts(Nft nft, Map<String, TransactionRequestDTO> transactionsRequestMap) {

        ResultDTO resultDTO = checkShare(nft.getId());
        if (resultDTO.getData() instanceof HashMap) {
            HashMap<String, AllocatedFundsDTO> share = (HashMap<String, AllocatedFundsDTO>) resultDTO.getData();
            for (Map.Entry<String, AllocatedFundsDTO> allocatedFunds : share.entrySet()) {
                TransactionRequestDTO transactionRequest = transactionsRequestMap.get(allocatedFunds.getKey());
                BigDecimal amount = new BigDecimal(transactionRequest.getBlockchainTransaction().getBody()
                        .getAmount()).movePointLeft(3);
                if (allocatedFunds.getValue().getAmount().compareTo(amount) != 0) {
                    return false;
                }
                String wallet = transactionRequest.getBlockchainTransaction().getBody().getTo();
                if (!allocatedFunds.getValue().getWallet().equals(wallet)) {
                    return false;
                }
            }
            return true;
        }
        logger.error(resultDTO.getData().getClass().getName());
        return false;
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

        return nftRepository.findById(nftId).orElse(null);
    }

    public ResultDTO getOneNft(NftRequestDTO request) {
        try {

            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            return new ResultDTO(true, nft, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO setNewValue(NewValueRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            if (!nft.getOwnerId().equals(client.getId())) {
                return error214;
            }
            if (!nft.isCanChangeValue()) {
                return error216;
            }
            if (nft.getCreativeValue().compareTo(request.getCreativeValue()) > 0
                    || nft.getFaceValue().compareTo(request.getFaceValue()) > 0) {
                return error215;
            }
            if (checkCreativeValueLimit(request.getFaceValue(), request.getCreativeValue(), new BigDecimal(100))) {
                return error219;
            }

            polClientService.increaseValue(client,
                    request.getFaceValue().subtract(nft.getFaceValue()),
                    request.getCreativeValue().subtract(nft.getCreativeValue()));


            nft.setCreativeValue(request.getCreativeValue());
            nft.setFaceValue(request.getFaceValue());
            nft.setCanChangeValue(false);
            nftRepository.save(nft);

            return new ResultDTO(true, nft, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO burnNft(NftRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            if (!nft.getOwnerId().equals(client.getId())) {
                return error214;
            }

            BigDecimal amountWithoutTax = nft.getFaceValue().multiply(new BigDecimal("0.9"))
                    .setScale(3, RoundingMode.HALF_UP);

            boolean result = aiService.transfer(amountWithoutTax.toString(), nft.getOwnerWallet(), AIService.AIWallets.MONEYBAG);
            if (result) {
                nft.setOwnerId(null);
                nft.setOwnerWallet(null);
                nft.setOwnerName(null);
                nft.setCreativeValue(BigDecimal.ZERO);
                nft.setBurned(true);
                nft.setFaceValue(BigDecimal.ZERO);
                nftRepository.save(nft);
                return new ResultDTO(true, nft, 0);
            } else {
                return error205;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    List<Nft> getNftByOwner(Long ownerId) {
        List<Nft> nftList = nftRepository.findByOwnerId(ownerId);
        if (nftList == null || nftList.size() == 0) {
            return new ArrayList<>();
        }
        return nftList;
    }

    public void save(Nft nft) {
        nftRepository.save(nft);
    }

    public List<Nft> getCollection(Long id) {
        List<Nft> collection = nftRepository.findByOwnerId(id);
        if (collection == null || collection.size() == 0) {
            return new ArrayList<>();
        }
        return collection;
    }

    public List<Nft> getCreation(Long id) {
        List<Nft> creation = nftRepository.findByAuthorId(id);
        if (creation == null || creation.size() == 0) {
            return new ArrayList<>();
        }
        return creation;
    }

    public ResultDTO checkShare(Long nftId) {
        try {
            Nft nft = nftRepository.findById(nftId).orElse(null);
            if (nft == null) {
                return error213;
            }

            if (nft.getWayOfAllocatingFunds().equals(BASE_WAY)) {

                Map<String, AllocatedFundsDTO> share = new HashMap<>();

                Client author = clientService.getClient(nft.getAuthorId());
                AllocatedFundsDTO authorFunds = new AllocatedFundsDTO();
                authorFunds.setPurpose(AUTHOR);
                authorFunds.setWallet(author.getWalletAddress());
                authorFunds.setAmount(nft.getCreativeValue().divide(BigDecimal.TEN, 3, RoundingMode.HALF_UP));
                share.put(AUTHOR, authorFunds);

                AllocatedFundsDTO sellerFunds = new AllocatedFundsDTO();
                sellerFunds.setPurpose(SELLER);
                sellerFunds.setWallet(nft.getOwnerWallet());
                sellerFunds.setAmount(nft.getCreativeValue().multiply(new BigDecimal("0.78")).setScale(3, RoundingMode.HALF_UP));
                share.put(SELLER, sellerFunds);

                AllocatedFundsDTO proofsFunds = new AllocatedFundsDTO();
                proofsFunds.setPurpose(TAX_AND_PROOFS);
                proofsFunds.setWallet(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
                proofsFunds.setAmount(nft.getCreativeValue()
                        .subtract(authorFunds.getAmount())
                        .subtract(sellerFunds.getAmount()));
                share.put(TAX_AND_PROOFS, proofsFunds);

                return new ResultDTO(true, share, 0);
            } else {
                return error220;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
