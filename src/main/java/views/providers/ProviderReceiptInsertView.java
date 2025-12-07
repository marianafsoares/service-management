package views.providers;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.math.BigDecimal;
import java.math.RoundingMode;
import controllers.ProviderController;
import controllers.ProviderReceiptController;
import controllers.ReceiptCashController;
import controllers.ReceiptCardController;
import controllers.ReceiptChequeController;
import controllers.ReceiptRetentionController;
import controllers.ReceiptTransferController;
import controllers.BankController;
import controllers.CardController;
import configs.MyBatisConfig;
import java.util.Objects;
import mappers.ProviderInvoiceMapper;
import mappers.ProviderMapper;
import mappers.receipts.*;
import mappers.BankMapper;
import mappers.CardMapper;
import models.Provider;
import models.Bank;
import models.receipts.*;
import org.apache.ibatis.session.SqlSession;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import repositories.ReceiptCashRepository;
import repositories.ReceiptCardRepository;
import repositories.ReceiptChequeRepository;
import repositories.ReceiptRetentionRepository;
import repositories.ReceiptTransferRepository;
import repositories.BankRepository;
import repositories.CardRepository;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import repositories.impl.ReceiptCashRepositoryImpl;
import repositories.impl.ReceiptCardRepositoryImpl;
import repositories.impl.ReceiptChequeRepositoryImpl;
import repositories.impl.ReceiptRetentionRepositoryImpl;
import repositories.impl.ReceiptTransferRepositoryImpl;
import repositories.impl.BankRepositoryImpl;
import repositories.impl.CardRepositoryImpl;
import services.ProviderService;
import services.ProviderReceiptService;
import services.ReceiptCashService;
import services.ReceiptCardService;
import services.ReceiptChequeService;
import services.ReceiptRetentionService;
import services.ReceiptTransferService;
import services.BankService;
import services.CardService;
import views.providers.ProviderManagementView;
import views.utils.ComboBoxItem;
import views.utils.ReceiptUtils;
import utils.CuitSelectorUtils;
import java.util.regex.Pattern;

