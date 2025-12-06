package views.products;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.math.RoundingMode;
import controllers.ProductController;
import models.Brand;
import models.Category;
import models.Product;
import models.Provider;
import models.Subcategory;
import java.util.function.Function;

public class ProductUpdatePricesView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private List<Product> products = null;

    private final ProductController productController;

    public ProductUpdatePricesView(ProductController productController) {
        this.productController = productController;
        initComponents();
        addKeyReleaseListeners();
        jTextFieldSubcategory.setEnabled(false);
        isOpen = true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldBrand = new javax.swing.JTextField();
        jTextFieldPercentage = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButtonAccept = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jCheckBoxAllProducts = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldProvider = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldCategory = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldSubcategory = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        setMinimumSize(new java.awt.Dimension(430, 480));
        setPreferredSize(new java.awt.Dimension(430, 480));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Marca");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 70, 80, 25);

        jTextFieldBrand.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldBrand);
        jTextFieldBrand.setBounds(130, 70, 180, 25);

        jTextFieldPercentage.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldPercentage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPercentageKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldPercentage);
        jTextFieldPercentage.setBounds(220, 280, 60, 25);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Porcentaje de actualizacion");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 280, 200, 25);

        jButtonAccept.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAccept.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonAccept.setText("Aceptar");
        jButtonAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAcceptActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAccept);
        jButtonAccept.setBounds(90, 330, 120, 27);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(220, 330, 140, 27);

        jCheckBoxAllProducts.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jCheckBoxAllProducts.setText("Todos los articulos");
        jCheckBoxAllProducts.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxAllProductsItemStateChanged(evt);
            }
        });
        getContentPane().add(jCheckBoxAllProducts);
        jCheckBoxAllProducts.setBounds(70, 20, 190, 24);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Proveedor");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(30, 110, 80, 25);

        jTextFieldProvider.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldProvider);
        jTextFieldProvider.setBounds(130, 110, 180, 25);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Rubro");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(30, 150, 80, 25);

        jTextFieldCategory.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldCategory);
        jTextFieldCategory.setBounds(130, 150, 180, 25);

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Subrubro");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(20, 190, 90, 25);

        jTextFieldSubcategory.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldSubcategory.setEnabled(false);
        getContentPane().add(jTextFieldSubcategory);
        jTextFieldSubcategory.setBounds(130, 190, 180, 25);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(30, 242, 370, 10);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAcceptActionPerformed
        try {
            if (!(Integer.parseInt(jTextFieldPercentage.getText()) == 0)) {

                float porcAumento = Float.parseFloat(jTextFieldPercentage.getText());

                if (porcAumento < 0 && Math.abs(porcAumento) >= 100) {
                    JOptionPane.showMessageDialog(this, "El porcentaje negativo no puede ser mayor o igual a 100", "Atencion", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (jCheckBoxAllProducts.isSelected()) {
                    products = productController.findEnabled();
                } else {
                    boolean validForm = validateForm();
                    if (!validForm) {
                        return;
                    }

                    Integer brandId = getCode(jTextFieldBrand.getText());
                    Integer providerId = getCode(jTextFieldProvider.getText());
                    Integer categoryId = getCode(jTextFieldCategory.getText());
                    Integer subcategoryId = getCode(jTextFieldSubcategory.getText());

                    products = productController.findEnabled().stream()
                            .filter(p -> brandId == null || (p.getBrand() != null && Objects.equals(p.getBrand().getId(), brandId)))
                            .filter(p -> providerId == null || (p.getProvider() != null && Objects.equals(p.getProvider().getId(), providerId)))
                            .filter(p -> categoryId == null || (p.getCategory() != null && Objects.equals(p.getCategory().getId(), categoryId)))
                            .filter(p -> subcategoryId == null || (p.getSubcategory() != null && Objects.equals(p.getSubcategory().getId(), subcategoryId)))
                            .collect(Collectors.toList());
                }

                for (Product product : products) {
                    if (porcAumento < 0) {
                        this.decreasePrice(product, Math.abs(porcAumento));
                    } else {
                        this.increasePrice(product, porcAumento);
                    }
                }

                JOptionPane.showMessageDialog(this, "Ya se han actualizado los precios correctamente", "Atencion", JOptionPane.WARNING_MESSAGE);

                ProductManagementView.refreshTable();
                isOpen = false;
                dispose();

                /*
                ArrayList remitos = Remito.buscarTodosRemitosAbiertos();
                int i = 0;
                int max = remitos.size();

                while (i < max) {
                    Remito r = (Remito) remitos.get(i);
                    ArrayList detalles = DetalleRemito.buscarDetallesRemitos(r.getCodigo());

                    int j = 0;
                    int maximo = detalles.size();

                    BigDecimal tot = BigDecimal.ZERO;
                    BigDecimal precio = BigDecimal.ZERO;
                    while (j < maximo) {
                        DetalleRemito dr = (DetalleRemito) detalles.get(j);

                        String cod = dr.getCodArticulo();
                        Product p = productController.findByCode(cod);

                        precio = dr.getPrecio();

                        if (p != null && p.getCashPrice() != null && p.getCashPrice().compareTo(dr.getPrecio()) != 0) {
                            if (!"99".equals(dr.getCodArticulo())) {
                                dr.modificarPrecioEnRemito(p.getCashPrice());
                                precio = p.getCashPrice();
                            }
                        }

                        tot = tot.add((precio.multiply(new BigDecimal(dr.getCantidad()))));
                        j++;
                    }
                    r.setTotal(tot);
                    r.modificarRemito();

                    i++;
                }
                */
            } else {
                JOptionPane.showMessageDialog(this, "El porcentaje debe distinto de cero", "Atencion", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductUpdatePricesView.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jButtonAcceptActionPerformed

    public void increasePrice(Product p, float percentage) {

        if (p != null) {
            try {
                BigDecimal purchasePrice = p.getPurchasePrice();

                BigDecimal increaseFactor = BigDecimal.valueOf(1 + percentage / 100);
                BigDecimal updatedCost = purchasePrice.multiply(increaseFactor);

                BigDecimal cashPrice = productController.calculateCashPrice(p.getVatRate(), updatedCost, p.getProfitMargin());
                BigDecimal financedPrice = productController.calculateFinancedPrice(cashPrice, p.getInterestRate());

                p.setPurchasePrice(updatedCost);
                p.setCashPrice(cashPrice);
                p.setFinancedPrice(financedPrice);

                productController.update(p);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void decreasePrice(Product p, float percentage) {
        try {
            float discountPercent = Math.abs(percentage);

            BigDecimal purchasePrice = p.getPurchasePrice();

            BigDecimal discountRate = BigDecimal.valueOf(discountPercent)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal discountFactor = BigDecimal.ONE.subtract(discountRate);
            if (discountFactor.compareTo(BigDecimal.ZERO) < 0) {
                discountFactor = BigDecimal.ZERO;
            }

            BigDecimal updatedCost = purchasePrice.multiply(discountFactor)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal cashPrice = productController.calculateCashPrice(p.getVatRate(), updatedCost, p.getProfitMargin());
            BigDecimal financedPrice = productController.calculateFinancedPrice(cashPrice, p.getInterestRate());

            p.setPurchasePrice(updatedCost);
            p.setCashPrice(cashPrice);
            p.setFinancedPrice(financedPrice);

            productController.update(p);

        } catch (Exception ex) {
            Logger.getLogger(ProductUpdatePricesView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean validateForm() {

        boolean result = true;

        return result;

    }


    private void addKeyReleaseListeners() {
        jTextFieldBrand.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                handleBrandKeyReleased(evt);
            }
        });
        jTextFieldCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                handleCategoryKeyReleased(evt);
            }
        });
        jTextFieldSubcategory.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                handleSubcategoryKeyReleased(evt);
            }
        });
        jTextFieldProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                handleProviderKeyReleased(evt);
            }
        });
    }

    private void handleBrandKeyReleased(java.awt.event.KeyEvent evt) {
        if (jTextFieldBrand.getText().length() >= 3) {
            List<Brand> suggestions = getBrandSuggestions(jTextFieldBrand.getText());
            showSuggestions(jTextFieldBrand, suggestions, Brand::getId, Brand::getName, null);
        }
    }

    private void handleCategoryKeyReleased(java.awt.event.KeyEvent evt) {
        if (jTextFieldCategory.getText().isEmpty()) {
            jTextFieldSubcategory.setText("");
            jTextFieldSubcategory.setEnabled(false);
        } else if (jTextFieldCategory.getText().length() >= 3) {
            List<Category> suggestions = getCategorySuggestions(jTextFieldCategory.getText());
            showSuggestions(jTextFieldCategory, suggestions, Category::getId, Category::getName, () -> {
                jTextFieldSubcategory.setEnabled(true);
                jTextFieldSubcategory.setText("");
            });
        }
    }

    private void handleSubcategoryKeyReleased(java.awt.event.KeyEvent evt) {
        if (!jTextFieldSubcategory.isEnabled()) {
            return;
        }
        if (jTextFieldSubcategory.getText().length() >= 3) {
            Integer categoryId = getCode(jTextFieldCategory.getText());
            List<Subcategory> suggestions = getSubcategorySuggestions(jTextFieldSubcategory.getText(), categoryId);
            showSuggestions(jTextFieldSubcategory, suggestions, Subcategory::getId, Subcategory::getName, null);
        }
    }

    private void handleProviderKeyReleased(java.awt.event.KeyEvent evt) {
        if (jTextFieldProvider.getText().length() >= 3) {
            List<Provider> suggestions = getProviderSuggestions(jTextFieldProvider.getText());
            showSuggestions(jTextFieldProvider, suggestions, Provider::getId, Provider::getName, null);
        }
    }

    private List<Brand> getBrandSuggestions(String query) {
        return productController.findAll().stream()
                .map(Product::getBrand)
                .filter(Objects::nonNull)
                .filter(b -> b.getName() != null && b.getName().toUpperCase().contains(query.toUpperCase()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Brand::getId, b -> b, (b1, b2) -> b1),
                        m -> new ArrayList<>(m.values())));
    }

    private List<Category> getCategorySuggestions(String query) {
        return productController.findAll().stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .filter(c -> c.getName() != null && c.getName().toUpperCase().contains(query.toUpperCase()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Category::getId, c -> c, (c1, c2) -> c1),
                        m -> new ArrayList<>(m.values())));
    }

    private List<Subcategory> getSubcategorySuggestions(String query, Integer categoryId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        return productController.findAll().stream()
                .filter(p -> p.getCategory() != null && Objects.equals(p.getCategory().getId(), categoryId))
                .map(Product::getSubcategory)
                .filter(Objects::nonNull)
                .filter(s -> s.getName() != null && s.getName().toUpperCase().contains(query.toUpperCase()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Subcategory::getId, s -> s, (s1, s2) -> s1),
                        m -> new ArrayList<>(m.values())));
    }

    private List<Provider> getProviderSuggestions(String query) {
        return productController.findAll().stream()
                .map(Product::getProvider)
                .filter(Objects::nonNull)
                .filter(p -> p.getName() != null && p.getName().toUpperCase().contains(query.toUpperCase()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Provider::getId, p -> p, (p1, p2) -> p1),
                        m -> new ArrayList<>(m.values())));
    }

    private <T> void showSuggestions(JTextField textField, List<T> suggestions,
            Function<T, Integer> idGetter, Function<T, String> nameGetter, Runnable afterSelect) {
        if (suggestions.isEmpty()) {
            return;
        }
        JPopupMenu popup = new JPopupMenu();
        for (T suggestion : suggestions) {
            String name = nameGetter.apply(suggestion);
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(e -> {
                Integer id = idGetter.apply(suggestion);
                textField.setText(id + " - " + name);
                if (afterSelect != null) {
                    afterSelect.run();
                }
                textField.requestFocus();
            });
            popup.add(item);
        }
        popup.show(textField, 0, textField.getHeight());
    }

    private Integer getCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String[] parts = text.split(" - ");
        try {
            return Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void jCheckBoxAllProductsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxAllProductsItemStateChanged
        if (jCheckBoxAllProducts.isSelected()) {
            jTextFieldBrand.setEditable(false);
            jTextFieldProvider.setEditable(false);
            jTextFieldCategory.setEditable(false);
            jTextFieldSubcategory.setEditable(false);
        } else {
            jTextFieldBrand.setEditable(true);
            jTextFieldProvider.setEditable(true);
            jTextFieldCategory.setEditable(true);
            jTextFieldSubcategory.setEditable(true);
        }

    }//GEN-LAST:event_jCheckBoxAllProductsItemStateChanged

    private void jTextFieldPercentageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPercentageKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButtonAccept.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldPercentageKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAccept;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JCheckBox jCheckBoxAllProducts;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JTextField jTextFieldBrand;
    public static javax.swing.JTextField jTextFieldCategory;
    private javax.swing.JTextField jTextFieldPercentage;
    public static javax.swing.JTextField jTextFieldProvider;
    public static javax.swing.JTextField jTextFieldSubcategory;
    // End of variables declaration//GEN-END:variables

}
