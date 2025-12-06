package views.providers;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.apache.ibatis.session.SqlSession;

import configs.MyBatisConfig;
import controllers.InvoiceCategoryController;
import controllers.InvoiceTypeController;
import controllers.ProviderController;
import controllers.ProviderInvoiceController;
import mappers.InvoiceCategoryMapper;
import mappers.ProviderInvoiceMapper;
import mappers.ProviderMapper;
import models.InvoiceCategory;
import models.Provider;
import models.ProviderInvoice;
import repositories.InvoiceCategoryRepository;
import repositories.InvoiceTypeRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderRepository;
import repositories.impl.InvoiceCategoryRepositoryImpl;
import repositories.impl.InvoiceTypeRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderRepositoryImpl;
import services.InvoiceCategoryService;
import services.InvoiceTypeService;
import services.ProviderInvoiceService;
import services.ProviderService;
import utils.CuitSelectorUtils;
import utils.DocumentValidator;
import utils.InvoiceTypeUtils;
import views.MainView;
import views.providers.ProviderInvoiceManagementView;

public class ProviderInvoiceUpsertView extends javax.swing.JInternalFrame {

    public static boolean abierta = false;

    private ProviderInvoiceController providerInvoiceController;
    private ProviderController providerController;
    private InvoiceCategoryController invoiceCategoryController;
    private InvoiceTypeController invoiceTypeController;
    private ProviderInvoice invoice;
    private boolean editing = false;
    private SqlSession sqlSession;
    private Provider selectedProvider;

    public ProviderInvoiceUpsertView() {
        this(null);
    }