public class ProviderReceiptInsertView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private SqlSession sqlSession;
    private ProviderReceiptController providerReceiptController;
    private ProviderController providerController;
    private ReceiptCashController receiptCashController;
    private ReceiptCardController receiptCardController;
    private ReceiptChequeController receiptChequeController;
    private ReceiptTransferController receiptTransferController;
    private ReceiptRetentionController receiptRetentionController;
    private BankController bankController;
    private CardController cardController;
    private Provider selectedProvider;
    private final SimpleDateFormat tableDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private boolean addingCheque = false;
    private boolean addingCard = false;
    private boolean addingTransfer = false;

    private static final int POINT_OF_SALE_DIGITS = 4;
    private static final int RECEIPT_NUMBER_DIGITS = 8;
    private static final Pattern RECEIPT_INPUT_PATTERN = Pattern.compile(
            "\\d{1," + POINT_OF_SALE_DIGITS + "}-\\d{1," + RECEIPT_NUMBER_DIGITS + "}"
    );
    private static final String RECEIPT_NUMBER_SAMPLE =
            "0".repeat(POINT_OF_SALE_DIGITS) + "-" + "0".repeat(RECEIPT_NUMBER_DIGITS);

    public ProviderReceiptInsertView() throws Exception {
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProviderReceiptMapper receiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);
        ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
        ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
        ReceiptCashMapper cashMapper = sqlSession.getMapper(ReceiptCashMapper.class);
        ReceiptCardMapper cardMapper = sqlSession.getMapper(ReceiptCardMapper.class);
        ReceiptChequeMapper chequeMapper = sqlSession.getMapper(ReceiptChequeMapper.class);
        ReceiptTransferMapper transferMapper = sqlSession.getMapper(ReceiptTransferMapper.class);
        ReceiptRetentionMapper retentionMapper = sqlSession.getMapper(ReceiptRetentionMapper.class);
        ProviderReceiptRepository receiptRepository = new ProviderReceiptRepositoryImpl(receiptMapper);
        ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
        ProviderInvoiceRepository providerInvoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
        ReceiptCashRepository cashRepository = new ReceiptCashRepositoryImpl(cashMapper);
        ReceiptCardRepository cardRepository = new ReceiptCardRepositoryImpl(cardMapper);
        ReceiptChequeRepository chequeRepository = new ReceiptChequeRepositoryImpl(chequeMapper);
        ReceiptTransferRepository transferRepository = new ReceiptTransferRepositoryImpl(transferMapper);
        ReceiptRetentionRepository retentionRepository = new ReceiptRetentionRepositoryImpl(retentionMapper);
        BankMapper bankMapper = sqlSession.getMapper(BankMapper.class);
        BankRepository bankRepository = new BankRepositoryImpl(bankMapper);
        CardMapper cardCatalogMapper = sqlSession.getMapper(CardMapper.class);
        CardRepository cardCatalogRepository = new CardRepositoryImpl(cardCatalogMapper);
        ProviderReceiptService receiptService = new ProviderReceiptService(receiptRepository);
        ProviderService providerService = new ProviderService(providerRepository, providerInvoiceRepository, receiptRepository);
        ReceiptCashService cashService = new ReceiptCashService(cashRepository);
        ReceiptCardService cardService = new ReceiptCardService(cardRepository);
        ReceiptChequeService chequeService = new ReceiptChequeService(chequeRepository);
        ReceiptTransferService transferService = new ReceiptTransferService(transferRepository);
        ReceiptRetentionService retentionService = new ReceiptRetentionService(retentionRepository);
        BankService bankService = new BankService(bankRepository);
        CardService cardCatalogService = new CardService(cardCatalogRepository);
        providerReceiptController = new ProviderReceiptController(receiptService);
        providerController = new ProviderController(providerService);
        receiptCashController = new ReceiptCashController(cashService);
        receiptCardController = new ReceiptCardController(cardService);
        receiptChequeController = new ReceiptChequeController(chequeService);
        receiptTransferController = new ReceiptTransferController(transferService);
        receiptRetentionController = new ReceiptRetentionController(retentionService);
        bankController = new BankController(bankService);
        cardController = new CardController(cardCatalogService);

        initComponents();
        configureChequeTable();
        buttonGroup1.add(jRadioButtonCredit);
        buttonGroup1.add(jRadioButtonDebit);
        jTextFieldChequeAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                attemptAutoAddCheque();
            }
        });
        jTextFieldCardAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                attemptAutoAddCard();
            }
        });
        jTextFieldChequeAmount1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                attemptAutoAddTransfer();
            }
        });
        jTextFieldCashAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyCashAmount(false);
                updateTotals();
            }
        });
        jTextFieldRetentionAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                applyRetentionAmount(false);
            }
        });
        CuitSelectorUtils.populateCuits(jComboBoxInvoiceReceiverCuit);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                isOpen = false;
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isOpen = false;
            }
        });
        isOpen = true;
        loadBanks();
        loadCards();
        
        updateTotals();

    }

    public ProviderReceiptInsertView(Provider provider) throws Exception {
        this();
        this.selectedProvider = provider;
    }

    public void setProvider(Provider provider) {
        this.selectedProvider = provider;
    }

    public void loadBanks() {
        ReceiptUtils.loadBanks(jComboBoxBank, bankController);
        ReceiptUtils.loadBanks(jComboBoxOriginBank, bankController);
        ReceiptUtils.loadBanks(jComboBoxDestinationBank, bankController);
        jComboBoxBank.setSelectedIndex(0);
        jComboBoxOriginBank.setSelectedIndex(0);
        jComboBoxDestinationBank.setSelectedIndex(0);
    }

    public BigDecimal Sumar() {

        BigDecimal efe = parseAmount(jLabelCashTotal.getText());
        BigDecimal che = parseAmount(jLabelChequeTotal.getText());
        BigDecimal tar = parseAmount(jLabelCardTotal.getText());
        BigDecimal ret = parseAmount(jLabelRetentionTotal.getText());
        BigDecimal tra = parseAmount(jLabelTransferTotal.getText());

        return efe.add(tar).add(che).add(ret).add(tra);

    }

    public static void SumarCheques() {

        BigDecimal total = BigDecimal.ZERO;

        int cantFilas = jTableCheques.getRowCount();

        for (int j = 0; j < cantFilas; j++) {
            Object value = jTableCheques.getValueAt(j, 4);
            if (value != null) {
                try {
                    total = total.add(new BigDecimal(value.toString().trim().replace(',', '.')));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        jLabelChequeTotal.setText(total.setScale(2, RoundingMode.HALF_EVEN).toString());

    }

    public void limpiarFormularioCheque() {
        jTextFieldChequeAmount.setText("");
        jTextFieldChequeTitular.setText("");
        jTextFieldChequeNumber.setText("");
        jComboBoxBank.setSelectedIndex(0);
        jDateChooserChequeDueDate.setDate(null);
    }

    public void limpiarFormularioEfectivo() {
        jTextFieldCashAmount.setText("");
        jTextFieldCashBack.setText("");

    }

    public void limpiarFormularioRetenciones() {
        jTextFieldRetentionAmount.setText("");

    }

    public void limpiarFormularioTarjeta() {
        jTextFieldCardAmount.setText("");
        jTextFieldCardNumber.setText("");
        jComboBoxCard.setSelectedIndex(0);
        buttonGroup1.clearSelection();

    }

    public void limpiarFormularioTransferencia() {
        jTextFieldOriginAccount.setText("");
        jComboBoxOriginBank.setSelectedIndex(0);
        jTextFieldDestinationAccount.setText("");
        jComboBoxDestinationBank.setSelectedIndex(0);
        jTextFieldReference.setText("");
        jTextFieldChequeAmount1.setText("");
    }

    public void limpiarFormularioPagos() {

        String zero = formatAmount(BigDecimal.ZERO);
        jLabelChequeTotal.setText(zero);
        jLabelCardTotal.setText(zero);
        jLabelCashTotal.setText(zero);
        jLabelRetentionTotal.setText(zero);
        jLabelTransferTotal.setText(zero);
        jLabelTotal.setText(zero);

    }

    public void loadCards() {
        ReceiptUtils.loadCards(jComboBoxCard, cardController);
    }


    private String getSelectedReceiverCuit() {
        return CuitSelectorUtils.getSelectedCuit(jComboBoxInvoiceReceiverCuit);
    }

    private ReceiptCode parseReceiptCode(String text) {
        if (text == null) {
            return null;
        }
        String normalized = text.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        normalized = normalized.replaceAll("\\s+", "");
        if (!RECEIPT_INPUT_PATTERN.matcher(normalized).matches()) {
            return null;
        }
        String[] parts = normalized.split("-", 2);
        if (parts.length != 2) {
            return null;
        }
        try {
            String pointOfSale = String.format("%0" + POINT_OF_SALE_DIGITS + "d", Integer.parseInt(parts[0]));
            String number = String.format("%0" + RECEIPT_NUMBER_DIGITS + "d", Long.parseLong(parts[1]));
            return new ReceiptCode(pointOfSale, number);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void showInvalidReceiptNumberMessage() {
        JOptionPane.showMessageDialog(this,
                "Debe ingresar el número de recibo con el formato " + RECEIPT_NUMBER_SAMPLE
                        + ". Se completará con ceros a la izquierda si ingresa menos dígitos.",
                "Comprobante", JOptionPane.WARNING_MESSAGE);
    }

    private String getLastReceiptNumber(String pointOfSale) {
        Integer providerId = selectedProvider != null ? selectedProvider.getId() : null;
        int last = providerReceiptController.findAll().stream()
                .filter(r -> pointOfSale.equals(r.getPointOfSale()))
                .filter(r -> providerId == null
                        || (r.getProvider() != null && r.getProvider().getId() != null
                        && providerId.equals(r.getProvider().getId())))
                .mapToInt(r -> {
                    try {
                        return Integer.parseInt(r.getReceiptNumber());
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .max()
                .orElse(0) + 1;
        return String.format("%08d", last);
    }

    private BigDecimal parseAmount(String text) {
        if (text == null) {
            return BigDecimal.ZERO;
        }
        String normalized = text.trim().replace(',', '.');
        if (normalized.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        return parseAmount(value.toString());
    }

    private String toNullableString(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    @SuppressWarnings("unchecked")
    private ComboBoxItem<Integer> toIntegerComboItem(Object value) {
        if (value instanceof ComboBoxItem<?> item) {
            Object raw = item.getValue();
            if (raw instanceof Integer) {
                return (ComboBoxItem<Integer>) item;
            }
        }
        return null;
    }

    private ComboBoxItem<Integer> findComboItemByLabel(javax.swing.JComboBox<?> combo, String label) {
        if (label == null) {
            return null;
        }
        String normalized = label.trim();
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object element = combo.getItemAt(i);
            if (element instanceof ComboBoxItem<?> item) {
                if (normalized.equalsIgnoreCase(item.getLabel())) {
                    return toIntegerComboItem(item);
                }
            }
        }
        return null;
    }

    private ComboBoxItem<Integer> findComboItemByValue(javax.swing.JComboBox<?> combo, Integer value) {
        if (value == null) {
            return null;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            ComboBoxItem<Integer> item = toIntegerComboItem(combo.getItemAt(i));
            if (item != null && value.equals(item.getValue())) {
                return item;
            }
        }
        return null;
    }

    private ComboBoxItem<Integer> getSelectedComboItem(javax.swing.JComboBox<?> combo) {
        return toIntegerComboItem(combo.getSelectedItem());
    }

    private String getComboLabel(Object value) {
        if (value instanceof ComboBoxItem<?> item) {
            return item.getLabel();
        }
        return value != null ? value.toString() : "";
    }

    private boolean applyCashAmount(boolean showWarnings) {
        String amountText = jTextFieldCashAmount.getText().trim();
        if (amountText.isEmpty()) {
            if (showWarnings) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el monto abonado", "Efectivo", JOptionPane.WARNING_MESSAGE);
                jTextFieldCashAmount.requestFocus();
                return false;
            }
            jLabelCashTotal.setText(formatAmount(BigDecimal.ZERO));
            jTextFieldCashBack.setText(formatAmount(BigDecimal.ZERO));
            return true;
        }
        BigDecimal pagaCon;
        try {
            pagaCon = new BigDecimal(amountText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            if (showWarnings) {
                JOptionPane.showMessageDialog(this, "Monto inválido", "Efectivo", JOptionPane.WARNING_MESSAGE);
                jTextFieldCashAmount.requestFocus();
            }
            jLabelCashTotal.setText(formatAmount(BigDecimal.ZERO));
            jTextFieldCashBack.setText(formatAmount(BigDecimal.ZERO));
            return false;
        }

        BigDecimal debe = parseAmount(jLabelDebe.getText());
        BigDecimal vuelto = BigDecimal.ZERO;
        if (pagaCon.compareTo(debe) >= 0) {
            vuelto = pagaCon.subtract(debe).setScale(2, RoundingMode.HALF_EVEN);
        }
        jTextFieldCashBack.setText(formatAmount(vuelto));
        jLabelCashTotal.setText(formatAmount(pagaCon));
        return true;
    }

    private boolean applyRetentionAmount(boolean showWarnings) {
        String amountText = jTextFieldRetentionAmount.getText().trim();
        if (amountText.isEmpty()) {
            if (showWarnings) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el monto de la retención", "Retenciones", JOptionPane.WARNING_MESSAGE);
                jTextFieldRetentionAmount.requestFocus();
                return false;
            }
            jLabelRetentionTotal.setText(formatAmount(BigDecimal.ZERO));
            updateTotals();
            return true;
        }
        BigDecimal retencion;
        try {
            retencion = new BigDecimal(amountText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            if (showWarnings) {
                JOptionPane.showMessageDialog(this, "Monto de retención inválido", "Retenciones", JOptionPane.WARNING_MESSAGE);
                jTextFieldRetentionAmount.requestFocus();
            }
            jLabelRetentionTotal.setText(formatAmount(BigDecimal.ZERO));
            updateTotals();
            return false;
        }

        jLabelRetentionTotal.setText(formatAmount(retencion));
        updateTotals();
        return true;
    }

    private boolean isChequeFormComplete() {
        ComboBoxItem<Integer> bankItem = getSelectedComboItem(jComboBoxBank);
        return !jTextFieldChequeNumber.getText().trim().isEmpty()
                && bankItem != null
                && bankItem.getValue() != null
                && !jTextFieldChequeTitular.getText().trim().isEmpty()
                && jDateChooserChequeDueDate.getDate() != null
                && !jTextFieldChequeAmount.getText().trim().isEmpty();
    }

    private boolean isCardFormComplete() {
        ComboBoxItem<Integer> cardItem = getSelectedComboItem(jComboBoxCard);
        return (jRadioButtonCredit.isSelected() || jRadioButtonDebit.isSelected())
                && cardItem != null
                && cardItem.getValue() != null
                && !jTextFieldCardNumber.getText().trim().isEmpty()
                && !jTextFieldCardAmount.getText().trim().isEmpty();
    }

    private boolean isTransferFormComplete() {
        return !jTextFieldChequeAmount1.getText().trim().isEmpty();
    }

    private void attemptAutoAddCheque() {
        if (isChequeFormComplete() && addChequeToTable()) {
            limpiarFormularioCheque();
            HabilitarFormularioCheque();
        }
    }

    private void attemptAutoAddCard() {
        if (isCardFormComplete() && addCardToTable()) {
            limpiarFormularioTarjeta();
        }
    }

    private void attemptAutoAddTransfer() {
        if (isTransferFormComplete() && addTransferToTable()) {
            limpiarFormularioTransferencia();
        }
    }

    private boolean confirmReceiptSave() {
        boolean cashOk = applyCashAmount(false);
        boolean retentionOk = applyRetentionAmount(false);
        if (!cashOk || !retentionOk) {
            updateTotals();
        }
        BigDecimal total = parseAmount(jLabelTotal.getText());
        String message = buildReceiptConfirmationMessage(total);
        int option = JOptionPane.showConfirmDialog(this, message,
                "Confirmar recibo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return option == JOptionPane.YES_OPTION;
    }

    private String buildReceiptConfirmationMessage(BigDecimal total) {
        StringBuilder sb = new StringBuilder();
        sb.append("Está generando un recibo por $")
                .append(formatAmount(total))
                .append('.');
        String detail = buildReceiptDetailText();
        if (!detail.isBlank()) {
            sb.append("\n\nDetalle:\n").append(detail);
        }
        sb.append("\n\n¿Desea continuar?");
        return sb.toString();
    }

    private String buildReceiptDetailText() {
        StringBuilder detail = new StringBuilder();
        appendSummaryLine(detail, "Efectivo", parseAmount(jLabelCashTotal.getText()));
        appendCardDetails(detail);
        appendChequeDetails(detail);
        appendTransferDetails(detail);
        appendSummaryLine(detail, "Retenciones", parseAmount(jLabelRetentionTotal.getText()));
        return detail.toString();
    }

    private void appendSummaryLine(StringBuilder detail, String label, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (detail.length() > 0) {
            detail.append('\n');
        }
        detail.append("• ").append(label).append(": $").append(formatAmount(amount));
    }

    private void appendCardDetails(StringBuilder detail) {
        BigDecimal total = parseAmount(jLabelCardTotal.getText());
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (detail.length() > 0) {
            detail.append('\n');
        }
        detail.append("• Tarjetas: $").append(formatAmount(total));
        for (int i = 0; i < jTableCards.getRowCount(); i++) {
            Object typeValue = jTableCards.getValueAt(i, 0);
            String type = typeValue != null ? typeValue.toString() : "";
            Object cardValue = jTableCards.getValueAt(i, 1);
            String cardLabel = getComboLabel(cardValue);
            Object numberValue = jTableCards.getValueAt(i, 2);
            String number = numberValue != null ? numberValue.toString() : "";
            Object amountValue = jTableCards.getValueAt(i, 3);
            detail.append("\n   - ")
                    .append(type)
                    .append(" - ")
                    .append(cardLabel)
                    .append(" (****")
                    .append(number.length() > 4 ? number.substring(number.length() - 4) : number)
                    .append(") : $")
                    .append(formatAmount(toBigDecimal(amountValue)));
        }
    }

    private void appendChequeDetails(StringBuilder detail) {
        BigDecimal total = parseAmount(jLabelChequeTotal.getText());
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (detail.length() > 0) {
            detail.append('\n');
        }
        detail.append("• Cheques: $").append(formatAmount(total));
        for (int i = 0; i < jTableCheques.getRowCount(); i++) {
            Object numberValue = jTableCheques.getValueAt(i, 0);
            String number = numberValue != null ? numberValue.toString() : "";
            Object holderValue = jTableCheques.getValueAt(i, 1);
            String holder = holderValue != null ? holderValue.toString() : "";
            Object bankValue = jTableCheques.getValueAt(i, 2);
            String bankLabel = getComboLabel(bankValue);
            Object dateObj = jTableCheques.getValueAt(i, 3);
            String dueDate = "";
            if (dateObj instanceof Date date) {
                dueDate = tableDateFormat.format(date);
            }
            Object amountValue = jTableCheques.getValueAt(i, 4);
            detail.append("\n   - N° ")
                    .append(number);
            if (!holder.isBlank()) {
                detail.append(" - Titular: ").append(holder);
            }
            detail.append(" - Banco: ")
                    .append(bankLabel);
            if (!dueDate.isEmpty()) {
                detail.append(" - Vto: ").append(dueDate);
            }
            detail.append(" : $")
                    .append(formatAmount(toBigDecimal(amountValue)));
        }
    }

    private void appendTransferDetails(StringBuilder detail) {
        BigDecimal total = parseAmount(jLabelTransferTotal.getText());
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (detail.length() > 0) {
            detail.append('\n');
        }
        detail.append("• Transferencias: $").append(formatAmount(total));
        for (int i = 0; i < jTableTransferencias.getRowCount(); i++) {
            String originBank = getComboLabel(jTableTransferencias.getValueAt(i, 0));
            Object originAccountValue = jTableTransferencias.getValueAt(i, 1);
            String originAccount = originAccountValue != null ? originAccountValue.toString() : "";
            String destinationBank = getComboLabel(jTableTransferencias.getValueAt(i, 2));
            Object destinationAccountValue = jTableTransferencias.getValueAt(i, 3);
            String destinationAccount = destinationAccountValue != null ? destinationAccountValue.toString() : "";
            Object referenceValue = jTableTransferencias.getValueAt(i, 4);
            String reference = referenceValue != null ? referenceValue.toString() : "";
            Object amountValue = jTableTransferencias.getValueAt(i, 5);
            detail.append("\n   - ")
                    .append(originBank)
                    .append(" (").append(originAccount).append(") → ")
                    .append(destinationBank)
                    .append(" (").append(destinationAccount).append(")");
            if (!reference.isBlank()) {
                detail.append(" Ref: ").append(reference);
            }
            detail.append(" : $")
                    .append(formatAmount(toBigDecimal(amountValue)));
        }
    }

    private void updateTotals() {
        BigDecimal total = Sumar();
        jLabelTotal.setText(formatAmount(total));
    }

    private DefaultTableModel crearModeloCheques(String numero, String banco, float monto, Date fecha) throws Exception {

        DefaultTableModel tm = new DefaultTableModel(new String[]{"Numero", "Banco", "Vencimiento", "Monto"}, 0) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        Vector fila = null;
        fila = new Vector();

        fila.add(numero);
        fila.add(banco);
        fila.add(fecha);
        fila.add(monto);
        

        tm.addRow(fila);

        return (tm);
    }

    private DefaultTableModel crearModeloTarjeta(String tipo, String tarjeta, String numero, float monto) throws Exception {

        DefaultTableModel tm = new DefaultTableModel(new String[]{"Tipo", "Tarjeta", "Numero", "Monto"}, 0) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        Vector fila = null;
        fila = new Vector();

        fila.add(tipo);
        fila.add(tarjeta);
        fila.add(numero);
        fila.add(monto);

        tm.addRow(fila);

        return (tm);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldCashAmount = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldCashBack = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldCardAmount = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldCardNumber = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jComboBoxCard = new javax.swing.JComboBox();
        jRadioButtonCredit = new javax.swing.JRadioButton();
        jRadioButtonDebit = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCards = new javax.swing.JTable();
        jButtonRemoveCard = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldChequeAmount = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldChequeNumber = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jComboBoxBank = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jDateChooserChequeDueDate = new com.toedter.calendar.JDateChooser();
        jButtonRemoveCheque = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldChequeTitular = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCheques = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldChequeAmount1 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldOriginAccount = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jComboBoxOriginBank = new javax.swing.JComboBox();
        jButtonRemoveCheque1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableTransferencias = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jTextFieldDestinationAccount = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jComboBoxDestinationBank = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jTextFieldReference = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldRetentionAmount = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaRetentionDetail = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jButtonConfirmReceipt = new javax.swing.JButton();
        jButtonCancelReceipt = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jLabelDebe = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldPointOfSaleAndNumberReceipt = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jComboBoxInvoiceReceiverCuit = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabelCashTotal = new javax.swing.JLabel();
        jLabelCardTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelChequeTotal = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabelRetentionTotal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabelTransferTotal = new javax.swing.JLabel();

        setTitle("Pago a proveedores");
        setPreferredSize(new java.awt.Dimension(1000, 600));
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Efectivo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel5.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setText("Abona con:");
        jPanel5.add(jLabel7);
        jLabel7.setBounds(30, 70, 90, 25);

        jTextFieldCashAmount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldCashAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCashAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCashAmountKeyPressed(evt);
            }
        });
        jPanel5.add(jTextFieldCashAmount);
        jTextFieldCashAmount.setBounds(30, 100, 180, 25);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setText("Su vuelto:");
        jPanel5.add(jLabel8);
        jLabel8.setBounds(30, 170, 90, 25);

        jTextFieldCashBack.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldCashBack.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel5.add(jTextFieldCashBack);
        jTextFieldCashBack.setBounds(30, 200, 180, 25);

        jPanel1.add(jPanel5);
        jPanel5.setBounds(20, 40, 260, 280);

        jTabbedPane1.addTab("Efectivo", jPanel1);

        jPanel3.setLayout(null);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tarjeta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel7.setLayout(null);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setText("Monto:");
        jPanel7.add(jLabel13);
        jLabel13.setBounds(30, 190, 90, 25);

        jTextFieldCardAmount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldCardAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCardAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCardAmountKeyPressed(evt);
            }
        });
        jPanel7.add(jTextFieldCardAmount);
        jTextFieldCardAmount.setBounds(30, 220, 180, 25);

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel14.setText("Numero de tarjeta:");
        jPanel7.add(jLabel14);
        jLabel14.setBounds(30, 130, 160, 25);

        jTextFieldCardNumber.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldCardNumber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel7.add(jTextFieldCardNumber);
        jTextFieldCardNumber.setBounds(30, 160, 180, 25);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel15.setText("Tarjeta:");
        jPanel7.add(jLabel15);
        jLabel15.setBounds(30, 60, 160, 25);

        jComboBoxCard.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jPanel7.add(jComboBoxCard);
        jComboBoxCard.setBounds(30, 90, 330, 25);

        jRadioButtonCredit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonCredit.setText("Credito");
        jPanel7.add(jRadioButtonCredit);
        jRadioButtonCredit.setBounds(30, 30, 110, 25);

        jRadioButtonDebit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonDebit.setText("Debito");
        jPanel7.add(jRadioButtonDebit);
        jRadioButtonDebit.setBounds(140, 30, 110, 25);

        jTableCards.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipo", "Tarjeta", "Numero", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableCards);

        jPanel7.add(jScrollPane2);
        jScrollPane2.setBounds(10, 290, 450, 80);

        jButtonRemoveCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonRemoveCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveCardActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonRemoveCard);
        jButtonRemoveCard.setBounds(20, 260, 25, 25);

        jPanel3.add(jPanel7);
        jPanel7.setBounds(20, 20, 480, 390);

        jTabbedPane1.addTab("Tarjeta", jPanel3);

        jPanel2.setLayout(null);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cheque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel6.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setText("Monto:");
        jPanel6.add(jLabel9);
        jLabel9.setBounds(30, 230, 90, 20);

        jTextFieldChequeAmount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldChequeAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldChequeAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldChequeAmountKeyPressed(evt);
            }
        });
        jPanel6.add(jTextFieldChequeAmount);
        jTextFieldChequeAmount.setBounds(30, 250, 180, 25);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setText("Banco:");
        jPanel6.add(jLabel10);
        jLabel10.setBounds(30, 130, 160, 20);

        jTextFieldChequeNumber.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldChequeNumber.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldChequeNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldChequeNumberKeyPressed(evt);
            }
        });
        jPanel6.add(jTextFieldChequeNumber);
        jTextFieldChequeNumber.setBounds(30, 50, 180, 25);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setText("Numero de cheque:");
        jPanel6.add(jLabel11);
        jLabel11.setBounds(30, 30, 160, 20);

        jComboBoxBank.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jComboBoxBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxBankActionPerformed(evt);
            }
        });
        jPanel6.add(jComboBoxBank);
        jComboBoxBank.setBounds(30, 150, 230, 25);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setText("Fecha de vencimiento:");
        jPanel6.add(jLabel12);
        jLabel12.setBounds(30, 180, 160, 20);
        jPanel6.add(jDateChooserChequeDueDate);
        jDateChooserChequeDueDate.setBounds(30, 200, 180, 25);

        jButtonRemoveCheque.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonRemoveCheque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonRemoveCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveChequeActionPerformed(evt);
            }
        });
        jPanel6.add(jButtonRemoveCheque);
        jButtonRemoveCheque.setBounds(10, 290, 25, 25);

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel16.setText("Titular:");
        jPanel6.add(jLabel16);
        jLabel16.setBounds(30, 80, 160, 20);

        jTextFieldChequeTitular.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldChequeTitular.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel6.add(jTextFieldChequeTitular);
        jTextFieldChequeTitular.setBounds(30, 100, 180, 25);

        jTableCheques.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Titular", "Banco", "Vencimiento", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableCheques);

        jPanel6.add(jScrollPane1);
        jScrollPane1.setBounds(10, 320, 470, 110);

        jPanel2.add(jPanel6);
        jPanel6.setBounds(10, 10, 500, 440);

        jTabbedPane1.addTab("Cheque", jPanel2);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transferencia", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel10.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel17.setText("Monto:");
        jPanel10.add(jLabel17);
        jLabel17.setBounds(30, 210, 90, 20);

        jTextFieldChequeAmount1.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldChequeAmount1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldChequeAmount1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldChequeAmount1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldChequeAmount1KeyReleased(evt);
            }
        });
        jPanel10.add(jTextFieldChequeAmount1);
        jTextFieldChequeAmount1.setBounds(30, 230, 180, 25);

        jLabel23.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel23.setText("Banco cuenta origen:");
        jPanel10.add(jLabel23);
        jLabel23.setBounds(30, 90, 160, 20);

        jTextFieldOriginAccount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldOriginAccount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldOriginAccount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldOriginAccountKeyPressed(evt);
            }
        });
        jPanel10.add(jTextFieldOriginAccount);
        jTextFieldOriginAccount.setBounds(30, 50, 180, 25);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel24.setText("Cuenta origen:");
        jPanel10.add(jLabel24);
        jLabel24.setBounds(30, 30, 160, 20);

        jComboBoxOriginBank.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jComboBoxOriginBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOriginBankActionPerformed(evt);
            }
        });
        jPanel10.add(jComboBoxOriginBank);
        jComboBoxOriginBank.setBounds(30, 110, 170, 25);

        jButtonRemoveCheque1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonRemoveCheque1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonRemoveCheque1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveCheque1ActionPerformed(evt);
            }
        });
        jPanel10.add(jButtonRemoveCheque1);
        jButtonRemoveCheque1.setBounds(20, 280, 25, 25);

        jTableTransferencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bco Origen", "Cta Origen", "Bco Destino", "Cta Destino", "Referencia", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableTransferencias);

        jPanel10.add(jScrollPane4);
        jScrollPane4.setBounds(10, 320, 480, 110);

        jLabel26.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel26.setText("Cuenta destino:");
        jPanel10.add(jLabel26);
        jLabel26.setBounds(240, 30, 160, 20);

        jTextFieldDestinationAccount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldDestinationAccount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel10.add(jTextFieldDestinationAccount);
        jTextFieldDestinationAccount.setBounds(240, 50, 180, 25);

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel27.setText("Banco cuenta destino:");
        jPanel10.add(jLabel27);
        jLabel27.setBounds(240, 90, 160, 20);

        jComboBoxDestinationBank.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jComboBoxDestinationBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDestinationBankActionPerformed(evt);
            }
        });
        jPanel10.add(jComboBoxDestinationBank);
        jComboBoxDestinationBank.setBounds(240, 110, 170, 25);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel25.setText("Referencia:");
        jPanel10.add(jLabel25);
        jLabel25.setBounds(30, 150, 90, 20);

        jTextFieldReference.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldReference.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldReference.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldReferenceKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldReferenceKeyReleased(evt);
            }
        });
        jPanel10.add(jTextFieldReference);
        jTextFieldReference.setBounds(30, 170, 180, 25);

        jTabbedPane1.addTab("Transferencias", jPanel10);

        jPanel4.setLayout(null);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Retenciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel8.setLayout(null);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel19.setText("Monto");
        jPanel8.add(jLabel19);
        jLabel19.setBounds(30, 70, 90, 25);

        jTextFieldRetentionAmount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldRetentionAmount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRetentionAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldRetentionAmountKeyPressed(evt);
            }
        });
        jPanel8.add(jTextFieldRetentionAmount);
        jTextFieldRetentionAmount.setBounds(30, 100, 180, 25);

        jLabel28.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel28.setText("Detalle:");
        jPanel8.add(jLabel28);
        jLabel28.setBounds(30, 150, 90, 25);

        jTextAreaRetentionDetail.setColumns(20);
        jTextAreaRetentionDetail.setRows(5);
        jScrollPane5.setViewportView(jTextAreaRetentionDetail);

        jPanel8.add(jScrollPane5);
        jScrollPane5.setBounds(30, 180, 270, 86);

        jPanel4.add(jPanel8);
        jPanel8.setBounds(30, 50, 360, 310);

        jTabbedPane1.addTab("Retenciones", jPanel4);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 10, 520, 500);

        jLabel6.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel6.setText("Nro Recibo");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(580, 100, 80, 20);

        jButtonConfirmReceipt.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonConfirmReceipt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonConfirmReceipt.setText("Confirmar");
        jButtonConfirmReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmReceiptActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonConfirmReceipt);
        jButtonConfirmReceipt.setBounds(740, 190, 150, 30);

        jButtonCancelReceipt.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancelReceipt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancelReceipt.setText("Cancelar");
        jButtonCancelReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelReceiptActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancelReceipt);
        jButtonCancelReceipt.setBounds(740, 250, 150, 30);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(740, 310, 150, 30);

        jLabelDebe.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabelDebe.setForeground(new java.awt.Color(255, 0, 0));
        getContentPane().add(jLabelDebe);
        jLabelDebe.setBounds(670, 50, 150, 30);

        jLabel18.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabel18.setText("Total");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(580, 50, 80, 30);

        jTextFieldPointOfSaleAndNumberReceipt.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        getContentPane().add(jTextFieldPointOfSaleAndNumberReceipt);
        jTextFieldPointOfSaleAndNumberReceipt.setBounds(670, 100, 140, 26);

        jLabel22.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Cuit recibidor");
        getContentPane().add(jLabel22);
        jLabel22.setBounds(530, 140, 125, 20);

        jComboBoxInvoiceReceiverCuit.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxInvoiceReceiverCuit);
        jComboBoxInvoiceReceiverCuit.setBounds(670, 140, 180, 21);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setText("Efectivo:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(550, 190, 75, 20);

        jLabelCashTotal.setFont(new java.awt.Font("Calibri", 2, 16)); // NOI18N
        jLabelCashTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCashTotal.setText("0.0");
        getContentPane().add(jLabelCashTotal);
        jLabelCashTotal.setBounds(630, 190, 70, 20);

        jLabelCardTotal.setFont(new java.awt.Font("Calibri", 2, 16)); // NOI18N
        jLabelCardTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCardTotal.setText("0.0");
        getContentPane().add(jLabelCardTotal);
        jLabelCardTotal.setBounds(630, 220, 70, 20);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setText("Tarjeta:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(550, 220, 75, 20);

        jLabelChequeTotal.setFont(new java.awt.Font("Calibri", 2, 16)); // NOI18N
        jLabelChequeTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelChequeTotal.setText("0.0");
        getContentPane().add(jLabelChequeTotal);
        jLabelChequeTotal.setBounds(630, 250, 70, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setText("Cheque:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(550, 250, 75, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel20.setText("Retenciones:");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(550, 280, 100, 20);

        jLabelRetentionTotal.setFont(new java.awt.Font("Calibri", 2, 16)); // NOI18N
        jLabelRetentionTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRetentionTotal.setText("0.0");
        getContentPane().add(jLabelRetentionTotal);
        jLabelRetentionTotal.setBounds(630, 280, 70, 20);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel1.setText("Total");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(550, 350, 50, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 16)); // NOI18N
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotal.setText("0.0");
        getContentPane().add(jLabelTotal);
        jLabelTotal.setBounds(630, 350, 70, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setText("Transferencias:");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(550, 310, 120, 20);

        jLabelTransferTotal.setFont(new java.awt.Font("Calibri", 2, 16)); // NOI18N
        jLabelTransferTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTransferTotal.setText("0.0");
        getContentPane().add(jLabelTransferTotal);
        jLabelTransferTotal.setBounds(630, 310, 70, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxBankActionPerformed
    }//GEN-LAST:event_jComboBoxBankActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed


    private void jTextFieldCashAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCashAmountKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (applyCashAmount(true)) {
                updateTotals();
            }
        }
    }//GEN-LAST:event_jTextFieldCashAmountKeyPressed

    private void jButtonRemoveChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveChequeActionPerformed

        int row = jTableCheques.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cheque", "Cheque", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount = BigDecimal.ZERO;
        Object value = jTableCheques.getValueAt(row, 4);
        if (value != null) {
            try {
                amount = new BigDecimal(value.toString().trim().replace(',', '.'));
            } catch (NumberFormatException ignored) {
            }
        }

        DefaultTableModel tm = (DefaultTableModel) jTableCheques.getModel();
        tm.removeRow(row);
        jTableCheques.setModel(tm);

        BigDecimal cheques = parseAmount(jLabelChequeTotal.getText());

        jLabelChequeTotal.setText(formatAmount(cheques.subtract(amount)));
        updateTotals();


}//GEN-LAST:event_jButtonRemoveChequeActionPerformed

    private void jButtonCancelReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelReceiptActionPerformed

        int k = JOptionPane.showConfirmDialog(this, "¿Esta seguro que desea cancelar el pago?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (k != 1) {
            limpiarFormularioCheque();
            limpiarFormularioEfectivo();
            limpiarFormularioTarjeta();
            limpiarFormularioPagos();
            limpiarFormularioRetenciones();
            limpiarFormularioTransferencia();
            updateTotals();

        }
    }//GEN-LAST:event_jButtonCancelReceiptActionPerformed

    private void jTextFieldCardAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCardAmountKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            if (addCardToTable()) {
                limpiarFormularioTarjeta();
            }

        }

    }//GEN-LAST:event_jTextFieldCardAmountKeyPressed

    private void jButtonRemoveCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveCardActionPerformed

        int row = jTableCards.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una tarjeta", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount = BigDecimal.ZERO;
        Object value = jTableCards.getValueAt(row, 3);
        if (value != null) {
            try {
                amount = new BigDecimal(value.toString().trim().replace(',', '.'));
            } catch (NumberFormatException ignored) {
            }
        }

        DefaultTableModel tm = (DefaultTableModel) jTableCards.getModel();
        tm.removeRow(row);
        jTableCards.setModel(tm);

        BigDecimal cards = parseAmount(jLabelCardTotal.getText());

        jLabelCardTotal.setText(formatAmount(cards.subtract(amount)));
        updateTotals();

    }//GEN-LAST:event_jButtonRemoveCardActionPerformed

    private boolean addChequeToTable() {
        if (addingCheque) {
            return false;
        }
        String number = jTextFieldChequeNumber.getText().trim();
        String normalizedNumber = number.replaceAll("\\s+", "");
        if (!normalizedNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El número de cheque solo puede contener dígitos", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeNumber.requestFocus();
            return false;
        }
        if (normalizedNumber.length() < 6) {
            JOptionPane.showMessageDialog(this, "El número de cheque debe tener al menos 6 dígitos", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeNumber.requestFocus();
            return false;
        }
        number = normalizedNumber;
        jTextFieldChequeNumber.setText(number);
        if (number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el número de cheque", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeNumber.requestFocus();
            return false;
        }

        ComboBoxItem<Integer> bankItem = getSelectedComboItem(jComboBoxBank);
        if (bankItem == null || bankItem.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un banco", "Cheque", JOptionPane.WARNING_MESSAGE);
            jComboBoxBank.requestFocus();
            return false;
        }

        String holder = jTextFieldChequeTitular.getText().trim();
        if (holder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el titular del cheque", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeTitular.requestFocus();
            return false;
        }

        Date dueDate = jDateChooserChequeDueDate.getDate();
        if (dueDate == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar la fecha de vencimiento", "Cheque", JOptionPane.WARNING_MESSAGE);
            jDateChooserChequeDueDate.requestFocus();
            return false;
        }

        String amountText = jTextFieldChequeAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto del cheque", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeAmount.requestFocus();
            return false;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto de cheque inválido", "Cheque", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeAmount.requestFocus();
            return false;
        }

        addingCheque = true;
        try {
            DefaultTableModel tm = (DefaultTableModel) jTableCheques.getModel();
            Vector<Object> fila = new Vector<>();
            fila.add(number);
            fila.add(holder);
            fila.add(bankItem);
            fila.add(dueDate);
            fila.add(amount);
            tm.addRow(fila);

            BigDecimal currentCheques = parseAmount(jLabelChequeTotal.getText());
            jLabelChequeTotal.setText(formatAmount(currentCheques.add(amount)));
            updateTotals();
            return true;
        } finally {
            addingCheque = false;
        }
    }

    private boolean addCardToTable() {
        if (addingCard) {
            return false;
        }
        if (!jRadioButtonCredit.isSelected() && !jRadioButtonDebit.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el tipo de tarjeta", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jRadioButtonCredit.requestFocus();
            return false;
        }

        ComboBoxItem<Integer> cardItem = getSelectedComboItem(jComboBoxCard);
        if (cardItem == null || cardItem.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una tarjeta", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jComboBoxCard.requestFocus();
            return false;
        }

        String number = jTextFieldCardNumber.getText().trim();
        String normalizedNumber = number.replaceAll("\\s+", "");
        if (!normalizedNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "El número de tarjeta solo puede contener dígitos", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jTextFieldCardNumber.requestFocus();
            return false;
        }
        if (normalizedNumber.length() < 12) {
            JOptionPane.showMessageDialog(this, "El número de tarjeta debe tener al menos 12 dígitos", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jTextFieldCardNumber.requestFocus();
            return false;
        }
        number = normalizedNumber;
        jTextFieldCardNumber.setText(number);
        if (number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el número de tarjeta", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jTextFieldCardNumber.requestFocus();
            return false;
        }

        String amountText = jTextFieldCardAmount.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jTextFieldCardAmount.requestFocus();
            return false;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto de tarjeta inválido", "Tarjeta", JOptionPane.WARNING_MESSAGE);
            jTextFieldCardAmount.requestFocus();
            return false;
        }

        String type = jRadioButtonCredit.isSelected() ? "Credito" : "Debito";

        addingCard = true;
        try {
            DefaultTableModel tm = (DefaultTableModel) jTableCards.getModel();
            Vector<Object> fila = new Vector<>();
            fila.add(type);
            fila.add(cardItem);
            fila.add(number);
            fila.add(amount);
            tm.addRow(fila);

            BigDecimal currentCards = parseAmount(jLabelCardTotal.getText());
            jLabelCardTotal.setText(formatAmount(currentCards.add(amount)));
            updateTotals();
            return true;
        } finally {
            addingCard = false;
        }
    }

    private boolean addTransferToTable() {
        if (addingTransfer) {
            return false;
        }
        String amountText = jTextFieldChequeAmount1.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto", "Transferencia", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeAmount1.requestFocus();
            return false;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto de transferencia inválido", "Transferencia", JOptionPane.WARNING_MESSAGE);
            jTextFieldChequeAmount1.requestFocus();
            return false;
        }

        ComboBoxItem<Integer> originBankItem = getSelectedComboItem(jComboBoxOriginBank);
        ComboBoxItem<Integer> destinationBankItem = getSelectedComboItem(jComboBoxDestinationBank);
        String originAccount = toNullableString(jTextFieldOriginAccount.getText());
        String destinationAccount = toNullableString(jTextFieldDestinationAccount.getText());
        String reference = toNullableString(jTextFieldReference.getText());

        addingTransfer = true;
        try {
            DefaultTableModel tm = (DefaultTableModel) jTableTransferencias.getModel();
            Vector<Object> fila = new Vector<>();
            fila.add(originBankItem);
            fila.add(originAccount);
            fila.add(destinationBankItem);
            fila.add(destinationAccount);
            fila.add(reference);
            fila.add(amount);
            tm.addRow(fila);

            BigDecimal currentTransfers = parseAmount(jLabelTransferTotal.getText());
            jLabelTransferTotal.setText(formatAmount(currentTransfers.add(amount)));
            updateTotals();
            return true;
        } finally {
            addingTransfer = false;
        }
    }

    private void configureChequeTable() {
        TableColumn dateColumn = jTableCheques.getColumnModel().getColumn(2);
        dateColumn.setCellRenderer(new DefaultTableCellRenderer() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            private final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            protected void setValue(Object value) {
                if (value instanceof Date) {
                    setText(dateFormat.format((Date) value));
                } else if (value instanceof LocalDate) {
                    setText(((LocalDate) value).format(localDateFormatter));
                } else if (value != null) {
                    setText(value.toString());
                } else {
                    setText("");
                }
            }
        });
    }
    private void jTextFieldChequeAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldChequeAmountKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (addChequeToTable()) {
                limpiarFormularioCheque();
                HabilitarFormularioCheque();
            }
        }

}//GEN-LAST:event_jTextFieldChequeAmountKeyPressed

    public boolean validarFormulario() {

        // Valida que el codigo no este vacio
        if (jLabelTotal.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un modo de pago", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldCashAmount.requestFocus();
            return (false);
        }

        ReceiptCode receiptCode = parseReceiptCode(jTextFieldPointOfSaleAndNumberReceipt.getText());
        if (receiptCode == null) {
            showInvalidReceiptNumberMessage();
            jTextFieldPointOfSaleAndNumberReceipt.requestFocus();
            return false;
        }

        Integer providerId = selectedProvider != null ? selectedProvider.getId() : null;
        if (providerId != null) {
            boolean receiptExists = providerReceiptController.findAll().stream()
                    .filter(r -> r.getProvider() != null && r.getProvider().getId() != null)
                    .anyMatch(r -> providerId.equals(r.getProvider().getId())
                            && receiptCode.pointOfSale().equals(r.getPointOfSale())
                            && receiptCode.number().equals(r.getReceiptNumber()));
            if (receiptExists) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe un recibo con el mismo punto de venta y número para el proveedor seleccionado",
                        "Atencion", JOptionPane.WARNING_MESSAGE);
                jTextFieldPointOfSaleAndNumberReceipt.requestFocus();
                return false;
            }
        }

        jTextFieldPointOfSaleAndNumberReceipt.setText(
                receiptCode.pointOfSale() + "-" + receiptCode.number());

        return true;

    }

    private ProviderReceipt saveReceipt() throws Exception {
        ProviderReceipt receipt = new ProviderReceipt(); 
        
        ReceiptCode receiptCode = parseReceiptCode(jTextFieldPointOfSaleAndNumberReceipt.getText());
        if (receiptCode == null) {
            showInvalidReceiptNumberMessage();
            return null;
        }
        receipt.setReceiptNumber(receiptCode.number());
        receipt.setPointOfSale(receiptCode.pointOfSale());
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setTotal(new BigDecimal(jLabelTotal.getText()));
        receipt.setReceiverCuit(getSelectedReceiverCuit());
        if (selectedProvider != null) {
            Provider provider = providerController.findById(selectedProvider.getId());
            if (provider == null) {
                JOptionPane.showMessageDialog(this, "El proveedor seleccionado ya no existe", "Pagos", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (!provider.isActive()) {
                JOptionPane.showMessageDialog(this, "El proveedor está deshabilitado", "Pagos", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            receipt.setProvider(provider);
        }
        providerReceiptController.save(receipt);

        BigDecimal cashAmount = parseAmount(jLabelCashTotal.getText());
        if (cashAmount.compareTo(BigDecimal.ZERO) > 0) {
            ReceiptCash cash = new ReceiptCash();
            cash.setReceiptId(receipt.getId());
            cash.setReceiptType("PROVIDER");
            cash.setAmount(cashAmount);
            receiptCashController.save(cash);
        }

        for (int i = 0; i < jTableCards.getRowCount(); i++) {
            ReceiptCard card = new ReceiptCard();
            card.setReceiptId(receipt.getId());
            card.setReceiptType("PROVIDER");
            String cardType = ReceiptUtils.normalizeCardType(jTableCards.getValueAt(i, 0));
            if (cardType == null) {
                throw new IllegalStateException("Tipo de tarjeta inválido: " + jTableCards.getValueAt(i, 0));
            }
            card.setCardType(cardType);
            ComboBoxItem<Integer> cardItem = toIntegerComboItem(jTableCards.getValueAt(i, 1));
            if (cardItem == null || cardItem.getValue() == null) {
                throw new IllegalStateException("Tarjeta sin identificador asociado en la fila " + (i + 1));
            }
            card.setCardId(cardItem.getValue());
            card.setCardName(cardItem.getLabel());
            String number = String.valueOf(jTableCards.getValueAt(i, 2));
            card.setLastFourDigits(number.length() > 4 ? number.substring(number.length() - 4) : number);
            Object amountValue = jTableCards.getValueAt(i, 3);
            card.setAmount(toBigDecimal(amountValue));
            receiptCardController.save(card);
        }

        for (int i = 0; i < jTableCheques.getRowCount(); i++) {
            ReceiptCheque cheque = new ReceiptCheque();
            cheque.setReceiptId(receipt.getId());
            cheque.setReceiptType("PROVIDER");
            cheque.setCheckNumber(String.valueOf(jTableCheques.getValueAt(i, 0)));
            cheque.setHolderName(String.valueOf(jTableCheques.getValueAt(i, 1)));
            ComboBoxItem<Integer> bankItem = toIntegerComboItem(jTableCheques.getValueAt(i, 2));
            if (bankItem == null || bankItem.getValue() == null) {
                throw new IllegalStateException("Banco de cheque inválido en la fila " + (i + 1));
            }
            cheque.setBankId(bankItem.getValue());
            cheque.setBankName(bankItem.getLabel());
            Object dateObj = jTableCheques.getValueAt(i, 3);
            if (dateObj instanceof Date) {
                Date d = (Date) dateObj;
                cheque.setDueDate(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            Object amountValue = jTableCheques.getValueAt(i, 4);
            cheque.setAmount(toBigDecimal(amountValue));
            receiptChequeController.save(cheque);
        }

        for (int i = 0; i < jTableTransferencias.getRowCount(); i++) {
            ReceiptTransfer transfer = new ReceiptTransfer();
            transfer.setReceiptId(receipt.getId());
            transfer.setReceiptType("PROVIDER");
            ComboBoxItem<Integer> originBankItem = toIntegerComboItem(jTableTransferencias.getValueAt(i, 0));
            ComboBoxItem<Integer> destinationBankItem = toIntegerComboItem(jTableTransferencias.getValueAt(i, 2));
            if (originBankItem != null && originBankItem.getValue() != null) {
                transfer.setOriginBankId(originBankItem.getValue());
                transfer.setOriginBankName(originBankItem.getLabel());
            }
            if (destinationBankItem != null && destinationBankItem.getValue() != null) {
                transfer.setDestinationBankId(destinationBankItem.getValue());
                transfer.setDestinationBankName(destinationBankItem.getLabel());
            }
            transfer.setOriginAccount(toNullableString(jTableTransferencias.getValueAt(i, 1)));
            transfer.setDestinationAccount(toNullableString(jTableTransferencias.getValueAt(i, 3)));
            transfer.setReference(toNullableString(jTableTransferencias.getValueAt(i, 4)));
            Object amountValue = jTableTransferencias.getValueAt(i, 5);
            transfer.setAmount(toBigDecimal(amountValue));
            receiptTransferController.save(transfer);
        }

        BigDecimal retentionAmount = parseAmount(jLabelRetentionTotal.getText());
        if (retentionAmount.compareTo(BigDecimal.ZERO) > 0) {
            ReceiptRetention retention = new ReceiptRetention();
            retention.setReceiptId(receipt.getId());
            retention.setReceiptType("PROVIDER");
            retention.setDescription(Objects.nonNull(jTextAreaRetentionDetail.getText()) ? jTextAreaRetentionDetail.getText(): null);
            retention.setAmount(retentionAmount);
            receiptRetentionController.save(retention);
        }

        return receipt;
    }

    private void jButtonConfirmReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmReceiptActionPerformed
        try {
            if (validarFormulario()) {
                if (!confirmReceiptSave()) {
                    return;
                }
                ProviderReceipt receipt = saveReceipt();
                if (receipt != null) {
                    refreshProviderTable(receipt.getProvider());
                    isOpen = false;
                    dispose();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ProviderReceiptInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonConfirmReceiptActionPerformed

    private void jTextFieldRetentionAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRetentionAmountKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            applyRetentionAmount(true);
        }
    }//GEN-LAST:event_jTextFieldRetentionAmountKeyPressed

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged

}//GEN-LAST:event_jComboBox3ItemStateChanged

    public void HabilitarFormularioCheque() {
        jTextFieldChequeAmount.setEnabled(true);
        jTextFieldChequeTitular.setEnabled(true);
        jComboBoxBank.setEnabled(true);
        jButtonRemoveCheque.setEnabled(true);
        jDateChooserChequeDueDate.setEnabled(true);
    }
    private void jTextFieldChequeNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldChequeNumberKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                String numCheque = jTextFieldChequeNumber.getText();
                ReceiptCheque chequeCliente = receiptChequeController.findByNumberAndType(numCheque, "CLIENT");
                ReceiptCheque chequeProveedor = receiptChequeController.findByNumberAndType(numCheque, "PROVIDER");

                if (chequeCliente == null && chequeProveedor == null) {
                    HabilitarFormularioCheque();
                } else {
                    jButtonRemoveCheque.setEnabled(true);
                    ReceiptCheque cheque = chequeProveedor != null ? chequeProveedor : chequeCliente;
                    ComboBoxItem<Integer> bankItem = cheque.getBankId() != null
                            ? findComboItemByValue(jComboBoxBank, cheque.getBankId())
                            : findComboItemByLabel(jComboBoxBank, cheque.getBankName());
                    if (bankItem != null) {
                        jComboBoxBank.setSelectedItem(bankItem);
                    }
                    if (cheque.getDueDate() != null) {
                        Date date = Date.from(cheque.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        jDateChooserChequeDueDate.setDate(date);
                    }
                    jTextFieldChequeAmount.setText(cheque.getAmount().toString());
                    jTextFieldChequeTitular.setText(cheque.getHolderName());
                    if (addChequeToTable()) {
                        limpiarFormularioCheque();
                        HabilitarFormularioCheque();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ProviderReceiptInsertView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTextFieldChequeNumberKeyPressed

    private void jTextFieldMontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMontoKeyReleased
    }//GEN-LAST:event_jTextFieldMontoKeyReleased

    private void jTextFieldChequeAmount1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldChequeAmount1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (addTransferToTable()) {
                limpiarFormularioTransferencia();
            }
        }
    }//GEN-LAST:event_jTextFieldChequeAmount1KeyPressed

    private void jTextFieldChequeAmount1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldChequeAmount1KeyReleased
        // Se evita agregar automáticamente mientras se ingresa el monto para permitir completar el valor.
    }//GEN-LAST:event_jTextFieldChequeAmount1KeyReleased

    private void jTextFieldOriginAccountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldOriginAccountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBoxOriginBank.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextFieldOriginAccountKeyPressed

    private void jComboBoxOriginBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxOriginBankActionPerformed
        attemptAutoAddTransfer();
    }//GEN-LAST:event_jComboBoxOriginBankActionPerformed

    private void jButtonRemoveCheque1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveCheque1ActionPerformed
        int row = jTableTransferencias.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una transferencia", "Transferencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount = BigDecimal.ZERO;
        Object value = jTableTransferencias.getValueAt(row, 5);
        amount = toBigDecimal(value);

        DefaultTableModel tm = (DefaultTableModel) jTableTransferencias.getModel();
        tm.removeRow(row);
        jTableTransferencias.setModel(tm);

        BigDecimal transfers = parseAmount(jLabelTransferTotal.getText());
        BigDecimal updated = transfers.subtract(amount);
        if (updated.compareTo(BigDecimal.ZERO) < 0) {
            updated = BigDecimal.ZERO;
        }
        jLabelTransferTotal.setText(formatAmount(updated));
        updateTotals();
    }//GEN-LAST:event_jButtonRemoveCheque1ActionPerformed

    private void jComboBoxDestinationBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDestinationBankActionPerformed
        attemptAutoAddTransfer();
    }//GEN-LAST:event_jComboBoxDestinationBankActionPerformed

    private void jTextFieldReferenceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldReferenceKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            attemptAutoAddTransfer();
        }
    }//GEN-LAST:event_jTextFieldReferenceKeyPressed

    private void jTextFieldReferenceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldReferenceKeyReleased
        // Se evita agregar automáticamente mientras se ingresa la referencia para permitir completar el texto.
    }//GEN-LAST:event_jTextFieldReferenceKeyReleased


    private void refreshProviderTable(Provider provider) {
        if (provider == null) {
            return;
        }
        try {
            BigDecimal totalBalance = providerController.fillTable(ProviderManagementView.jTable1, null);
            int rows = ProviderManagementView.jTable1.getRowCount();
            for (int i = 0; i < rows; i++) {
                Object value = ProviderManagementView.jTable1.getValueAt(i, 0);
                if (value != null && Integer.parseInt(value.toString()) == provider.getId()) {
                    ProviderManagementView.jTable1.setRowSelectionInterval(i, i);
                    break;
                }
            }
            ProviderManagementView.jLabelTotalProviders.setText(providerController.formatCurrency(totalBalance));
        } catch (Exception ex) {
            Logger.getLogger(ProviderReceiptInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private record ReceiptCode(String pointOfSale, String number) {}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancelReceipt;
    private javax.swing.JButton jButtonConfirmReceipt;
    private javax.swing.JButton jButtonRemoveCard;
    private javax.swing.JButton jButtonRemoveCheque;
    private javax.swing.JButton jButtonRemoveCheque1;
    private javax.swing.JButton jButtonReturn;
    public static javax.swing.JComboBox jComboBoxBank;
    public static javax.swing.JComboBox jComboBoxCard;
    public static javax.swing.JComboBox jComboBoxDestinationBank;
    public static javax.swing.JComboBox jComboBoxInvoiceReceiverCuit;
    public static javax.swing.JComboBox jComboBoxOriginBank;
    private com.toedter.calendar.JDateChooser jDateChooserChequeDueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCardTotal;
    private javax.swing.JLabel jLabelCashTotal;
    public static javax.swing.JLabel jLabelChequeTotal;
    public static javax.swing.JLabel jLabelDebe;
    public static javax.swing.JLabel jLabelRetentionTotal;
    private javax.swing.JLabel jLabelTotal;
    public static javax.swing.JLabel jLabelTransferTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButtonCredit;
    private javax.swing.JRadioButton jRadioButtonDebit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    public static javax.swing.JTable jTableCards;
    public static javax.swing.JTable jTableCheques;
    public static javax.swing.JTable jTableTransferencias;
    private javax.swing.JTextArea jTextAreaRetentionDetail;
    private javax.swing.JTextField jTextFieldCardAmount;
    private javax.swing.JTextField jTextFieldCardNumber;
    private javax.swing.JTextField jTextFieldCashAmount;
    private javax.swing.JTextField jTextFieldCashBack;
    private javax.swing.JTextField jTextFieldChequeAmount;
    private javax.swing.JTextField jTextFieldChequeAmount1;
    private javax.swing.JTextField jTextFieldChequeNumber;
    private javax.swing.JTextField jTextFieldChequeTitular;
    private javax.swing.JTextField jTextFieldDestinationAccount;
    private javax.swing.JTextField jTextFieldOriginAccount;
    private javax.swing.JTextField jTextFieldPointOfSaleAndNumberReceipt;
    private javax.swing.JTextField jTextFieldReference;
    private javax.swing.JTextField jTextFieldRetentionAmount;
    // End of variables declaration//GEN-END:variables

}
