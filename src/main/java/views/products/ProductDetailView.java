package views.products;

import controllers.ProductController;
import models.Product;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDetailView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private final ProductController productController;
    private final Product product;

    public ProductDetailView(ProductController productController, String code) {
        this.productController = productController;
        this.product = productController.findByCode(code);
        initComponents();
        isOpen = true;
        loadProduct();
    }

    private void loadProduct() {
        if (product == null) {
            Logger.getLogger(ProductDetailView.class.getName()).log(Level.WARNING, "Producto no encontrado");
            return;
        }
        jLabelCodigo.setText(product.getCode());
        jLabelDetalle.setText(product.getDescription());
        jLabelMarca.setText(product.getBrand() != null ? product.getBrand().getName() : "");
        jLabelRubro.setText(product.getCategory() != null ? product.getCategory().getName() : "");
        jLabelSubrubro.setText(product.getSubcategory() != null ? product.getSubcategory().getName() : "");
        jLabelProveedor.setText(product.getProvider() != null ? product.getProvider().getName() : "");
        if (product.getStockQuantity() != null) {
            jLabelCantidad.setText(product.getStockQuantity().toString());
        }
        jTextAreaObservaciones.setText(product.getNotes() != null ? product.getNotes() : "");
        jLabelPrecioCosto.setText(product.getPurchasePrice() != null ? product.getPurchasePrice().toString() : "");
        jLabelEfectivo.setText(product.getCashPrice() != null ? product.getCashPrice().toString() : "");
        jLabelFinanciado.setText(product.getFinancedPrice() != null ? product.getFinancedPrice().toString() : "");
        jLabelCredito.setText(product.getFinancedPrice() != null ? product.getFinancedPrice().toString() : "");
        jLabelDebito.setText(product.getCashPrice() != null ? product.getCashPrice().toString() : "");
        jLabelGanancia.setText(product.getProfitMargin() != null ? product.getProfitMargin().toString() : "");
        jLabelFinanciacion.setText(product.getInterestRate() != null ? product.getInterestRate().toString() : "");
        jLabelIVA.setText(product.getVatRate() != null ? product.getVatRate().toString() : "");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabelCodigo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelDetalle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelMarca = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelRubro = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabelSubrubro = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabelIVA = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaObservaciones = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabelProveedor = new javax.swing.JLabel();
        jLabelDebito = new javax.swing.JLabel();
        jLabelGanancia = new javax.swing.JLabel();
        jLabelFinanciacion = new javax.swing.JLabel();
        jLabelPrecioCosto = new javax.swing.JLabel();
        jLabelEfectivo = new javax.swing.JLabel();
        jLabelFinanciado = new javax.swing.JLabel();
        jLabelCredito = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel35 = new javax.swing.JLabel();
        jLabelCantidad = new javax.swing.JLabel();

        setResizable(true);
        setMinimumSize(new java.awt.Dimension(846, 550));
        setPreferredSize(new java.awt.Dimension(950, 550));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Codigo");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 40, 100, 20);

        jLabelCodigo.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCodigo);
        jLabelCodigo.setBounds(150, 40, 120, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Detalle");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 70, 100, 20);

        jLabelDetalle.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDetalle);
        jLabelDetalle.setBounds(150, 70, 120, 20);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Marca");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 100, 100, 20);

        jLabelMarca.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelMarca);
        jLabelMarca.setBounds(150, 100, 120, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Rubro");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(30, 130, 100, 20);

        jLabelRubro.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelRubro);
        jLabelRubro.setBounds(150, 130, 120, 20);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Subrubro");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(30, 160, 100, 20);

        jLabelSubrubro.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelSubrubro);
        jLabelSubrubro.setBounds(150, 160, 120, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Proveedor");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(30, 190, 100, 20);

        jLabelIVA.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelIVA);
        jLabelIVA.setBounds(410, 70, 80, 20);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Observaciones");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(560, 40, 100, 20);

        jTextAreaObservaciones.setColumns(20);
        jTextAreaObservaciones.setEditable(false);
        jTextAreaObservaciones.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jTextAreaObservaciones.setRows(5);
        jScrollPane2.setViewportView(jTextAreaObservaciones);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(670, 40, 220, 110);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton1.setText("Volver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(360, 380, 130, 30);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Precio de Costo");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(290, 40, 110, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Ganancia");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(290, 100, 100, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Financiacion");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(290, 130, 100, 20);

        jLabel12.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Efectivo");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(80, 310, 100, 20);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Financiado");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(280, 310, 100, 20);

        jLabel14.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Credito");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(480, 310, 70, 20);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Debito");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(650, 310, 60, 20);

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("IVA");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(290, 70, 100, 20);

        jLabelProveedor.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelProveedor);
        jLabelProveedor.setBounds(150, 190, 120, 20);

        jLabelDebito.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDebito);
        jLabelDebito.setBounds(720, 310, 80, 20);

        jLabelGanancia.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelGanancia);
        jLabelGanancia.setBounds(410, 100, 60, 20);

        jLabelFinanciacion.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelFinanciacion);
        jLabelFinanciacion.setBounds(410, 130, 80, 20);

        jLabelPrecioCosto.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelPrecioCosto);
        jLabelPrecioCosto.setBounds(410, 40, 80, 20);

        jLabelEfectivo.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelEfectivo);
        jLabelEfectivo.setBounds(190, 310, 80, 20);

        jLabelFinanciado.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelFinanciado);
        jLabelFinanciado.setBounds(390, 310, 80, 20);

        jLabelCredito.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCredito);
        jLabelCredito.setBounds(560, 310, 80, 20);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(70, 262, 790, 10);

        jLabel35.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Cantidad");
        getContentPane().add(jLabel35);
        jLabel35.setBounds(290, 190, 100, 20);

        jLabelCantidad.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCantidad);
        jLabelCantidad.setBounds(410, 190, 120, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabelCantidad;
    public static javax.swing.JLabel jLabelCodigo;
    public static javax.swing.JLabel jLabelCredito;
    public static javax.swing.JLabel jLabelDebito;
    public static javax.swing.JLabel jLabelDetalle;
    public static javax.swing.JLabel jLabelEfectivo;
    public static javax.swing.JLabel jLabelFinanciacion;
    public static javax.swing.JLabel jLabelFinanciado;
    public static javax.swing.JLabel jLabelGanancia;
    public static javax.swing.JLabel jLabelIVA;
    public static javax.swing.JLabel jLabelMarca;
    public static javax.swing.JLabel jLabelPrecioCosto;
    public static javax.swing.JLabel jLabelProveedor;
    public static javax.swing.JLabel jLabelRubro;
    public static javax.swing.JLabel jLabelSubrubro;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JTextArea jTextAreaObservaciones;
    // End of variables declaration//GEN-END:variables
}
