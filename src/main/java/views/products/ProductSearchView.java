package views.products;

import controllers.ProductController;
import models.Product;

import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import utils.TableUtils;

/**
 * Ventana de búsqueda de productos.
 * Los textos visibles para el usuario se mantienen en español y los nombres
 * internos de variables y métodos están en inglés.
 */
public class ProductSearchView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;

    private static final BigDecimal ZERO_STOCK = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private static ProductSearchView activeInstance;

    private final ProductController productController;
    private Consumer<Product> onProductSelected;
    private DefaultTableModel tableModel;
    private List<Product> currentProducts;

      public ProductSearchView(ProductController productController) {
        this(productController, null);
    }

      public ProductSearchView(ProductController productController, Consumer<Product> onProductSelected) {
        this.productController = productController;
        this.onProductSelected = onProductSelected;
        initComponents();
        isOpen = true;
        activeInstance = this;

        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setColumnCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Código", "Descripción", "Stock"});
        TableUtils.configureProductSearchViewTable(jTable1);
        TableUtils.applyDecimalRenderer(jTable1, new int[]{2}, 2);

        loadProducts();

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
    }

    private void loadProducts() {
        loadProducts(jTextField1.getText());
    }

    private void loadProducts(String filter) {
        tableModel.setRowCount(0);
        currentProducts = new ArrayList<>();
        List<Product> products = productController.findEnabled();
        String lower = filter == null ? "" : filter.toLowerCase().trim();
        for (Product product : products) {
            if (product.getCode() != null && "99".equals(product.getCode().trim())) {
                continue;
            }
            if (lower.isEmpty() || matches(product, lower)) {
                tableModel.addRow(new Object[]{
                    product.getCode(),
                    product.getDescription(),
                    formatStock(product)
                });
                currentProducts.add(product);
            }
        }
    }

    private BigDecimal formatStock(Product product) {
        if (product == null || product.getStockQuantity() == null) {
            return ZERO_STOCK;
        }
        BigDecimal stock = BigDecimal.valueOf(product.getStockQuantity());
        return stock.setScale(2, RoundingMode.HALF_UP);
    }

    private Product getProductAt(int rowIndex) {
        if (currentProducts == null || rowIndex < 0 || rowIndex >= currentProducts.size()) {
            return null;
        }
        return currentProducts.get(rowIndex);
    }

    private boolean matches(Product product, String filter) {
        if (product == null) {
            return false;
        }

        String searchable = String.join(" ",
                product.getCode() != null ? product.getCode() : "",
                product.getDescription() != null ? product.getDescription() : "",
                getBrandName(product),
                getProviderName(product))
                .toLowerCase();

        String[] tokens = filter.split("\\s+");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && !searchable.contains(trimmed)) {
                return false;
            }
        }
        return true;
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(640, 630));
        setPreferredSize(new java.awt.Dimension(640, 630));
        getContentPane().setLayout(null);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo", "Detalle", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.math.BigDecimal.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
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
        jScrollPane1.setBounds(20, 50, 597, 481);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton1.setText("Volver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(262, 538, 104, 30);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(37, 10, 56, 20);

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(105, 6, 437, 22);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2 && jTable1.getSelectedRow() >= 0) {
            int selectedRow = jTable1.getSelectedRow();
            Product selectedProduct = getProductAt(selectedRow);
            if (onProductSelected != null && selectedProduct != null) {
                onProductSelected.accept(selectedProduct);
            }
            isOpen = false;
            dispose();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    public static void bringToFront() {
        if (activeInstance == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                if (activeInstance.isIcon()) {
                    activeInstance.setIcon(false);
                }
                activeInstance.setSelected(true);
            } catch (PropertyVetoException ignored) {
            }
            activeInstance.toFront();
            activeInstance.requestFocus();
        });
    }

    public static void updateSelectionListener(Consumer<Product> listener) {
        if (activeInstance != null) {
            activeInstance.onProductSelected = listener;
        }
    }

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        loadProducts(jTextField1.getText());
    }//GEN-LAST:event_jTextField1KeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

