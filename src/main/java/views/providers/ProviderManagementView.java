
package views.providers;

import controllers.ProviderController;
import controllers.AddressController;
import controllers.CityController;
import controllers.TaxConditionController;
import configs.MyBatisConfig;
import mappers.AddressMapper;
import mappers.CityMapper;
import mappers.TaxConditionMapper;
import mappers.ProviderMapper;
import mappers.ProviderInvoiceMapper;
import mappers.receipts.ProviderReceiptMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.AddressRepository;
import repositories.CityRepository;
import repositories.TaxConditionRepository;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.ProviderReceiptRepository;
import repositories.impl.AddressRepositoryImpl;
import repositories.impl.CityRepositoryImpl;
import repositories.impl.TaxConditionRepositoryImpl;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import services.ProviderService;
import services.AddressService;
import services.CityService;
import services.TaxConditionService;
import views.AdjustInsertView;
import views.MainView;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.swing.JDesktopPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

import models.Address;
import models.City;
import models.TaxCondition;
import models.Provider;
import utils.DocumentValidator;
import utils.TableUtils;


public class ProviderManagementView extends javax.swing.JInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(ProviderManagementView.class.getName());
    private static ProviderManagementView activeInstance;
    private static Consumer<Provider> providerSelectionListener;
    private static boolean closeOnProviderSelection;

    private ProviderController providerController;
    private AddressController addressController;
    private CityController cityController;
    private TaxConditionController conditionController;
    private SqlSession sqlSession;
    public static boolean isOpen = false;
    public static ArrayList providers = null;
    // public static Pagos paymentsWindow = null;
    // public static Ajuste adjustmentWindow = null;
    private Provider currentProvider;
    private boolean editing = false;
    private static final int BASE_WIDTH = 1150;
    private static final int BASE_HEIGHT = 650;
    private static final int HEIGHT_MARGIN = 40;
    private static final int BOTTOM_MARGIN = 20;
    private static final int MIN_TABLE_HEIGHT = 200;
    private static final int MIN_PANEL_HEIGHT = 240;
    private static final int TABLE_X = 50;
    private static final int TABLE_Y = 120;
    private static final int TABLE_WIDTH = 430;
    private static final int TABLE_HEIGHT = 400;
    private static final int PANEL_X = 550;
    private static final int PANEL_Y = 100;
    private static final int PANEL_WIDTH = 410;
    private static final int PANEL_HEIGHT = 460;
    private static final int RETURN_X = 420;
    private static final int RETURN_Y = 570;
    private static final int RETURN_WIDTH = 120;
    private static final int RETURN_HEIGHT = 30;
    private static final int BALANCE_X = 380;
    private static final int BALANCE_Y = 520;
    private static final int BALANCE_WIDTH = 80;
    private static final int BALANCE_HEIGHT = 30;
    private static final Set<Character> ALLOWED_NAME_SYMBOLS = Set.of('.', ',', '-', '_', '\'', '/', '&', '(', ')');

    public ProviderManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            AddressMapper addressMapper = sqlSession.getMapper(AddressMapper.class);
            CityMapper cityMapper = sqlSession.getMapper(CityMapper.class);
            TaxConditionMapper conditionMapper = sqlSession.getMapper(TaxConditionMapper.class);
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
            ProviderReceiptMapper providerReceiptMapper = sqlSession.getMapper(ProviderReceiptMapper.class);

            AddressRepository addressRepository = new AddressRepositoryImpl(addressMapper);
            CityRepository cityRepository = new CityRepositoryImpl(cityMapper);
            TaxConditionRepository conditionRepository = new TaxConditionRepositoryImpl(conditionMapper);
            ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
            ProviderInvoiceRepository providerInvoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
            ProviderReceiptRepository providerReceiptRepository = new ProviderReceiptRepositoryImpl(providerReceiptMapper);

            AddressService addressService = new AddressService(addressRepository);
            CityService cityService = new CityService(cityRepository);
            TaxConditionService conditionService = new TaxConditionService(conditionRepository);
            ProviderService providerService = new ProviderService(providerRepository, providerInvoiceRepository, providerReceiptRepository);

            this.addressController = new AddressController(addressService);
            this.cityController = new CityController(cityService);
            this.conditionController = new TaxConditionController(conditionService);
            this.providerController = new ProviderController(providerService);
            initComponents();
            isOpen = true;
            activeInstance = this;
            applyRequiredFieldIndicators();

            ProviderTableCellRenderer providerRenderer = new ProviderTableCellRenderer();
            jTable1.setDefaultRenderer(Object.class, providerRenderer);
            jTable1.setDefaultRenderer(Number.class, providerRenderer);

            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameOpened(InternalFrameEvent e) {
                    adjustHeightForLargeScreens();
                }

                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                    clearProviderSelectionListener();
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                    clearProviderSelectionListener();
                }
            });

            TableUtils.configureProviderManagementViewTable(jTable1);

            loadAddresses();
            loadCities();
            loadConditions();

            buttonGroup1.add(jRadioButtonCuit);
            buttonGroup1.add(jRadioButtonDni);

            loadTable("");
            updateActionButtonsState();
            jCheckBoxActive.setSelected(true);
            jCheckBoxActive.setEnabled(false);

            allowNameCharacters(jTextFieldName);
            allowOnlyNumbers(jTextFieldDniCuit);
            allowOnlyNumbers(jTextFieldPhone);
            allowOnlyNumbers(jTextFieldCelPhone);
            allowAdjustmentCharacters(jTextFieldPriceAdjustment);
            allowCodePrefixCharacters(jTextFieldCodePrefix);
            registerValidationListeners();
            updateSaveButtonState();

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateLayoutForHeight(getHeight());
                }
            });

            SwingUtilities.invokeLater(this::adjustHeightForLargeScreens);

        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void allowNameCharacters(JTextField field){
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if (Character.isISOControl(c)
                        || Character.isLetterOrDigit(c)
                        || Character.isSpaceChar(c)
                        || ALLOWED_NAME_SYMBOLS.contains(c)) {
                    return;
                }
                getToolkit().beep();
                e.consume();
            }

        });
    }

    public void allowOnlyNumbers(JTextField field){
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if(field == jTextFieldDniCuit && jRadioButtonCuit.isSelected() && c == '-'){
                    return;
                }
                if(!Character.isDigit(c)){
                    getToolkit().beep();
                    e.consume();
                }
            }

        });
    }

    public void allowAdjustmentCharacters(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                boolean isValidSymbol = c == '+' || c == '-' || c == '.' || Character.isWhitespace(c);
                if (!Character.isDigit(c) && !isValidSymbol) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
    }

    private void allowCodePrefixCharacters(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) {
                    return;
                }
                if (!Character.isLetterOrDigit(c) && c != '-') {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });
    }

    private void applyRequiredFieldIndicators() {
        jLabel4.setText(formatRequiredLabel("Nombre"));
        jLabel12.setText(formatRequiredLabel("Condicion"));
        jRadioButtonCuit.setText(formatRequiredLabel("Cuit"));
        jRadioButtonDni.setText("Dni");
    }

    private String formatRequiredLabel(String label) {
        return "<html>" + label + " <font color='red'>*</font></html>";
    }

    private void registerValidationListeners() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSaveButtonState();
            }
        };

        jTextFieldName.getDocument().addDocumentListener(documentListener);
        jTextFieldDniCuit.getDocument().addDocumentListener(documentListener);
        jComboBoxTaxCondition.addItemListener(e -> updateSaveButtonState());
        jRadioButtonCuit.addActionListener(e -> updateSaveButtonState());
        jRadioButtonDni.addActionListener(e -> updateSaveButtonState());
    }

    private void updateSaveButtonState() {
        boolean formEditable = jTextFieldName.isEditable();
        if (!formEditable) {
            jButtonSave.setEnabled(false);
            return;
        }

        boolean hasName = !jTextFieldName.getText().trim().isEmpty();
        boolean hasDocument = !jTextFieldDniCuit.getText().trim().isEmpty();
        boolean hasTaxCondition = jComboBoxTaxCondition.getSelectedIndex() > 0;

        jButtonSave.setEnabled(hasName && hasDocument && hasTaxCondition);
    }

    private String normalizeCodePrefix(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        while (normalized.endsWith("-")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }

    public void loadAddresses() {
        addressController.loadAddresses(jComboBoxAddress);
    }

    public void loadCities() {
        cityController.loadCities(jComboBoxCity);
    }

    public void loadConditions() {
        conditionController.loadConditions(jComboBoxTaxCondition);
    }

    public void loadTable(String value) throws ClassNotFoundException, SQLException {
        BigDecimal totalBalance = providerController.fillTable(jTable1, value);
        jLabelTotalProviders.setText(providerController.formatCurrency(totalBalance));
    }

    public void refreshTable() throws Exception{

        loadTable("");
        updateActionButtonsState();
    }


    private void updateActionButtonsState() {
        boolean providerSelected = currentProvider != null;
        boolean providerActive = providerSelected && currentProvider.isActive();
        jButtonPayments.setEnabled(providerActive);
        jButtonAdjust.setEnabled(providerActive);
    }

    public static void setProviderSelectionListener(Consumer<Provider> listener, boolean closeAfterSelection) {
        providerSelectionListener = listener;
        closeOnProviderSelection = closeAfterSelection;
    }

    public static void bringToFront() {
        if (activeInstance == null) {
            return;
        }
        try {
            if (activeInstance.isIcon()) {
                activeInstance.setIcon(false);
            }
            activeInstance.setSelected(true);
        } catch (PropertyVetoException ex) {
            LOGGER.log(Level.FINE, "No se pudo seleccionar la vista de proveedores", ex);
        }
        activeInstance.toFront();
        activeInstance.requestFocus();
    }

    private static void clearProviderSelectionListener() {
        providerSelectionListener = null;
        closeOnProviderSelection = false;
    }

    private void handleProviderDoubleClick(int rowIndex) {
        Provider provider = currentProvider;
        Object idValue = jTable1.getValueAt(rowIndex, 0);
        Integer rowId = idValue instanceof Integer ? (Integer) idValue : null;
        if (provider == null || provider.getId() == null || !provider.getId().equals(rowId)) {
            provider = getProviderFromRow(rowIndex);
            if (provider == null && rowId != null) {
                provider = providerController.findById(rowId);
            }
        }
        if (provider != null) {
            notifyProviderSelected(provider);
        }
    }

    private Provider getProviderFromRow(int rowIndex) {
        Object property = jTable1.getClientProperty("providersList");
        if (!(property instanceof List<?>)) {
            return null;
        }
        List<?> providers = (List<?>) property;
        if (rowIndex < 0 || rowIndex >= providers.size()) {
            return null;
        }
        Object candidate = providers.get(rowIndex);
        return candidate instanceof Provider ? (Provider) candidate : null;
    }

    private void notifyProviderSelected(Provider provider) {
        Consumer<Provider> listener = providerSelectionListener;
        boolean shouldClose = closeOnProviderSelection;
        clearProviderSelectionListener();
        if (listener != null) {
            try {
                listener.accept(provider);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al notificar la selección de proveedor", ex);
            }
        }
        if (shouldClose) {
            disposeActiveInstance();
        }
    }

    private static void disposeActiveInstance() {
        if (activeInstance == null) {
            return;
        }
        try {
            activeInstance.setClosed(true);
        } catch (PropertyVetoException ex) {
            LOGGER.log(Level.FINE, "No se pudo cerrar la vista de proveedores", ex);
            activeInstance.dispose();
        }
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonAdd = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPaneForm = new javax.swing.JScrollPane();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldPhone = new javax.swing.JTextField();
        jTextFieldCelPhone = new javax.swing.JTextField();
        jTextFieldMail = new javax.swing.JTextField();
        jComboBoxAddress = new javax.swing.JComboBox();
        jComboBoxCity = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldNumberAddress = new javax.swing.JTextField();
        jRadioButtonCuit = new javax.swing.JRadioButton();
        jRadioButtonDni = new javax.swing.JRadioButton();
        jComboBoxTaxCondition = new javax.swing.JComboBox();
        jTextFieldDniCuit = new javax.swing.JTextField();
        jLabelCodePrefix = new javax.swing.JLabel();
        jTextFieldCodePrefix = new javax.swing.JTextField();
        jLabelPriceAdjustment = new javax.swing.JLabel();
        jTextFieldPriceAdjustment = new javax.swing.JTextField();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jCheckBoxActive = new javax.swing.JCheckBox();
        jButtonHistory = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonPayments = new javax.swing.JButton();
        jLabelTotalProviders = new javax.swing.JLabel();
        jButtonAdjust = new javax.swing.JButton();

        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(70, 80, 44, 20);

        jTextFieldSearch.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldSearch);
        jTextFieldSearch.setBounds(120, 80, 350, 21);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Telefono", "Saldo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(51, 51, 51));
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.setText("Agregar");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(50, 20, 130, 30);

        jButtonUpdate.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modificar.png"))); // NOI18N
        jButtonUpdate.setText("Modificar");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonUpdate);
        jButtonUpdate.setBounds(190, 20, 130, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N
        jPanel1.setEnabled(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(PANEL_WIDTH - 20, PANEL_HEIGHT));
        jPanel1.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Nombre");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(50, 40, 90, 20);

        jTextFieldName.setEditable(false);
        jTextFieldName.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldName);
        jTextFieldName.setBounds(170, 40, 190, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Ciudad");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(50, 70, 90, 20);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Direccion");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(50, 100, 90, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Telefono");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(50, 220, 90, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Celular");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(50, 250, 90, 20);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Mail");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(50, 280, 90, 20);

        jTextFieldPhone.setEditable(false);
        jTextFieldPhone.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldPhone);
        jTextFieldPhone.setBounds(170, 220, 190, 20);

        jTextFieldCelPhone.setEditable(false);
        jTextFieldCelPhone.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldCelPhone);
        jTextFieldCelPhone.setBounds(170, 250, 190, 20);

        jTextFieldMail.setEditable(false);
        jTextFieldMail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldMail);
        jTextFieldMail.setBounds(170, 280, 190, 20);

        jComboBoxAddress.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxAddress);
        jComboBoxAddress.setBounds(170, 100, 190, 20);

        jComboBoxCity.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxCity);
        jComboBoxCity.setBounds(170, 70, 190, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Numero");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(50, 130, 90, 20);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Condicion");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(50, 160, 90, 20);

        jTextFieldNumberAddress.setEditable(false);
        jTextFieldNumberAddress.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldNumberAddress);
        jTextFieldNumberAddress.setBounds(170, 130, 190, 20);

        jRadioButtonCuit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonCuit.setText("Cuit");
        jPanel1.add(jRadioButtonCuit);
        jRadioButtonCuit.setBounds(20, 190, 70, 20);

        jRadioButtonDni.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonDni.setText("Dni");
        jPanel1.add(jRadioButtonDni);
        jRadioButtonDni.setBounds(100, 190, 60, 20);

        jComboBoxTaxCondition.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxTaxCondition);
        jComboBoxTaxCondition.setBounds(170, 160, 190, 21);

        jTextFieldDniCuit.setEditable(false);
        jTextFieldDniCuit.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldDniCuit);
        jTextFieldDniCuit.setBounds(170, 190, 190, 20);

        jLabelCodePrefix.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelCodePrefix.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCodePrefix.setText("Prefijo código");
        jPanel1.add(jLabelCodePrefix);
        jLabelCodePrefix.setBounds(40, 340, 110, 20);

        jTextFieldCodePrefix.setEditable(false);
        jTextFieldCodePrefix.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldCodePrefix);
        jTextFieldCodePrefix.setBounds(170, 340, 190, 20);

        jLabelPriceAdjustment.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelPriceAdjustment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelPriceAdjustment.setText("Ajuste precio");
        jPanel1.add(jLabelPriceAdjustment);
        jLabelPriceAdjustment.setBounds(40, 310, 110, 20);

        jTextFieldPriceAdjustment.setEditable(false);
        jTextFieldPriceAdjustment.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldPriceAdjustment);
        jTextFieldPriceAdjustment.setBounds(170, 310, 190, 20);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.setEnabled(false);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonSave);
        jButtonSave.setBounds(100, 420, 120, 30);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.setEnabled(false);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonCancel);
        jButtonCancel.setBounds(230, 420, 120, 30);

        jCheckBoxActive.setText("Activo");
        jPanel1.add(jCheckBoxActive);
        jCheckBoxActive.setBounds(170, 370, 130, 20);

        jScrollPaneForm.setBorder(null);
        jScrollPaneForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneForm.setViewportView(jPanel1);
        jScrollPaneForm.getVerticalScrollBar().setUnitIncrement(16);

        getContentPane().add(jScrollPaneForm);
        jScrollPaneForm.setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

        jButtonHistory.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/listado.png"))); // NOI18N
        jButtonHistory.setText("Historial");
        jButtonHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHistoryActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonHistory);
        jButtonHistory.setBounds(330, 20, 130, 30);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(RETURN_X, RETURN_Y, RETURN_WIDTH, RETURN_HEIGHT);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.setText("Eliminar");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(470, 20, 130, 30);

        jButtonPayments.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPayments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cobranza.png"))); // NOI18N
        jButtonPayments.setText("Pagos");
        jButtonPayments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPaymentsActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPayments);
        jButtonPayments.setBounds(610, 20, 130, 30);

        jLabelTotalProviders.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabelTotalProviders.setForeground(new java.awt.Color(204, 0, 0));
        jLabelTotalProviders.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        getContentPane().add(jLabelTotalProviders);
        jLabelTotalProviders.setBounds(BALANCE_X, BALANCE_Y, BALANCE_WIDTH, BALANCE_HEIGHT);

        jButtonAdjust.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdjust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ajuste.png"))); // NOI18N
        jButtonAdjust.setText("Ajuste");
        jButtonAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdjustActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdjust);
        jButtonAdjust.setBounds(750, 20, 130, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void adjustHeightForLargeScreens() {
        JDesktopPane desktopPane = getDesktopPane();
        Dimension reference = desktopPane != null ? desktopPane.getSize() : Toolkit.getDefaultToolkit().getScreenSize();

        if (reference == null || reference.height <= 0) {
            updateLayoutForHeight(getHeight() > 0 ? getHeight() : BASE_HEIGHT);
            return;
        }

        int availableHeight = reference.height - HEIGHT_MARGIN;
        if (availableHeight <= 0) {
            availableHeight = reference.height;
        }
        if (availableHeight <= 0) {
            availableHeight = BASE_HEIGHT;
        }

        int targetHeight = reference.height >= BASE_HEIGHT + HEIGHT_MARGIN
                ? availableHeight
                : Math.min(BASE_HEIGHT, availableHeight);

        setPreferredSize(new Dimension(BASE_WIDTH, targetHeight));
        setSize(new Dimension(BASE_WIDTH, targetHeight));

        updateLayoutForHeight(targetHeight);
    }

    private void updateLayoutForHeight(int containerHeight) {
        if (containerHeight <= 0) {
            return;
        }

        int extra = containerHeight - BASE_HEIGHT;

        int bottomLimit = Math.max(0, containerHeight - RETURN_HEIGHT - BOTTOM_MARGIN);
        int minButtonY = TABLE_Y + 120;
        if (bottomLimit < minButtonY) {
            minButtonY = bottomLimit;
        }

        int desiredButtonY = RETURN_Y + extra;
        int buttonY = Math.min(desiredButtonY, bottomLimit);
        buttonY = Math.max(buttonY, minButtonY);
        buttonY = Math.min(buttonY, bottomLimit);

        int tableMaxHeight = Math.max(0, buttonY - TABLE_Y - BOTTOM_MARGIN);
        int tableHeight = TABLE_HEIGHT + extra;
        if (tableMaxHeight <= MIN_TABLE_HEIGHT) {
            tableHeight = tableMaxHeight;
        } else {
            tableHeight = Math.max(MIN_TABLE_HEIGHT, Math.min(tableHeight, tableMaxHeight));
        }

        int panelMaxHeight = Math.max(0, buttonY - PANEL_Y - BOTTOM_MARGIN);
        int panelHeight = PANEL_HEIGHT + extra;
        if (panelMaxHeight <= 0) {
            panelHeight = Math.max(MIN_PANEL_HEIGHT, PANEL_HEIGHT + extra);
        } else {
            panelHeight = Math.max(Math.min(panelHeight, panelMaxHeight), Math.min(MIN_PANEL_HEIGHT, panelMaxHeight));
        }

        int labelMaxY = Math.max(TABLE_Y + 10, buttonY - BALANCE_HEIGHT - 10);
        int labelY = BALANCE_Y + extra;
        labelY = Math.max(TABLE_Y + 10, Math.min(labelY, labelMaxY));

        jScrollPane1.setBounds(TABLE_X, TABLE_Y, TABLE_WIDTH, tableHeight);
        jScrollPaneForm.setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, Math.max(0, panelHeight));
        jButtonReturn.setBounds(RETURN_X, buttonY, RETURN_WIDTH, RETURN_HEIGHT);
        jLabelTotalProviders.setBounds(BALANCE_X, labelY, BALANCE_WIDTH, BALANCE_HEIGHT);
    }

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchKeyReleased
        try {
            loadTable(jTextFieldSearch.getText());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jTextFieldSearchKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        if (evt.getClickCount() == 2) {
            handleProviderDoubleClick(selectedRow);
            return;
        }
        if (evt.getClickCount() == 1) {
            int i = selectedRow;
            if (i != -1) {
                try {
                    loadAddresses();
                    loadCities();
                    loadConditions();
                } catch (Exception ex) {
                    Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
                }
                Object idValue = jTable1.getValueAt(i, 0);
                Integer id = idValue instanceof Integer ? (Integer) idValue : null;
                currentProvider = id != null ? providerController.findById(id) : null;
                if (currentProvider != null) {
                    jTextFieldName.setText(currentProvider.getName());
                    if (currentProvider.getAddress() != null) {
                        jComboBoxAddress.setSelectedItem(currentProvider.getAddress().getName());
                    } else {
                        jComboBoxAddress.setSelectedItem(null);
                    }
                    if (currentProvider.getCity() != null) {
                        jComboBoxCity.setSelectedItem(currentProvider.getCity().getName());
                    } else {
                        jComboBoxCity.setSelectedItem(null);
                    }
                    if (currentProvider.getTaxCondition() != null) {
                        jComboBoxTaxCondition.setSelectedItem(currentProvider.getTaxCondition().getName());
                    } else {
                        jComboBoxTaxCondition.setSelectedItem(null);
                    }
                    String doc = currentProvider.getDocumentNumber();
                    if ("CUIT".equalsIgnoreCase(currentProvider.getDocumentType())) {
                        doc = DocumentValidator.formatCuit(doc);
                    }
                    jTextFieldDniCuit.setText(doc);
                    jTextFieldNumberAddress.setText(currentProvider.getAddressNumber());
                    jTextFieldPhone.setText(currentProvider.getPhone());
                    jTextFieldCelPhone.setText(currentProvider.getMobile());
                    jTextFieldMail.setText(currentProvider.getEmail());
                    jTextFieldCodePrefix.setText(currentProvider.getCodePrefix());
                    jTextFieldPriceAdjustment.setText(currentProvider.getPriceAdjustmentFormula());
                    if ("CUIT".equalsIgnoreCase(currentProvider.getDocumentType())) {
                        jRadioButtonCuit.setSelected(true);
                    } else if ("DNI".equalsIgnoreCase(currentProvider.getDocumentType())) {
                        jRadioButtonDni.setSelected(true);
                    } else {
                        buttonGroup1.clearSelection();
                    }
                    jCheckBoxActive.setSelected(currentProvider.isActive());
                    ponerNoEditable();
                    jButtonSave.setEnabled(false);
                    jButtonCancel.setEnabled(false);
                    editing = false;
                    updateActionButtonsState();
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        try {
            loadAddresses();
            loadCities();
            loadConditions();
            limpiarFormulario();
            jButtonCancel.setEnabled(true);
            ponerEditable();
            currentProvider = null;
            editing = false;
            updateActionButtonsState();
            updateSaveButtonState();
        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void ponerEditable(){


            jTextFieldName.setEditable(true);
            jTextFieldName.requestFocus();

            jComboBoxAddress.setEnabled(true);
            jComboBoxCity.setEnabled(true);
            jComboBoxTaxCondition.setEnabled(true);

            jTextFieldDniCuit.setEditable(true);
            jTextFieldNumberAddress.setEditable(true);
            jTextFieldPhone.setEditable(true);
            jTextFieldCelPhone.setEditable(true);
            jTextFieldMail.setEditable(true);
            jTextFieldCodePrefix.setEditable(true);
            jTextFieldPriceAdjustment.setEditable(true);

            jButtonAdd.setSelected(true);
            jButtonUpdate.setSelected(true);
            jCheckBoxActive.setEnabled(true);
    }

    private void ponerEditableMenosSaldoInicial(){


            jTextFieldName.setEditable(true);
            jTextFieldName.requestFocus();

            jComboBoxAddress.setEnabled(true);
            jComboBoxCity.setEnabled(true);
            jComboBoxTaxCondition.setEnabled(true);

            jTextFieldDniCuit.setEditable(true);
            jTextFieldNumberAddress.setEditable(true);
            jTextFieldPhone.setEditable(true);
            jTextFieldCelPhone.setEditable(true);
            jTextFieldMail.setEditable(true);
            jTextFieldCodePrefix.setEditable(true);
            jTextFieldPriceAdjustment.setEditable(true);

            jButtonAdd.setSelected(true);
            jButtonUpdate.setSelected(true);
            jCheckBoxActive.setEnabled(true);
    }

    private void ponerNoEditable(){


            jTextFieldName.setEditable(false);
            jTextFieldName.requestFocus();

            jComboBoxAddress.setEnabled(false);
            jComboBoxCity.setEnabled(false);
            jComboBoxTaxCondition.setEnabled(false);

            jTextFieldDniCuit.setEditable(false);
            jTextFieldNumberAddress.setEditable(false);
            jTextFieldPhone.setEditable(false);
            jTextFieldCelPhone.setEditable(false);
            jTextFieldMail.setEditable(false);
            jTextFieldCodePrefix.setEditable(false);
            jTextFieldPriceAdjustment.setEditable(false);

            jButtonAdd.setSelected(false);
            jButtonUpdate.setSelected(false);
            jCheckBoxActive.setEnabled(false);
    }

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        try {
            int i = ProviderManagementView.jTable1.getSelectedRow();

            if (i == -1) {
                JOptionPane.showMessageDialog(this, "No ha seleccionado ningun proveedor", "Modificar", JOptionPane.OK_OPTION);
            } else {
                int id = Integer.parseInt(ProviderManagementView.jTable1.getValueAt(i, 0).toString());
                currentProvider = providerController.findById(id);
                if (currentProvider != null) {
                    jTextFieldName.setText(currentProvider.getName());
                    if (currentProvider.getAddress() != null) {
                        jComboBoxAddress.setSelectedItem(currentProvider.getAddress().getName());
                    }
                    if (currentProvider.getCity() != null) {
                        jComboBoxCity.setSelectedItem(currentProvider.getCity().getName());
                    }
                    if (currentProvider.getTaxCondition() != null) {
                        jComboBoxTaxCondition.setSelectedItem(currentProvider.getTaxCondition().getName());
                    }
                    String doc2 = currentProvider.getDocumentNumber();
                    if ("CUIT".equalsIgnoreCase(currentProvider.getDocumentType())) {
                        doc2 = DocumentValidator.formatCuit(doc2);
                    }
                    jTextFieldDniCuit.setText(doc2);
                    jTextFieldNumberAddress.setText(currentProvider.getAddressNumber());
                    jTextFieldPhone.setText(currentProvider.getPhone());
                    jTextFieldCelPhone.setText(currentProvider.getMobile());
                    jTextFieldMail.setText(currentProvider.getEmail());
                    jTextFieldCodePrefix.setText(currentProvider.getCodePrefix());
                    jTextFieldPriceAdjustment.setText(currentProvider.getPriceAdjustmentFormula());
                    if ("CUIT".equalsIgnoreCase(currentProvider.getDocumentType())) {
                        jRadioButtonCuit.setSelected(true);
                    } else {
                        jRadioButtonDni.setSelected(true);
                    }
                    jCheckBoxActive.setSelected(currentProvider.isActive());
                }

            jButtonCancel.setEnabled(true);
            ponerEditableMenosSaldoInicial();
            editing = true;
            updateActionButtonsState();
            updateSaveButtonState();
        }
        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButtonUpdateActionPerformed

    public boolean validarFormulario(){

        // Valida que el nombre no este vacio
        if(this.jTextFieldName.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El nombre del proveedor no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldName.requestFocus();
            return false;
        }

        if(this.jTextFieldDniCuit.getText().equals("")){
            JOptionPane.showMessageDialog(this, "El cuit/dni del proveedor no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return false;
        }

        if (jRadioButtonCuit.isSelected()) {
            if (!DocumentValidator.isValidCuit(jTextFieldDniCuit.getText())) {
                JOptionPane.showMessageDialog(this, "El CUIT ingresado es inválido!", "Atencion", JOptionPane.WARNING_MESSAGE);
                jTextFieldDniCuit.requestFocus();
                return false;
            }
        } else {
            if (!DocumentValidator.isValidDni(jTextFieldDniCuit.getText())) {
                JOptionPane.showMessageDialog(this, "El DNI ingresado es inválido!", "Atencion", JOptionPane.WARNING_MESSAGE);
                jTextFieldDniCuit.requestFocus();
                return false;
            }
        }

        if(this.jComboBoxTaxCondition.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(this, "La condicion del proveedor no puede estar vacia!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jComboBoxTaxCondition.requestFocus();
            return false;
        }

        Provider existing = providerController.findByDocument(DocumentValidator.normalizeCuit(jTextFieldDniCuit.getText()));
        if(existing != null && (currentProvider == null || !existing.getId().equals(currentProvider.getId()))){
            JOptionPane.showMessageDialog(this, "Ya existe un proveedor con ese cuit/dni!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return false;
        }

        return true;
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if (!validarFormulario()) {
            return;
        }

        if (currentProvider == null) {
            currentProvider = new Provider();
        }

        currentProvider.setName(jTextFieldName.getText().toUpperCase());
        currentProvider.setDocumentType(jRadioButtonCuit.isSelected() ? "CUIT" : "DNI");
        String docSave = jTextFieldDniCuit.getText();
        if (jRadioButtonCuit.isSelected()) {
            docSave = DocumentValidator.normalizeCuit(docSave);
        }
        currentProvider.setDocumentNumber(docSave);
        currentProvider.setAddressNumber(jTextFieldNumberAddress.getText());
        currentProvider.setPhone(jTextFieldPhone.getText());
        currentProvider.setMobile(jTextFieldCelPhone.getText());
        currentProvider.setEmail(jTextFieldMail.getText());
        currentProvider.setCodePrefix(normalizeCodePrefix(jTextFieldCodePrefix.getText()));
        currentProvider.setPriceAdjustmentFormula(jTextFieldPriceAdjustment.getText());
        currentProvider.setActive(jCheckBoxActive.isSelected());

        List<Address> addresses = addressController.findAll();
        int addrIdx = jComboBoxAddress.getSelectedIndex();
        if (addrIdx > 0) {
            currentProvider.setAddress(addresses.get(addrIdx - 1));
        } else {
            currentProvider.setAddress(null);
        }

        List<City> cities = cityController.findAll();
        int cityIdx = jComboBoxCity.getSelectedIndex();
        if (cityIdx > 0) {
            currentProvider.setCity(cities.get(cityIdx - 1));
        } else {
            currentProvider.setCity(null);
        }

        List<TaxCondition> conditions = conditionController.findAll();
        int condIdx = jComboBoxTaxCondition.getSelectedIndex();
        if (condIdx > 0) {
            currentProvider.setTaxCondition(conditions.get(condIdx - 1));
        } else {
            currentProvider.setTaxCondition(null);
        }

        if (editing) {
            providerController.update(currentProvider);
            JOptionPane.showMessageDialog(this, "Proveedor actualizado");
        } else {
            providerController.save(currentProvider);
            JOptionPane.showMessageDialog(this, "Proveedor guardado");
        }

        try {
            loadTable(jTextFieldSearch.getText().toUpperCase());
        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }

        ponerNoEditable();
        limpiarFormulario();
        jButtonSave.setEnabled(false);
        jButtonCancel.setEnabled(false);
        currentProvider = null;
        editing = false;
        updateActionButtonsState();
    }//GEN-LAST:event_jButtonSaveActionPerformed


    public void limpiarFormulario(){

        jTextFieldName.setText("");
        jComboBoxAddress.setSelectedItem(null);
        jComboBoxCity.setSelectedItem(null);
        jComboBoxTaxCondition.setSelectedItem(null);
        jTextFieldDniCuit.setText("");
        jTextFieldNumberAddress.setText("");
        jTextFieldPhone.setText("");
        jTextFieldCelPhone.setText("");
        jTextFieldMail.setText("");
        jTextFieldCodePrefix.setText("");
        jTextFieldPriceAdjustment.setText("");
        jCheckBoxActive.setSelected(true);
        updateSaveButtonState();

    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        limpiarFormulario();
        ponerNoEditable();
        jButtonSave.setEnabled(false);
        jButtonCancel.setEnabled(false);
        currentProvider = null;
        editing = false;
        updateActionButtonsState();

}//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHistoryActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor", "Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ProviderHistoryView.isOpen) {
            return;
        }
        ProviderHistoryView view = new ProviderHistoryView();
        MainView.jDesktopPane1.add(view);
        view.setVisible(true);
    }//GEN-LAST:event_jButtonHistoryActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        if (currentProvider == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            updateActionButtonsState();
            return;
        }
        currentProvider = providerController.findById(currentProvider.getId());
        if (currentProvider == null) {
            JOptionPane.showMessageDialog(this, "El proveedor seleccionado ya no existe", "Eliminar", JOptionPane.WARNING_MESSAGE);
            updateActionButtonsState();
            return;
        }

        boolean hasAssociations = providerController.hasAssociations(currentProvider.getId());
        if (hasAssociations) {
            if (!currentProvider.isActive()) {
                JOptionPane.showMessageDialog(this,
                        "El proveedor posee comprobantes asociados y ya se encuentra deshabilitado",
                        "Eliminar", JOptionPane.INFORMATION_MESSAGE);
                updateActionButtonsState();
                return;
            }
            int disableOption = JOptionPane.showConfirmDialog(this,
                    "El proveedor tiene comprobantes asociados. ¿Desea deshabilitarlo?",
                    "Deshabilitar", JOptionPane.YES_NO_OPTION);
            if (disableOption == JOptionPane.YES_OPTION) {
                try {
                    currentProvider.setActive(false);
                    providerController.update(currentProvider);
                    refreshTable();
                    limpiarFormulario();
                    currentProvider = null;
                    JOptionPane.showMessageDialog(this, "Proveedor deshabilitado", "Deshabilitar", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Error deshabilitando el proveedor", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    updateActionButtonsState();
                }
            }
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el proveedor seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                providerController.delete(currentProvider.getId());
                refreshTable();
                limpiarFormulario();
                currentProvider = null;
                JOptionPane.showMessageDialog(this, "Proveedor eliminado", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error eliminando el proveedor", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                updateActionButtonsState();
            }
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonPaymentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPaymentsActionPerformed
        if (currentProvider == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor", "Pagos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!currentProvider.isActive()) {
            JOptionPane.showMessageDialog(this, "El proveedor está deshabilitado", "Pagos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ProviderReceiptInsertView.isOpen) {
            return;
        }

        try {
            ProviderReceiptInsertView view = new ProviderReceiptInsertView(currentProvider);
            int selectedRow = jTable1.getSelectedRow();
            String balanceText = "0.00";
            if (selectedRow >= 0) {
                Object value = jTable1.getValueAt(selectedRow, 3);
                if (value != null) {
                    balanceText = value.toString().trim();
                }
            }

            ProviderReceiptInsertView.jLabelDebe.setText(balanceText);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al abrir la pantalla de pagos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPaymentsActionPerformed

    private void jButtonAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdjustActionPerformed
        if (currentProvider == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor", "Ajuste", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!currentProvider.isActive()) {
            JOptionPane.showMessageDialog(this, "El proveedor está deshabilitado", "Ajuste", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (AdjustInsertView.isOpen) {
            return;
        }
        try {
            AdjustInsertView view = new AdjustInsertView(currentProvider);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ProviderManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonAdjustActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAdjust;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonHistory;
    public static javax.swing.JButton jButtonPayments;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JCheckBox jCheckBoxActive;
    public static javax.swing.JComboBox jComboBoxAddress;
    public static javax.swing.JComboBox jComboBoxCity;
    private javax.swing.JComboBox jComboBoxTaxCondition;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCodePrefix;
    private javax.swing.JLabel jLabelPriceAdjustment;
    public static javax.swing.JLabel jLabelTotalProviders;
    public static javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButtonCuit;
    private javax.swing.JRadioButton jRadioButtonDni;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneForm;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldCelPhone;
    private javax.swing.JTextField jTextFieldCodePrefix;
    private javax.swing.JTextField jTextFieldDniCuit;
    private javax.swing.JTextField jTextFieldMail;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNumberAddress;
    private javax.swing.JTextField jTextFieldPriceAdjustment;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables

}