    public ProviderInvoiceUpsertView(Integer invoiceId) {
        abierta = true;
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
        InvoiceCategoryMapper invoiceCategoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);
        ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);

        ProviderInvoiceRepository providerInvoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
        InvoiceCategoryRepository invoiceCategoryRepository = new InvoiceCategoryRepositoryImpl(invoiceCategoryMapper);
        ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
        InvoiceTypeRepository invoiceTypeRepository = new InvoiceTypeRepositoryImpl();

        ProviderInvoiceService providerInvoiceService = new ProviderInvoiceService(providerInvoiceRepository);
        InvoiceCategoryService invoiceCategoryService = new InvoiceCategoryService(invoiceCategoryRepository);
        ProviderService providerService = new ProviderService(providerRepository);
        InvoiceTypeService invoiceTypeService = new InvoiceTypeService(invoiceTypeRepository);

        providerInvoiceController = new ProviderInvoiceController(providerInvoiceService);
        invoiceCategoryController = new InvoiceCategoryController(invoiceCategoryService);
        providerController = new ProviderController(providerService);
        invoiceTypeController = new InvoiceTypeController(invoiceTypeService);

        initComponents();
        configureInvoiceIdentifierFormatters();

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                abierta = false;
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                abierta = false;
            }
        });

        loadInvoiceTypes();
        loadCategories();
        CuitSelectorUtils.populateCuits(jComboBoxInvoiceReceiverCuit);
        jDateChooserPresentationDate.setDate(new Date());
        clearProviderInfo();

        if (invoiceId != null) {
            invoice = providerInvoiceController.findById(invoiceId);
            if (invoice != null) {
                editing = true;
                loadInvoice();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelCity = new javax.swing.JLabel();
        jLabelAddress = new javax.swing.JLabel();
        jLabelProvider = new javax.swing.JLabel();
        jLabelCondition = new javax.swing.JLabel();
        jTextFieldProviderCuit = new javax.swing.JTextField();
        jButtonProviderSearch = new javax.swing.JButton();
        jComboBoxType = new javax.swing.JComboBox();
        jDateChooserDate = new com.toedter.calendar.JDateChooser();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelCodigo = new javax.swing.JLabel();
        jTextFieldInvoiceNumber = new javax.swing.JTextField();
        jTextFieldTotal = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldVatPerception = new javax.swing.JTextField();
        jTextFieldVat21 = new javax.swing.JTextField();
        jTextFieldVat105 = new javax.swing.JTextField();
        jTextFieldVat27 = new javax.swing.JTextField();
        jTextFieldStampTax = new javax.swing.JTextField();
        jTextFieldGrossIncomePerception = new javax.swing.JTextField();
        jTextFieldIncomeTaxPerception = new javax.swing.JTextField();
        jTextFieldExemptAmount = new javax.swing.JTextField();
        jTextFieldSubtotal = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jDateChooserPresentationDate = new com.toedter.calendar.JDateChooser();
        jLabel24 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxInvoiceCategory = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPointOfSale = new javax.swing.JTextField();
        jComboBoxInvoiceReceiverCuit = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(850, 620));
        setPreferredSize(new java.awt.Dimension(850, 620));
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Dni/Cuit Proveedor");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(40, 30, 125, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Ciudad");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 60, 125, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Dirección");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 90, 125, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Proveedor");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(40, 120, 125, 20);

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Condición");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(40, 150, 125, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Fecha Comprobante");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(400, 60, 140, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Tipo Comprobante");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(410, 120, 130, 20);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Comprobante Número");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(640, 10, 160, 20);

        jLabelCity.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCity);
        jLabelCity.setBounds(190, 60, 150, 20);

        jLabelAddress.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelAddress);
        jLabelAddress.setBounds(190, 90, 150, 20);

        jLabelProvider.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelProvider);
        jLabelProvider.setBounds(190, 120, 150, 20);

        jLabelCondition.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCondition);
        jLabelCondition.setBounds(190, 150, 150, 20);

        jTextFieldProviderCuit.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldProviderCuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldProviderCuitActionPerformed(evt);
            }
        });
        jTextFieldProviderCuit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldProviderCuitKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldProviderCuit);
        jTextFieldProviderCuit.setBounds(180, 30, 130, 20);

        jButtonProviderSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonProviderSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProviderSearchActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonProviderSearch);
        jButtonProviderSearch.setBounds(320, 30, 25, 25);

        jComboBoxType.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxType);
        jComboBoxType.setBounds(560, 120, 180, 21);
        getContentPane().add(jDateChooserDate);
        jDateChooserDate.setBounds(560, 60, 110, 22);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(260, 550, 130, 30);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(400, 550, 130, 30);
        getContentPane().add(jLabelCodigo);
        jLabelCodigo.setBounds(360, 30, 50, 20);

        jTextFieldInvoiceNumber.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getContentPane().add(jTextFieldInvoiceNumber);
        jTextFieldInvoiceNumber.setBounds(700, 30, 100, 20);

        jTextFieldTotal.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        getContentPane().add(jTextFieldTotal);
        jTextFieldTotal.setBounds(250, 250, 220, 30);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        jLabel1.setText("Importe Total");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(70, 250, 180, 30);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(40, 230, 820, 10);

        jLabel9.setBackground(new java.awt.Color(255, 102, 0));
        jLabel9.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 102, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Percepcion IVA");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(620, 320, 140, 30);

        jLabel11.setBackground(new java.awt.Color(255, 102, 0));
        jLabel11.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 102, 0));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("IVA 21");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(70, 320, 140, 30);

        jLabel15.setBackground(new java.awt.Color(255, 102, 0));
        jLabel15.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 102, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("IVA 10.5");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(250, 320, 140, 30);

        jLabel16.setBackground(new java.awt.Color(255, 102, 0));
        jLabel16.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 102, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("IVA 27");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(430, 320, 140, 30);

        jLabel17.setBackground(new java.awt.Color(255, 102, 0));
        jLabel17.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 102, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Sellados");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(620, 430, 140, 30);

        jLabel18.setBackground(new java.awt.Color(255, 102, 0));
        jLabel18.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 102, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Percepcion IIBB");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(70, 430, 140, 30);

        jLabel19.setBackground(new java.awt.Color(255, 102, 0));
        jLabel19.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 102, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Perc. Ganancias");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(250, 430, 140, 30);

        jLabel20.setBackground(new java.awt.Color(255, 102, 0));
        jLabel20.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 102, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("No Gravado");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(430, 430, 140, 30);

        jTextFieldVatPerception.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldVatPerception.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldVatPerception);
        jTextFieldVatPerception.setBounds(620, 360, 140, 30);

        jTextFieldVat21.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldVat21.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldVat21);
        jTextFieldVat21.setBounds(70, 360, 140, 30);

        jTextFieldVat105.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldVat105.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldVat105);
        jTextFieldVat105.setBounds(250, 360, 140, 30);

        jTextFieldVat27.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldVat27.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldVat27);
        jTextFieldVat27.setBounds(430, 360, 140, 30);

        jTextFieldStampTax.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldStampTax.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldStampTax);
        jTextFieldStampTax.setBounds(620, 470, 140, 30);

        jTextFieldGrossIncomePerception.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldGrossIncomePerception.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldGrossIncomePerception);
        jTextFieldGrossIncomePerception.setBounds(70, 470, 140, 30);

        jTextFieldIncomeTaxPerception.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldIncomeTaxPerception.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldIncomeTaxPerception);
        jTextFieldIncomeTaxPerception.setBounds(250, 470, 140, 30);

        jTextFieldExemptAmount.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jTextFieldExemptAmount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        getContentPane().add(jTextFieldExemptAmount);
        jTextFieldExemptAmount.setBounds(430, 470, 140, 30);

        jTextFieldSubtotal.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        getContentPane().add(jTextFieldSubtotal);
        jTextFieldSubtotal.setBounds(630, 250, 110, 30);

        jLabel23.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel23.setText("Subtotal");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(520, 250, 100, 30);
        getContentPane().add(jDateChooserPresentationDate);
        jDateChooserPresentationDate.setBounds(560, 90, 110, 22);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Fecha Presentación");
        getContentPane().add(jLabel24);
        jLabel24.setBounds(400, 90, 140, 20);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Categoría");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(410, 150, 130, 20);

        jComboBoxInvoiceCategory.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxInvoiceCategory);
        jComboBoxInvoiceCategory.setBounds(560, 150, 180, 21);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Cuit recibidor");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(420, 180, 125, 20);

        jTextFieldPointOfSale.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        getContentPane().add(jTextFieldPointOfSale);
        jTextFieldPointOfSale.setBounds(650, 30, 40, 20);

        jComboBoxInvoiceReceiverCuit.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxInvoiceReceiverCuit);
        jComboBoxInvoiceReceiverCuit.setBounds(560, 180, 180, 21);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearForm() {
        jTextFieldProviderCuit.setText("");
        jTextFieldTotal.setText("");
        jTextFieldInvoiceNumber.setText("");
        jTextFieldPointOfSale.setText("");
        jTextFieldSubtotal.setText("");
        jTextFieldVatPerception.setText("");
        jTextFieldVat21.setText("");
        jTextFieldVat105.setText("");
        jTextFieldVat27.setText("");
        jTextFieldStampTax.setText("");
        jTextFieldGrossIncomePerception.setText("");
        jTextFieldIncomeTaxPerception.setText("");
        jTextFieldExemptAmount.setText("");
        jComboBoxType.setSelectedIndex(0);
        jComboBoxInvoiceCategory.setSelectedIndex(0);
        CuitSelectorUtils.populateCuits(jComboBoxInvoiceReceiverCuit);
        jDateChooserDate.setDate(null);
        jDateChooserPresentationDate.setDate(new Date());
        clearProviderInfo();
    }

    public void loadInvoiceTypes() {
        invoiceTypeController.loadTypes(jComboBoxType);
    }

    public void loadCategories() {
        invoiceCategoryController.loadProviderCategories(jComboBoxInvoiceCategory);
    }

    private void loadInvoice() {
        jTextFieldInvoiceNumber.setText(invoice.getInvoiceNumber());
        jTextFieldPointOfSale.setText(invoice.getPointOfSale());
        formatInvoiceIdentifiers();
        jComboBoxType.setSelectedItem(InvoiceTypeUtils.toDisplayValue(invoice.getInvoiceType()));
        selectProviderById(invoice.getProvider() != null ? invoice.getProvider().getId() : null);
        selectCategory(invoice.getCategory());
        CuitSelectorUtils.selectCuit(jComboBoxInvoiceReceiverCuit, invoice.getReceiverCuit());
        if (invoice.getReceiverCuit() != null && jComboBoxInvoiceReceiverCuit.getSelectedIndex() == -1) {
            String formatted = DocumentValidator.formatCuit(invoice.getReceiverCuit());
            jComboBoxInvoiceReceiverCuit.addItem(formatted);
            jComboBoxInvoiceReceiverCuit.setSelectedItem(formatted);
        }
        if (invoice.getInvoiceDate() != null) {
            jDateChooserDate.setDate(java.util.Date.from(invoice.getInvoiceDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        if (invoice.getPresentationDate() != null) {
            jDateChooserPresentationDate.setDate(java.util.Date.from(invoice.getPresentationDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        setTextIfNotNull(jTextFieldSubtotal, invoice.getSubtotal());
        setTextIfNotNull(jTextFieldVatPerception, invoice.getVatPerception());
        setTextIfNotNull(jTextFieldVat21, invoice.getVat21());
        setTextIfNotNull(jTextFieldVat105, invoice.getVat105());
        setTextIfNotNull(jTextFieldVat27, invoice.getVat27());
        setTextIfNotNull(jTextFieldStampTax, invoice.getStampTax());
        setTextIfNotNull(jTextFieldGrossIncomePerception, invoice.getGrossIncomePerception());
        setTextIfNotNull(jTextFieldIncomeTaxPerception, invoice.getIncomeTaxPerception());
        setTextIfNotNull(jTextFieldExemptAmount, invoice.getExemptAmount());
        setTextIfNotNull(jTextFieldTotal, invoice.getTotal());
    }

    private void setTextIfNotNull(javax.swing.JTextField field, BigDecimal value) {
        if (value != null) {
            field.setText(value.toString());
        }
    }

    private java.time.LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    private BigDecimal parseBigDecimal(String text) {
        if (text == null || text.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(text);
    }

    private void jButtonProviderSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProviderSearchActionPerformed
        openProviderSearch();
    }//GEN-LAST:event_jButtonProviderSearchActionPerformed

    private void jTextFieldProviderCuitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldProviderCuitKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            findProvider();
        }
    }//GEN-LAST:event_jTextFieldProviderCuitKeyPressed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        abierta = false;
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed


    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        try {
            formatInvoiceIdentifiers();
            if (!validateForm()) {
                return;
            }

            ProviderInvoice current = editing ? invoice : new ProviderInvoice();
            current.setInvoiceNumber(jTextFieldInvoiceNumber.getText());
            current.setPointOfSale(jTextFieldPointOfSale.getText());
            current.setInvoiceType(InvoiceTypeUtils.toStorageValue(jComboBoxType.getSelectedItem().toString()));

            Provider provider = new Provider();
            provider.setId(Integer.parseInt(jLabelCodigo.getText()));
            current.setProvider(provider);

            Object selectedCategory = jComboBoxInvoiceCategory.getSelectedItem();
            if (selectedCategory instanceof InvoiceCategory) {
                current.setCategory((InvoiceCategory) selectedCategory);
            }

            current.setReceiverCuit(CuitSelectorUtils.getSelectedCuit(jComboBoxInvoiceReceiverCuit));
            current.setInvoiceDate(toLocalDateTime(jDateChooserDate.getDate()));
            current.setPresentationDate(toLocalDateTime(jDateChooserPresentationDate.getDate()));
            current.setSubtotal(parseBigDecimal(jTextFieldSubtotal.getText()));
            current.setVatPerception(parseBigDecimal(jTextFieldVatPerception.getText()));
            current.setVat21(parseBigDecimal(jTextFieldVat21.getText()));
            current.setVat105(parseBigDecimal(jTextFieldVat105.getText()));
            current.setVat27(parseBigDecimal(jTextFieldVat27.getText()));
            current.setStampTax(parseBigDecimal(jTextFieldStampTax.getText()));
            current.setGrossIncomePerception(parseBigDecimal(jTextFieldGrossIncomePerception.getText()));
            current.setIncomeTaxPerception(parseBigDecimal(jTextFieldIncomeTaxPerception.getText()));
            current.setExemptAmount(parseBigDecimal(jTextFieldExemptAmount.getText()));
            current.setTotal(parseBigDecimal(jTextFieldTotal.getText()));

            if (editing) {
                current.setId(invoice.getId());
                providerInvoiceController.update(current);
                JOptionPane.showMessageDialog(this, "El comprobante ha sido actualizado!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
            } else {
                providerInvoiceController.save(current);
                JOptionPane.showMessageDialog(this, "El comprobante ha sido guardado!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            }
            ProviderInvoiceManagementView.refreshTable();

        } catch (Exception ex) {
            Logger.getLogger(ProviderInvoiceUpsertView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al guardar el comprobante", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jTextFieldProviderCuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldProviderCuitActionPerformed
        jTextFieldTotal.requestFocus();
}//GEN-LAST:event_jTextFieldProviderCuitActionPerformed

    public boolean validateForm() {

        boolean dev = true;

        // Valida que el codigo no este vacio
        if (ProviderInvoiceUpsertView.jTextFieldProviderCuit.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldProviderCuit.requestFocus();
            dev = false;
        }

        if (jLabelCodigo.getText() == null || jLabelCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor válido", "Atencion", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        if (selectedProvider == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor activo", "Proveedor", JOptionPane.WARNING_MESSAGE);
            dev = false;
        } else if (!selectedProvider.isActive()) {
            JOptionPane.showMessageDialog(this, "El proveedor seleccionado está deshabilitado", "Proveedor", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        if (ProviderInvoiceUpsertView.jComboBoxType.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el tipo de comprobante", "Atencion", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        if (ProviderInvoiceUpsertView.jTextFieldTotal.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el monto total", "Atencion", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        if (ProviderInvoiceUpsertView.jTextFieldPointOfSale.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el punto de venta", "Atencion", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        if (ProviderInvoiceUpsertView.jTextFieldInvoiceNumber.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el numero de la factura", "Atencion", JOptionPane.WARNING_MESSAGE);
            dev = false;
        }

        String totalText = ProviderInvoiceUpsertView.jTextFieldTotal.getText();
        if (totalText != null && !totalText.trim().isEmpty()) {
            try {
                BigDecimal subtotal = parseBigDecimal(jTextFieldSubtotal.getText());
                BigDecimal vatPerception = parseBigDecimal(jTextFieldVatPerception.getText());
                BigDecimal vat21 = parseBigDecimal(jTextFieldVat21.getText());
                BigDecimal vat105 = parseBigDecimal(jTextFieldVat105.getText());
                BigDecimal vat27 = parseBigDecimal(jTextFieldVat27.getText());
                BigDecimal stampTax = parseBigDecimal(jTextFieldStampTax.getText());
                BigDecimal grossIncome = parseBigDecimal(jTextFieldGrossIncomePerception.getText());
                BigDecimal incomeTax = parseBigDecimal(jTextFieldIncomeTaxPerception.getText());
                BigDecimal exemptAmount = parseBigDecimal(jTextFieldExemptAmount.getText());
                BigDecimal total = parseBigDecimal(totalText);

                BigDecimal calculated = subtotal
                        .add(vatPerception)
                        .add(vat21)
                        .add(vat105)
                        .add(vat27)
                        .add(stampTax)
                        .add(grossIncome)
                        .add(incomeTax)
                        .add(exemptAmount);

                if (total.compareTo(calculated) != 0) {
                    JOptionPane.showMessageDialog(this,
                            "El total debe ser igual a la suma de subtotal, impuestos y percepciones",
                            "Total inválido",
                            JOptionPane.WARNING_MESSAGE);
                    dev = false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar importes válidos utilizando números y separador decimal",
                        "Importes inválidos",
                        JOptionPane.WARNING_MESSAGE);
                dev = false;
            }
        }

        return dev;

    }

    private void configureInvoiceIdentifierFormatters() {
        FocusAdapter adapter = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                formatInvoiceIdentifiers();
            }
        };
        jTextFieldPointOfSale.addFocusListener(adapter);
        jTextFieldInvoiceNumber.addFocusListener(adapter);
    }

    private void formatInvoiceIdentifiers() {
        jTextFieldPointOfSale.setText(formatPointOfSale(jTextFieldPointOfSale.getText()));
        jTextFieldInvoiceNumber.setText(formatInvoiceNumber(jTextFieldInvoiceNumber.getText()));
    }

    private String formatPointOfSale(String value) {
        return padLeftDigits(value, 4);
    }

    private String formatInvoiceNumber(String value) {
        return padLeftDigits(value, 8);
    }

    private String padLeftDigits(String value, int length) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "";
        }
        if (digits.length() >= length) {
            return digits;
        }
        return String.format("%" + length + "s", digits).replace(' ', '0');
    }

    private void findProvider() {
        String document = jTextFieldProviderCuit.getText();
        if (document == null || document.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el CUIT/DNI del proveedor", "Buscar proveedor", JOptionPane.WARNING_MESSAGE);
            clearProviderInfo();
            return;
        }
        document = document.trim();
        String normalized = DocumentValidator.normalizeCuit(document);
        if (normalized != null && !normalized.isBlank()) {
            document = normalized;
        }
        Provider provider = providerController.findByDocument(document);
        if (provider == null) {
            JOptionPane.showMessageDialog(this, "No se encontró un proveedor con ese documento", "Buscar proveedor", JOptionPane.INFORMATION_MESSAGE);
            clearProviderInfo();
            return;
        }
        setProviderInfo(provider);
    }

    private void openProviderSearch() {
        if (!ProviderManagementView.isOpen) {
            ProviderManagementView view = new ProviderManagementView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
        ProviderManagementView.setProviderSelectionListener(this::handleProviderSelectedFromSearch, true);
        ProviderManagementView.bringToFront();
    }

    private void handleProviderSelectedFromSearch(Provider provider) {
        if (provider == null) {
            return;
        }
        setProviderInfo(provider);
    }

    private void selectProviderById(Integer providerId) {
        if (providerId == null) {
            clearProviderInfo();
            return;
        }
        Provider provider = providerController.findById(providerId);
        if (provider != null) {
            setProviderInfo(provider);
        } else {
            clearProviderInfo();
            jLabelCodigo.setText(providerId.toString());
        }
    }

    private void selectCategory(InvoiceCategory category) {
        if (category == null) {
            jComboBoxInvoiceCategory.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < jComboBoxInvoiceCategory.getItemCount(); i++) {
            Object item = jComboBoxInvoiceCategory.getItemAt(i);
            if (item instanceof InvoiceCategory) {
                InvoiceCategory option = (InvoiceCategory) item;
                if (category.getId() != null && category.getId().equals(option.getId())) {
                    jComboBoxInvoiceCategory.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void setProviderInfo(Provider provider) {
        if (provider == null) {
            selectedProvider = null;
            clearProviderInfo();
            return;
        }
        if (!provider.isActive() && !editing) {
            JOptionPane.showMessageDialog(this, "El proveedor está deshabilitado", "Proveedor", JOptionPane.WARNING_MESSAGE);
            selectedProvider = null;
            clearProviderInfo();
            return;
        }
        selectedProvider = provider;
        if (provider.getId() != null) {
            jLabelCodigo.setText(provider.getId().toString());
        } else {
            jLabelCodigo.setText("");
        }
        jLabelProvider.setText(safe(provider.getName()));
        jLabelCity.setText(provider.getCity() != null ? safe(provider.getCity().getName()) : "");
        String address = provider.getAddress() != null ? safe(provider.getAddress().getName()) : "";
        if (provider.getAddressNumber() != null && !provider.getAddressNumber().isBlank()) {
            address = address.isEmpty() ? provider.getAddressNumber() : address + " " + provider.getAddressNumber();
        }
        jLabelAddress.setText(address);
        jLabelCondition.setText(provider.getTaxCondition() != null ? safe(provider.getTaxCondition().getName()) : "");
        if (provider.getDocumentNumber() != null) {
            jTextFieldProviderCuit.setText(DocumentValidator.formatCuit(provider.getDocumentNumber()));
        }
    }

    private void clearProviderInfo() {
        jLabelCodigo.setText("");
        jLabelProvider.setText("");
        jLabelCity.setText("");
        jLabelAddress.setText("");
        jLabelCondition.setText("");
        jTextFieldProviderCuit.setText("");
        selectedProvider = null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonProviderSearch;
    private javax.swing.JButton jButtonSave;
    public static javax.swing.JComboBox jComboBoxInvoiceCategory;
    public static javax.swing.JComboBox jComboBoxInvoiceReceiverCuit;
    public static javax.swing.JComboBox jComboBoxType;
    public static com.toedter.calendar.JDateChooser jDateChooserDate;
    public static com.toedter.calendar.JDateChooser jDateChooserPresentationDate;
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
    public static javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    public static javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabelAddress;
    public static javax.swing.JLabel jLabelCity;
    public static javax.swing.JLabel jLabelCodigo;
    public static javax.swing.JLabel jLabelCondition;
    public static javax.swing.JLabel jLabelProvider;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JTextField jTextFieldExemptAmount;
    public static javax.swing.JTextField jTextFieldGrossIncomePerception;
    public static javax.swing.JTextField jTextFieldIncomeTaxPerception;
    public static javax.swing.JTextField jTextFieldInvoiceNumber;
    public static javax.swing.JTextField jTextFieldPointOfSale;
    public static javax.swing.JTextField jTextFieldProviderCuit;
    public static javax.swing.JTextField jTextFieldStampTax;
    public static javax.swing.JTextField jTextFieldSubtotal;
    public static javax.swing.JTextField jTextFieldTotal;
    public static javax.swing.JTextField jTextFieldVat105;
    public static javax.swing.JTextField jTextFieldVat21;
    public static javax.swing.JTextField jTextFieldVat27;
    public static javax.swing.JTextField jTextFieldVatPerception;
    // End of variables declaration//GEN-END:variables

}
