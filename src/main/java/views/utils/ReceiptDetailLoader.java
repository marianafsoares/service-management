package views.utils;

import controllers.ClientController;
import controllers.ProviderController;
import controllers.ReceiptCardController;
import controllers.ReceiptCashController;
import controllers.ReceiptChequeController;
import controllers.ReceiptRetentionController;
import controllers.ReceiptTransferController;
import mappers.receipts.ReceiptCardMapper;
import mappers.receipts.ReceiptCashMapper;
import mappers.receipts.ReceiptChequeMapper;
import mappers.receipts.ReceiptRetentionMapper;
import mappers.receipts.ReceiptTransferMapper;
import models.Client;
import models.Provider;
import models.receipts.ClientReceipt;
import models.receipts.ProviderReceipt;
import models.receipts.ReceiptCard;
import models.receipts.ReceiptCash;
import models.receipts.ReceiptCheque;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptRetention;
import models.receipts.ReceiptTransfer;
import models.receipts.ReceiptType;
import org.apache.ibatis.session.SqlSession;
import repositories.ReceiptCardRepository;
import repositories.ReceiptCashRepository;
import repositories.ReceiptChequeRepository;
import repositories.ReceiptRetentionRepository;
import repositories.ReceiptTransferRepository;
import repositories.impl.ReceiptCardRepositoryImpl;
import repositories.impl.ReceiptCashRepositoryImpl;
import repositories.impl.ReceiptChequeRepositoryImpl;
import repositories.impl.ReceiptRetentionRepositoryImpl;
import repositories.impl.ReceiptTransferRepositoryImpl;
import services.ReceiptCardService;
import services.ReceiptCashService;
import services.ReceiptChequeService;
import services.ReceiptRetentionService;
import services.ReceiptTransferService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ReceiptDetailLoader {
    private ReceiptDetailLoader() {
    }

    public static ReceiptDetailData loadClientReceipt(SqlSession sqlSession,
                                                      ClientReceipt receipt,
                                                      ClientController clientController) {
        ReceiptDetailData detail = new ReceiptDetailData();
        detail.setType(ReceiptType.CLIENT);
        detail.setPointOfSale(receipt.getPointOfSale());
        detail.setReceiptNumber(receipt.getReceiptNumber());
        detail.setReceiptDate(receipt.getReceiptDate());
        detail.setTotal(receipt.getTotal());
        detail.setNotes(receipt.getNotes());
        if (receipt.getClient() != null && receipt.getClient().getId() != null) {
            Client client = clientController.findById(receipt.getClient().getId());
            if (client != null) {
                detail.setEntityName(client.getFullName());
                detail.setBalance(clientController.getBalance(client.getId()));
            }
        }
        populatePaymentDetails(sqlSession, detail, receipt.getId(), ReceiptType.CLIENT);
        return detail;
    }

    public static ReceiptDetailData loadProviderReceipt(SqlSession sqlSession,
                                                         ProviderReceipt receipt,
                                                         ProviderController providerController) {
        ReceiptDetailData detail = new ReceiptDetailData();
        detail.setType(ReceiptType.PROVIDER);
        detail.setPointOfSale(receipt.getPointOfSale());
        detail.setReceiptNumber(receipt.getReceiptNumber());
        detail.setReceiptDate(receipt.getReceiptDate());
        detail.setTotal(receipt.getTotal());
        detail.setNotes(receipt.getNotes());
        if (receipt.getProvider() != null && receipt.getProvider().getId() != null) {
            Provider provider = providerController.findById(receipt.getProvider().getId());
            if (provider != null) {
                detail.setEntityName(provider.getName());
            }
        }
        populatePaymentDetails(sqlSession, detail, receipt.getId(), ReceiptType.PROVIDER);
        return detail;
    }

    private static void populatePaymentDetails(SqlSession sqlSession,
                                               ReceiptDetailData detail,
                                               Integer receiptId,
                                               ReceiptType type) {
        if (receiptId == null) {
            return;
        }
        List<String> receiptTypes = resolveReceiptTypes(type);
        if (receiptTypes.isEmpty()) {
            return;
        }

        ReceiptCashController cashController = buildCashController(sqlSession);
        List<ReceiptCash> cashPayments = cashController.findByReceipt(receiptId, receiptTypes);
        if (!cashPayments.isEmpty()) {
            detail.setCashPayment(cashPayments.get(0));
        }

        ReceiptCardController cardController = buildCardController(sqlSession);
        List<ReceiptCard> cards = cardController.findByReceipt(receiptId, receiptTypes);
        detail.setCardPayments(cards);

        ReceiptChequeController chequeController = buildChequeController(sqlSession);
        List<ReceiptCheque> cheques = chequeController.findByReceipt(receiptId, receiptTypes);
        detail.setChequePayments(cheques);

        ReceiptTransferController transferController = buildTransferController(sqlSession);
        List<ReceiptTransfer> transfers = transferController.findByReceipt(receiptId, receiptTypes);
        detail.setTransferPayments(transfers);

        ReceiptRetentionController retentionController = buildRetentionController(sqlSession);
        List<ReceiptRetention> retentions = retentionController.findByReceipt(receiptId, receiptTypes);
        detail.setRetentionPayments(retentions);
    }

    private static List<String> resolveReceiptTypes(ReceiptType type) {
        if (type == null) {
            return List.of();
        }
        Set<String> values = new LinkedHashSet<>();
        values.add(type.name());
        switch (type) {
            case CLIENT -> {
                values.add("CLIENTE");
                values.add("cliente");
            }
            case PROVIDER -> {
                values.add("PROVEEDOR");
                values.add("proveedor");
            }
        }
        values.add(type.name().toLowerCase(Locale.ROOT));
        return new ArrayList<>(values);
    }

    private static ReceiptCashController buildCashController(SqlSession sqlSession) {
        ReceiptCashMapper mapper = sqlSession.getMapper(ReceiptCashMapper.class);
        ReceiptCashRepository repository = new ReceiptCashRepositoryImpl(mapper);
        ReceiptCashService service = new ReceiptCashService(repository);
        return new ReceiptCashController(service);
    }

    private static ReceiptCardController buildCardController(SqlSession sqlSession) {
        ReceiptCardMapper mapper = sqlSession.getMapper(ReceiptCardMapper.class);
        ReceiptCardRepository repository = new ReceiptCardRepositoryImpl(mapper);
        ReceiptCardService service = new ReceiptCardService(repository);
        return new ReceiptCardController(service);
    }

    private static ReceiptChequeController buildChequeController(SqlSession sqlSession) {
        ReceiptChequeMapper mapper = sqlSession.getMapper(ReceiptChequeMapper.class);
        ReceiptChequeRepository repository = new ReceiptChequeRepositoryImpl(mapper);
        ReceiptChequeService service = new ReceiptChequeService(repository);
        return new ReceiptChequeController(service);
    }

    private static ReceiptTransferController buildTransferController(SqlSession sqlSession) {
        ReceiptTransferMapper mapper = sqlSession.getMapper(ReceiptTransferMapper.class);
        ReceiptTransferRepository repository = new ReceiptTransferRepositoryImpl(mapper);
        ReceiptTransferService service = new ReceiptTransferService(repository);
        return new ReceiptTransferController(service);
    }

    private static ReceiptRetentionController buildRetentionController(SqlSession sqlSession) {
        ReceiptRetentionMapper mapper = sqlSession.getMapper(ReceiptRetentionMapper.class);
        ReceiptRetentionRepository repository = new ReceiptRetentionRepositoryImpl(mapper);
        ReceiptRetentionService service = new ReceiptRetentionService(repository);
        return new ReceiptRetentionController(service);
    }
}
