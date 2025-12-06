package views.products;

import controllers.ProductController;
import configs.MyBatisConfig;
import mappers.ProductMapper;
import models.Product;
import org.apache.ibatis.session.SqlSession;
import repositories.ProductRepository;
import repositories.impl.ProductRepositoryImpl;
import services.ProductService;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Mariana
 */
public class ProductStockManagementView extends javax.swing.JInternalFrame {

    public static boolean isOpen;
    private final ProductController productController;
    private Product product;

    public ProductStockManagementView() throws Exception {
        this(createProductController());
    }

    public ProductStockManagementView(ProductController productController) {
        this.productController = productController;
        isOpen = true;
        initComponents();
        loadSelectedProduct();
    }

    private static ProductController createProductController() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProductMapper mapper = session.getMapper(ProductMapper.class);
        ProductRepository repository = new ProductRepositoryImpl(mapper);
        ProductService service = new ProductService(repository);
        return new ProductController(service);
    }

    private void loadSelectedProduct() {
        int i = ProductManagementView.jTable1.getSelectedRow();
        String code = ProductManagementView.jTable1.getValueAt(i, 0).toString();
        product = productController.findByCode(code);
        if (product != null) {
            jLabelCode.setText(product.getCode());
            jLabelDetail.setText(product.getDescription());
            jLabelBrand.setText(product.getBrand() != null ? product.getBrand().getName() : "");
            jLabelStockQuantity.setText(product.getStockQuantity() != null ? String.valueOf(product.getStockQuantity()) : "0");
        }
    }

    private void closeWindow() {
        isOpen = false;
        dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelCode = new javax.swing.JLabel();
        jLabelDetail = new javax.swing.JLabel();
        jButtonIncrease = new javax.swing.JButton();
        jButtonDiscount = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabelBrand = new javax.swing.JLabel();
        jLabelStockQuantity = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldStock = new javax.swing.JTextField();

        setTitle("Stock Articulo");
        setMinimumSize(new java.awt.Dimension(340, 350));
        setPreferredSize(new java.awt.Dimension(340, 350));
        getContentPane().setLayout(null);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Cantidad");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(70, 170, 100, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Detalle");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(20, 50, 60, 20);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Codigo");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 20, 60, 20);

        jLabelCode.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelCode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jLabelCode);
        jLabelCode.setBounds(90, 20, 140, 20);

        jLabelDetail.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelDetail.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jLabelDetail);
        jLabelDetail.setBounds(90, 50, 140, 20);

        jButtonIncrease.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonIncrease.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonIncrease.setText("Aumentar");
        jButtonIncrease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIncreaseActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonIncrease);
        jButtonIncrease.setBounds(20, 220, 140, 30);

        jButtonDiscount.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDiscount.setText("Descontar");
        jButtonDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscountActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDiscount);
        jButtonDiscount.setBounds(170, 220, 140, 30);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(100, 260, 140, 30);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Marca");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 80, 60, 20);

        jLabelBrand.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelBrand.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jLabelBrand);
        jLabelBrand.setBounds(90, 80, 120, 20);

        jLabelStockQuantity.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelStockQuantity.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jLabelStockQuantity);
        jLabelStockQuantity.setBounds(90, 110, 50, 20);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Cantidad");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(20, 110, 60, 20);

        jTextFieldStock.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldStock);
        jTextFieldStock.setBounds(180, 170, 50, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonIncreaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIncreaseActionPerformed
        try {
            float quantity = Float.parseFloat(jTextFieldStock.getText());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Atención", JOptionPane.WARNING_MESSAGE);
                jTextFieldStock.requestFocus();
                return;
            }
            productController.increaseStock(product.getCode(), quantity);
            closeWindow();
            ProductManagementView.refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ProductStockManagementView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al aumentar el stock", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonIncreaseActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscountActionPerformed
        try {
            float quantity = Float.parseFloat(jTextFieldStock.getText());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "El stock debe ser mayor a 0", "Atención", JOptionPane.WARNING_MESSAGE);
                jTextFieldStock.requestFocus();
                return;
            }
            Product fresh = productController.findByCode(product.getCode());
            float current = fresh.getStockQuantity() == null ? 0f : fresh.getStockQuantity();
            if (quantity <= current) {
                productController.decreaseStock(product.getCode(), quantity);
                float newStock = current - quantity;
                if (newStock <= 0) {
                    int k = JOptionPane.showConfirmDialog(this, "¿Desea deshabilitar el producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (k == JOptionPane.YES_OPTION) {
                        fresh.setEnabled(false);
                        productController.update(fresh);
                    }
                }
                closeWindow();
                ProductManagementView.refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "La cantidad que quiere eliminar es mayor a la disponible", "Atención", JOptionPane.WARNING_MESSAGE);
                jTextFieldStock.requestFocus();
                jTextFieldStock.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida", "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ProductStockManagementView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error al descontar el stock", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonDiscountActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDiscount;
    private javax.swing.JButton jButtonIncrease;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBrand;
    private javax.swing.JLabel jLabelCode;
    private javax.swing.JLabel jLabelDetail;
    private javax.swing.JLabel jLabelStockQuantity;
    private javax.swing.JTextField jTextFieldStock;
    // End of variables declaration//GEN-END:variables

}
