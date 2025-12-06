
package views;

import controllers.BrandController;
import controllers.CategoryController;
import controllers.ProductController;
import controllers.ProviderController;
import controllers.SubcategoryController;
import configs.AppConfig;
import configs.MyBatisConfig;
import mappers.BrandMapper;
import mappers.CategoryMapper;
import mappers.ProductMapper;
import mappers.ProviderMapper;
import mappers.SubcategoryMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.BrandRepository;
import repositories.CategoryRepository;
import repositories.ProductRepository;
import repositories.ProviderRepository;
import repositories.SubcategoryRepository;
import repositories.impl.BrandRepositoryImpl;
import repositories.impl.CategoryRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.SubcategoryRepositoryImpl;
import services.BrandService;
import services.CategoryService;
import services.ProductService;
import services.ProviderService;
import services.SubcategoryService;
import services.backup.BackupException;
import services.backup.DatabaseBackupService;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import utils.BackgroundImage;

import views.accountant.PurchasesVatBookView;
import views.accountant.SalesVatBookView;
import views.clients.ClientAutomaticInvoiceView;
import views.clients.ClientBudgetManagementView;
import views.clients.ClientInvoiceInsertView;
import views.clients.ClientInvoiceManagementView;
import views.clients.ClientManagementView;
import views.clients.ClientReceiptManagementView;
import views.products.ProductManagementView;
import views.providers.ProviderInvoiceManagementView;
import views.providers.ProviderManagementView;
import views.providers.ProviderReceiptManagementView;
import views.CheckSearchView;
import views.utils.AddressManagement;
import views.utils.CityManagement;
import views.utils.BankManagement;
import views.utils.BrandManagement;
import views.utils.CardManagement;
import views.utils.InvoiceCategoryManagement;
import views.utils.ProductCategoryManagement;
import views.utils.ProductSubcategoryManagement;

public class MainView extends JFrame {

    private ProductManagementView productManagementView;
    private ClientManagementView clientManagementView;
    private ClientInvoiceInsertView clientInvoiceInsertView;
    private ClientAutomaticInvoiceView clientAutomaticInvoiceView;
    private ClientBudgetManagementView clientBudgetManagementView;
    private ClientInvoiceManagementView clientInvoiceManagementView;
    private ClientReceiptManagementView clientReceiptManagementView;
    private ProviderManagementView providerManagementView;
    private ProviderInvoiceManagementView providerInvoiceManagementView;
    private ProviderReceiptManagementView providerReceiptManagementView;
    private SalesVatBookView salesVatBookView;
    private PurchasesVatBookView purchasesVatBookView;
    private CheckSearchView checkSearchView;
    private CityManagement cityManagementView;
    private AddressManagement addressManagementView;
    private InvoiceCategoryManagement invoiceCategoryManagementView;
    private BrandManagement brandManagementView;
    private ProductCategoryManagement productCategoryManagementView;
    private ProductSubcategoryManagement productSubcategoryManagementView;
    private BankManagement bankManagementView;
    private CardManagement cardManagementView;
    private final java.util.concurrent.atomic.AtomicInteger backgroundTaskCount = new java.util.concurrent.atomic.AtomicInteger(0);
    private final AtomicBoolean closing = new AtomicBoolean(false);
    private final DatabaseBackupService backupService = new DatabaseBackupService();
    private final boolean notifyBackupSuccess =
            Boolean.parseBoolean(AppConfig.get("backup.notify.success", "false"));

