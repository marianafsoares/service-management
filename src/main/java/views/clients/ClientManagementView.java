package views.clients;

import controllers.ClientController;
import controllers.AddressController;
import controllers.CityController;
import controllers.TaxConditionController;
import configs.MyBatisConfig;
import mappers.AddressMapper;
import mappers.CityMapper;
import mappers.TaxConditionMapper;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.ClientRemitMapper;
import mappers.receipts.ClientReceiptMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.AddressRepository;
import repositories.CityRepository;
import repositories.TaxConditionRepository;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientReceiptRepository;
import repositories.ClientRemitRepository;
import repositories.impl.AddressRepositoryImpl;
import repositories.impl.CityRepositoryImpl;
import repositories.impl.TaxConditionRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import repositories.impl.ClientRemitRepositoryImpl;
import services.ClientService;
import services.AddressService;
import services.CityService;
import services.TaxConditionService;
import models.Client;
import models.Address;
import models.City;
import models.TaxCondition;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import views.AdjustInsertView;
import views.MainView;
import views.clients.ClientRemitManagementView;
import utils.DocumentValidator;
import utils.TableUtils;

public class ClientManagementView extends javax.swing.JInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(ClientManagementView.class.getName());
    private static final int BASE_WIDTH = 1150;
    private static final int BASE_HEIGHT = 650;
    private static final int HEIGHT_MARGIN = 40;
    private static final int BOTTOM_MARGIN = 20;
    private static final int MIN_TABLE_HEIGHT = 200;
    private static final int MIN_PANEL_HEIGHT = 240;
    private static final int TABLE_X = 50;
    private static final int TABLE_Y = 150;
    private static final int TABLE_WIDTH = 430;
    private static final int TABLE_HEIGHT = 370;
    private static final int PANEL_X = 540;
    private static final int PANEL_Y = 70;
    private static final int PANEL_WIDTH = 390;
    private static final int PANEL_HEIGHT = 500;
    private static final int RETURN_X = 420;
    private static final int RETURN_Y = 570;
    private static final int RETURN_WIDTH = 120;
    private static final int RETURN_HEIGHT = 30;
    private static final int BALANCE_X = 290;
    private static final int BALANCE_Y = 530;
    private static final int BALANCE_WIDTH = 160;
    private static final int BALANCE_HEIGHT = 30;
    private static final Set<Character> ALLOWED_NAME_SYMBOLS = Set.of('.', ',', '-', '_', '\'', '/', '&', '(', ')');

    private static ClientManagementView activeInstance;
    private static Consumer<Client> clientSelectionListener;
    private static boolean closeOnClientSelection;

    private ClientController clientController;
    private AddressController addressController;
    private CityController cityController;
    private TaxConditionController conditionController;
    private SqlSession sqlSession;
    private Client currentClient;
    private boolean editing = false;
    public static boolean isOpen = false;
    public static ArrayList clients = null;
    // public static Pagos paymentsWindow = null;
    // public static Ajuste adjustmentWindow = null;
    // public static RemitoCliente customerRemitoWindow = null;

    public ClientManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            AddressMapper addressMapper = sqlSession.getMapper(AddressMapper.class);
            CityMapper cityMapper = sqlSession.getMapper(CityMapper.class);
            TaxConditionMapper conditionMapper = sqlSession.getMapper(TaxConditionMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientInvoiceMapper clientInvoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
            ClientReceiptMapper clientReceiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
            ClientRemitMapper clientRemitMapper = sqlSession.getMapper(ClientRemitMapper.class);

            AddressRepository addressRepository = new AddressRepositoryImpl(addressMapper);
            CityRepository cityRepository = new CityRepositoryImpl(cityMapper);
            TaxConditionRepository conditionRepository = new TaxConditionRepositoryImpl(conditionMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientInvoiceRepository clientInvoiceRepository = new ClientInvoiceRepositoryImpl(clientInvoiceMapper);
            ClientReceiptRepository clientReceiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
            ClientRemitRepository clientRemitRepository = new ClientRemitRepositoryImpl(clientRemitMapper);

            AddressService addressService = new AddressService(addressRepository);
            CityService cityService = new CityService(cityRepository);
            TaxConditionService conditionService = new TaxConditionService(conditionRepository);
            ClientService clientService = new ClientService(clientRepository, clientInvoiceRepository, clientReceiptRepository, clientRemitRepository);

            this.addressController = new AddressController(addressService);
            this.cityController = new CityController(cityService);
            this.conditionController = new TaxConditionController(conditionService);
            this.clientController = new ClientController(clientService);
            initComponents();
            isOpen = true;
            activeInstance = this;

            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameOpened(InternalFrameEvent e) {
                    adjustHeightForLargeScreens();
                }

                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                    clearClientSelectionListener();
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                    clearClientSelectionListener();
                }
            });

            TableUtils.configureClientManagementViewTable(jTable1);

            ClientTableCellRenderer clientRenderer = new ClientTableCellRenderer();
            jTable1.setDefaultRenderer(Object.class, clientRenderer);
            jTable1.setDefaultRenderer(Number.class, clientRenderer);

            jCheckBoxActive.setSelected(true);
            jCheckBoxActive.setEnabled(false);

            allowNameCharacters(jTextFieldName);

            loadTable("");

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateLayoutForHeight(getHeight());
                }
            });

            SwingUtilities.invokeLater(this::adjustHeightForLargeScreens);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void allowOnlyNumbers(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (field == jTextFieldDniCuit && jRadioButtonCuit.isSelected() && c == '-') {
                    return;
                }
                if (!Character.isDigit(c)) {
                    getToolkit().beep();
                    e.consume();
                }
            }

        });
    }

    public void allowNameCharacters(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
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

    public void loadAddresses() {
        addressController.loadAddresses(jComboBoxAddress);
    }

    public void loadCities() {
        cityController.loadCities(jComboBoxCity);
    }

    public void loadConditions() {
        conditionController.loadConditions(jComboBoxTaxCondition);
    }

    public void refreshTable() throws Exception {
        loadTable("");
        // jTable1.setDefaultRenderer(Object.class, new FormatoTablaRemitosAbiertos());
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
        jButtonModify = new javax.swing.JButton();
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
        jTextFieldAddressNumber = new javax.swing.JTextField();
        jRadioButtonCuit = new javax.swing.JRadioButton();
        jRadioButtonDni = new javax.swing.JRadioButton();
        jComboBoxTaxCondition = new javax.swing.JComboBox();
        jTextFieldDniCuit = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldSubscription = new javax.swing.JTextField();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jCheckBoxActive = new javax.swing.JCheckBox();
        jCheckBoxFxBilling = new javax.swing.JCheckBox();
        jButtonRemit = new javax.swing.JButton();
        jButtonHistory = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jButtonList = new javax.swing.JButton();
        jButtonAdjust = new javax.swing.JButton();
        jLabelsaldoDeudor = new javax.swing.JLabel();
        jButtonPayments = new javax.swing.JButton();

        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(50, 80, 44, 20);

        jTextFieldSearch.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldSearch);
        jTextFieldSearch.setBounds(100, 80, 320, 21);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Celular", "Saldo"
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
        jButtonAdd.setBounds(30, 20, 130, 30);

        jButtonModify.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonModify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modificar.png"))); // NOI18N
        jButtonModify.setText("Modificar");
        jButtonModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModifyActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonModify);
        jButtonModify.setBounds(170, 20, 130, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N
        jPanel1.setEnabled(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(PANEL_WIDTH - 20, PANEL_HEIGHT));
        jPanel1.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Nombre");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(40, 50, 90, 20);

        jTextFieldName.setEditable(false);
        jTextFieldName.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldName);
        jTextFieldName.setBounds(160, 50, 190, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Ciudad");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(40, 80, 90, 20);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Direccion");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(40, 110, 90, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Telefono");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(40, 230, 90, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Celular");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(40, 260, 90, 20);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Mail");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(40, 290, 90, 20);

        jTextFieldPhone.setEditable(false);
        jTextFieldPhone.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldPhone);
        jTextFieldPhone.setBounds(160, 230, 190, 20);

        jTextFieldCelPhone.setEditable(false);
        jTextFieldCelPhone.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldCelPhone);
        jTextFieldCelPhone.setBounds(160, 260, 190, 20);

        jTextFieldMail.setEditable(false);
        jTextFieldMail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldMail);
        jTextFieldMail.setBounds(160, 290, 190, 20);

        jComboBoxAddress.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxAddress);
        jComboBoxAddress.setBounds(160, 110, 190, 20);

        jComboBoxCity.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxCity);
        jComboBoxCity.setBounds(160, 80, 190, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Numero");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(40, 140, 90, 20);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Condicion");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(40, 170, 90, 20);

        jTextFieldAddressNumber.setEditable(false);
        jTextFieldAddressNumber.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldAddressNumber);
        jTextFieldAddressNumber.setBounds(160, 140, 190, 20);

        jRadioButtonCuit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonCuit.setText("Cuit");
        jPanel1.add(jRadioButtonCuit);
        jRadioButtonCuit.setBounds(20, 200, 60, 20);

        jRadioButtonDni.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jRadioButtonDni.setText("Dni");
        jPanel1.add(jRadioButtonDni);
        jRadioButtonDni.setBounds(80, 200, 70, 20);

        jComboBoxTaxCondition.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jComboBoxTaxCondition);
        jComboBoxTaxCondition.setBounds(160, 170, 190, 21);

        jTextFieldDniCuit.setEditable(false);
        jTextFieldDniCuit.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldDniCuit);
        jTextFieldDniCuit.setBounds(160, 200, 190, 20);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Abono");
        jPanel1.add(jLabel15);
        jLabel15.setBounds(40, 320, 90, 20);

        jTextFieldSubscription.setEditable(false);
        jTextFieldSubscription.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldSubscription);
        jTextFieldSubscription.setBounds(160, 320, 190, 20);

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

        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("*");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(130, 200, 30, 20);

        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("*");
        jPanel1.add(jLabel13);
        jLabel13.setBounds(130, 50, 30, 20);

        jLabel14.setForeground(new java.awt.Color(255, 0, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("*");
        jPanel1.add(jLabel14);
        jLabel14.setBounds(130, 170, 30, 20);

        jCheckBoxActive.setText("Activo");
        jPanel1.add(jCheckBoxActive);
        jCheckBoxActive.setBounds(160, 380, 150, 20);

        jCheckBoxFxBilling.setText("Facturar como FX");
        jPanel1.add(jCheckBoxFxBilling);
        jCheckBoxFxBilling.setBounds(160, 350, 190, 20);

        jScrollPaneForm.setBorder(null);
        jScrollPaneForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneForm.setViewportView(jPanel1);
        jScrollPaneForm.getVerticalScrollBar().setUnitIncrement(16);

        getContentPane().add(jScrollPaneForm);
        jScrollPaneForm.setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

        jButtonRemit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonRemit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/remitoCliente.png"))); // NOI18N
        jButtonRemit.setText("Remitos");
        jButtonRemit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemitActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonRemit);
        jButtonRemit.setBounds(870, 20, 160, 30);

        jButtonHistory.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Historial.png"))); // NOI18N
        jButtonHistory.setText("Historial");
        jButtonHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHistoryActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonHistory);
        jButtonHistory.setBounds(310, 20, 130, 30);

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

        jButtonList.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/listado.png"))); // NOI18N
        jButtonList.setText("Listado ");
        jButtonList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonList);
        jButtonList.setBounds(450, 20, 130, 30);

        jButtonAdjust.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdjust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ajuste.png"))); // NOI18N
        jButtonAdjust.setText("Ajuste");
        jButtonAdjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdjustActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdjust);
        jButtonAdjust.setBounds(590, 20, 130, 30);


        jLabelsaldoDeudor.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabelsaldoDeudor.setForeground(new java.awt.Color(204, 0, 0));
        jLabelsaldoDeudor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        getContentPane().add(jLabelsaldoDeudor);
        jLabelsaldoDeudor.setBounds(BALANCE_X, BALANCE_Y, BALANCE_WIDTH, BALANCE_HEIGHT);

        jButtonPayments.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPayments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cobranza.png"))); // NOI18N
        jButtonPayments.setText("Cobranza");
        jButtonPayments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPaymentsActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPayments);
        jButtonPayments.setBounds(730, 20, 130, 30);

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
        jLabelsaldoDeudor.setBounds(BALANCE_X, labelY, BALANCE_WIDTH, BALANCE_HEIGHT);
    }

    public void loadTable(String value) throws ClassNotFoundException, SQLException {
        BigDecimal totalBalance = clientController.fillTable(jTable1, value);
        jLabelsaldoDeudor.setText(clientController.formatCurrency(totalBalance));
        Object property = jTable1.getClientProperty("clientsList");
        if (!(property instanceof List<?>)) {
            currentClient = null;
        } else if (currentClient != null) {
            List<?> clients = (List<?>) property;
            boolean found = false;
            for (Object obj : clients) {
                if (obj instanceof Client) {
                    Client listed = (Client) obj;
                    if (listed.getId() != null && listed.getId().equals(currentClient.getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                currentClient = null;
            }
        }
        updateActionButtonsState();
    }

    private void updateActionButtonsState() {
        boolean clientSelected = currentClient != null;
        boolean clientActive = clientSelected && currentClient.isActive();
        jButtonPayments.setEnabled(clientActive);
        jButtonAdjust.setEnabled(clientActive);
    }

    public static void setClientSelectionListener(Consumer<Client> listener, boolean closeAfterSelection) {
        clientSelectionListener = listener;
        closeOnClientSelection = closeAfterSelection;
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
            LOGGER.log(Level.FINE, "No se pudo seleccionar la vista de clientes", ex);
        }
        activeInstance.toFront();
        activeInstance.requestFocus();
    }

    private static void clearClientSelectionListener() {
        clientSelectionListener = null;
        closeOnClientSelection = false;
    }

    private void handleClientDoubleClick(int rowIndex) {
        Client client = currentClient;
        if (client == null || client.getId() == null
                || !client.getId().equals(jTable1.getValueAt(rowIndex, 0))) {
            client = getClientFromRow(rowIndex);
            if (client == null) {
                Object value = jTable1.getValueAt(rowIndex, 0);
                if (value instanceof Integer) {
                    client = clientController.findById((Integer) value);
                }
            }
        }
        if (client != null) {
            notifyClientSelected(client);
        }
    }

    private Client getClientFromRow(int rowIndex) {
        Object property = jTable1.getClientProperty("clientsList");
        if (!(property instanceof List<?>)) {
            return null;
        }
        List<?> clients = (List<?>) property;
        if (rowIndex < 0 || rowIndex >= clients.size()) {
            return null;
        }
        Object candidate = clients.get(rowIndex);
        return candidate instanceof Client ? (Client) candidate : null;
    }

    private void notifyClientSelected(Client client) {
        Consumer<Client> listener = clientSelectionListener;
        boolean shouldClose = closeOnClientSelection;
        clearClientSelectionListener();
        if (listener != null) {
            try {
                listener.accept(client);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error al notificar la selección de cliente", ex);
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
            LOGGER.log(Level.FINE, "No se pudo cerrar la vista de clientes", ex);
            activeInstance.dispose();
        }
    }

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchKeyReleased
        try {
            loadTable(jTextFieldSearch.getText().toUpperCase());
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jTextFieldSearchKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        if (evt.getClickCount() == 2) {
            handleClientDoubleClick(selectedRow);
            return;
        }
        if (evt.getClickCount() == 1) {
            try {
                loadAddresses();
                loadCities();
                loadConditions();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            int id = (int) jTable1.getValueAt(selectedRow, 0);
            currentClient = clientController.findById(id);
            if (currentClient != null) {
                jTextFieldName.setText(currentClient.getFullName());
                if (currentClient.getAddress() != null) {
                    jComboBoxAddress.setSelectedItem(currentClient.getAddress().getName());
                } else {
                    jComboBoxAddress.setSelectedItem(null);
                }
                if (currentClient.getCity() != null) {
                    jComboBoxCity.setSelectedItem(currentClient.getCity().getName());
                } else {
                    jComboBoxCity.setSelectedItem(null);
                }
                if (currentClient.getTaxCondition() != null) {
                    jComboBoxTaxCondition.setSelectedItem(currentClient.getTaxCondition().getName());
                } else {
                    jComboBoxTaxCondition.setSelectedItem(null);
                }
                String doc = currentClient.getDocumentNumber();
                if ("CUIT".equalsIgnoreCase(currentClient.getDocumentType())) {
                    doc = DocumentValidator.formatCuit(doc);
                }
                jTextFieldDniCuit.setText(doc);
                jTextFieldAddressNumber.setText(currentClient.getAddressNumber());
                jTextFieldPhone.setText(currentClient.getPhone());
                jTextFieldCelPhone.setText(currentClient.getMobile());
                jTextFieldMail.setText(currentClient.getEmail());
                buttonGroup1.add(jRadioButtonCuit);
                buttonGroup1.add(jRadioButtonDni);
                if ("CUIT".equalsIgnoreCase(currentClient.getDocumentType())) {
                    jRadioButtonCuit.setSelected(true);
                } else {
                    jRadioButtonDni.setSelected(true);
                }
                jCheckBoxFxBilling.setSelected(currentClient.isFxBilling());
                jCheckBoxActive.setSelected(currentClient.isActive());
            } else {
                jCheckBoxActive.setSelected(true);
            }
            jCheckBoxActive.setEnabled(false);
            disableFields();
            jButtonSave.setEnabled(false);
            jButtonCancel.setEnabled(false);
            editing = false;
            updateActionButtonsState();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        try {
            loadAddresses();
            loadCities();
            loadConditions();

            allowOnlyNumbers(jTextFieldDniCuit);
            allowOnlyNumbers(jTextFieldPhone);
            allowOnlyNumbers(jTextFieldCelPhone);

            buttonGroup1.add(jRadioButtonCuit);
            buttonGroup1.add(jRadioButtonDni);

            limpiarFormulario();
            jButtonSave.setEnabled(true);
            jButtonCancel.setEnabled(true);
            enableFields();
            currentClient = null;
            editing = false;
            updateActionButtonsState();
        } catch (Exception ex) {
            Logger.getLogger(ClientManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void enableFields() {

        jTextFieldName.setEditable(true);
        jTextFieldName.requestFocus();

        jComboBoxAddress.setEnabled(true);
        jComboBoxCity.setEnabled(true);
        jComboBoxTaxCondition.setEnabled(true);

        jTextFieldDniCuit.setEditable(true);
        jTextFieldAddressNumber.setEditable(true);
        jTextFieldPhone.setEditable(true);
        jTextFieldCelPhone.setEditable(true);
        jTextFieldMail.setEditable(true);
        jTextFieldSubscription.setEditable(true);

        jButtonAdd.setSelected(true);
        jButtonModify.setSelected(true);
        jCheckBoxActive.setEnabled(true);
        jCheckBoxFxBilling.setEnabled(true);
    }

    private void disableFields() {

        jTextFieldName.setEditable(false);
        jTextFieldName.requestFocus();

        jComboBoxAddress.setEnabled(false);
        jComboBoxCity.setEnabled(false);
        jComboBoxTaxCondition.setEnabled(false);

        jTextFieldDniCuit.setEditable(false);
        jTextFieldAddressNumber.setEditable(false);
        jTextFieldPhone.setEditable(false);
        jTextFieldCelPhone.setEditable(false);
        jTextFieldMail.setEditable(false);
        jTextFieldSubscription.setEditable(false);

        jButtonAdd.setSelected(false);
        jButtonModify.setSelected(false);
        jCheckBoxActive.setEnabled(false);
        jCheckBoxFxBilling.setEnabled(false);
    }

    private void jButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModifyActionPerformed
        try {
            int i = ClientManagementView.jTable1.getSelectedRow();

            if (i == -1) {
                JOptionPane.showMessageDialog(this, "No ha seleccionado ningun cliente", "Modificar", JOptionPane.OK_OPTION);
            } else {

                loadAddresses();
                loadCities();
                loadConditions();

                allowOnlyNumbers(jTextFieldDniCuit);
                allowOnlyNumbers(jTextFieldPhone);
                allowOnlyNumbers(jTextFieldCelPhone);

                buttonGroup1.add(jRadioButtonCuit);
                buttonGroup1.add(jRadioButtonDni);

                jButtonSave.setEnabled(true);
                jButtonCancel.setEnabled(true);
                int id = (int) jTable1.getValueAt(i, 0);
                currentClient = clientController.findById(id);
                if (currentClient != null) {
                    jTextFieldName.setText(currentClient.getFullName());
                    if (currentClient.getAddress() != null) {
                        jComboBoxAddress.setSelectedItem(currentClient.getAddress().getName());
                    }
                    if (currentClient.getCity() != null) {
                        jComboBoxCity.setSelectedItem(currentClient.getCity().getName());
                    }
                    if (currentClient.getTaxCondition() != null) {
                        jComboBoxTaxCondition.setSelectedItem(currentClient.getTaxCondition().getName());
                    }
                    String doc2 = currentClient.getDocumentNumber();
                    if ("CUIT".equalsIgnoreCase(currentClient.getDocumentType())) {
                        doc2 = DocumentValidator.formatCuit(doc2);
                    }
                    jTextFieldDniCuit.setText(doc2);
                    jTextFieldAddressNumber.setText(currentClient.getAddressNumber());
                    jTextFieldPhone.setText(currentClient.getPhone());
                    jTextFieldCelPhone.setText(currentClient.getMobile());
                    jTextFieldMail.setText(currentClient.getEmail());
                    if (currentClient.getSubscriptionAmount() != null) {
                        jTextFieldSubscription.setText(currentClient.getSubscriptionAmount().toPlainString());
                    } else {
                        jTextFieldSubscription.setText("");
                    }
                    jCheckBoxFxBilling.setSelected(currentClient.isFxBilling());
                    if ("CUIT".equalsIgnoreCase(currentClient.getDocumentType())) {
                        jRadioButtonCuit.setSelected(true);
                    } else {
                        jRadioButtonDni.setSelected(true);
                    }
                    jCheckBoxActive.setSelected(currentClient.isActive());
                } else {
                    jCheckBoxActive.setSelected(true);
                }
                enableFields();
                editing = true;
                updateActionButtonsState();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButtonModifyActionPerformed

    public boolean validateForm() {

        // Valida que el nombre no este vacio
        if (this.jTextFieldName.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El nombre del cliente no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldName.requestFocus();
            return (false);
        }

        if (this.jTextFieldDniCuit.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El cuit/dni del cliente no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return (false);
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

        if (this.jComboBoxTaxCondition.getSelectedItem().equals("")) {
            JOptionPane.showMessageDialog(this, "La condicion del cliente no puede estar vacia!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return (false);
        }

        Client existing = clientController.findByDocument(DocumentValidator.normalizeCuit(jTextFieldDniCuit.getText()));
        if (existing != null && (currentClient == null || !existing.getId().equals(currentClient.getId()))) {
            JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese cuit/dni!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDniCuit.requestFocus();
            return false;
        }

        return true;
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if (!validateForm()) {
            return;
        }

        if (currentClient == null) {
            currentClient = new Client();
        }

        currentClient.setFullName(jTextFieldName.getText().toUpperCase());
        currentClient.setDocumentType(jRadioButtonCuit.isSelected() ? "CUIT" : "DNI");
        String doc = jTextFieldDniCuit.getText();
        if (jRadioButtonCuit.isSelected()) {
            doc = DocumentValidator.normalizeCuit(doc);
        }
        currentClient.setDocumentNumber(doc);
        currentClient.setAddressNumber(jTextFieldAddressNumber.getText());
        currentClient.setPhone(jTextFieldPhone.getText());
        currentClient.setMobile(jTextFieldCelPhone.getText());
        currentClient.setEmail(jTextFieldMail.getText());
        String subscriptionText = jTextFieldSubscription.getText();
        if (subscriptionText != null && !subscriptionText.isBlank()) {
            try {
                currentClient.setSubscriptionAmount(new BigDecimal(subscriptionText.replace(',', '.')));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El abono ingresado no es válido", "Atencion", JOptionPane.WARNING_MESSAGE);
                jTextFieldSubscription.requestFocus();
                return;
            }
        } else {
            currentClient.setSubscriptionAmount(null);
        }
        currentClient.setFxBilling(jCheckBoxFxBilling.isSelected());
        currentClient.setActive(jCheckBoxActive.isSelected());

        List<Address> addresses = addressController.findAll();
        int addrIdx = jComboBoxAddress.getSelectedIndex();
        if (addrIdx > 0) {
            currentClient.setAddress(addresses.get(addrIdx - 1));
        } else {
            currentClient.setAddress(null);
        }

        List<City> cities = cityController.findAll();
        int cityIdx = jComboBoxCity.getSelectedIndex();
        if (cityIdx > 0) {
            currentClient.setCity(cities.get(cityIdx - 1));
        } else {
            currentClient.setCity(null);
        }

        List<TaxCondition> conditions = conditionController.findAll();
        int condIdx = jComboBoxTaxCondition.getSelectedIndex();
        if (condIdx > 0) {
            currentClient.setTaxCondition(conditions.get(condIdx - 1));
        } else {
            currentClient.setTaxCondition(null);
        }

        if (editing) {
            clientController.update(currentClient);
            JOptionPane.showMessageDialog(this, "Cliente actualizado");
        } else {
            clientController.save(currentClient);
            JOptionPane.showMessageDialog(this, "Cliente guardado");
        }

        try {
            loadTable(jTextFieldSearch.getText().toUpperCase());
        } catch (Exception ex) {
            Logger.getLogger(ClientManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }

        disableFields();
        limpiarFormulario();
        jButtonSave.setEnabled(false);
        jButtonCancel.setEnabled(false);
        currentClient = null;
        editing = false;
        updateActionButtonsState();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    public void limpiarFormulario() {

        jTextFieldName.setText("");
        jComboBoxAddress.setSelectedItem(null);
        jComboBoxCity.setSelectedItem(null);
        jComboBoxTaxCondition.setSelectedItem(null);
        jTextFieldDniCuit.setText("");
        jTextFieldAddressNumber.setText("");
        jTextFieldPhone.setText("");
        jTextFieldCelPhone.setText("");
        jTextFieldMail.setText("");
        jTextFieldSubscription.setText("");
        jCheckBoxActive.setSelected(true);
        jCheckBoxFxBilling.setSelected(false);

    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        disableFields();
        limpiarFormulario();
        jButtonSave.setEnabled(false);
        jButtonCancel.setEnabled(false);
        currentClient = null;
        editing = false;
        updateActionButtonsState();
}//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonRemitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemitActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Remitos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Client client = currentClient;
        Object tableId = jTable1.getValueAt(selectedRow, 0);
        if (client == null || client.getId() == null || !client.getId().equals(tableId)) {
            if (tableId instanceof Integer) {
                client = clientController.findById((Integer) tableId);
            } else {
                client = null;
            }
        }

        if (client == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el cliente seleccionado",
                    "Remitos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ClientRemitManagementView.isOpen) {
            ClientRemitManagementView view = new ClientRemitManagementView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
            view.setSelectedClient(client);
        } else {
            ClientRemitManagementView.setActiveClient(client);
        }
    }//GEN-LAST:event_jButtonRemitActionPerformed

    private void jButtonHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHistoryActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Historial", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ClientHistoryView.isOpen) {
            return;
        }
        ClientHistoryView view = new ClientHistoryView();
        MainView.jDesktopPane1.add(view);
        view.setVisible(true);
    }//GEN-LAST:event_jButtonHistoryActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListActionPerformed
        // Feature disabled pending refactor
    }//GEN-LAST:event_jButtonListActionPerformed

    private void jButtonAdjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdjustActionPerformed
        if (currentClient == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Ajuste", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!currentClient.isActive()) {
            JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Ajuste", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (AdjustInsertView.isOpen) {
            return;
        }
        try {
            AdjustInsertView view = new AdjustInsertView(currentClient);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ClientManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonAdjustActionPerformed


    private void jButtonPaymentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPaymentsActionPerformed
        if (currentClient == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Cobranza", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!currentClient.isActive()) {
            JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Cobranza", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ClientReceiptInsertView.isOpen) {
            return;
        }

        try {
            ClientReceiptInsertView view = new ClientReceiptInsertView();
            int selectedRow = jTable1.getSelectedRow();
            String balanceText = "0.00";
            if (selectedRow >= 0) {
                Object value = jTable1.getValueAt(selectedRow, 3);
                if (value != null) {
                    balanceText = value.toString().trim();
                }
            }

            ClientReceiptInsertView.jLabelDebe.setText(balanceText);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ClientManagementView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al abrir la pantalla de cobranza", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPaymentsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAdjust;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonHistory;
    private javax.swing.JButton jButtonList;
    private javax.swing.JButton jButtonModify;
    public static javax.swing.JButton jButtonPayments;
    private javax.swing.JButton jButtonRemit;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JCheckBox jCheckBoxActive;
    public static javax.swing.JComboBox jComboBoxAddress;
    public static javax.swing.JComboBox jComboBoxCity;
    private javax.swing.JComboBox jComboBoxTaxCondition;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabelsaldoDeudor;
    public static javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox jCheckBoxFxBilling;
    private javax.swing.JRadioButton jRadioButtonCuit;
    private javax.swing.JRadioButton jRadioButtonDni;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneForm;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldAddressNumber;
    private javax.swing.JTextField jTextFieldCelPhone;
    private javax.swing.JTextField jTextFieldDniCuit;
    private javax.swing.JTextField jTextFieldMail;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTextField jTextFieldSubscription;
    // End of variables declaration//GEN-END:variables

}
