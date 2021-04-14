package fashion.coin.wallet.back.nft.service;

import com.google.gson.Gson;
import fashion.coin.wallet.back.dto.ResultDTO;
import fashion.coin.wallet.back.dto.TransactionRequestDTO;
import fashion.coin.wallet.back.dto.blockchain.BlockchainTransactionDTO;
import fashion.coin.wallet.back.entity.Client;
import fashion.coin.wallet.back.nft.dto.*;
import fashion.coin.wallet.back.nft.entity.Nft;
import fashion.coin.wallet.back.nft.entity.NftFile;
import fashion.coin.wallet.back.nft.entity.NftHistory;
import fashion.coin.wallet.back.nft.entity.NftTirage;
import fashion.coin.wallet.back.nft.repository.NftHistoryRepository;
import fashion.coin.wallet.back.nft.repository.NftRepository;
import fashion.coin.wallet.back.nft.repository.NftTirageRepository;
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
import static fashion.coin.wallet.back.nft.service.ProofService.DAY;

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

    Gson gson;

    // Way of allocated funds
    public static final String BASE_WAY = "base";

    // Purpose of payments
    public static final String AUTHOR = "author";
    public static final String SELLER = "seller";
    public static final String TAX_AND_PROOFS = "taxAndProofs";
    public static final String FEE = "0.0002";

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

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Autowired
    HashtagService hashtagService;


    @Autowired
    TirageService tirageService;

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
        if (nftList != null) {
            nftList.removeIf(nft -> nft.isBurned());
            if (nftList.size() > 0) {
                return error123;
            }
        }

        Client client = clientService.findByCryptoname(login);

        if (new BigDecimal(blockchainTransaction.getBody().getAmount()).compareTo(faceValue.movePointRight(3)) != 0) {
            return error210;
        }

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(faceValue.toString());
        transactionRequestDTO.setBlockchainTransaction(blockchainTransaction);
        transactionRequestDTO.setReceiverLogin(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
        transactionRequestDTO.setSenderWallet(client.getWalletAddress());
        resultDTO = transactionService.send(transactionRequestDTO);
        if (!resultDTO.isResult()) return resultDTO;


        Nft nft = new Nft();
        nft.setTxhash(resultDTO.getMessage());
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

        hashtagService.checkTags(nft.getDescription());

        return new ResultDTO(true, nft, 0);
    }

    public ResultDTO mintFree(MultipartFile multipartFile, String apikey, String login,
                              String title, String description) {

        BigDecimal faceValue = new BigDecimal(1000);
        BigDecimal creativeValue = new BigDecimal(100000);


        if (!clientService.checkApiKey(login, apikey)) return error109;

        if (!checkTenFreeNft(login)) {
            return error225;
        }


        ResultDTO resultDTO = fileUploadService.saveNft(multipartFile);
        if (!resultDTO.isResult()) return resultDTO;
        NftFile nftFile = (NftFile) resultDTO.getData();

        List<Nft> nftList = nftRepository.findByFileName(nftFile.getFilename());
        if (nftList != null && nftList.size() > 0) {
            return error123;
        }

        Client client = clientService.findByCryptoname(login);


        /*

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(faceValue.toString());
        transactionRequestDTO.setBlockchainTransaction(blockchainTransaction);
        transactionRequestDTO.setReceiverLogin(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
        transactionRequestDTO.setSenderWallet(client.getWalletAddress());
        resultDTO = transactionService.send(transactionRequestDTO);
        if (!resultDTO.isResult()) return resultDTO;
*/

        Nft nft = new Nft();
//        nft.setTxhash(resultDTO.getMessage());
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
        nft.setFree(true);
        nftRepository.save(nft);

        hashtagService.checkTags(nft.getDescription());

        return new ResultDTO(true, nft, 0);
    }

    private boolean checkTenFreeNft(String login) {

        Long timestamp = System.currentTimeMillis() - DAY;

        List<Nft> nftList = nftRepository.findByAuthorNameAndTimestampIsGreaterThan(login, timestamp);
        if (nftList == null) return true;
        return nftList.size() < 10;
    }

    private boolean checkCreativeValueLimit(BigDecimal faceValue, BigDecimal creativeValue, BigDecimal maxRate) {
        BigDecimal rate = creativeValue.divide(faceValue, 1, RoundingMode.HALF_UP);
        return rate.compareTo(maxRate) <= 0;
    }

    public ResultDTO buy(BuyNftDTO buyNftDTO) {
        NftTirage nftTirage = null;
        Nft nft = null;
        try {
            nft = nftRepository.findById(buyNftDTO.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }

            if (nft.isInsale()) {
                return error221;
            }

            if (nft.isTirage()) {
                nftTirage = tirageService.tirageFindByNftAndOwnerId(buyNftDTO.getNftId(), buyNftDTO.getOwnerId());
                if (nftTirage.getTirage() < buyNftDTO.getPieces()) {
                    return error226;
                }
            }

            Client clientFrom = clientService.findByWallet(buyNftDTO.getTransactionsRequestMap().get(SELLER)
                    .getBlockchainTransaction().getBody().getTo());
            Client clientTo = clientService.findByWallet(buyNftDTO.getTransactionsRequestMap().get(SELLER)
                    .getBlockchainTransaction().getBody().getFrom());

            if (!checkBuyAmounts(nft, buyNftDTO, buyNftDTO.getTransactionsRequestMap())) {
                return error212;
            }
            BigDecimal amount;
            if (nft.isTirage()) {
                amount = nftTirage.getCreativeValue().multiply(BigDecimal.valueOf(buyNftDTO.getPieces()))
                        .setScale(3, RoundingMode.HALF_UP);
            } else {
                amount = nft.getCreativeValue();
            }


            NftHistory nftHistory = new NftHistory();
            nftHistory.setCryptonameFrom(clientFrom.getCryptoname());
            nftHistory.setCryptonameTo(clientTo.getCryptoname());
            nftHistory.setNftId(nft.getId());
            nftHistory.setAmount(amount);

            if (nft.isTirage()) {
                if (!nftTirage.isInsale()) {
                    nftTirage.setInsale(true);
                    tirageService.save(nftTirage);
                } else {
                    return error221;
                }
            } else {
                if (!nft.isInsale()) {
                    nft.setInsale(true);
                    nftRepository.save(nft);
                } else {
                    return error221;
                }
            }

            ResultDTO result = sendAllTransactions(buyNftDTO.getTransactionsRequestMap());
            if (nft.isTirage()) {
                nftTirage.setInsale(false);
                tirageService.save(nftTirage);
            } else {
                nft.setInsale(false);
                nftRepository.save(nft);
            }

            if (!result.isResult()) {

                return result;
            }

            nftHistory.setTxhash(result.getMessage());
            if (!nft.isTirage()) {

                result = proofService.dividendPayment(nft, clientFrom);
                if (!result.isResult()) {

                    return result;
                }
            }


            nftHistory.setTimestamp(System.currentTimeMillis());
            if (nft.isTirage()) {
                NftTirage newOwnerNft = tirageService.tirageFindByNftAndOwnerId(nft.getId(), clientTo.getId());

                newOwnerNft.setCanChangeValue(true);
                newOwnerNft.setCreativeValue(nftTirage.getCreativeValue());
                newOwnerNft.setTxhash(result.getMessage());
                tirageService.setPieces(nftTirage, -buyNftDTO.getPieces());
                tirageService.setPieces(newOwnerNft, buyNftDTO.getPieces());

            } else {
                nft.setOwnerId(clientTo.getId());
                nft.setOwnerName(clientTo.getCryptoname());
                nft.setOwnerWallet(clientTo.getWalletAddress());
                nft.setCanChangeValue(true);
                nftRepository.save(nft);
            }
            nftHistoryRepository.save(nftHistory);

            nftRepository.save(nft);
            return new ResultDTO(true, nftHistory, 0);
        } catch (Exception e) {
            e.printStackTrace();

            if (nft.isInsale()) {
                nft.setInsale(false);
                nftRepository.save(nft);
            }
            if (nftTirage != null && nftTirage.isInsale()) {
                nftTirage.setInsale(false);
                tirageService.save(nftTirage);
            }
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    private ResultDTO sendAllTransactions(Map<String, TransactionRequestDTO> transactionsRequestMap) {
        String txhash = "";
        for (Map.Entry<String, TransactionRequestDTO> transactionRequestDTOEntry : transactionsRequestMap.entrySet()) {
            ResultDTO resultDTO = transactionService.send(transactionRequestDTOEntry.getValue());
            if (!resultDTO.isResult()) {
                return resultDTO;
            }
            if (transactionRequestDTOEntry.getKey().equals(SELLER)) {
                txhash = resultDTO.getMessage();
            }
        }
        return new ResultDTO(true, txhash, 0);
    }

    private boolean checkBuyAmounts(Nft nft, BuyNftDTO request, Map<String, TransactionRequestDTO> transactionsRequestMap) {
        ResultDTO resultDTO = null;
        if (nft.isTirage()) {
            resultDTO = checkShareTirage(request);
        } else {
            resultDTO = checkShare(request);
        }


        logger.info(gson.toJson(resultDTO));
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
//            Client client = clientService.findClientByApikey(request.getApikey());
//            if (client == null) {
//                return error109;
//            }
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

            Long ownerId = nft.getOwnerId();
            if (nft.isTirage()) {

                return error229;
            }


            Client client = clientService.getClient(ownerId);

            OneNftResponceDTO oneNft = new OneNftResponceDTO();
            oneNft.setId(nft.getId());
            oneNft.setAvaExists(client.avaExists());
            oneNft.setAvatar(client.getAvatar());
            oneNft.setAuthorId(nft.getAuthorId());
            oneNft.setAuthorName(nft.getAuthorName());
            oneNft.setBanned(nft.isBanned());
            oneNft.setBurned(nft.isBurned());
            oneNft.setCanChangeValue(nft.isCanChangeValue());
            oneNft.setCreativeValue(nft.getCreativeValue());
            oneNft.setDescription(nft.getDescription());
            oneNft.setFaceValue(nft.getFaceValue());
            oneNft.setFileName(nft.getFileName());
            oneNft.setInsale(nft.isInsale());
            oneNft.setOwnerId(ownerId);
            oneNft.setOwnerName(client.getCryptoname());
            oneNft.setOwnerWallet(client.getWalletAddress());
            oneNft.setProofs(nft.getProofs());
            oneNft.setTimestamp(nft.getTimestamp());
            oneNft.setTitle(nft.getTitle());
            oneNft.setTxhash(nft.getTxhash());
            oneNft.setWayOfAllocatingFunds(nft.getWayOfAllocatingFunds());

            oneNft.setPieces(1L);


            return new ResultDTO(true, oneNft, 0);
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
            NftTirage nftTirage = null;
            if (nft.isTirage()) {
                nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(), client.getId());
                if (nftTirage.getTirage() == 0) {
                    return error214;
                }
                if (!nftTirage.isCanChangeValue()) {
                    return error216;
                }
                if (nftTirage.getCreativeValue().compareTo(request.getCreativeValue()) > 0) {
                    return error215;
                }

                nftTirage.setCreativeValue(request.getCreativeValue());
                nftTirage.setCanChangeValue(false);
                tirageService.save(nftTirage);
                return new ResultDTO(true, nftTirage, 0);
            } else {

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
                if (!checkCreativeValueLimit(request.getFaceValue(), request.getCreativeValue(), new BigDecimal(100))) {
                    return error219;
                }

                ResultDTO resultDTO = checkIncreaseTransaction(nft, request);
                if (!resultDTO.isResult()) {
                    return resultDTO;
                }

                resultDTO = transactionService.send(request.getTransactionRequest());
                if (!resultDTO.isResult()) {
                    return resultDTO;
                }

                nft.setCreativeValue(request.getCreativeValue());
                nft.setFaceValue(request.getFaceValue());
                nft.setCanChangeValue(false);
                nftRepository.save(nft);
                return new ResultDTO(true, nft, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private ResultDTO checkIncreaseTransaction(Nft nft, NewValueRequestDTO request) {

        BigDecimal increaseFaceValue = request.getFaceValue().subtract(nft.getFaceValue());

        BigDecimal transactionAmount = new BigDecimal(request.getTransactionRequest().getBlockchainTransaction()
                .getBody().getAmount()).movePointLeft(3);

        if (increaseFaceValue.compareTo(transactionAmount) != 0) {
            return error205;
        }

        if (!request.getTransactionRequest().getBlockchainTransaction().getBody()
                .getTo().equals(aiService.getPubKey(AIService.AIWallets.MONEYBAG))) {
            return error205;
        }
        if (!request.getTransactionRequest().getBlockchainTransaction().getBody()
                .getFrom().equals(nft.getOwnerWallet())) {
            return error214;
        }
        return new ResultDTO(true, "Ok", 0);
    }

    public ResultDTO burnNft(NftRequestDTO request) {
        NftTirage nftTirage = null;
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }
            BigDecimal amountWithoutTax = null;
            if (nft.isTirage()) {
                nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(), client.getId());
                if (!nftTirage.getOwnerId().equals(client.getId())) {
                    return error214;
                }
                amountWithoutTax = nft.getFaceValue().multiply(BigDecimal.valueOf(request.getPieces()))
                        .multiply(new BigDecimal("0.9"))
                        .setScale(3, RoundingMode.HALF_UP);
            } else {

                if (!nft.getOwnerId().equals(client.getId())) {
                    return error214;
                }
                amountWithoutTax = nft.getFaceValue().multiply(new BigDecimal("0.9"))
                        .setScale(3, RoundingMode.HALF_UP);
            }


            boolean result =false;
            if(nft.isTirage()) {
                aiService.transfer(amountWithoutTax.toString(),
                        nftTirage.getOwnerWallet(),
                        AIService.AIWallets.MONEYBAG).isResult();
            }else{
                aiService.transfer(amountWithoutTax.toString(),
                        nft.getOwnerWallet(),
                        AIService.AIWallets.MONEYBAG).isResult();
            }

            if (result) {
                if (nft.isTirage()) {
                    tirageService.setPieces(nftTirage, -request.getPieces());
                    if (tirageService.totalNfts(nft) == 0) {
                        nft.setOwnerId(null);
                        nft.setOwnerWallet(null);
                        nft.setOwnerName(null);
                        nft.setCreativeValue(BigDecimal.ZERO);
                        nft.setBurned(true);
                        nft.setFaceValue(BigDecimal.ZERO);
                        nftRepository.save(nft);
                        return new ResultDTO(true, nft, 0);
                    }
                    return new ResultDTO(true, nftTirage, 0);
                } else {

                    nft.setOwnerId(null);
                    nft.setOwnerWallet(null);
                    nft.setOwnerName(null);
                    nft.setCreativeValue(BigDecimal.ZERO);
                    nft.setBurned(true);
                    nft.setFaceValue(BigDecimal.ZERO);
                    nftRepository.save(nft);
                    return new ResultDTO(true, nft, 0);
                }
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
        List<Nft> tirage = tirageService.tirageFindByOwnerId(id);
        if ((collection == null || collection.size() == 0)
                && (tirage == null || tirage.size() == 0)) {
            return new ArrayList<>();
        }
        collection.addAll(tirage);
        return collection;
    }

    public List<Nft> getCreation(Long id) {
        List<Nft> creation = nftRepository.findByAuthorId(id);
        if (creation == null || creation.size() == 0) {
            return new ArrayList<>();
        }
        return creation;
    }

    public ResultDTO checkShare(BuyNftDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }

            if (nft.getWayOfAllocatingFunds().equals(BASE_WAY)) {

                Map<String, AllocatedFundsDTO> share = new HashMap<>();


                Client author = clientService.getClient(nft.getAuthorId());
                AllocatedFundsDTO authorFunds = new AllocatedFundsDTO();
                authorFunds.setPurpose(AUTHOR);
                authorFunds.setWallet(author.getWalletAddress());

                if (nft.getAuthorId().equals(client.getId())) {
                    logger.info("Buyer is author: {}", client.getCryptoname());
                    authorFunds.setAmount(BigDecimal.ZERO);
                } else {
                    authorFunds.setAmount(nft.getCreativeValue().divide(BigDecimal.TEN, 3, RoundingMode.HALF_UP));
                    share.put(AUTHOR, authorFunds);
                }

                if (nft.getOwnerId().equals(client.getId())) {
                    logger.error("Seller is Buyer: {}", client.getCryptoname());
                    return new ResultDTO(false, "Seller is buyer", -1);
                }
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

    public ResultDTO myHistory(HistoryNftRequestDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }
            List<NftHistory> nftHistoryList = nftHistoryRepository.findByCryptonameFromOrCryptonameToOrderByTimestampDesc(
                    client.getCryptoname(), client.getCryptoname());
            if (nftHistoryList == null || nftHistoryList.size() == 0) {
                return new ResultDTO(true, new ArrayList<NftHistory>(), 0);
            }
            List<MyHistoryDTO> myHistoryList = new ArrayList<>();
            for (NftHistory nftHistory : nftHistoryList) {
                MyHistoryDTO myHistory = new MyHistoryDTO();
                myHistory.setId(nftHistory.getId());
                Nft nft = nftRepository.findById(nftHistory.getNftId()).orElse(null);
                myHistory.setNftId(nft.getId());
                myHistory.setTitle(nft.getTitle());
                myHistory.setFileName(nft.getFileName());
                myHistory.setCryptonameFrom(nftHistory.getCryptonameFrom());
                myHistory.setCryptonameTo(nftHistory.getCryptonameTo());
                myHistory.setAmount(nftHistory.getAmount());
                myHistory.setTimestamp(nftHistory.getTimestamp());
                myHistory.setTxhash(nftHistory.getTxhash());
                myHistoryList.add(myHistory);
            }


            return new ResultDTO(true, myHistoryList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO transfer(NftTransferDTO request) {
        NftTirage nftTirage = null;
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }

            if (nft.isTirage()) {
                nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(), client.getId());
                if (nftTirage.getTirage() == 0) {
                    return error214;
                }
                if (nftTirage.getTirage() < request.getPieces()) {
                    return error226;
                }
            } else {
                if (!nft.getOwnerId().equals(client.getId())) {
                    return error214;
                }
            }

            Client friend = clientService.findByCryptonameOrWallet(request.getReceiver());
            if (friend == null) {
                return error112;
            }

            if (nft.isTirage()) {
                if (!checkTirageTransferFee(nft, request.getPieces(), request.getTransactionRequest())) {
                    return error224;
                }
            } else {
                if (!checkTransferFee(nft, request.getTransactionRequest())) {
                    return error224;
                }
            }

            ResultDTO resultDTO = transactionService.send(request.getTransactionRequest());
            if (!resultDTO.isResult()) {
                return resultDTO;
            }


            NftHistory nftHistory = new NftHistory();
            nftHistory.setCryptonameFrom(client.getCryptoname());
            nftHistory.setCryptonameTo(friend.getCryptoname());
            nftHistory.setTimestamp(System.currentTimeMillis());
            nftHistory.setAmount(nft.getFaceValue());
            nftHistory.setNftId(nft.getId());
            nftHistory.setTxhash(resultDTO.getMessage());
            nftHistoryRepository.save(nftHistory);

            if (nft.isTirage()) {
                NftTirage newOwnerTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(), friend.getId());
                newOwnerTirage.setCanChangeValue(false);
                newOwnerTirage.setCreativeValue(nftTirage.getCreativeValue());
                newOwnerTirage.setTxhash(nftHistory.getTxhash());
                tirageService.setPieces(nftTirage, -request.getPieces());
                tirageService.setPieces(newOwnerTirage, request.getPieces());

            } else {
                nft.setOwnerId(friend.getId());
                nft.setOwnerName(friend.getCryptoname());
                nft.setOwnerWallet(friend.getWalletAddress());
                nftRepository.save(nft);

            }
            return new ResultDTO(true, nftHistory, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private boolean checkTransferFee(Nft nft, TransactionRequestDTO transactionRequest) {

        BigDecimal transactionFee = new BigDecimal(transactionRequest.getBlockchainTransaction().getBody().getAmount()).movePointLeft(3);


        BigDecimal nftFee = nft.getFaceValue().multiply(new BigDecimal(FEE)).setScale(3, RoundingMode.HALF_UP);
        BigDecimal minimafFee = new BigDecimal("0.001");
        if (nftFee.compareTo(minimafFee) < 0) {
            nftFee = minimafFee;
        }

        if (transactionFee.compareTo(nftFee) != 0) {
            logger.error("transactionFee {} != {}", transactionFee, nftFee);
            return false;
        }

        if (!transactionRequest.getBlockchainTransaction().getBody().getTo().equals(aiService.getPubKey(AIService.AIWallets.MONEYBAG))) {
            logger.error("Receiver: {}", transactionRequest.getBlockchainTransaction().getBody().getTo());
            logger.error("MoneyBag: {}", aiService.getPubKey(AIService.AIWallets.MONEYBAG));
            return false;
        }

        return true;
    }

    private boolean checkTirageTransferFee(Nft nft, Long pieces, TransactionRequestDTO transactionRequest) {

        BigDecimal transactionFee = new BigDecimal(transactionRequest.getBlockchainTransaction().getBody().getAmount()).movePointLeft(3);


        BigDecimal nftFee = nft.getFaceValue().multiply(BigDecimal.valueOf(pieces))
                .multiply(new BigDecimal(FEE)).setScale(3, RoundingMode.HALF_UP);
        BigDecimal minimafFee = new BigDecimal("0.001");
        if (nftFee.compareTo(minimafFee) < 0) {
            nftFee = minimafFee;
        }

        if (transactionFee.compareTo(nftFee) != 0) {
            logger.error("transactionFee {} != {}", transactionFee, nftFee);
            return false;
        }

        if (!transactionRequest.getBlockchainTransaction().getBody().getTo().equals(aiService.getPubKey(AIService.AIWallets.MONEYBAG))) {
            logger.error("Receiver: {}", transactionRequest.getBlockchainTransaction().getBody().getTo());
            logger.error("MoneyBag: {}", aiService.getPubKey(AIService.AIWallets.MONEYBAG));
            return false;
        }

        return true;
    }


    public ResultDTO mintTirage(MultipartFile multipartFile, String apikey, String login, String title,
                                String description, BigDecimal faceValue, BigDecimal creativeValue, Long tirage,
                                BlockchainTransactionDTO blockchainTransaction) {

        if (!clientService.checkApiKey(login, apikey)) return error109;

        if (creativeValue.compareTo(BigDecimal.ZERO) <= 0 || faceValue.compareTo(BigDecimal.ZERO) <= 0) {
            return error218;
        }


        ResultDTO resultDTO = fileUploadService.saveNft(multipartFile);
        if (!resultDTO.isResult()) return resultDTO;
        NftFile nftFile = (NftFile) resultDTO.getData();

        List<Nft> nftList = nftRepository.findByFileName(nftFile.getFilename());
        if (nftList != null) {
            nftList.removeIf(nft -> nft.isBurned());
            if (nftList.size() > 0) {
                return error123;
            }
        }


        Client client = clientService.findByCryptoname(login);

        BigDecimal total = faceValue.multiply(new BigDecimal(tirage)).setScale(3, RoundingMode.HALF_UP);
        if (new BigDecimal(blockchainTransaction.getBody().getAmount()).compareTo(total.movePointRight(3)) != 0) {
            return error210;
        }

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(total.toString());
        transactionRequestDTO.setBlockchainTransaction(blockchainTransaction);
//        transactionRequestDTO.setReceiverLogin(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
        transactionRequestDTO.setReceiverWallet(aiService.getPubKey(AIService.AIWallets.MONEYBAG));
        transactionRequestDTO.setSenderWallet(client.getWalletAddress());
        resultDTO = transactionService.send(transactionRequestDTO);
        if (!resultDTO.isResult()) return resultDTO;

        Nft nft = new Nft();
        nft.setTxhash(resultDTO.getMessage());
        nft.setAuthorId(client.getId());
        nft.setAuthorName(client.getCryptoname());

        nft.setTitle(title);
        nft.setDescription(description);
        nft.setFaceValue(faceValue);

        nft.setFileName(nftFile.getFilename());
        nft.setTimestamp(System.currentTimeMillis());
        nft.setProofs(BigDecimal.ZERO);
        nft.setTirage(true);
        nftRepository.save(nft);

        NftTirage nftTirage = tirageService.tirageFindByNftAndOwnerId(nft.getId(), client.getId());

        nftTirage.setCreativeValue(creativeValue);

        nftTirage.setTxhash(resultDTO.getMessage());
        nftTirage.setTirage(tirage);
        tirageService.save(nftTirage);

        hashtagService.checkTags(nft.getDescription());

        return new ResultDTO(true, nft, 0);
    }

    public ResultDTO checkShareTirage(BuyNftDTO request) {
        try {
            Client client = clientService.findClientByApikey(request.getApikey());
            if (client == null) {
                return error109;
            }

            Nft nft = nftRepository.findById(request.getNftId()).orElse(null);
            if (nft == null) {
                return error213;
            }

            NftTirage nftTirage = tirageService.tirageFindByNftAndOwnerId(request.getNftId(), request.getOwnerId());
            if (nftTirage.getTirage() < request.getPieces()) {
                return error226;
            }

            Client owner = clientService.getClient(request.getOwnerId());

            if (nft.getWayOfAllocatingFunds().equals(BASE_WAY)) {

                Map<String, AllocatedFundsDTO> share = new HashMap<>();


                Client author = clientService.getClient(nft.getAuthorId());
                AllocatedFundsDTO authorFunds = new AllocatedFundsDTO();
                authorFunds.setPurpose(AUTHOR);
                authorFunds.setWallet(author.getWalletAddress());

                if (nft.getAuthorId().equals(client.getId())) {
                    logger.info("Buyer is author: {}", client.getCryptoname());
                    authorFunds.setAmount(BigDecimal.ZERO);
                } else {
                    authorFunds.setAmount(nftTirage.getCreativeValue()
                            .multiply(BigDecimal.valueOf(request.getPieces()))
                            .divide(BigDecimal.TEN, 3, RoundingMode.HALF_UP));
                    share.put(AUTHOR, authorFunds);
                }

                if (owner.getId().equals(client.getId())) {
                    logger.error("Seller is Buyer: {}", client.getCryptoname());
                    return new ResultDTO(false, "Seller is buyer", -1);
                }
                AllocatedFundsDTO sellerFunds = new AllocatedFundsDTO();
                sellerFunds.setPurpose(SELLER);
                sellerFunds.setWallet(owner.getWalletAddress());
                sellerFunds.setAmount(nftTirage.getCreativeValue()
                        .multiply(BigDecimal.valueOf(request.getPieces()))
                        .multiply(new BigDecimal("0.78")).setScale(3, RoundingMode.HALF_UP));
                share.put(SELLER, sellerFunds);

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