    /** Creates new form Principal */
    public MainView() throws Exception {

            initComponents();
            jDesktopPane1.setBorder(new BackgroundImage());
            this.setExtendedState(MainView.MAXIMIZED_BOTH);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    exitApplication();
                }
            });
    }


   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuProductManagement = new javax.swing.JMenu();
        jMenuClientManagement = new javax.swing.JMenu();
        jMenuNewInvoiceClient = new javax.swing.JMenu();
        jMenuItemManualInvoice = new javax.swing.JMenuItem();
        jMenuItemAutomaticInvoice = new javax.swing.JMenuItem();
        jMenuAccountant = new javax.swing.JMenu();
        jMenuItemClientReceiptManagement = new javax.swing.JMenuItem();
        jMenuItemSalesVATBook = new javax.swing.JMenuItem();
        jMenuItemPurchasesVATBook = new javax.swing.JMenuItem();
        jMenuItemSearchCheques = new javax.swing.JMenuItem();
        jMenuClientBudgetManagement = new javax.swing.JMenu();
        jMenuClientInvoceSearch = new javax.swing.JMenu();
        jMenuPurchases = new javax.swing.JMenu();
        jMenuItemProviderManagement = new javax.swing.JMenuItem();
        jMenuItemProviderInvoiceManagement = new javax.swing.JMenuItem();
        jMenuItemProviderReceiptManagement = new javax.swing.JMenuItem();
        jMenuMaintenance = new javax.swing.JMenu();
        jMenuItemCityManagement = new javax.swing.JMenuItem();
        jMenuItemAddressManagement = new javax.swing.JMenuItem();
        jMenuItemInvoiceCategoryManagement = new javax.swing.JMenuItem();
        jMenuItemProductCategoryManagement = new javax.swing.JMenuItem();
        jMenuItemProductSubcategoryManagement = new javax.swing.JMenuItem();
        jMenuItemBrandManagement = new javax.swing.JMenuItem();
        jMenuItemBankManagement = new javax.swing.JMenuItem();
        jMenuItemCardManagement = new javax.swing.JMenuItem();
        jMenuExit = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jDesktopPane1.setBackground(new java.awt.Color(255, 255, 255));
        jDesktopPane1.setAutoscrolls(true);
        jDesktopPane1.setPreferredSize(new java.awt.Dimension(790, 473));
        jDesktopPane1.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                jDesktopPane1ComponentAdded(evt);
            }
        });

        jMenuBar1.setMaximumSize(new java.awt.Dimension(900, 49000));

        jMenuProductManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Articulos.png"))); // NOI18N
        jMenuProductManagement.setText("Articulos");
        jMenuProductManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuProductManagement.setMaximumSize(new java.awt.Dimension(125, 1500));
        jMenuProductManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuProductManagementMouseClicked(evt);
            }
        });
        jMenuProductManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuProductManagementActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenuProductManagement);

        jMenuClientManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Clientes.png"))); // NOI18N
        jMenuClientManagement.setText("Clientes");
        jMenuClientManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuClientManagement.setMaximumSize(new java.awt.Dimension(125, 1500));
        jMenuClientManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuClientManagementMouseClicked(evt);
            }
        });
        jMenuClientManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuClientManagementActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenuClientManagement);

        jMenuNewInvoiceClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Ventas.png"))); // NOI18N
        jMenuNewInvoiceClient.setText("Ventas");
        jMenuNewInvoiceClient.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuNewInvoiceClient.setMaximumSize(new java.awt.Dimension(125, 1500));
        jMenuNewInvoiceClient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuNewInvoiceClientMouseClicked(evt);
            }
        });
        jMenuNewInvoiceClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewInvoiceClientActionPerformed(evt);
            }
        });
        
        jMenuItemManualInvoice.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemManualInvoice.setText("Factura manual");
        jMenuItemManualInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemManualInvoiceActionPerformed(evt);
            }
        });
        jMenuNewInvoiceClient.add(jMenuItemManualInvoice);

        jMenuItemAutomaticInvoice.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemAutomaticInvoice.setText("Facturación automática");
        jMenuItemAutomaticInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAutomaticInvoiceActionPerformed(evt);
            }
        });
        jMenuNewInvoiceClient.add(jMenuItemAutomaticInvoice);
        jMenuBar1.add(jMenuNewInvoiceClient);

        jMenuAccountant.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Caja.png"))); // NOI18N
        jMenuAccountant.setText("Caja");
        jMenuAccountant.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuAccountant.setMaximumSize(new java.awt.Dimension(125, 1500));
        jMenuAccountant.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuAccountantMouseClicked(evt);
            }
        });
        jMenuAccountant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAccountantActionPerformed(evt);
            }
        });

        jMenuItemClientReceiptManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemClientReceiptManagement.setText("Maestro de Recibos");
        jMenuItemClientReceiptManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItemClientReceiptManagementMouseClicked(evt);
            }
        });
        jMenuItemClientReceiptManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClientReceiptManagementActionPerformed(evt);
            }
        });
        jMenuAccountant.add(jMenuItemClientReceiptManagement);

        jMenuItemSalesVATBook.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemSalesVATBook.setText("Iva Ventas");
        jMenuItemSalesVATBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalesVATBookActionPerformed(evt);
            }
        });
        jMenuAccountant.add(jMenuItemSalesVATBook);

        jMenuItemPurchasesVATBook.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemPurchasesVATBook.setText("Iva Compras");
        jMenuItemPurchasesVATBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPurchasesVATBookActionPerformed(evt);
            }
        });
        jMenuAccountant.add(jMenuItemPurchasesVATBook);

        jMenuItemSearchCheques.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemSearchCheques.setText("Consulta Cheques");
        jMenuItemSearchCheques.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSearchChequesActionPerformed(evt);
            }
        });
        jMenuAccountant.add(jMenuItemSearchCheques);

        jMenuBar1.add(jMenuAccountant);

        jMenuClientBudgetManagement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Informes.png"))); // NOI18N
        jMenuClientBudgetManagement.setText("Presupuestos");
        jMenuClientBudgetManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuClientBudgetManagement.setPreferredSize(new java.awt.Dimension(120, 48));
        jMenuClientBudgetManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuClientBudgetManagementMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuClientBudgetManagement);

        jMenuClientInvoceSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/facturacion.png"))); // NOI18N
        jMenuClientInvoceSearch.setText("Facturacion");
        jMenuClientInvoceSearch.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuClientInvoceSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuClientInvoceSearchMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuClientInvoceSearch);

        jMenuPurchases.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Compras.png"))); // NOI18N
        jMenuPurchases.setText("Compras");
        jMenuPurchases.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuPurchases.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuPurchasesMouseClicked(evt);
            }
        });
        jMenuPurchases.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuPurchasesActionPerformed(evt);
            }
        });

        jMenuItemProviderManagement.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jMenuItemProviderManagement.setText("Proveedores");
        jMenuItemProviderManagement.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItemProviderManagementMouseClicked(evt);
            }
        });
        jMenuItemProviderManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProviderManagementActionPerformed(evt);
            }
        });
        jMenuPurchases.add(jMenuItemProviderManagement);

        jMenuItemProviderInvoiceManagement.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jMenuItemProviderInvoiceManagement.setText("Maestro de facturas");
        jMenuItemProviderInvoiceManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProviderInvoiceManagementActionPerformed(evt);
            }
        });
        jMenuPurchases.add(jMenuItemProviderInvoiceManagement);

        jMenuItemProviderReceiptManagement.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jMenuItemProviderReceiptManagement.setText("Maestro de recibos");
        jMenuItemProviderReceiptManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProviderReceiptManagementActionPerformed(evt);
            }
        });
        jMenuPurchases.add(jMenuItemProviderReceiptManagement);

        jMenuBar1.add(jMenuPurchases);

        jMenuMaintenance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        jMenuMaintenance.setText("Mantenimiento");
        jMenuMaintenance.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        jMenuItemCityManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemCityManagement.setText("Ciudades");
        jMenuItemCityManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCityManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemCityManagement);

        jMenuItemAddressManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemAddressManagement.setText("Direcciones");
        jMenuItemAddressManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddressManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemAddressManagement);

        jMenuItemInvoiceCategoryManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemInvoiceCategoryManagement.setText("Categorias de facturas");
        jMenuItemInvoiceCategoryManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInvoiceCategoryManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemInvoiceCategoryManagement);

        jMenuItemProductCategoryManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemProductCategoryManagement.setText("Rubros");
        jMenuItemProductCategoryManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProductCategoryManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemProductCategoryManagement);

        jMenuItemProductSubcategoryManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemProductSubcategoryManagement.setText("Subrubros");
        jMenuItemProductSubcategoryManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProductSubcategoryManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemProductSubcategoryManagement);

        jMenuItemBrandManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemBrandManagement.setText("Marcas");
        jMenuItemBrandManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBrandManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemBrandManagement);

        jMenuItemBankManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemBankManagement.setText("Bancos");
        jMenuItemBankManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBankManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemBankManagement);

        jMenuItemCardManagement.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuItemCardManagement.setText("Tarjetas");
        jMenuItemCardManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCardManagementActionPerformed(evt);
            }
        });
        jMenuMaintenance.add(jMenuItemCardManagement);

        jMenuBar1.add(jMenuMaintenance);

        jMenuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Salir.png"))); // NOI18N
        jMenuExit.setText("Salir");
        jMenuExit.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jMenuExit.setMaximumSize(new java.awt.Dimension(125, 1500));
        jMenuExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuExitMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuExit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(909, Short.MAX_VALUE))
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jDesktopPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 9, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private javax.swing.ImageIcon loadIcon(String path) {
        java.net.URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Icon not found: " + path);
        }
        return new javax.swing.ImageIcon(resource);
    }


    private ProductController createProductController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProductMapper mapper = session.getMapper(ProductMapper.class);
        ProductRepository repository = new ProductRepositoryImpl(mapper);
        ProductService service = new ProductService(repository);
        return new ProductController(service);
    }

    private BrandController createBrandController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        BrandMapper mapper = session.getMapper(BrandMapper.class);
        BrandRepository repository = new BrandRepositoryImpl(mapper);
        BrandService service = new BrandService(repository);
        return new BrandController(service);
    }

    private CategoryController createCategoryController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        CategoryMapper mapper = session.getMapper(CategoryMapper.class);
        CategoryRepository repository = new CategoryRepositoryImpl(mapper);
        CategoryService service = new CategoryService(repository);
        return new CategoryController(service);
    }

    private SubcategoryController createSubcategoryController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        SubcategoryMapper mapper = session.getMapper(SubcategoryMapper.class);
        SubcategoryRepository repository = new SubcategoryRepositoryImpl(mapper);
        SubcategoryService service = new SubcategoryService(repository);
        return new SubcategoryController(service);
    }

    private ProviderController createProviderController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProviderMapper mapper = session.getMapper(ProviderMapper.class);
        ProviderRepository repository = new ProviderRepositoryImpl(mapper);
        ProviderService service = new ProviderService(repository);
        return new ProviderController(service);
    }

    private void jMenuExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuExitMouseClicked
        exitApplication();
}//GEN-LAST:event_jMenuExitMouseClicked

    private void exitApplication() {
        if (!closing.compareAndSet(false, true)) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final boolean backupEnabled = Boolean.parseBoolean(AppConfig.get("backup.enabled", "true"));
        Path plannedDirectory = null;
        if (backupEnabled) {
            try {
                plannedDirectory = backupService.previewOutputDirectory();
            } catch (BackupException ex) {
                Logger.getLogger(MainView.class.getName()).log(Level.WARNING,
                        "No se pudo preparar el directorio local de backups", ex);
            }
        }
        final Path progressDirectory = plannedDirectory;
        final JDialog progressDialog = backupEnabled ? showBackupProgressDialog(progressDirectory) : null;

        SwingWorker<Path, Void> worker = new SwingWorker<>() {
            private Exception failure;
            private Path backupFile;

            @Override
            protected Path doInBackground() {
                try {
                    backupFile = backupService.backupNow();
                } catch (BackupException ex) {
                    failure = ex;
                } catch (Exception ex) {
                    failure = ex;
                }
                return backupFile;
            }

            @Override
            protected void done() {
                if (progressDialog != null) {
                    progressDialog.dispose();
                }
                setCursor(Cursor.getDefaultCursor());
                if (failure != null) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, "Error generando backup automático", failure);
                    String detail = failure.getMessage();
                    if (detail == null || detail.isBlank()) {
                        detail = failure.getClass().getSimpleName();
                    }
                    String message = "No se pudo generar el backup automático.\n" + detail + "\n¿Desea salir igualmente?";
                    int choice = JOptionPane.showConfirmDialog(MainView.this, message, "Error al generar backup", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        closing.set(false);
                        return;
                    }
                }
                if (notifyBackupSuccess && failure == null && backupFile != null) {
                    showBackupSuccess(backupFile);
                }
                setVisible(false);
                dispose();
            }
        };

        worker.execute();
    }

    private void showBackupSuccess(Path backupFile) {
        StringBuilder message = new StringBuilder();
        message.append("Se generó una copia de seguridad.\n\n");
        message.append("Archivo local:\n");
        message.append(backupFile.toAbsolutePath());

        Path directory = backupFile.getParent();
        if (directory != null) {
            message.append("\n\nCarpeta local donde quedan los respaldos:\n");
            message.append(directory.toAbsolutePath());
        }

        String postCommand = AppConfig.get("backup.post.command", "");
        if (postCommand != null && !postCommand.isBlank()) {
            message.append("\n\nTambién se ejecutó la sincronización configurada (por ejemplo, Google Drive).\n");
            message.append("Verifique el destino remoto si necesita confirmar la copia en la nube.");
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Backup completado", JOptionPane.INFORMATION_MESSAGE);
    }

    private JDialog showBackupProgressDialog(Path directory) {
        JDialog dialog = new JDialog(this, "Realizando backup", false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        JLabel label = new JLabel(buildProgressMessage(directory));
        label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        dialog.getContentPane().add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return dialog;
    }

    private String buildProgressMessage(Path directory) {
        if (directory == null) {
            return "Realizando backup...";
        }
        String absolute = directory.toAbsolutePath().toString();
        String escaped = escapeHtml(absolute);
        return "<html>Realizando backup...<br/><br/>Guardando en:<br/>"
                + "<span style='font-family:monospace;'>" + escaped + "</span></html>";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void jDesktopPane1ComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jDesktopPane1ComponentAdded
        Component child = evt.getChild();
        if (!(child instanceof JInternalFrame)) {
            return;
        }
        final JInternalFrame frame = (JInternalFrame) child;

        SwingUtilities.invokeLater(() -> {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            try {
                if (frame.isIcon()) {
                    frame.setIcon(false);
                }
                frame.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(MainView.class.getName()).log(Level.FINE,
                        "No se pudo seleccionar la ventana recién agregada", ex);
            }
            frame.toFront();
            frame.requestFocusInWindow();
        });
    }//GEN-LAST:event_jDesktopPane1ComponentAdded

    private void jMenuProductManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuProductManagementMouseClicked
        if (!ProductManagementView.isOpen && jMenuProductManagement.isEnabled()) {
            openViewAsync(jMenuProductManagement,
                    () -> {
                        ProductController controller = createProductController();
                        BrandController brandController = createBrandController();
                        CategoryController categoryController = createCategoryController();
                        SubcategoryController subcategoryController = createSubcategoryController();
                        ProviderController providerController = createProviderController();
                        return new ProductManagementView(controller, brandController, categoryController, subcategoryController, providerController);
                    },
                    view -> {
                        productManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de artículos");
        }
    }//GEN-LAST:event_jMenuProductManagementMouseClicked

    private void jMenuProductManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuProductManagementActionPerformed
        
    }//GEN-LAST:event_jMenuProductManagementActionPerformed

    private void jMenuClientManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuClientManagementMouseClicked
        if (!ClientManagementView.isOpen && jMenuClientManagement.isEnabled()) {
            openViewAsync(jMenuClientManagement,
                    ClientManagementView::new,
                    view -> {
                        clientManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de clientes");
        }
    }//GEN-LAST:event_jMenuClientManagementMouseClicked

    private void jMenuClientManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuClientManagementActionPerformed
        
    }//GEN-LAST:event_jMenuClientManagementActionPerformed

    private void jMenuNewInvoiceClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNewInvoiceClientActionPerformed
        
    }//GEN-LAST:event_jMenuNewInvoiceClientActionPerformed

    private void jMenuAccountantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAccountantActionPerformed
        
    }//GEN-LAST:event_jMenuAccountantActionPerformed

    private void jMenuAccountantMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuAccountantMouseClicked
        
    }//GEN-LAST:event_jMenuAccountantMouseClicked

    private void jMenuNewInvoiceClientMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuNewInvoiceClientMouseClicked
        
    }//GEN-LAST:event_jMenuNewInvoiceClientMouseClicked

    private void jMenuClientBudgetManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuClientBudgetManagementMouseClicked

        if (!ClientBudgetManagementView.isOpen && jMenuClientBudgetManagement.isEnabled()) {
            openViewAsync(jMenuClientBudgetManagement,
                    ClientBudgetManagementView::new,
                    view -> {
                        clientBudgetManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de presupuestos");
        }
    }//GEN-LAST:event_jMenuClientBudgetManagementMouseClicked

    private void jMenuClientInvoceSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuClientInvoceSearchMouseClicked

        if (!ClientInvoiceManagementView.isOpen && jMenuClientInvoceSearch.isEnabled()) {
            openViewAsync(jMenuClientInvoceSearch,
                    ClientInvoiceManagementView::new,
                    view -> {
                        clientInvoiceManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de facturas");
        }
    }//GEN-LAST:event_jMenuClientInvoceSearchMouseClicked

    private void jMenuItemClientReceiptManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemClientReceiptManagementMouseClicked
        
    }//GEN-LAST:event_jMenuItemClientReceiptManagementMouseClicked

    private void jMenuItemClientReceiptManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClientReceiptManagementActionPerformed
        if (!ClientReceiptManagementView.isOpen && jMenuItemClientReceiptManagement.isEnabled()) {
            openViewAsync(jMenuItemClientReceiptManagement,
                    ClientReceiptManagementView::new,
                    view -> {
                        clientReceiptManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de recibos de clientes");
        }
    }//GEN-LAST:event_jMenuItemClientReceiptManagementActionPerformed

    private void jMenuItemProviderManagementMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemProviderManagementMouseClicked
        // No action needed on mouse click
    }//GEN-LAST:event_jMenuItemProviderManagementMouseClicked

    private void jMenuItemProviderManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProviderManagementActionPerformed
        if (!ProviderManagementView.isOpen && jMenuItemProviderManagement.isEnabled()) {
            openViewAsync(jMenuItemProviderManagement,
                    ProviderManagementView::new,
                    view -> {
                        providerManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de proveedores");
        }
}//GEN-LAST:event_jMenuItemProviderManagementActionPerformed

    private void jMenuItemProviderInvoiceManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProviderInvoiceManagementActionPerformed
        if (!ProviderInvoiceManagementView.isOpen && jMenuItemProviderInvoiceManagement.isEnabled()) {
            openViewAsync(jMenuItemProviderInvoiceManagement,
                    ProviderInvoiceManagementView::new,
                    view -> {
                        providerInvoiceManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de facturas de proveedores");
        }
}//GEN-LAST:event_jMenuItemProviderInvoiceManagementActionPerformed

    private void jMenuItemProviderReceiptManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProviderReceiptManagementActionPerformed

        if (!ProviderReceiptManagementView.isOpen && jMenuItemProviderReceiptManagement.isEnabled()) {
            openViewAsync(jMenuItemProviderReceiptManagement,
                    ProviderReceiptManagementView::new,
                    view -> {
                        providerReceiptManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de recibos de proveedores");
        }
}//GEN-LAST:event_jMenuItemProviderReceiptManagementActionPerformed

    private void jMenuItemCityManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCityManagementActionPerformed
        if (!CityManagement.isOpen && jMenuItemCityManagement.isEnabled()) {
            openViewAsync(jMenuItemCityManagement,
                    CityManagement::new,
                    view -> {
                        cityManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de ciudades");
        }
    }//GEN-LAST:event_jMenuItemCityManagementActionPerformed

    private void jMenuItemAddressManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddressManagementActionPerformed
        if (!AddressManagement.isOpen && jMenuItemAddressManagement.isEnabled()) {
            openViewAsync(jMenuItemAddressManagement,
                    AddressManagement::new,
                    view -> {
                        addressManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de direcciones");
        }
    }//GEN-LAST:event_jMenuItemAddressManagementActionPerformed

    private void jMenuItemInvoiceCategoryManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInvoiceCategoryManagementActionPerformed
        if (!InvoiceCategoryManagement.isOpen && jMenuItemInvoiceCategoryManagement.isEnabled()) {
            openViewAsync(jMenuItemInvoiceCategoryManagement,
                    InvoiceCategoryManagement::new,
                    view -> {
                        invoiceCategoryManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de categorías de facturas");
        }
    }//GEN-LAST:event_jMenuItemInvoiceCategoryManagementActionPerformed

    private void jMenuItemProductCategoryManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProductCategoryManagementActionPerformed
        if (!ProductCategoryManagement.isOpen && jMenuItemProductCategoryManagement.isEnabled()) {
            openViewAsync(jMenuItemProductCategoryManagement,
                    ProductCategoryManagement::new,
                    view -> {
                        productCategoryManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de rubros");
        }
    }//GEN-LAST:event_jMenuItemProductCategoryManagementActionPerformed

    private void jMenuItemProductSubcategoryManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProductSubcategoryManagementActionPerformed
        if (!ProductSubcategoryManagement.isOpen && jMenuItemProductSubcategoryManagement.isEnabled()) {
            openViewAsync(jMenuItemProductSubcategoryManagement,
                    ProductSubcategoryManagement::new,
                    view -> {
                        productSubcategoryManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de subrubros");
        }
    }//GEN-LAST:event_jMenuItemProductSubcategoryManagementActionPerformed

    private void jMenuItemBrandManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBrandManagementActionPerformed
        if (!BrandManagement.isOpen && jMenuItemBrandManagement.isEnabled()) {
            openViewAsync(jMenuItemBrandManagement,
                    BrandManagement::new,
                    view -> {
                        brandManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de marcas");
        }
    }//GEN-LAST:event_jMenuItemBrandManagementActionPerformed

    private void jMenuItemBankManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBankManagementActionPerformed
        if (!BankManagement.isOpen && jMenuItemBankManagement.isEnabled()) {
            openViewAsync(jMenuItemBankManagement,
                    BankManagement::new,
                    view -> {
                        bankManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de bancos");
        }
    }//GEN-LAST:event_jMenuItemBankManagementActionPerformed

    private void jMenuItemCardManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCardManagementActionPerformed
        if (!CardManagement.isOpen && jMenuItemCardManagement.isEnabled()) {
            openViewAsync(jMenuItemCardManagement,
                    CardManagement::new,
                    view -> {
                        cardManagementView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de tarjetas");
        }
    }//GEN-LAST:event_jMenuItemCardManagementActionPerformed

    private void jMenuPurchasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuPurchasesMouseClicked

}//GEN-LAST:event_jMenuPurchasesMouseClicked

    private void jMenuPurchasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuPurchasesActionPerformed

}//GEN-LAST:event_jMenuPurchasesActionPerformed

    private void jMenuItemManualInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemManualInvoiceActionPerformed
        if (jMenuItemManualInvoice.isEnabled() && !ClientInvoiceInsertView.isOpen) {
            openViewAsync(jMenuItemManualInvoice,
                    ClientInvoiceInsertView::new,
                    view -> {
                        clientInvoiceInsertView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la vista de ventas");
        }
    }//GEN-LAST:event_jMenuItemManualInvoiceActionPerformed

    private void jMenuItemAutomaticInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutomaticInvoiceActionPerformed
        if (jMenuItemAutomaticInvoice.isEnabled() && !ClientAutomaticInvoiceView.isOpen) {
            openViewAsync(jMenuItemAutomaticInvoice,
                    ClientAutomaticInvoiceView::new,
                    view -> {
                        clientAutomaticInvoiceView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la facturación automática");
        }
    }//GEN-LAST:event_jMenuItemAutomaticInvoiceActionPerformed

    private void jMenuItemSalesVATBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalesVATBookActionPerformed
        if (!SalesVatBookView.open && jMenuItemSalesVATBook.isEnabled()) {
            openViewAsync(jMenuItemSalesVATBook,
                    SalesVatBookView::new,
                    view -> {
                        salesVatBookView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir el libro IVA ventas");
        }
    }//GEN-LAST:event_jMenuItemSalesVATBookActionPerformed

    private void jMenuItemPurchasesVATBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPurchasesVATBookActionPerformed
        if (!PurchasesVatBookView.open && jMenuItemPurchasesVATBook.isEnabled()) {
            openViewAsync(jMenuItemPurchasesVATBook,
                    PurchasesVatBookView::new,
                    view -> {
                        purchasesVatBookView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir el libro IVA compras");
        }
    }//GEN-LAST:event_jMenuItemPurchasesVATBookActionPerformed

    private void jMenuItemSearchChequesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSearchChequesActionPerformed
        if (!CheckSearchView.open && jMenuItemSearchCheques.isEnabled()) {
            openViewAsync(jMenuItemSearchCheques,
                    CheckSearchView::new,
                    view -> {
                        checkSearchView = view;
                        jDesktopPane1.add(view);
                        view.setVisible(true);
                    },
                    "No se pudo abrir la consulta de cheques");
        }
}//GEN-LAST:event_jMenuItemSearchChequesActionPerformed

    private <T extends JInternalFrame> void openViewAsync(JComponent trigger,
                                FrameSupplier<T> supplier,
                                FrameConsumer<T> onSuccess,
                                String errorMessage) {
        if (trigger != null) {
            trigger.setEnabled(false);
        }
        startBackgroundTask();
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return supplier.get();
            }

            @Override
            protected void done() {
                try {
                    T frame = get();
                    if (frame != null && onSuccess != null) {
                        onSuccess.accept(frame);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, errorMessage, ex);
                    JOptionPane.showMessageDialog(MainView.this,
                            buildDetailedErrorMessage(errorMessage, ex),
                            "Bits&Bytes",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (trigger != null) {
                        trigger.setEnabled(true);
                    }
                    finishBackgroundTask();
                }
            }
        }.execute();
    }

    private String buildDetailedErrorMessage(String baseMessage, Exception ex) {
        StringBuilder message = new StringBuilder(baseMessage == null ? "" : baseMessage);

        Throwable root = ex;
        while (root != null && root.getCause() != null) {
            root = root.getCause();
        }

        if (root != null && root.getMessage() != null && !root.getMessage().isBlank()) {
            message.append("\n\nDetalle: ").append(root.getMessage());
        }

        String logsDir = System.getProperty("bitsandbytes.logs.dir");
        if (logsDir != null && !logsDir.isBlank()) {
            message.append("\n\nConsultá el archivo ")
                    .append(Paths.get(logsDir, "errores.log").toAbsolutePath())
                    .append(" para más información.");
        }

        return message.toString();
    }

    private void startBackgroundTask() {
        if (backgroundTaskCount.getAndIncrement() == 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    private void finishBackgroundTask() {
        int remaining = backgroundTaskCount.decrementAndGet();
        if (remaining <= 0) {
            backgroundTaskCount.set(0);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @FunctionalInterface
    private interface FrameSupplier<T extends JInternalFrame> {
        T get() throws Exception;
    }

    @FunctionalInterface
    private interface FrameConsumer<T extends JInternalFrame> {
        void accept(T frame);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JDesktopPane jDesktopPane1;
    public static javax.swing.JMenu jMenuAccountant;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuClientBudgetManagement;
    private javax.swing.JMenu jMenuClientInvoceSearch;
    private javax.swing.JMenu jMenuClientManagement;
    private javax.swing.JMenu jMenuExit;
    private javax.swing.JMenuItem jMenuItemAddressManagement;
    private javax.swing.JMenuItem jMenuItemAutomaticInvoice;
    private javax.swing.JMenuItem jMenuItemBankManagement;
    private javax.swing.JMenuItem jMenuItemBrandManagement;
    private javax.swing.JMenuItem jMenuItemCardManagement;
    private javax.swing.JMenuItem jMenuItemCityManagement;
    private javax.swing.JMenuItem jMenuItemClientReceiptManagement;
    private javax.swing.JMenuItem jMenuItemInvoiceCategoryManagement;
    private javax.swing.JMenuItem jMenuItemManualInvoice;
    private javax.swing.JMenuItem jMenuItemProductCategoryManagement;
    private javax.swing.JMenuItem jMenuItemProductSubcategoryManagement;
    private javax.swing.JMenuItem jMenuItemProviderInvoiceManagement;
    private javax.swing.JMenuItem jMenuItemProviderManagement;
    private javax.swing.JMenuItem jMenuItemProviderReceiptManagement;
    private javax.swing.JMenuItem jMenuItemPurchasesVATBook;
    private javax.swing.JMenuItem jMenuItemSalesVATBook;
    private javax.swing.JMenuItem jMenuItemSearchCheques;
    private javax.swing.JMenu jMenuMaintenance;
    public static javax.swing.JMenu jMenuNewInvoiceClient;
    private javax.swing.JMenu jMenuProductManagement;
    private javax.swing.JMenu jMenuPurchases;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
