
package views.products;

import controllers.BrandController;
import controllers.CategoryController;
import controllers.ProductController;
import controllers.ProviderController;
import controllers.SubcategoryController;
import configs.AppConfig;
import models.Product;
import views.MainView;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.TableUtils;

/**
 *
 * @author Mariana
 */
public class ProductManagementView extends javax.swing.JInternalFrame {

    private static final int BASE_WIDTH = 1040;
    private static final int BASE_HEIGHT = 650;
    private static final int HEIGHT_MARGIN = 40;
    private static final int BOTTOM_MARGIN = 20;
    private static final int MIN_TABLE_HEIGHT = 220;
    private static final int TABLE_X = 50;
    private static final int TABLE_Y = 130;
    private static final int TABLE_WIDTH = 940;
    private static final int TABLE_HEIGHT = 410;
    private static final int RETURN_X = 460;
    private static final int RETURN_Y = 560;
    private static final int RETURN_WIDTH = 110;
    private static final int RETURN_HEIGHT = 29;
    private static final String DISPLAY_MODE_PROPERTY = "product.management.display";

    public static boolean isOpen = false;
    private static ProductManagementView instance;

    private final ProductController productController;
    private final BrandController brandController;
    private final CategoryController categoryController;
    private final SubcategoryController subcategoryController;
    private final ProviderController providerController;
    private final boolean showProviderColumn;
    private DefaultTableModel tableModel;
    private List<Product> currentProducts;

