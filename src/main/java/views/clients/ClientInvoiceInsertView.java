package views.clients;

import controllers.ClientBudgetController;
import controllers.ClientBudgetDetailController;
import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.ClientInvoiceDetailController;
import controllers.ClientRemitController;
import controllers.ClientRemitDetailController;
import controllers.InvoiceCategoryController;
import controllers.InvoiceTypeController;
import controllers.ProductController;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import mappers.ClientBudgetDetailMapper;
import mappers.ClientBudgetMapper;
import mappers.ClientInvoiceDetailMapper;
import mappers.ClientInvoiceMapper;
import mappers.ClientMapper;
import mappers.ClientRemitDetailMapper;
import mappers.ClientRemitMapper;
import mappers.ProductMapper;
import mappers.InvoiceCategoryMapper;
import models.Client;
import models.ClientBudget;
import models.ClientBudgetDetail;
import models.ClientInvoice;
import models.ClientInvoiceDetail;
import models.ClientRemit;
import models.ClientRemitDetail;
import models.Product;
import models.InvoiceCategory;
import org.apache.ibatis.session.SqlSession;
import configs.MyBatisConfig;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import repositories.ClientBudgetDetailRepository;
import repositories.ClientBudgetRepository;
import repositories.ClientInvoiceDetailRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientRemitDetailRepository;
import repositories.ClientRemitRepository;
import repositories.ClientRepository;
import repositories.InvoiceTypeRepository;
import repositories.ProductRepository;
import repositories.InvoiceCategoryRepository;
import repositories.impl.ClientBudgetDetailRepositoryImpl;
import repositories.impl.ClientBudgetRepositoryImpl;
import repositories.impl.ClientInvoiceDetailRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientRemitDetailRepositoryImpl;
import repositories.impl.ClientRemitRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.InvoiceTypeRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import repositories.impl.InvoiceCategoryRepositoryImpl;
import services.ClientBudgetDetailService;
import services.ClientBudgetService;
import services.ClientInvoiceDetailService;
import services.ClientInvoiceService;
import services.ClientRemitDetailService;
import services.ClientRemitService;
import services.ClientService;
import services.InvoiceTypeService;
import services.ProductService;
import services.InvoiceCategoryService;
import services.afip.AfipAuthorizationException;
import services.afip.AfipAuthorizationResult;
import services.afip.AfipAuthorizationService;
import services.afip.AfipPdfException;
import services.afip.AfipPdfService;
import services.reports.ClientInvoiceManualPrintService;
import services.reports.ManualInvoicePrintException;
import views.clients.ClientBudgetManagementView;
import views.clients.ClientManagementView;
import views.clients.ClientReceiptInsertView;
import views.clients.ClientRemitManagementView;
import views.products.ProductSearchView;
import views.MainView;
import configs.AppConfig;
import configs.InvoiceEmissionConfig;
import utils.Constants;
import utils.pyAfip.AfipManagement;
import utils.DocumentValidator;
import utils.CuitSelectorUtils;
import utils.InvoiceTypeUtils;
import utils.PointOfSaleCuitResolver;
import utils.TableUtils;