    public ProductManagementView(ProductController productController,
            BrandController brandController,
            CategoryController categoryController,
            SubcategoryController subcategoryController,
            ProviderController providerController) {
        this.productController = productController;
        this.brandController = brandController;
        this.categoryController = categoryController;
        this.subcategoryController = subcategoryController;
        this.providerController = providerController;
        this.showProviderColumn = shouldShowProviderColumn();
        initComponents();
        isOpen = true;
        instance = this;
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setColumnIdentifiers(new String[]{
            "Código", "Descripción", resolveThirdColumnHeader(), "Precio", "Stock"
        });

        TableUtils.configureProductManagementViewTable(jTable1);

        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (currentProducts != null && row < currentProducts.size()) {
                    Product product = currentProducts.get(row);
                    if (Boolean.FALSE.equals(product.getEnabled())) {
                        c.setBackground(isSelected ? new Color(255, 200, 200) : new Color(220, 220, 220));
                    } else {
                        c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    }
                }
                return c;
            }
        });

        loadProducts();
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                adjustHeightForLargeScreens();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isOpen = false;
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayoutForHeight(getHeight());
            }
        });

        SwingUtilities.invokeLater(this::adjustHeightForLargeScreens);
    }

    private void loadProducts() {
        loadProducts(jTextFieldFiltro.getText());
    }

    private void loadProducts(String filter) {
        tableModel.setRowCount(0);
        currentProducts = new ArrayList<>();
        List<Product> products = productController.findAll();
        String lower = filter == null ? "" : filter.toLowerCase().trim();
        for (Product product : products) {
            if (product.getCode() != null && "99".equals(product.getCode().trim())) {
                continue;
            }
            if (lower.isEmpty() || matches(product, lower)) {
                tableModel.addRow(new Object[]{
                    product.getCode(),
                    product.getDescription(),
                    resolveThirdColumnValue(product),
                    product.getCashPrice(),
                    product.getStockQuantity()
                });
                currentProducts.add(product);
            }
        }

        TableUtils.configureProductManagementViewTable(jTable1);
    }

    private boolean matches(Product product, String filter) {
        String searchable = String.join(" ",
                product.getCode() != null ? product.getCode() : "",
                product.getDescription() != null ? product.getDescription() : "",
                getBrandName(product),
                getProviderName(product))
                .toLowerCase();

        String[] tokens = filter.split("\\s+");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (!searchable.contains(trimmed)) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldShowProviderColumn() {
        String configured = AppConfig.get(DISPLAY_MODE_PROPERTY, "brand");
        return configured != null && configured.trim().equalsIgnoreCase("provider");
    }

    private String resolveThirdColumnHeader() {
        return showProviderColumn ? "Proveedor" : "Marca";
    }

    private String resolveThirdColumnValue(Product product) {
        if (product == null) {
            return "";
        }
        return showProviderColumn ? getProviderName(product) : getBrandName(product);
    }

    private String getBrandName(Product product) {
        if (product.getBrand() != null && product.getBrand().getName() != null) {
            return product.getBrand().getName();
        }
        return "";
    }

    private String getProviderName(Product product) {
        if (product.getProvider() != null && product.getProvider().getName() != null) {
            return product.getProvider().getName();
        }
        return "";
    }


    public static void refreshTable() {
        if (instance != null) {
            instance.loadProducts();
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonModify = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButtonStock = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jButtonUpdatePrices = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButtonViewAll = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldFiltro = new javax.swing.JTextField();
        jButtonImport = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setIconifiable(true);
        setTitle("Listado Articulos");
        setAutoscrolls(true);
        setMinimumSize(new java.awt.Dimension(1040, 650));
        setPreferredSize(new java.awt.Dimension(1040, 650));
        getContentPane().setLayout(null);

        jButtonModify.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonModify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modificar.png"))); // NOI18N
        jButtonModify.setText("Modificar");
        jButtonModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModifyActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonModify);
        jButtonModify.setBounds(180, 30, 130, 30);

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
        jButtonAdd.setBounds(40, 30, 130, 30);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.setText("Eliminar");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(320, 30, 130, 30);

        jButton5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/listado.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(720, 90, 30, 30);

        jButtonStock.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/movimiento stock.png"))); // NOI18N
        jButtonStock.setText("Mov. Stock");
        jButtonStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStockActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonStock);
        jButtonStock.setBounds(460, 30, 130, 30);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Detalle", "Marca", "Precio", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(50, 130, 940, 410);

        jButton8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton8.setText("Volver");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);
        jButton8.setBounds(460, 560, 110, 27);

        jButtonUpdatePrices.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonUpdatePrices.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/peso.png"))); // NOI18N
        jButtonUpdatePrices.setText("Actualizar ");
        jButtonUpdatePrices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdatePricesActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonUpdatePrices);
        jButtonUpdatePrices.setBounds(880, 30, 130, 30);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(680, 90, 30, 30);

        jButtonViewAll.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonViewAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonViewAll.setText("Ver Todos");
        jButtonViewAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonViewAllActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonViewAll);
        jButtonViewAll.setBounds(600, 30, 130, 30);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Filtrar");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(50, 90, 70, 20);

        jTextFieldFiltro.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFiltroKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldFiltro);
        jTextFieldFiltro.setBounds(130, 90, 540, 20);

        jButtonImport.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/peso.png"))); // NOI18N
        jButtonImport.setText("Importar");
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonImport);
        jButtonImport.setBounds(740, 30, 130, 30);

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

        jScrollPane1.setBounds(TABLE_X, TABLE_Y, TABLE_WIDTH, tableHeight);
        jButton8.setBounds(RETURN_X, buttonY, RETURN_WIDTH, RETURN_HEIGHT);
    }


    private void jButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModifyActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Modificar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String code = jTable1.getValueAt(selectedRow, 0).toString();
        if (ProductUpsertView.isOpen) {
            return;
        }
        ProductUpsertView view = new ProductUpsertView(productController, brandController, categoryController, subcategoryController, providerController, code);
        MainView.jDesktopPane1.add(view);
        view.setVisible(true);
    }//GEN-LAST:event_jButtonModifyActionPerformed



    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String code = jTable1.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar producto " + code + "?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            productController.delete(code);
            loadProducts();
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed


    

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JOptionPane.showMessageDialog(this, "No implementado");
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButtonStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStockActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Movimiento de stock", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (currentProducts == null || selectedRow >= currentProducts.size()) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener el producto seleccionado", "Movimiento de stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ProductStockManagementView.isOpen) {
            return;
        }

        try {
            ProductStockManagementView view = new ProductStockManagementView(productController);
            JDesktopPane desktopPane = getDesktopPane();
            if (desktopPane != null) {
                desktopPane.add(view);
            }
            view.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ProductManagementView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al abrir la ventana de stock", "Movimiento de stock", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonStockActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (ProductUpsertView.isOpen) {
            return;
        }
        ProductUpsertView view = new ProductUpsertView(productController, brandController, categoryController, subcategoryController, providerController);
        MainView.jDesktopPane1.add(view);
        view.setVisible(true);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            int row = jTable1.rowAtPoint(evt.getPoint());
            if (row >= 0) {
                jTable1.setRowSelectionInterval(row, row);
                openProductDetail(row);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void openProductDetail(int row) {
        if (currentProducts == null || row < 0 || row >= currentProducts.size()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener el producto seleccionado",
                    "Detalle",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = currentProducts.get(row);
        if (product == null || product.getCode() == null || product.getCode().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "El producto seleccionado no tiene un código válido",
                    "Detalle",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ProductDetailView.isOpen) {
            return;
        }

        ProductDetailView view = new ProductDetailView(productController, product.getCode());
        MainView.jDesktopPane1.add(view);
        view.setVisible(true);
    }

    private void jButtonUpdatePricesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdatePricesActionPerformed
        if (ProductUpdatePricesView.isOpen) {
            return;
        }
        ProductUpdatePricesView view = new ProductUpdatePricesView(productController);
        JDesktopPane desktopPane = getDesktopPane();
        if (desktopPane != null) {
            desktopPane.add(view);
            view.setVisible(true);
        }

    }//GEN-LAST:event_jButtonUpdatePricesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextFieldFiltro.setText("");
        loadProducts();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonViewAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewAllActionPerformed
        jTextFieldFiltro.setText("");
        loadProducts();
    }//GEN-LAST:event_jButtonViewAllActionPerformed

    
    private void jTextFieldFiltroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFiltroKeyReleased
        loadProducts(jTextFieldFiltro.getText());
}//GEN-LAST:event_jTextFieldFiltroKeyReleased

    private void jButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportActionPerformed
        if (ProductImportView.isOpen) {
            return;
        }
        ProductImportView view = new ProductImportView(
                productController,
                providerController,
                brandController,
                categoryController,
                subcategoryController);
        JDesktopPane desktopPane = getDesktopPane();
        if (desktopPane != null) {
            desktopPane.add(view);
            view.setVisible(true);
        }
    }//GEN-LAST:event_jButtonImportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    public static javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    public static javax.swing.JButton jButtonImport;
    private javax.swing.JButton jButtonModify;
    private javax.swing.JButton jButtonStock;
    public static javax.swing.JButton jButtonUpdatePrices;
    public static javax.swing.JButton jButtonViewAll;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldFiltro;
    // End of variables declaration//GEN-END:variables

}