public class ClientInvoiceInsertView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private static ClientInvoiceInsertView activeInstance;
    float cantidad = 0;
    public static Map<String, Float> articulos = null;
    private static final Float[] VAT_OPTIONS = new Float[]{10.5f, 21f};
    private static final Logger LOGGER = Logger.getLogger(ClientInvoiceInsertView.class.getName());
    private static final int DISPLAY_SCALE = 3;
    private static final RoundingMode DISPLAY_ROUNDING_MODE = RoundingMode.DOWN;
    private static final int CALCULATION_SCALE = 8;
    private static final int[] DECIMAL_COLUMNS = {2, 3, 4, 6};
    private static final DecimalFormat DISPLAY_DECIMAL_FORMAT = createDisplayDecimalFormat();
    private static final String INVOICE_TYPE_PLACEHOLDER = "Seleccione...";
    private static final String TYPE_C_DISABLED_CUITS_PROPERTY = "invoice.typeC.disabled.cuits";

    private SqlSession sqlSession;
    private ClientInvoiceController clientInvoiceController;
    private ClientInvoiceDetailController clientInvoiceDetailController;
    private ClientController clientController;
    private ClientRemitController clientRemitController;
    private ClientRemitDetailController clientRemitDetailController;
    private ClientBudgetController clientBudgetController;
    private ClientBudgetDetailController clientBudgetDetailController;
    private ProductController productController;
    private InvoiceTypeController invoiceTypeController;
    private InvoiceCategoryController invoiceCategoryController;
    private AfipAuthorizationService afipAuthorizationService;
    private AfipPdfService afipPdfService;
    private ClientInvoiceManualPrintService manualPrintService;
    private boolean loadedFromRemit;
    private boolean loadedFromBudget;
    private TableModelListener invoiceTableModelListener;
    private TableModel currentInvoiceTableModel;
    private boolean adjustingInvoiceTable;
    private Map<String, String> pointOfSaleIssuerCuits = Collections.emptyMap();
    private Map<String, String> pointOfSaleDescriptions = Collections.emptyMap();
    private List<String> allInvoiceTypeOptions = Collections.emptyList();

    public ClientInvoiceInsertView() throws SQLException, Exception {
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientInvoiceMapper invoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
        ClientInvoiceDetailMapper detailMapper = sqlSession.getMapper(ClientInvoiceDetailMapper.class);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientRemitMapper remitMapper = sqlSession.getMapper(ClientRemitMapper.class);
        ClientRemitDetailMapper remitDetailMapper = sqlSession.getMapper(ClientRemitDetailMapper.class);
        ClientBudgetMapper budgetMapper = sqlSession.getMapper(ClientBudgetMapper.class);
        ClientBudgetDetailMapper budgetDetailMapper = sqlSession.getMapper(ClientBudgetDetailMapper.class);
        ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
        InvoiceCategoryMapper invoiceCategoryMapper = sqlSession.getMapper(InvoiceCategoryMapper.class);

        ClientInvoiceRepository invoiceRepository = new ClientInvoiceRepositoryImpl(invoiceMapper);
        ClientInvoiceDetailRepository detailRepository = new ClientInvoiceDetailRepositoryImpl(detailMapper);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientRemitRepository remitRepository = new ClientRemitRepositoryImpl(remitMapper);
        ClientRemitDetailRepository remitDetailRepository = new ClientRemitDetailRepositoryImpl(remitDetailMapper);
        ClientBudgetRepository budgetRepository = new ClientBudgetRepositoryImpl(budgetMapper);
        ClientBudgetDetailRepository budgetDetailRepository = new ClientBudgetDetailRepositoryImpl(budgetDetailMapper);
        ProductRepository productRepository = new ProductRepositoryImpl(productMapper);
        InvoiceCategoryRepository invoiceCategoryRepository = new InvoiceCategoryRepositoryImpl(invoiceCategoryMapper);
        InvoiceTypeRepository invoiceTypeRepository = new InvoiceTypeRepositoryImpl();

        ClientInvoiceService invoiceService = new ClientInvoiceService(invoiceRepository);
        ClientInvoiceDetailService detailService = new ClientInvoiceDetailService(detailRepository);
        ClientService clientService = new ClientService(clientRepository);
        ClientRemitService remitService = new ClientRemitService(remitRepository);
        ClientRemitDetailService remitDetailService = new ClientRemitDetailService(remitDetailRepository);
        ClientBudgetService budgetService = new ClientBudgetService(budgetRepository);
        ClientBudgetDetailService budgetDetailService = new ClientBudgetDetailService(budgetDetailRepository);
        ProductService prodService = new ProductService(productRepository);
        InvoiceTypeService invoiceTypeService = new InvoiceTypeService(invoiceTypeRepository);
        InvoiceCategoryService invoiceCategoryService = new InvoiceCategoryService(invoiceCategoryRepository);

        clientInvoiceController = new ClientInvoiceController(invoiceService);
        clientInvoiceDetailController = new ClientInvoiceDetailController(detailService);
        clientController = new ClientController(clientService);
        clientRemitController = new ClientRemitController(remitService);
        clientRemitDetailController = new ClientRemitDetailController(remitDetailService);
        clientBudgetController = new ClientBudgetController(budgetService);
        clientBudgetDetailController = new ClientBudgetDetailController(budgetDetailService);
        productController = new ProductController(prodService);
        invoiceTypeController = new InvoiceTypeController(invoiceTypeService);
        invoiceCategoryController = new InvoiceCategoryController(invoiceCategoryService);
        afipAuthorizationService = new AfipAuthorizationService();
        afipPdfService = new AfipPdfService();
        manualPrintService = new ClientInvoiceManualPrintService();

        isOpen = true;
        activeInstance = this;
        initComponents();
        configureIssuerCuitCombo();
        invoiceCategoryController.loadClientCategories(jComboBoxInvoiceCategory);
        if (jComboBoxInvoiceCategory.getItemCount() > 0) {
            jComboBoxInvoiceCategory.setSelectedIndex(0);
        }

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isOpen = false;
                activeInstance = null;
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                isOpen = false;
                activeInstance = null;
            }
        });

        invoiceTypeController.loadTypes(jComboBoxTipoCompro);
        captureInvoiceTypeOptions();
        loadPointOfSales();
        updateAssociatedInvoiceComponentsVisibility();
        updateInvoiceNumber();
        jTextFieldDniCuit.requestFocus();

        loadedFromRemit = false;
        loadedFromBudget = false;
        initializeInvoiceContext();
        setModelTable();
    }

    private void initializeInvoiceContext() {
        try {
            loadedFromRemit = false;
            setBudgetMode(false);
            if (ClientRemitManagementView.isOpen) {
                loadFromRemit();
            } else if (ClientBudgetManagementView.isOpen) {
                loadFromBudget();
            } else {
                jTable1.setModel(createEmptyInvoiceModel());
                jDateChooser1.setDate(new Date());
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void reloadRemitContext() {
        ClientInvoiceInsertView instance = activeInstance;
        if (instance == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            instance.loadedFromRemit = false;
            instance.loadFromRemit();
        });
    }

    private static float normalizeVat(Float vat) {
        if (vat == null) {
            return 21f;
        }
        if (Math.abs(vat - 10.5f) < 0.01f) {
            return 10.5f;
        }
        return 21f;
    }

    private void loadFromRemit() {
        ClientRemitManagementView.RemitInvoiceContext context = ClientRemitManagementView.consumePendingInvoiceContext();
        if (context != null) {
            applyRemitInvoiceContext(context);
            return;
        }

        if (ClientRemitManagementView.jTable1 == null) {
            return;
        }

        int row = ClientRemitManagementView.jTable1.getSelectedRow();
        if (row >= 0) {
            String cod = ClientRemitManagementView.jTable1.getValueAt(row, 0).toString();
            ClientRemit remit = clientRemitController.findById(Integer.parseInt(cod));
            if (remit == null) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar el remito seleccionado.", "Remitos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<ClientRemitDetail> details = clientRemitDetailController.findByRemit(remit.getId());
            applyRemitContext(remit.getClient(), details);
        }
    }

    private void applyRemitInvoiceContext(ClientRemitManagementView.RemitInvoiceContext context) {
        if (context == null) {
            return;
        }
        applyRemitContext(context.getClient(), context.getDetails());
    }

    private void applyRemitContext(Client client, List<ClientRemitDetail> details) {
        if (client != null) {
            populateClientInfo(client);
        } else {
            clearClientInfo();
        }
        List<ClientRemitDetail> safeDetails = details != null ? details : Collections.emptyList();
        try {
            jTable1.setModel(createModelFromRemit(safeDetails));
            jTable1.setRowSelectionAllowed(true);
            jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            SumarComprobante();
            setBudgetMode(false);
            loadedFromRemit = true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "No se pudieron cargar los artículos del remito", ex);
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los artículos del remito.", "Remitos", JOptionPane.ERROR_MESSAGE);
            loadedFromRemit = false;
            setBudgetMode(false);
        }
        jDateChooser1.setDate(new Date());
    }

    private void loadFromBudget() {
        int row = ClientBudgetManagementView.jTable1.getSelectedRow();
        if (row >= 0) {
            String cod = ClientBudgetManagementView.jTable1.getValueAt(row, 0).toString();
            ClientBudget budget = clientBudgetController.findById(Integer.parseInt(cod));
            populateClientInfo(budget.getClient());
            List<ClientBudgetDetail> details = clientBudgetDetailController.findByBudget(budget.getId());
            try {
                jTable1.setModel(createModelFromBudget(details));
                jTable1.setRowSelectionAllowed(true);
                jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                SumarComprobante();
                setBudgetMode(true);
                loadedFromBudget = true;
            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
                setBudgetMode(false);
            }
            jDateChooser1.setDate(new Date());
            loadedFromRemit = false;
        } else {
            setBudgetMode(false);
        }
    }

    private void populateClientInfo(Client client) {
        if (client == null) {
            clearClientInfo();
            return;
        }
        if (!client.isActive()) {
            JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Cliente", JOptionPane.WARNING_MESSAGE);
            clearClientInfo();
            return;
        }
        jTextFieldCodigo.setText(client.getId() != null ? client.getId().toString() : "");
        jTextFieldDniCuit.setText(DocumentValidator.formatCuit(client.getDocumentNumber()));
        jLabelCliente.setText(client.getFullName());
        String address = client.getAddress() != null ? client.getAddress().getName() : "";
        jLabelDireccion.setText(address + " " + (client.getAddressNumber() == null ? "" : client.getAddressNumber()));
        jLabelCondicion.setText(client.getTaxCondition() != null ? client.getTaxCondition().getName() : "");
        jLabelCiudad.setText(client.getCity() != null ? client.getCity().getName() : "");

        jComboBoxTipoCompro.setEnabled(true);
        jComboBoxPtoVenta.setEnabled(true);

        String configuredDefault = AppConfig.get("subscription.invoice.type.default", Constants.FACTURA_A_ABBR);
        String normalizedDefault = InvoiceTypeUtils.toStorageValue(
                InvoiceTypeUtils.toAbbreviation(configuredDefault != null ? configuredDefault.trim() : ""));
        if (normalizedDefault.isEmpty()) {
            normalizedDefault = Constants.FACTURA_A_ABBR;
        }

        boolean defaultApplied = selectInvoiceTypeByNormalizedValue(normalizedDefault);
        if (!defaultApplied) {
            if (client.getTaxCondition() != null
                    && "RESPONSABLE INSCRIPTO".equals(client.getTaxCondition().getName())) {
                jComboBoxTipoCompro.setSelectedItem("Factura A");
            } else {
                jComboBoxTipoCompro.setSelectedItem("Factura B");
            }
        }

        jComboBoxPtoVenta.setSelectedItem("0003");
    }

    private void clearClientInfo() {
        jTextFieldCodigo.setText("");
        jTextFieldDniCuit.setText("");
        jLabelCliente.setText("");
        jLabelDireccion.setText("");
        jLabelCondicion.setText("");
        jLabelCiudad.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabelInvoiceCategory = new javax.swing.JLabel();
        jComboBoxInvoiceCategory = new javax.swing.JComboBox();
        jLabelCiudad = new javax.swing.JLabel();
        jLabelDireccion = new javax.swing.JLabel();
        jLabelCondicion = new javax.swing.JLabel();
        jTextFieldDniCuit = new javax.swing.JTextField();
        jButtonSearchClient = new javax.swing.JButton();
        jComboBoxTipoCompro = new javax.swing.JComboBox();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jTextFieldArticulo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonBorrarItem = new javax.swing.JButton();
        jButtonBorrarTodo = new javax.swing.JButton();
        jButtonGuardar = new javax.swing.JButton();
        jButtonCancelar = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldSubtotal = new javax.swing.JLabel();
        jTextFieldIva21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextFieldIva105 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelNumeroComprobante = new javax.swing.JLabel();
        jComboBoxPtoVenta = new javax.swing.JComboBox();
        jLabelCliente = new javax.swing.JLabel();
        jTextFieldCodigo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldAsociado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxInvoiceIssuerCuit = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(1200, 620));
        setPreferredSize(new java.awt.Dimension(1200, 620));
        getContentPane().setLayout(null);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Cliente");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(20, 60, 125, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Dirección");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(20, 120, 125, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Dni/Cuit");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(220, 20, 60, 20);

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Condicion");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(20, 150, 125, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Buscar Articulo");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(30, 210, 125, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Fecha");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(820, 100, 125, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Tipo Comprobante");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(820, 130, 125, 20);

        jLabel23.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Cbte Asociado");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(820, 190, 125, 20);

        jLabelInvoiceCategory.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelInvoiceCategory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelInvoiceCategory.setText("Categoría");
        getContentPane().add(jLabelInvoiceCategory);
        jLabelInvoiceCategory.setBounds(820, 220, 125, 20);

        jComboBoxInvoiceCategory.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jComboBoxInvoiceCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInvoiceCategoryActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxInvoiceCategory);
        jComboBoxInvoiceCategory.setBounds(970, 220, 140, 21);

        jLabelCiudad.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCiudad);
        jLabelCiudad.setBounds(170, 90, 230, 20);

        jLabelDireccion.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDireccion);
        jLabelDireccion.setBounds(170, 120, 230, 20);

        jLabelCondicion.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCondicion);
        jLabelCondicion.setBounds(170, 150, 230, 20);

        jTextFieldDniCuit.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldDniCuit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldDniCuitKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldDniCuit);
        jTextFieldDniCuit.setBounds(290, 20, 180, 20);

        jButtonSearchClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonSearchClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchClientActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSearchClient);
        jButtonSearchClient.setBounds(480, 20, 25, 25);

        jComboBoxTipoCompro.setEditable(true);
        jComboBoxTipoCompro.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jComboBoxTipoCompro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTipoComproItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBoxTipoCompro);
        jComboBoxTipoCompro.setBounds(970, 130, 140, 21);
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(970, 100, 110, 22);

        jTextFieldArticulo.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextFieldArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldArticuloActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldArticulo);
        jTextFieldArticulo.setBounds(170, 210, 340, 21);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripción", "Cantidad", "Precio", "Bonif", "%Iva", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(40, 250, 1110, 170);

        jButtonBorrarItem.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonBorrarItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButtonBorrarItem.setText("Borrar Item");
        jButtonBorrarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBorrarItemActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonBorrarItem);
        jButtonBorrarItem.setBounds(130, 440, 150, 30);

        jButtonBorrarTodo.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonBorrarTodo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar items.png"))); // NOI18N
        jButtonBorrarTodo.setText("Borrar Todo");
        jButtonBorrarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBorrarTodoActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonBorrarTodo);
        jButtonBorrarTodo.setBounds(290, 440, 150, 30);

        jButtonGuardar.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonGuardar);
        jButtonGuardar.setBounds(130, 500, 150, 30);

        jButtonCancelar.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancelar);
        jButtonCancelar.setBounds(290, 500, 150, 30);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(520, 210, 25, 25);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Subtotal:");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(10, 10, 100, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Iva 21:");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(20, 40, 90, 20);

        jTextFieldSubtotal.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jTextFieldSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel2.add(jTextFieldSubtotal);
        jTextFieldSubtotal.setBounds(130, 10, 210, 20);

        jTextFieldIva21.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jTextFieldIva21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel2.add(jTextFieldIva21);
        jTextFieldIva21.setBounds(130, 40, 210, 20);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Total:");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(20, 100, 90, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 153, 51));
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel2.add(jLabelTotal);
        jLabelTotal.setBounds(130, 100, 210, 20);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Iva 10.5:");
        jPanel2.add(jLabel25);
        jLabel25.setBounds(20, 70, 90, 20);

        jTextFieldIva105.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jTextFieldIva105.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel2.add(jTextFieldIva105);
        jTextFieldIva105.setBounds(130, 70, 210, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(790, 440, 350, 130);

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("-");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(970, 20, 20, 30);

        jLabelNumeroComprobante.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        getContentPane().add(jLabelNumeroComprobante);
        jLabelNumeroComprobante.setBounds(1000, 20, 120, 30);

        jComboBoxPtoVenta.setEditable(false);
        jComboBoxPtoVenta.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jComboBoxPtoVenta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxPtoVentaItemStateChanged(evt);
            }
        });
        getContentPane().add(jComboBoxPtoVenta);
        jComboBoxPtoVenta.setBounds(900, 20, 70, 30);

        jLabelPointOfSaleDescription = new javax.swing.JLabel();
        jLabelPointOfSaleDescription.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabelPointOfSaleDescription.setForeground(new java.awt.Color(102, 102, 102));
        jLabelPointOfSaleDescription.setText("");
        getContentPane().add(jLabelPointOfSaleDescription);
        jLabelPointOfSaleDescription.setBounds(900, 55, 270, 20);

        jLabelCliente.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCliente);
        jLabelCliente.setBounds(170, 60, 330, 20);

        jTextFieldCodigo.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCodigoKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldCodigo);
        jTextFieldCodigo.setBounds(160, 20, 50, 20);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Ciudad");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(20, 90, 125, 20);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Cod Cliente");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(20, 20, 125, 20);
        getContentPane().add(jTextFieldAsociado);
        jTextFieldAsociado.setBounds(970, 190, 140, 22);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Cuit emisor");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(830, 160, 125, 20);

        jComboBoxInvoiceIssuerCuit.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxInvoiceIssuerCuit);
        jComboBoxInvoiceIssuerCuit.setBounds(970, 160, 180, 21);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private static void setModelTable() {
        TableUtils.configureClientInvoiceInsertViewTable(jTable1);
        jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jTable1.setSurrendersFocusOnKeystroke(true);
        installInvoiceEditors();
        if (jTable1.getColumnModel().getColumnCount() > 5) {
            if (activeInstance != null && activeInstance.isInvoiceTableEditable()) {
                JComboBox<Float> vatComboBox = new JComboBox<>(VAT_OPTIONS);
                jTable1.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(vatComboBox));
            } else {
                jTable1.getColumnModel().getColumn(5).setCellEditor(null);
            }
        }
        TableUtils.applyDecimalRenderer(jTable1, DECIMAL_COLUMNS, DISPLAY_SCALE, DISPLAY_ROUNDING_MODE);
        if (activeInstance != null) {
            activeInstance.attachInvoiceTableListener();
        }
    }

    private static void installInvoiceEditors() {
        if (jTable1 == null) {
            return;
        }
        installInvoiceNumericEditor(2);
        installInvoiceNumericEditor(3);
        installInvoiceNumericEditor(4);
    }

    private static void installInvoiceNumericEditor(int columnIndex) {
        if (jTable1.getColumnModel().getColumnCount() <= columnIndex) {
            return;
        }
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.RIGHT);
        DefaultCellEditor editor = new DefaultCellEditor(field) {
            @Override
            public boolean stopCellEditing() {
                int editingRow = jTable1.getEditingRow();
                boolean stopped = super.stopCellEditing();
                if (stopped) {
                    SwingUtilities.invokeLater(() -> handleInvoiceEditorCommit(editingRow));
                }
                return stopped;
            }
        };
        editor.setClickCountToStart(1);
        jTable1.getColumnModel().getColumn(columnIndex).setCellEditor(editor);
    }

    private static void handleInvoiceEditorCommit(int editingRow) {
        if (activeInstance == null) {
            return;
        }
        int targetRow = editingRow >= 0 ? editingRow : jTable1.getSelectedRow();
        if (targetRow >= 0) {
            activeInstance.updateInvoiceRow(targetRow);
        }
        SumarComprobante();
    }

    private boolean isInvoiceTableEditable() {
        return !loadedFromBudget;
    }

    private void setBudgetMode(boolean budgetMode) {
        loadedFromBudget = budgetMode;
        boolean enableEditing = !budgetMode;
        jTable1.setEnabled(enableEditing);
        jButtonBorrarItem.setEnabled(enableEditing);
        jButtonBorrarTodo.setEnabled(enableEditing);
        jButton6.setEnabled(enableEditing);
    }

    private static DecimalFormat createDisplayDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        StringBuilder pattern = new StringBuilder("#,##0");
        if (DISPLAY_SCALE > 0) {
            pattern.append('.');
            for (int i = 0; i < DISPLAY_SCALE; i++) {
                pattern.append('0');
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat(pattern.toString(), symbols);
        decimalFormat.setRoundingMode(DISPLAY_ROUNDING_MODE);
        decimalFormat.setGroupingUsed(true);
        return decimalFormat;
    }

    private static String formatAmount(BigDecimal value) {
        BigDecimal safeValue = value == null ? BigDecimal.ZERO : value;
        BigDecimal scaled = safeValue.setScale(DISPLAY_SCALE, DISPLAY_ROUNDING_MODE);
        synchronized (DISPLAY_DECIMAL_FORMAT) {
            return DISPLAY_DECIMAL_FORMAT.format(scaled);
        }
    }

    private static BigDecimal parseAmountLabel(javax.swing.JLabel label) {
        if (label == null) {
            return BigDecimal.ZERO;
        }

        String text = label.getText();
        if (text == null) {
            return BigDecimal.ZERO;
        }

        String normalized = text.replace(".", "").replace(',', '.').trim();
        if (normalized.isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "No se pudo convertir el valor formateado a BigDecimal: {0}", text);
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        try {
            String text = value.toString().trim();
            if (text.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "No se pudo convertir el valor a BigDecimal: {0}", value);
            return BigDecimal.ZERO;
        }
    }

    private void logInvoiceWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    private void logInvoiceError(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    private void logInvoiceError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }

    private void attachInvoiceTableListener() {
        if (invoiceTableModelListener == null) {
            invoiceTableModelListener = this::onInvoiceTableChanged;
        }

        TableModel model = jTable1.getModel();
        if (currentInvoiceTableModel == model) {
            return;
        }

        if (currentInvoiceTableModel != null) {
            currentInvoiceTableModel.removeTableModelListener(invoiceTableModelListener);
        }

        currentInvoiceTableModel = model;
        currentInvoiceTableModel.addTableModelListener(invoiceTableModelListener);
    }

    private void commitInvoiceTableEdits() {
        if (jTable1 != null && jTable1.isEditing()) {
            TableCellEditor editor = jTable1.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    private void onInvoiceTableChanged(TableModelEvent event) {
        if (adjustingInvoiceTable) {
            return;
        }

        int type = event.getType();
        if (type == TableModelEvent.UPDATE) {
            int column = event.getColumn();
            if (column == TableModelEvent.ALL_COLUMNS) {
                SumarComprobante();
                return;
            }

            if (column >= 2 && column <= 5) {
                int row = event.getFirstRow();
                updateInvoiceRow(row);
            }
        } else if (type == TableModelEvent.INSERT || type == TableModelEvent.DELETE) {
            SumarComprobante();
        }
    }

    private void updateInvoiceRow(int row) {
        if (row < 0 || row >= jTable1.getRowCount()) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        BigDecimal quantity = parseBigDecimal(model.getValueAt(row, 2));
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            quantity = BigDecimal.ZERO;
            setTableCellValue(model, row, 2, quantity);
        }

        BigDecimal price = parseBigDecimal(model.getValueAt(row, 3));
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            price = BigDecimal.ZERO;
            setTableCellValue(model, row, 3, price);
        }

        BigDecimal discount = parseBigDecimal(model.getValueAt(row, 4));
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
            setTableCellValue(model, row, 4, discount);
        }

        String code = getStringValue(model.getValueAt(row, 0));
        BigDecimal vatPercent = parseBigDecimal(model.getValueAt(row, 5));

        quantity = enforceStockLimit(model, row, quantity);

        BigDecimal subtotal = calculateInvoiceSubtotal(code, quantity, price, discount, vatPercent);
        setTableCellValue(model, row, 6, subtotal);

        SumarComprobante();
    }

    private BigDecimal calculateInvoiceSubtotal(String code, BigDecimal quantity, BigDecimal price, BigDecimal discount, BigDecimal vatPercent) {
        BigDecimal basePrice = "99".equals(code) ? calculateNetFromGross(price, vatPercent) : price;
        BigDecimal priceWithDiscount = applyDiscount(basePrice, discount);
        return quantity.multiply(priceWithDiscount);
    }

    private BigDecimal applyDiscount(BigDecimal basePrice, BigDecimal discountPercent) {
        BigDecimal price = basePrice != null ? basePrice : BigDecimal.ZERO;
        BigDecimal discount = discountPercent != null ? discountPercent : BigDecimal.ZERO;
        BigDecimal discountFactor = discount.movePointLeft(2);
        BigDecimal discounted = price.subtract(price.multiply(discountFactor));
        if (discounted.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return discounted;
    }

    private static final BigDecimal SUBTOTAL_TOLERANCE = new BigDecimal("0.01");

    private BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : normalized;
    }

    private boolean isSubtotalWithinTolerance(BigDecimal expected, BigDecimal actual) {
        BigDecimal normalizedExpected = normalize(expected);
        BigDecimal normalizedActual = normalize(actual);
        BigDecimal roundedExpected = normalizedExpected.setScale(DISPLAY_SCALE, DISPLAY_ROUNDING_MODE);
        BigDecimal roundedActual = normalizedActual.setScale(DISPLAY_SCALE, DISPLAY_ROUNDING_MODE);
        BigDecimal difference = roundedExpected.subtract(roundedActual).abs();
        return difference.compareTo(SUBTOTAL_TOLERANCE) <= 0;
    }

    private BigDecimal enforceStockLimit(DefaultTableModel model, int row, BigDecimal desiredQuantity) {
        if (esNota()) {
            return desiredQuantity;
        }

        String code = getStringValue(model.getValueAt(row, 0));
        if (code.isEmpty() || "99".equals(code)) {
            return desiredQuantity;
        }

        try {
            Product product = productController.findByCode(code);
            if (product == null) {
                return desiredQuantity;
            }

            float stockValue = product.getStockQuantity() == null ? 0f : product.getStockQuantity();
            BigDecimal stock = BigDecimal.valueOf(stockValue);
            BigDecimal otherRows = sumQuantitiesForProduct(code, row);
            BigDecimal maxAllowed = stock.subtract(otherRows);
            if (maxAllowed.compareTo(BigDecimal.ZERO) < 0) {
                maxAllowed = BigDecimal.ZERO;
            }

            if (desiredQuantity.compareTo(maxAllowed) > 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad que ha seleccionado es mayor que el stock disponible! Articulo: " + product.getCode(),
                        "Atencion", JOptionPane.WARNING_MESSAGE);
                setTableCellValue(model, row, 2, maxAllowed);
                return maxAllowed;
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return desiredQuantity;
    }

    private void setTableCellValue(DefaultTableModel model, int row, int column, Object value) {
        adjustingInvoiceTable = true;
        try {
            model.setValueAt(value, row, column);
        } finally {
            adjustingInvoiceTable = false;
        }
    }

    private BigDecimal parseBigDecimal(Object value) {
        return toBigDecimal(value);
    }

    private BigDecimal calculateNetFromGross(BigDecimal grossPrice, BigDecimal vatPercent) {
        BigDecimal gross = grossPrice != null ? grossPrice : BigDecimal.ZERO;
        BigDecimal vat = vatPercent != null ? vatPercent : BigDecimal.ZERO;
        BigDecimal divisor = BigDecimal.ONE.add(vat.movePointLeft(2));
        if (divisor.compareTo(BigDecimal.ZERO) <= 0) {
            return gross;
        }
        return gross.divide(divisor, CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);
    }

    private BigDecimal sumQuantitiesForProduct(String code) {
        return sumQuantitiesForProduct(code, -1);
    }

    private BigDecimal sumQuantitiesForProduct(String code, int rowToExclude) {
        if (code == null || code.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (i == rowToExclude) {
                continue;
            }
            if (code.equals(getStringValue(jTable1.getValueAt(i, 0)))) {
                total = total.add(parseBigDecimal(jTable1.getValueAt(i, 2)));
            }
        }
        return total;
    }

    private boolean isProductAlreadyInInvoice(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (code.equals(getStringValue(jTable1.getValueAt(i, 0)))) {
                return true;
            }
        }

        return false;
    }

    private String getStringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private void configureIssuerCuitCombo() {
        CuitSelectorUtils.populateCuits(jComboBoxInvoiceIssuerCuit);
        jComboBoxInvoiceIssuerCuit.addItemListener(evt -> {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                loadPointOfSales();
                updateInvoiceNumber();
            }
        });
    }

    private boolean isInvoiceTypeCDisabledForIssuer(String issuerCuit) {
        if (issuerCuit == null || issuerCuit.isBlank()) {
            return false;
        }
        String configured = AppConfig.get(TYPE_C_DISABLED_CUITS_PROPERTY, "");
        if (configured == null || configured.isBlank()) {
            return false;
        }
        String normalizedIssuer = DocumentValidator.normalizeCuit(issuerCuit.trim());
        if (normalizedIssuer == null || normalizedIssuer.isBlank()) {
            return false;
        }
        String[] values = configured.split(",");
        for (String raw : values) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String normalized = DocumentValidator.normalizeCuit(raw.trim());
            if (normalized != null && !normalized.isBlank() && normalized.equals(normalizedIssuer)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInvoiceTypeCOption(String option) {
        if (option == null) {
            return false;
        }
        String normalized = InvoiceTypeUtils.toStorageValue(option);
        return !normalized.isEmpty() && normalized.equalsIgnoreCase(Constants.FACTURA_C_ABBR);
    }

    private void captureInvoiceTypeOptions() {
        int count = jComboBoxTipoCompro.getItemCount();
        List<String> options = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Object item = jComboBoxTipoCompro.getItemAt(i);
            options.add(item != null ? item.toString() : null);
        }
        allInvoiceTypeOptions = options;
    }

    private void updateInvoiceTypeOptionsForPointOfSale() {
        if (allInvoiceTypeOptions == null || allInvoiceTypeOptions.isEmpty()) {
            captureInvoiceTypeOptions();
        }

        Object previousSelection = jComboBoxTipoCompro.getSelectedItem();
        String previousNormalized = InvoiceTypeUtils.toStorageValue(previousSelection != null ? previousSelection.toString() : "");
        String pointOfSale = getSelectedPointOfSale();
        boolean manualOnly = isPointOfSaleZero(pointOfSale);
        boolean disableInvoiceC = isInvoiceTypeCDisabledForIssuer(getSelectedIssuerCuit());

        jComboBoxTipoCompro.removeAllItems();

        Set<String> allowedLetters = InvoiceEmissionConfig.getAllowedLetters(getSelectedIssuerCuit());

        for (String option : allInvoiceTypeOptions) {
            if (option == null) {
                continue;
            }

            if (isPlaceholderOption(option)) {
                jComboBoxTipoCompro.addItem(option);
                continue;
            }

            if (!isInvoiceTypeAllowedForIssuer(option, allowedLetters)) {
                continue;
            }

            boolean manualOption = isManualInvoiceType(option);
            if ((manualOnly && manualOption) || (!manualOnly && !manualOption)) {
                jComboBoxTipoCompro.addItem(option);
            }
        }

        if (!previousNormalized.isEmpty() && !selectInvoiceTypeByNormalizedValue(previousNormalized)) {
            previousNormalized = "";
        }

        if (previousNormalized.isEmpty() && manualOnly) {
            if (!selectInvoiceTypeByNormalizedValue(Constants.PRESUPUESTO_ABBR)) {
                selectInvoiceTypeByNormalizedValue(Constants.NOTA_DEVOLUCION_ABBR);
            }
        }

        if (jComboBoxTipoCompro.getSelectedItem() == null && jComboBoxTipoCompro.getItemCount() > 0) {
            jComboBoxTipoCompro.setSelectedIndex(0);
        }

        updateAssociatedInvoiceComponentsVisibility();
    }

    private boolean selectInvoiceTypeByNormalizedValue(String normalizedValue) {
        if (normalizedValue == null || normalizedValue.isEmpty()) {
            return false;
        }
        for (int i = 0; i < jComboBoxTipoCompro.getItemCount(); i++) {
            Object item = jComboBoxTipoCompro.getItemAt(i);
            String optionNormalized = InvoiceTypeUtils.toStorageValue(item != null ? item.toString() : "");
            if (normalizedValue.equals(optionNormalized)) {
                jComboBoxTipoCompro.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private boolean isInvoiceTypeAllowedForIssuer(String option, Set<String> allowedLetters) {
        if (option == null) {
            return false;
        }
        if (allowedLetters == null || allowedLetters.isEmpty()) {
            return true;
        }
        String documentLetter = InvoiceTypeUtils.findDocumentLetter(option);
        if (documentLetter.isEmpty()) {
            return true;
        }
        return allowedLetters.contains(documentLetter);
    }

    private boolean isManualInvoiceType(String description) {
        String normalized = InvoiceTypeUtils.toStorageValue(description != null ? description : "");
        return Constants.PRESUPUESTO_ABBR.equals(normalized) || Constants.NOTA_DEVOLUCION_ABBR.equals(normalized);
    }

    private boolean isPointOfSaleZero(String pointOfSale) {
        String normalized = normalizePointOfSale(pointOfSale);
        return normalized != null && "0".equals(normalized);
    }

    private boolean isPlaceholderOption(String option) {
        return option != null && option.equals(INVOICE_TYPE_PLACEHOLDER);
    }

    private void loadPointOfSales() {
        PointOfSaleCuitResolver.PointOfSaleConfiguration configuration = PointOfSaleCuitResolver.loadConfiguration();
        pointOfSaleIssuerCuits = configuration.getPointToCuitMap();

        List<PointOfSaleCuitResolver.PointOfSaleOption> options = configuration.getPointsForCuit(getSelectedIssuerCuit());

        Map<String, String> descriptions = new LinkedHashMap<>();
        Set<String> availablePoints = new LinkedHashSet<>();

        jComboBoxPtoVenta.removeAllItems();
        for (PointOfSaleCuitResolver.PointOfSaleOption option : options) {
            String code = option.getCode();
            jComboBoxPtoVenta.addItem(code);
            availablePoints.add(code);
            String description = option.getDescription();
            if (description != null && !description.isBlank()) {
                descriptions.put(code, description);
            }
        }

        pointOfSaleDescriptions = descriptions.isEmpty() ? Collections.emptyMap() : descriptions;

        String defaultPoint = formatPointOfSale(AppConfig.get("pos.default", "0"));
        if (defaultPoint != null && availablePoints.contains(defaultPoint)) {
            jComboBoxPtoVenta.setSelectedItem(defaultPoint);
        } else if (!availablePoints.isEmpty()) {
            jComboBoxPtoVenta.setSelectedIndex(0);
        }

        updatePointOfSaleDescription();
        updateIssuerCuitForCurrentPointOfSale();
        updateInvoiceTypeOptionsForPointOfSale();
    }

    private String formatPointOfSale(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return String.format("%04d", Integer.parseInt(trimmed));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void updateIssuerCuitForCurrentPointOfSale() {
        updateIssuerCuitForPointOfSale(getSelectedPointOfSale());
    }

    private void updateIssuerCuitForPointOfSale(String pointOfSale) {
        if (pointOfSale == null) {
            jComboBoxInvoiceIssuerCuit.setEnabled(true);
            return;
        }

        if ("0000".equals(pointOfSale)) {
            jComboBoxInvoiceIssuerCuit.setEnabled(true);
            return;
        }

        String configuredCuit = pointOfSaleIssuerCuits.get(pointOfSale);
        if (configuredCuit != null && !configuredCuit.isBlank()) {
            CuitSelectorUtils.selectCuit(jComboBoxInvoiceIssuerCuit, configuredCuit);
            jComboBoxInvoiceIssuerCuit.setEnabled(false);
        } else {
            LOGGER.log(Level.WARNING, "No se encontró un CUIT configurado para el punto de venta {0}", pointOfSale);
            jComboBoxInvoiceIssuerCuit.setEnabled(true);
        }
    }

    private void updatePointOfSaleDescription() {
        if (jLabelPointOfSaleDescription == null) {
            return;
        }

        String point = getSelectedPointOfSale();
        if (point == null) {
            jLabelPointOfSaleDescription.setText("");
            return;
        }

        String description = pointOfSaleDescriptions.get(point);
        jLabelPointOfSaleDescription.setText(description == null ? "" : description);
    }

    private String getSelectedPointOfSale() {
        Object selected = jComboBoxPtoVenta.getSelectedItem();
        return selected != null ? formatPointOfSale(selected.toString()) : null;
    }

    private void updateInvoiceNumber() {
        String pointOfSale = jComboBoxPtoVenta.getSelectedItem() != null
                ? jComboBoxPtoVenta.getSelectedItem().toString()
                : "0000";
        int nextNumber = getLastInvoiceNumber(pointOfSale, getSelectedInvoiceType()) + 1;
        jLabelNumeroComprobante.setText(String.format("%08d", nextNumber));
    }

    private String getSelectedIssuerCuit() {
        return CuitSelectorUtils.getSelectedCuit(jComboBoxInvoiceIssuerCuit);
    }

    private String getSelectedInvoiceType() {
        Object selectedType = jComboBoxTipoCompro.getSelectedItem();
        String normalized = InvoiceTypeUtils.toStorageValue(selectedType != null ? selectedType.toString() : "");
        return normalized.isEmpty() ? null : normalized;
    }

    private int getLastInvoiceNumber(String pointOfSale, String invoiceType) {
        return clientInvoiceController.findAll().stream()
                .filter(inv -> matchesPointOfSale(pointOfSale, inv.getPointOfSale()))
                .filter(inv -> invoiceType == null
                || invoiceType.equals(InvoiceTypeUtils.toStorageValue(inv.getInvoiceType())))
                .mapToInt(inv -> {
                    try {
                        return Integer.parseInt(inv.getInvoiceNumber());
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);
    }

    private boolean matchesPointOfSale(String expected, String actual) {
        String normalizedExpected = normalizePointOfSale(expected);
        String normalizedActual = normalizePointOfSale(actual);

        if (normalizedExpected != null && normalizedActual != null) {
            return normalizedExpected.equals(normalizedActual);
        }

        if (expected == null || actual == null) {
            return expected == null && actual == null;
        }

        return expected.trim().equals(actual.trim());
    }

    private String normalizePointOfSale(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return Integer.toString(Integer.parseInt(trimmed));
        } catch (NumberFormatException ex) {
            return trimmed;
        }
    }

    private DefaultTableModel createModelFromRemit(List<ClientRemitDetail> details) throws Exception {
        final DefaultTableModel tm = new DefaultTableModel(
                new String[]{"Codigo", "Detalle", "Cantidad", "NetoGravado", "Bonif", "IVA", "Subtotal"}, 0
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (ClientRemitDetail r : details) {
            Product p = productController.findByCode(r.getProductCode());
            Float productVat = p != null ? p.getVatRate() : null;
            float iva = normalizeVat(productVat);
            BigDecimal precioConIva = r.getPrice();
            BigDecimal netoGravado;
            if (iva == 21.0f) {
                netoGravado = precioConIva.divide(new BigDecimal("1.21"), CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);
            } else if (iva == 10.5f) {
                netoGravado = precioConIva.divide(new BigDecimal("1.105"), CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);
            } else {
                netoGravado = precioConIva;
            }

            Vector<Object> fila = new Vector<>();
            fila.add(r.getProductCode());
            fila.add(r.getDescription());
            fila.add(r.getQuantity());
            fila.add(netoGravado);
            fila.add(0);
            fila.add(iva);
            fila.add(netoGravado.multiply(BigDecimal.valueOf(r.getQuantity())));
            tm.addRow(fila);
        }
        return tm;
    }

    private DefaultTableModel createModelFromBudget(List<ClientBudgetDetail> details) throws Exception {
        final DefaultTableModel tm = new DefaultTableModel(
                new String[]{"Codigo", "Detalle", "Cantidad", "NetoGravado", "Bonif", "IVA", "Subtotal"}, 0
        ) {
            boolean[] canEdit = new boolean[]{false, true, true, true, true, true, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (ClientBudgetDetail r : details) {
            Product p = productController.findByCode(r.getProductCode());
            Float productVat = p != null ? p.getVatRate() : null;
            float iva = normalizeVat(productVat);
            BigDecimal precioConIva = r.getPrice();
            BigDecimal netoGravado;
            if (iva == 21.0f) {
                netoGravado = precioConIva.divide(new BigDecimal("1.21"), CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);
            } else if (iva == 10.5f) {
                netoGravado = precioConIva.divide(new BigDecimal("1.105"), CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);
            } else {
                netoGravado = precioConIva;
            }
            Vector<Object> fila = new Vector<>();
            fila.add(r.getProductCode());
            fila.add(r.getDescription());
            fila.add(r.getQuantity());
            fila.add(netoGravado);
            fila.add(0);
            fila.add(iva);
            fila.add(netoGravado.multiply(BigDecimal.valueOf(r.getQuantity())));
            tm.addRow(fila);
        }
        return tm;
    }

    public static DefaultTableModel AgregarProducto(Product product, DefaultTableModel tm) {
        try {
            Vector<Object> fila = new Vector<>();
            fila.add(product.getCode());
            fila.add(product.getDescription());
            float cant = 1f;
            fila.add(cant); // cantidad inicial en 1

            BigDecimal precioFinal = product.getCashPrice() != null ? product.getCashPrice() : BigDecimal.ZERO;
            float iva = normalizeVat(product.getVatRate());

            // Precio neto (sin IVA)
            BigDecimal ivaFraction = BigDecimal.valueOf(iva).movePointLeft(2);
            BigDecimal divisor = BigDecimal.ONE.add(ivaFraction);
            BigDecimal precioNeto = precioFinal.divide(divisor, CALCULATION_SCALE, DISPLAY_ROUNDING_MODE);

            fila.add(precioNeto); // mostrar neto SIEMPRE
            fila.add(0); // bonif
            fila.add(iva);
            BigDecimal subtotal = BigDecimal.valueOf(cant).multiply(precioNeto);
            fila.add(subtotal);

            tm.addRow(fila);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm;
    }

    private DefaultTableModel ModificarArticulo(DefaultTableModel tm, int i) {
        if (tm == null || i < 0 || i >= tm.getRowCount()) {
            return tm;
        }
        try {
            BigDecimal precioPantalla = new BigDecimal(jTable1.getValueAt(i, 3).toString());
            BigDecimal bonificacion = new BigDecimal(jTable1.getValueAt(i, 4).toString());
            BigDecimal iva = new BigDecimal(jTable1.getValueAt(i, 5).toString());
            BigDecimal cantidad = new BigDecimal(jTable1.getValueAt(i, 2).toString());

            BigDecimal precio = precioPantalla;

            if (bonificacion.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal bon = bonificacion.movePointLeft(2);
                precio = precio.subtract(precio.multiply(bon));
            }

            BigDecimal subtotal = cantidad.multiply(precio);
            tm.setValueAt(subtotal, i, 6);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm;
    }

    public static DefaultTableModel AgregarLineaVacia(DefaultTableModel tm) {
        try {
            if (activeInstance != null) {
                activeInstance.setBudgetMode(false);
            }

            float iva = 21f;

            Vector fila = null;
            fila = new Vector();

            fila.add("99");

            fila.add("");
            float cant = 1;
            fila.add(cant);

            fila.add(BigDecimal.ZERO);
            fila.add(0);

            fila.add(iva);
            fila.add(BigDecimal.ZERO);

            tm.addRow(fila);
            jTable1.changeSelection(0, 3, false, false);

        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm;

    }

    private static DefaultTableModel createEmptyInvoiceModel() throws Exception {

        final DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Detalle", "Cantidad", "Precio", "Bonif", "IVA", "Subtotal"}, 0) {
            boolean[] canEdit = new boolean[]{
                false, true, true, true, true, true, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

        };

        return tm;
    }

    public static void SumarComprobante() {
        BigDecimal sub21 = BigDecimal.ZERO;
        BigDecimal sub105 = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal i21 = BigDecimal.ZERO;
        BigDecimal i105 = BigDecimal.ZERO;

        int cantFilas = jTable1.getRowCount();

        for (int j = 0; j < cantFilas; j++) {
            BigDecimal iva = toBigDecimal(jTable1.getValueAt(j, 5));
            BigDecimal sub = toBigDecimal(jTable1.getValueAt(j, 6));

            if (iva.compareTo(new BigDecimal("21.00")) == 0) {
                sub21 = sub21.add(sub);
            } else if (iva.compareTo(new BigDecimal("10.5")) == 0) {
                sub105 = sub105.add(sub);
            }
        }

        i21 = sub21.multiply(new BigDecimal("0.21"));
        i105 = sub105.multiply(new BigDecimal("0.105"));

        subtotal = sub21.add(sub105);

        jTextFieldSubtotal.setText(formatAmount(subtotal));
        jTextFieldIva21.setText(formatAmount(i21));
        jTextFieldIva105.setText(formatAmount(i105));
        BigDecimal total = subtotal.add(i21).add(i105);
        jLabelTotal.setText(formatAmount(total));
    }

    public static void limpiarFormulario() {
        try {
            jTextFieldDniCuit.setText("");
            jTextFieldArticulo.setText("");
            jLabelCiudad.setText("");
            jLabelCondicion.setText("");
            jTextFieldCodigo.setText("");
            jLabelDireccion.setText("");
            jLabelNumeroComprobante.setText("");
            jTextFieldSubtotal.setText("");
            jTextFieldIva21.setText("");
            jTextFieldIva105.setText("");
            jLabelTotal.setText("");
            jComboBoxTipoCompro.setSelectedItem(Constants.PRESUPUESTO);
            jDateChooser1.getDate();

            jTable1.setModel(createEmptyInvoiceModel());
            setModelTable();
            if (activeInstance != null) {
                activeInstance.updateInvoiceNumber();
                activeInstance.setBudgetMode(false);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    private void jButtonSearchClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchClientActionPerformed
        if (!ClientManagementView.isOpen) {
            ClientManagementView view = new ClientManagementView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
        ClientManagementView.setClientSelectionListener(this::handleClientSelectedFromSearch, true);
        ClientManagementView.bringToFront();
    }//GEN-LAST:event_jButtonSearchClientActionPerformed

    private void jTextFieldDniCuitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDniCuitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                Client client = clientController.findByDocument(DocumentValidator.normalizeCuit(jTextFieldDniCuit.getText()));
                if (client != null) {
                    if (ClientInvoiceInsertView.isOpen) {
                        populateClientInfo(client);
                        java.util.Date fecha = new Date();
                        jDateChooser1.setDate(fecha);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se ha encontrado cliente!");
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}//GEN-LAST:event_jTextFieldDniCuitKeyPressed

    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        int cant = 0;
        cant = jTable1.getRowCount();

        if (cant == 0) {

            isOpen = false;
            dispose();
        } else {
            int k = JOptionPane.showConfirmDialog(this, "Hay facturacion sin guardar ¿Esta seguro que desea salir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (k != 1) {
                isOpen = false;
                dispose();
            }

        }
    }//GEN-LAST:event_jButtonCancelarActionPerformed

    private void jButtonBorrarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBorrarTodoActionPerformed
        try {

            jTable1.setModel(createEmptyInvoiceModel());
            setModelTable();
            setBudgetMode(false);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel20.setText("");
    }//GEN-LAST:event_jButtonBorrarTodoActionPerformed

    private void jButtonBorrarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBorrarItemActionPerformed
        int i = jTable1.getSelectedRow();

        if (i == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun articulo", "Facturacion", JOptionPane.OK_OPTION);
        } else {
            try {

                DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
                tm.removeRow(i);
                jTable1.setModel(tm);

                jTable1.requestFocus();

                ClientInvoiceInsertView.SumarComprobante();
                setModelTable();

            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonBorrarItemActionPerformed

    public static boolean estaCargado(String cod) {

        int i = 0;
        int cantFilas = 0;

        boolean cargado = false;

        cantFilas = ClientInvoiceInsertView.jTable1.getRowCount();

        while (i < cantFilas) {

            String codCargado = "";
            codCargado = (ClientInvoiceInsertView.jTable1.getValueAt(i, 0).toString());

            if (codCargado.equals(cod)) {

                cargado = true;
                i = cantFilas;
            }

            i++;

        }

        return (cargado);
    }

    private void handleProductSelectedFromSearch(Product product) {
        addProductToInvoice(product);
    }

    private void handleClientSelectedFromSearch(Client client) {
        if (client != null) {
            populateClientInfo(client);
            jDateChooser1.setDate(new Date());
        }
    }

    private boolean addProductToInvoice(Product product) {
        if (product == null) {
            return false;
        }

        setBudgetMode(false);

        if (isProductAlreadyInInvoice(product.getCode())) {
            JOptionPane.showMessageDialog(this,
                    "El articulo seleccionado ya esta cargado. Por favor actualice su cantidad.",
                    "Atencion",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        if (!esNota()) {
            float stockValue = product.getStockQuantity() == null ? 0f : product.getStockQuantity();
            if (stockValue <= 0f) {
                JOptionPane.showMessageDialog(this, "El articulo no posee stock", "Atencion", JOptionPane.OK_OPTION);
                return false;
            }

            BigDecimal available = BigDecimal.valueOf(stockValue);
            BigDecimal currentQuantity = sumQuantitiesForProduct(product.getCode());
            if (available.compareTo(currentQuantity) < 0) {
                JOptionPane.showMessageDialog(this, "El articulo no posee stock", "Atencion", JOptionPane.OK_OPTION);
                return false;
            }
        }

        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
        AgregarProducto(product, tm);
        jTable1.setModel(tm);
        SumarComprobante();
        setModelTable();
        jTable1.requestFocus();
        return true;
    }

    private void jTextFieldArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldArticuloActionPerformed
        try {
            String valor = jTextFieldArticulo.getText().trim();
            if (valor.isEmpty()) {
                return;
            }

            if (!"99".equals(valor)) {
                Product product = productController.findByCode(valor);
                if (product == null) {
                    JOptionPane.showMessageDialog(this, "El articulo no existe", "Atencion", JOptionPane.OK_OPTION);
                } else {
                    addProductToInvoice(product);
                }
            } else {
                DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
                AgregarLineaVacia(tm);
                jTable1.setModel(tm);
                SumarComprobante();
                setModelTable();
            }

            jTextFieldArticulo.requestFocus();
            jTextFieldArticulo.setText("");
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTextFieldArticuloActionPerformed

    private void jComboBoxTipoComproItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTipoComproItemStateChanged

        if (evt.getSource() == jComboBoxTipoCompro && evt.getStateChange() == ItemEvent.SELECTED) {
            try {
                Object selected = jComboBoxTipoCompro.getSelectedItem();
                String selectedValue = InvoiceTypeUtils.toStorageValue(selected != null ? selected.toString() : "");
                if (Constants.PRESUPUESTO_ABBR.equals(selectedValue) || Constants.NOTA_DEVOLUCION_ABBR.equals(selectedValue)) {
                    jComboBoxPtoVenta.setSelectedItem("0000");
                }

                updateAssociatedInvoiceComponentsVisibility();
                updateInvoiceNumber();

                if (!jTextFieldDniCuit.getText().isEmpty()) {
                    initializeInvoiceContext();
                    SumarComprobante();
                    setModelTable();
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jComboBoxTipoComproItemStateChanged

    private void jComboBoxInvoiceCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInvoiceCategoryActionPerformed
        // Selección gestionada al momento de guardar el comprobante
    }//GEN-LAST:event_jComboBoxInvoiceCategoryActionPerformed

    private DefaultTableModel ModificarPrecio(DefaultTableModel tm, BigDecimal precioPantalla, int i) {
        try {
            BigDecimal bonificacion = new BigDecimal(jTable1.getValueAt(i, 4).toString());
            BigDecimal iva = new BigDecimal(jTable1.getValueAt(i, 5).toString());
            BigDecimal cantidad = new BigDecimal(jTable1.getValueAt(i, 2).toString());

            BigDecimal precio = precioPantalla;

            if (bonificacion.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal bon = bonificacion.movePointLeft(2);
                precio = precio.subtract(precio.multiply(bon));
            }

            BigDecimal subtotal = cantidad.multiply(precio);
            tm.setValueAt(precio, i, 3);
            tm.setValueAt(subtotal, i, 6);

        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm;
    }


    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased

        try {
            int i = jTable1.getSelectedRow();
            if (i >= 0) {
                aplicarModificacion(i);
            }
            setModelTable();
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jTable1KeyReleased

    private boolean esNota() {
        Object selected = jComboBoxTipoCompro.getSelectedItem();
        if (selected == null) {
            return false;
        }
        String storageValue = InvoiceTypeUtils.toStorageValue(selected.toString());
        return Constants.NOTA_DEVOLUCION_ABBR.equals(storageValue)
                || Constants.NOTA_CREDITO_A_ABBR.equals(storageValue)
                || Constants.NOTA_CREDITO_B_ABBR.equals(storageValue)
                || Constants.NOTA_CREDITO_C_ABBR.equals(storageValue);
    }

    private boolean requiresAssociatedInvoiceField() {
        Object selected = jComboBoxTipoCompro.getSelectedItem();
        String selectedValue = selected != null ? selected.toString() : "";
        String storageValue = InvoiceTypeUtils.toStorageValue(selectedValue);
        return InvoiceTypeUtils.isCreditDocument(storageValue)
                || InvoiceTypeUtils.isDebitDocument(storageValue);
    }

    private void updateAssociatedInvoiceComponentsVisibility() {
        boolean visible = requiresAssociatedInvoiceField();
        jLabel23.setVisible(visible);
        jTextFieldAsociado.setVisible(visible);
        if (!visible) {
            jTextFieldAsociado.setText("");
        }
    }

    private void aplicarModificacion(int i) {
        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
        if (tm == null || i < 0 || i >= tm.getRowCount()) {
            return;
        }
        ModificarArticulo(tm, i);
        jTable1.setModel(tm);
        SumarComprobante();
    }

    private boolean saveInvoice() {
        try {
            commitInvoiceTableEdits();
            ClientInvoice invoice = buildInvoiceFromForm();
            if (invoice == null) {
                return false;
            }

            List<ClientInvoiceDetail> details = buildInvoiceDetails(invoice);
            invoice.setDetails(details);

            String invoiceTypeDescription = invoice.getInvoiceType();
            boolean requiresAfip = InvoiceTypeUtils.requiresAfipAuthorization(invoiceTypeDescription);

            InvoiceReference associatedReference = parseAssociatedInvoice(
                    jTextFieldAsociado.getText(), invoice.getPointOfSale());
            if (!associatedReference.isPartial()) {
                invoice.setAssociatedInvoiceNumber(associatedReference.number);
            } else {
                invoice.setAssociatedInvoiceNumber("");
            }

            ClientInvoice associatedInvoice = null;
            if (requiresAfip && associatedReference.hasInput()
                    && !associatedReference.isPartial() && associatedReference.number.isEmpty()) {
                String message = "El número de comprobante asociado es inválido";
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this,
                        message, "AFIP", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (associatedReference.hasInput()) {
                String associatedType = InvoiceTypeUtils.findAssociatedInvoiceType(invoiceTypeDescription);
                if (associatedReference.isPartial()) {
                    Integer clientId = invoice.getClient() != null ? invoice.getClient().getId() : null;
                    associatedInvoice = clientInvoiceController.findByPointOfSaleAndNumberSuffix(
                            associatedReference.pointOfSale, associatedReference.suffix, associatedType, clientId);
                } else {
                    associatedInvoice = clientInvoiceController.findByPointOfSaleAndNumber(
                            associatedReference.pointOfSale, associatedReference.number, associatedType);
                }
                if (associatedInvoice == null) {
                    if (requiresAfip) {
                        String message = "No se encontró el comprobante asociado ingresado";
                        logInvoiceWarning(message);
                        JOptionPane.showMessageDialog(this,
                                message, "AFIP", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                } else {
                    if (!belongsToClient(associatedInvoice, invoice.getClient())) {
                        String message = "El comprobante asociado pertenece a otro cliente";
                        logInvoiceWarning(message);
                        JOptionPane.showMessageDialog(this,
                                message, "AFIP", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    String issuerCuit = getSelectedIssuerCuit();
                    if (issuerCuit != null && associatedInvoice.getIssuerCuit() != null
                            && !DocumentValidator.normalizeCuit(issuerCuit)
                                    .equals(DocumentValidator.normalizeCuit(associatedInvoice.getIssuerCuit()))) {
                        String message = "El comprobante asociado pertenece a otro CUIT emisor";
                        logInvoiceWarning(message);
                        JOptionPane.showMessageDialog(this,
                                message, "AFIP", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                    if (!confirmAssociatedInvoice(associatedInvoice)) {
                        return false;
                    }
                    invoice.setAssociatedInvoiceNumber(associatedInvoice.getInvoiceNumber());
                    jTextFieldAsociado.setText(associatedInvoice.getInvoiceNumber());
                }
            }

            if (requiresAfip) {
                AfipAuthorizationResult result = afipAuthorizationService.authorize(invoice, details, associatedInvoice);
                if (!result.isApproved()) {
                    String message = result.getMessage().isEmpty()
                            ? "AFIP rechazó la autorización del comprobante." : result.getMessage();
                    logInvoiceError(message);
                    JOptionPane.showMessageDialog(this, message, "AFIP", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                String cae = result.getCae();
                if (cae == null || cae.trim().isEmpty()) {
                    String message = "AFIP aprobó el comprobante pero no devolvió un CAE.";
                    logInvoiceError(message);
                    JOptionPane.showMessageDialog(this, message, "AFIP", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                invoice.setCae(cae);
                invoice.setCaeExpirationDate(result.getCaeExpirationDate());
            }

            clientInvoiceController.save(invoice);
            for (ClientInvoiceDetail detail : details) {
                detail.setInvoice(invoice);
                clientInvoiceDetailController.save(detail);
            }

            adjustStock(invoiceTypeDescription, details);

            if (requiresAfip) {
                try {
                    afipPdfService.generateAndPrint(invoice, associatedInvoice);
                } catch (AfipPdfException ex) {
                    logInvoiceError("Error al generar o imprimir la factura en PyAfipWs", ex);
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(), "AFIP", JOptionPane.WARNING_MESSAGE);
                }
            } else if (isBudget(invoiceTypeDescription)) {
                try {
                    manualPrintService.printBudget(invoice);
                } catch (ManualInvoicePrintException ex) {
                    logInvoiceError("Error al imprimir el presupuesto", ex);
                    JOptionPane.showMessageDialog(this,
                            ex.getMessage(), "Imprimir", JOptionPane.WARNING_MESSAGE);
                }
            }
            return true;
        } catch (AfipAuthorizationException ex) {
            logInvoiceError("Error autorizando comprobante en AFIP", ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "AFIP", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            logInvoiceError("Error inesperado al guardar factura", ex);
            JOptionPane.showMessageDialog(this, "Error al guardar factura", "Guardar", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private ClientInvoice buildInvoiceFromForm() {
        String issuerCuit = getSelectedIssuerCuit();
        if (issuerCuit == null) {
            String message = "Debe seleccionar un CUIT emisor";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Guardar", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ClientInvoice invoice = new ClientInvoice();
        invoice.setIssuerCuit(issuerCuit);
        invoice.setInvoiceNumber(jLabelNumeroComprobante.getText());
        Object selectedPointOfSale = jComboBoxPtoVenta.getSelectedItem();
        invoice.setPointOfSale(selectedPointOfSale != null ? selectedPointOfSale.toString() : "");

        int clientId = Integer.parseInt(jTextFieldCodigo.getText());
        Client client = clientController.findById(clientId);
        if (client == null) {
            String message = "El cliente seleccionado ya no existe";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Guardar", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!client.isActive()) {
            String message = "El cliente está deshabilitado";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Guardar", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        invoice.setClient(client);

        Object invoiceType = jComboBoxTipoCompro.getSelectedItem();
        String invoiceTypeDescription = invoiceType != null ? invoiceType.toString() : "";
        String invoiceTypeValue = InvoiceTypeUtils.toStorageValue(invoiceTypeDescription);
        invoice.setInvoiceType(invoiceTypeValue);

        Object selectedCategory = jComboBoxInvoiceCategory.getSelectedItem();
        if (selectedCategory instanceof InvoiceCategory) {
            invoice.setCategory((InvoiceCategory) selectedCategory);
        } else {
            invoice.setCategory(null);
        }

        if (jDateChooser1.getDate() != null) {
            invoice.setInvoiceDate(jDateChooser1.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            invoice.setInvoiceDate(LocalDateTime.now());
        }

        invoice.setSubtotal(parseAmountLabel(jTextFieldSubtotal));
        invoice.setVat21(parseAmountLabel(jTextFieldIva21));
        invoice.setVat105(parseAmountLabel(jTextFieldIva105));
        invoice.setVat27(BigDecimal.ZERO);
        invoice.setTotal(parseAmountLabel(jLabelTotal));

        return invoice;
    }

    private List<ClientInvoiceDetail> buildInvoiceDetails(ClientInvoice invoice) {
        List<ClientInvoiceDetail> details = new ArrayList<>();
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            Object codeObj = jTable1.getValueAt(i, 0);
            Object quantityObj = jTable1.getValueAt(i, 2);
            Object unitPriceObj = jTable1.getValueAt(i, 3);
            Object discountObj = jTable1.getValueAt(i, 4);
            Object vatObj = jTable1.getValueAt(i, 5);
            Object subtotalObj = jTable1.getValueAt(i, 6);

            if (codeObj == null || quantityObj == null || unitPriceObj == null || subtotalObj == null) {
                continue;
            }

            String code = codeObj.toString();
            if (code.trim().isEmpty()) {
                continue;
            }

            ClientInvoiceDetail detail = new ClientInvoiceDetail();
            detail.setInvoice(invoice);
            detail.setArticleCode(code);
            detail.setArticleDescription(String.valueOf(jTable1.getValueAt(i, 1)));
            detail.setQuantity(new BigDecimal(quantityObj.toString()));
            BigDecimal vatPercent = new BigDecimal(vatObj != null ? vatObj.toString() : "0");
            BigDecimal unitPrice = new BigDecimal(unitPriceObj.toString());
            if ("99".equals(code.trim())) {
                unitPrice = calculateNetFromGross(unitPrice, vatPercent);
            }
            detail.setUnitPrice(unitPrice);
            detail.setDiscountPercent(new BigDecimal(discountObj != null ? discountObj.toString() : "0"));
            detail.setVatAmount(vatPercent);
            detail.setSubtotal(new BigDecimal(subtotalObj.toString()));
            details.add(detail);
        }
        return details;
    }

    private InvoiceReference parseAssociatedInvoice(String input, String defaultPointOfSale) {
        if (input == null) {
            return new InvoiceReference(defaultPointOfSale, "", "", false, false);
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new InvoiceReference(defaultPointOfSale, "", "", false, false);
        }

        String pointOfSale = defaultPointOfSale;
        String numberPart = trimmed;
        if (trimmed.contains("-")) {
            String parsedPointOfSale = AfipManagement.extractPointOfSale(trimmed);
            if (!parsedPointOfSale.isEmpty()) {
                pointOfSale = parsedPointOfSale;
            }
            numberPart = AfipManagement.extractReceiptNumber(trimmed);
        }

        String digits = numberPart.replaceAll("[^0-9]", "");
        boolean hasDigits = !digits.isEmpty();
        if (!hasDigits) {
            return new InvoiceReference(pointOfSale, "", "", false, false);
        }

        boolean partialMatch = digits.length() == 4;
        String suffix = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
        String normalizedNumber = partialMatch ? "" : normalizeInvoiceNumber(numberPart);
        return new InvoiceReference(pointOfSale, normalizedNumber, suffix, true, partialMatch);
    }

    private String normalizeInvoiceNumber(String value) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return "";
        }
        if (digits.length() > 8) {
            digits = digits.substring(digits.length() - 8);
        }
        int number = Integer.parseInt(digits);
        return String.format("%08d", number);
    }

    private boolean belongsToClient(ClientInvoice associatedInvoice, Client selectedClient) {
        if (associatedInvoice == null || selectedClient == null) {
            return true;
        }
        Client invoiceClient = associatedInvoice.getClient();
        if (invoiceClient == null || invoiceClient.getId() == null) {
            return true;
        }
        return invoiceClient.getId().equals(selectedClient.getId());
    }

    private boolean confirmAssociatedInvoice(ClientInvoice associatedInvoice) {
        if (associatedInvoice == null) {
            return true;
        }

        String clientName = "";
        Client invoiceClient = associatedInvoice.getClient();
        if (invoiceClient != null && invoiceClient.getId() != null) {
            Client fullClient = clientController.findById(invoiceClient.getId());
            if (fullClient != null && fullClient.getFullName() != null) {
                clientName = fullClient.getFullName();
            }
        }

        StringBuilder message = new StringBuilder();
        message.append("Se encontró el comprobante asociado:");
        message.append(String.format("%nCliente: %s", clientName.isEmpty() ? "-" : clientName));
        message.append(String.format("%nTotal: %s", formatAmount(associatedInvoice.getTotal())));
        message.append(String.format("%nPunto de venta: %s", associatedInvoice.getPointOfSale()));
        message.append(String.format("%nNúmero: %s", associatedInvoice.getInvoiceNumber()));
        message.append(String.format("%n%n¿Desea continuar?"));

        int option = JOptionPane.showConfirmDialog(this, message.toString(),
                "Comprobante asociado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        return option == JOptionPane.YES_OPTION;
    }

    private boolean isBudget(String invoiceTypeDescription) {
        String normalized = InvoiceTypeUtils.toStorageValue(invoiceTypeDescription);
        return Constants.PRESUPUESTO_ABBR.equalsIgnoreCase(normalized);
    }

    private void adjustStock(String invoiceTypeDescription, List<ClientInvoiceDetail> details) {
        if (loadedFromRemit || details == null || details.isEmpty()) {
            return;
        }

        boolean isCredit = InvoiceTypeUtils.isCreditDocument(invoiceTypeDescription);
        boolean isDebit = InvoiceTypeUtils.isDebitDocument(invoiceTypeDescription);
        boolean isInvoice = InvoiceTypeUtils.isInvoiceDocument(invoiceTypeDescription);

        if (!isCredit && !isDebit && !isInvoice) {
            return;
        }

        for (ClientInvoiceDetail detail : details) {
            String code = detail.getArticleCode();
            String trimmedCode = code != null ? code.trim() : "";
            if (trimmedCode.isEmpty() || "99".equals(trimmedCode)) {
                continue;
            }

            BigDecimal quantityValue = detail.getQuantity();
            float quantity = quantityValue != null ? quantityValue.floatValue() : 0f;
            if (quantity <= 0f) {
                continue;
            }

            if (isCredit) {
                productController.increaseStock(trimmedCode, quantity);
            } else {
                boolean success = productController.decreaseStock(trimmedCode, quantity);
                if (!success) {
                    Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.WARNING,
                            "No se pudo descontar stock para el artículo {0}", trimmedCode);
                }
            }
        }
    }

    private static final class InvoiceReference {

        private final String pointOfSale;
        private final String number;
        private final String suffix;
        private final boolean hasInput;
        private final boolean partial;

        private InvoiceReference(String pointOfSale, String number, String suffix, boolean hasInput, boolean partial) {
            this.pointOfSale = pointOfSale;
            this.number = number;
            this.suffix = suffix;
            this.hasInput = hasInput;
            this.partial = partial;
        }

        private boolean hasInput() {
            return hasInput;
        }

        private boolean isPartial() {
            return partial;
        }
    }

    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        if (!validarFormulario() || !validarStock()) {
            return;
        }
        if (saveInvoice()) {
            limpiarFormulario();
            jTextFieldCodigo.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButtonGuardarActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (!ProductSearchView.isOpen) {
            ProductSearchView view = new ProductSearchView(productController, this::handleProductSelectedFromSearch);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
}//GEN-LAST:event_jButton6ActionPerformed

    private void jComboBoxPtoVentaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxPtoVentaItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            String pointOfSale = formatPointOfSale(evt.getItem() != null ? evt.getItem().toString() : null);

            updateInvoiceTypeOptionsForPointOfSale();
            updateIssuerCuitForPointOfSale(pointOfSale);
            updatePointOfSaleDescription();
            updateInvoiceNumber();
        }
    }//GEN-LAST:event_jComboBoxPtoVentaItemStateChanged

    private void jTextFieldCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCodigoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                Client client = clientController.findById(Integer.parseInt(jTextFieldCodigo.getText()));
                if (client != null) {
                    if (ClientInvoiceInsertView.isOpen) {
                        populateClientInfo(client);
                        java.util.Date fecha = new Date();
                        jDateChooser1.setDate(fecha);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se ha encontrado cliente!");
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientInvoiceInsertView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTextFieldCodigoKeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_DELETE) {
            jButtonBorrarItemActionPerformed(null);
            evt.consume();
        } else if (keyCode == KeyEvent.VK_INSERT) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            AgregarLineaVacia(model);
            jTable1.setModel(model);
            setModelTable();
            int newRow = model.getRowCount() - 1;
            if (newRow >= 0) {
                jTable1.changeSelection(newRow, 0, false, false);
            }
            evt.consume();
        } else if (keyCode == KeyEvent.VK_F4) {
            jButton6ActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_jTable1KeyPressed

    public boolean validarStock() {
        boolean dev = true;

        try {

            Object selectedType = jComboBoxTipoCompro.getSelectedItem();
            String normalizedType = InvoiceTypeUtils.toStorageValue(selectedType != null ? selectedType.toString() : "");
            if ((Constants.NOTA_DEVOLUCION_ABBR.equals(normalizedType))
                    || (Constants.NOTA_CREDITO_B_ABBR.equals(normalizedType))
                    || (Constants.NOTA_CREDITO_A_ABBR.equals(normalizedType))
                    || ClientRemitManagementView.isOpen) {
                dev = true;
            } else {
                Product product;
                String cod = "";
                int cantFilas = jTable1.getRowCount();
                int i = 0;
                while (i < cantFilas) {
                    cod = jTable1.getValueAt(i, 0).toString();
                    if (cod.equals("99")) {
                        dev = true;
                    } else {
                        product = productController.findByCode(cod);
                        String cant = jTable1.getValueAt(i, 2).toString();
                        float canti = Float.parseFloat(cant);
                        float cantTotal = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
                        if (cantTotal < canti) {
                            i = cantFilas;
                            String message = "La cantidad que ha seleccionado es mayor que el stock disponible! Articulo: " + product.getCode();
                            logInvoiceWarning(message);
                            JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                            dev = false;
                        }
                    }
                    i++;
                }
            }

        } catch (Exception ex) {
            logInvoiceError("Error al validar stock", ex);
        }
        return dev;
    }

    public boolean validarFormulario() {
        commitInvoiceTableEdits();

        if (getSelectedIssuerCuit() == null) {
            String message = "Debe seleccionar un CUIT emisor";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
            jComboBoxInvoiceIssuerCuit.requestFocus();
            return false;
        }

        Object pointOfSaleObj = jComboBoxPtoVenta.getSelectedItem();
        String pointOfSale = pointOfSaleObj != null ? pointOfSaleObj.toString() : "";
        Object selectedTypeObj = jComboBoxTipoCompro.getSelectedItem();
        String normalizedType = InvoiceTypeUtils.toStorageValue(selectedTypeObj != null ? selectedTypeObj.toString() : "");

        if ("0000".equals(pointOfSale)) {
            if (!Constants.PRESUPUESTO_ABBR.equals(normalizedType) && !Constants.NOTA_DEVOLUCION_ABBR.equals(normalizedType)) {
                String message = "No coincide punto de venta con tipo de comprobante";
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            if (Constants.PRESUPUESTO_ABBR.equals(normalizedType) || Constants.NOTA_DEVOLUCION_ABBR.equals(normalizedType)) {
                String message = "No coincide punto de venta con tipo de comprobante";
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (jTextFieldDniCuit.getText().trim().isEmpty()) {
            String message = "Debe seleccionar un cliente";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return false;
        }

        if (jTable1.getRowCount() == 0) {
            String message = "Debe seleccionar algun articulo para la factura";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (jComboBoxTipoCompro.getSelectedIndex() == 0) {
            String message = "Debe seleccionar el tipo de comprobante";
            logInvoiceWarning(message);
            JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Object codeObj = model.getValueAt(i, 0);
            String code = codeObj != null ? codeObj.toString().trim() : "";
            if (code.isEmpty()) {
                continue;
            }

            BigDecimal quantity = parseBigDecimal(model.getValueAt(i, 2));
            BigDecimal price = parseBigDecimal(model.getValueAt(i, 3));
            BigDecimal discount = parseBigDecimal(model.getValueAt(i, 4));
            BigDecimal vatPercent = parseBigDecimal(model.getValueAt(i, 5));
            BigDecimal subtotal = parseBigDecimal(model.getValueAt(i, 6));

            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                String message = "La cantidad debe ser mayor a cero. Fila: " + (i + 1);
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
                String message = "Debe ingresar un precio válido y confirmar la edición. Fila: " + (i + 1);
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            BigDecimal expectedSubtotal = calculateInvoiceSubtotal(code, quantity, price, discount, vatPercent);
            if (!isSubtotalWithinTolerance(expectedSubtotal, subtotal)) {
                if ("99".equals(code)) {
                    BigDecimal alternativeSubtotal = quantity.multiply(applyDiscount(price, discount));
                    if (isSubtotalWithinTolerance(alternativeSubtotal, subtotal)) {
                        continue;
                    }
                }

                String message = "No coincide subtotal! Fila: " + (i + 1);
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message, "Atencion", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (InvoiceTypeUtils.isCreditDocument(normalizedType)
                || InvoiceTypeUtils.isDebitDocument(normalizedType)) {

            if (jTextFieldAsociado.getText().trim().isEmpty()) {
                String message = "Debera ingresar un comprobante asociado. Solo numero de factura sin los ceros";
                logInvoiceWarning(message);
                JOptionPane.showMessageDialog(this, message);
                return false;
            }
        }

        return true;

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButtonBorrarItem;
    private javax.swing.JButton jButtonBorrarTodo;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JButton jButtonSearchClient;
    public static javax.swing.JComboBox jComboBoxInvoiceCategory;
    public static javax.swing.JComboBox jComboBoxInvoiceIssuerCuit;
    public static javax.swing.JComboBox jComboBoxPtoVenta;
    public static javax.swing.JComboBox jComboBoxTipoCompro;
    public static com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    public static javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelPointOfSaleDescription;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabelCiudad;
    public static javax.swing.JLabel jLabelCliente;
    public static javax.swing.JLabel jLabelCondicion;
    public static javax.swing.JLabel jLabelDireccion;
    private javax.swing.JLabel jLabelInvoiceCategory;
    public static javax.swing.JLabel jLabelNumeroComprobante;
    public static javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    public static javax.swing.JTextField jTextFieldArticulo;
    private javax.swing.JTextField jTextFieldAsociado;
    public static javax.swing.JTextField jTextFieldCodigo;
    public static javax.swing.JTextField jTextFieldDniCuit;
    public static javax.swing.JLabel jTextFieldIva105;
    public static javax.swing.JLabel jTextFieldIva21;
    public static javax.swing.JLabel jTextFieldSubtotal;
    // End of variables declaration//GEN-END:variables

}
