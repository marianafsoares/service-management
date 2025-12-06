package views.products;

import controllers.BrandController;
import controllers.CategoryController;
import controllers.ProductController;
import controllers.ProviderController;
import controllers.SubcategoryController;
import models.Brand;
import models.Category;
import models.Product;
import models.Provider;
import models.Subcategory;

import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class ProductUpsertView extends javax.swing.JInternalFrame {

    private final ProductController productController;
    private final BrandController brandController;
    private final CategoryController categoryController;
    private final SubcategoryController subcategoryController;
    private final ProviderController providerController;

    private Product product;
    private final boolean editing;
    public static boolean isOpen = false;

    private List<Brand> brandOptions = Collections.emptyList();
    private List<Category> categoryOptions = Collections.emptyList();
    private List<Subcategory> subcategoryOptions = Collections.emptyList();
    private List<Provider> providerOptions = Collections.emptyList();

    public ProductUpsertView(ProductController productController,
            BrandController brandController,
            CategoryController categoryController,
            SubcategoryController subcategoryController,
            ProviderController providerController) {
        this(productController, brandController, categoryController, subcategoryController, providerController, null);
    }

    public ProductUpsertView(ProductController productController,
            BrandController brandController,
            CategoryController categoryController,
            SubcategoryController subcategoryController,
            ProviderController providerController,
            String code) {
        this.productController = productController;
        this.brandController = brandController;
        this.categoryController = categoryController;
        this.subcategoryController = subcategoryController;
        this.providerController = providerController;
        this.product = code != null ? productController.findByCode(code) : new Product();
        this.editing = code != null;
        initComponents();
        initializeSelectors();
        setTitle(editing ? "Modificar Articulo" : "Agregar Articulo");
        buttonGroup1.add(jRadioButton21);
        buttonGroup1.add(jRadioButton105);
        if (editing) {
            loadProduct();
        } else {
            suggestNextProductCode();
        }
        isOpen = true;
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                isOpen = false;
            }
        });
    }

    private void loadProduct() {
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Artículo no encontrado");
            dispose();
            return;
        }
        jTextFieldCode.setText(product.getCode());
        jTextFieldCode.setEditable(false);
        jTextFieldDetail.setText(product.getDescription());
        if (product.getPurchasePrice() != null) {
            jTextFieldCostAmount.setText(product.getPurchasePrice().toString());
        }
        if (product.getProfitMargin() != null) {
            jTextFieldGain.setText(product.getProfitMargin().toString());
        }
        if (product.getInterestRate() != null) {
            jTextFieldFinanciacion.setText(product.getInterestRate().toString());
        }
        if (product.getCashPrice() != null) {
            jTextFieldCashPrice.setText(product.getCashPrice().toString());
        }
        if (product.getFinancedPrice() != null) {
            jTextFieldFinancePrice.setText(product.getFinancedPrice().toString());
        }

        if (product.getBrand() != null) {
            Brand brand = ensureBrandDetails(product.getBrand());
            selectBrand(brand);
        } else {
            jComboBoxBrand.setSelectedIndex(0);
        }
        if (product.getCategory() != null) {
            Category category = ensureCategoryDetails(product.getCategory());
            selectCategory(category);
        } else {
            jComboBoxCategory.setSelectedIndex(0);
        }
        if (product.getSubcategory() != null) {
            Subcategory subcategory = ensureSubcategoryDetails(product.getSubcategory());
            selectSubcategory(subcategory);
        } else {
            jComboBoxSubcategory.setSelectedIndex(0);
        }
        if (product.getProvider() != null) {
            Provider provider = ensureProviderDetails(product.getProvider());
            selectProvider(provider);
        } else {
            jComboBoxProvider.setSelectedIndex(0);
        }
        if (product.getStockQuantity() != null) {
            jTextFieldStockQuantity.setText(product.getStockQuantity().toString());
        }

        jTextAreaObs.setText(product.getNotes());
    }

    private Brand ensureBrandDetails(Brand brand) {
        if (brand == null) {
            return null;
        }
        if (brand.getName() != null || brand.getId() == null || brandController == null) {
            return brand;
        }
        Brand loaded = brandController.findById(brand.getId());
        return loaded != null ? loaded : brand;
    }

    private Category ensureCategoryDetails(Category category) {
        if (category == null) {
            return null;
        }
        if (category.getName() != null || category.getId() == null || categoryController == null) {
            return category;
        }
        Category loaded = categoryController.findById(category.getId());
        return loaded != null ? loaded : category;
    }

    private Subcategory ensureSubcategoryDetails(Subcategory subcategory) {
        if (subcategory == null) {
            return null;
        }
        if ((subcategory.getName() != null)
                && (subcategory.getCategory() == null || subcategory.getCategory().getName() != null)) {
            return subcategory;
        }
        if (subcategory.getId() == null || subcategoryController == null) {
            return subcategory;
        }
        Subcategory loaded = subcategoryController.findById(subcategory.getId());
        return loaded != null ? loaded : subcategory;
    }

    private Provider ensureProviderDetails(Provider provider) {
        if (provider == null) {
            return null;
        }
        boolean hasName = provider.getName() != null && !provider.getName().trim().isEmpty();
        boolean hasDocument = provider.getDocumentNumber() != null && !provider.getDocumentNumber().trim().isEmpty();
        if ((hasName && hasDocument) || provider.getId() == null || providerController == null) {
            return provider;
        }
        Provider loaded = providerController.findById(provider.getId());
        if (loaded == null) {
            return provider;
        }
        if (hasName && (loaded.getName() == null || loaded.getName().trim().isEmpty())) {
            loaded.setName(provider.getName());
        }
        return loaded;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jButtonReturn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        setNextFocusableComponent(jTextFieldDetail);
        jTextFieldCode = new javax.swing.JTextField();
        jTextFieldDetail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxBrand = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jComboBoxSubcategory = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboBoxProvider = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldCostAmount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldGain = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldFinanciacion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldFinancePrice = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jTextFieldCashPrice = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton105 = new javax.swing.JRadioButton();
        jRadioButton21 = new javax.swing.JRadioButton();
        jButtonSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jTextFieldStockQuantity = new javax.swing.JTextField();

        setIconifiable(true);
        setTitle("Agregar Articulo");
        setMinimumSize(new java.awt.Dimension(72, 33));
        setPreferredSize(new java.awt.Dimension(740, 600));
        getContentPane().setLayout(null);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(350, 490, 130, 30);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Codigo");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 30, 100, 20);

        jTextFieldCode.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldCode);
        jTextFieldCode.setBounds(140, 30, 120, 20);

        jTextFieldDetail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldDetailKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldDetail);
        jTextFieldDetail.setBounds(140, 60, 160, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Detalle");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(30, 60, 100, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Marca");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 90, 100, 20);

        jComboBoxBrand.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jComboBoxBrand);
        jComboBoxBrand.setBounds(140, 90, 160, 21);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Rubro");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(30, 120, 100, 20);

        jComboBoxCategory.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jComboBoxCategory);
        jComboBoxCategory.setBounds(140, 120, 160, 21);

        jComboBoxSubcategory.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jComboBoxSubcategory);
        jComboBoxSubcategory.setBounds(140, 150, 160, 21);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Subrubro");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(30, 150, 100, 20);

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Proveedor");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(30, 180, 100, 20);

        jComboBoxProvider.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jComboBoxProvider);
        jComboBoxProvider.setBounds(140, 180, 200, 21);

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Cantidad");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(30, 220, 100, 20);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Observaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(291, 163));
        jPanel1.setPreferredSize(new java.awt.Dimension(291, 163));

        jTextAreaObs.setColumns(20);
        jTextAreaObs.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextAreaObs.setRows(5);
        jTextAreaObs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextAreaObsKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextAreaObs);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(320, 20, 380, 160);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Precio de costo");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 320, 100, 20);

        jTextFieldCostAmount.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldCostAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCostAmountKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldCostAmount);
        jTextFieldCostAmount.setBounds(140, 320, 50, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Ganancia");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(230, 320, 100, 20);

        jTextFieldGain.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldGain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldGainKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldGain);
        jTextFieldGain.setBounds(340, 320, 30, 20);

        jLabel9.setText("%");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(380, 320, 15, 15);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Financiacion");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(400, 320, 100, 20);

        jTextFieldFinanciacion.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldFinanciacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldFinanciacionKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldFinanciacion);
        jTextFieldFinanciacion.setBounds(510, 320, 30, 20);

        jLabel10.setText("%");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(550, 320, 15, 15);

        jTextFieldFinancePrice.setEditable(false);
        jTextFieldFinancePrice.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldFinancePrice);
        jTextFieldFinancePrice.setBounds(300, 420, 80, 20);

        jLabel36.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel36.setText("Financiado");
        getContentPane().add(jLabel36);
        jLabel36.setBounds(210, 420, 80, 20);

        jTextFieldCashPrice.setEditable(false);
        jTextFieldCashPrice.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldCashPrice);
        jTextFieldCashPrice.setBounds(120, 420, 80, 20);

        jLabel35.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel35.setText("Efectivo");
        getContentPane().add(jLabel35);
        jLabel35.setBounds(50, 420, 60, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("IVA");
        jLabel4.setEnabled(false);
        getContentPane().add(jLabel4);
        jLabel4.setBounds(30, 360, 100, 20);

        jRadioButton105.setFont(new java.awt.Font("Calibri", 2, 14)); // NOI18N
        jRadioButton105.setText("10,5%");
        jRadioButton105.setEnabled(false);
        jRadioButton105.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton105KeyPressed(evt);
            }
        });
        getContentPane().add(jRadioButton105);
        jRadioButton105.setBounds(140, 360, 70, 25);

        jRadioButton21.setFont(new java.awt.Font("Calibri", 2, 14)); // NOI18N
        jRadioButton21.setText("21%");
        jRadioButton21.setEnabled(false);
        jRadioButton21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton21KeyPressed(evt);
            }
        });
        getContentPane().add(jRadioButton21);
        jRadioButton21.setBounds(210, 360, 49, 25);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(210, 490, 130, 30);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(20, 292, 680, 10);

        jTextFieldStockQuantity.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        getContentPane().add(jTextFieldStockQuantity);
        jTextFieldStockQuantity.setBounds(140, 220, 60, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void ponerNoEditable() {

        jTextFieldCode.setEditable(false);
        jTextFieldDetail.setEditable(false);

        jComboBoxBrand.setEnabled(false);
        jComboBoxCategory.setEnabled(false);
        jComboBoxSubcategory.setEnabled(false);
        jTextFieldStockQuantity.setEditable(false);
        jComboBoxProvider.setEnabled(false);
        jTextFieldCostAmount.setEditable(false);
        jTextFieldGain.setEditable(false);
        jTextFieldFinanciacion.setEditable(false);

    }

    private void ponerEditable() {

        jTextFieldCode.setEditable(true);
        jTextFieldDetail.setEditable(true);

        jComboBoxBrand.setEnabled(true);
        jComboBoxCategory.setEnabled(true);
        jComboBoxSubcategory.setEnabled(true);
        jTextFieldStockQuantity.setEditable(true);
        jComboBoxProvider.setEnabled(true);
        jTextFieldCostAmount.setEditable(true);
        jTextFieldGain.setEditable(true);
        jTextFieldFinanciacion.setEditable(true);
    }

    private void jTextAreaObsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaObsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextFieldCostAmount.requestFocus();
        }
}//GEN-LAST:event_jTextAreaObsKeyPressed

    private void jTextFieldDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDetailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBoxBrand.requestFocusInWindow();
        }
}//GEN-LAST:event_jTextFieldDetailKeyPressed

    private void jRadioButton105KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton105KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextFieldGain.requestFocus();
        }
}//GEN-LAST:event_jRadioButton105KeyPressed

    private void jRadioButton21KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton21KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextFieldGain.requestFocus();
        }
}//GEN-LAST:event_jRadioButton21KeyPressed

    private void jTextFieldFinanciacionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFinanciacionKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BigDecimal cash = new BigDecimal(jTextFieldCashPrice.getText());
            Float interest = Float.parseFloat(jTextFieldFinanciacion.getText());
            BigDecimal financed = productController.calculateFinancedPrice(cash, interest);
            jTextFieldFinancePrice.setText(financed.toString());
        }
    }//GEN-LAST:event_jTextFieldFinanciacionKeyPressed

    private void jTextFieldGainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldGainKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Float vat = 0f;
            BigDecimal cost = new BigDecimal(jTextFieldCostAmount.getText());
            Float profit = Float.parseFloat(jTextFieldGain.getText());
            BigDecimal cash = productController.calculateCashPrice(vat, cost, profit);
            jTextFieldCashPrice.setText(cash.toString());
            jTextFieldFinanciacion.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldGainKeyPressed

    private void jTextFieldCostAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCostAmountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextFieldGain.requestFocus();
        }
}//GEN-LAST:event_jTextFieldCostAmountKeyPressed


    private void initializeSelectors() {
        configureComboRenderers();
        brandOptions = filterNonNull(brandController != null ? brandController.findAll() : Collections.emptyList());
        categoryOptions = filterNonNull(categoryController != null ? categoryController.findAll() : Collections.emptyList());
        subcategoryOptions = filterNonNull(subcategoryController != null ? subcategoryController.findAll() : Collections.emptyList());
        providerOptions = filterNonNull(providerController != null ? providerController.findAll() : Collections.emptyList());

        sortBrands();
        sortCategories();
        sortProviders();

        refreshBrandCombo();
        refreshCategoryCombo();
        refreshProviderCombo();

        jComboBoxCategory.addActionListener(evt -> handleCategorySelectionChange());
        handleCategorySelectionChange();
    }

    private void configureComboRenderers() {
        DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        jComboBoxBrand.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value instanceof Brand brand ? safeName(brand.getName()) : (value == null ? "Seleccione" : value.toString());
            return defaultRenderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        });
        jComboBoxCategory.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value instanceof Category category ? safeName(category.getName()) : (value == null ? "Seleccione" : value.toString());
            return defaultRenderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        });
        jComboBoxSubcategory.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = value instanceof Subcategory subcategory ? safeName(subcategory.getName()) : (value == null ? "Seleccione" : value.toString());
            return defaultRenderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        });
        jComboBoxProvider.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text;
            if (value instanceof Provider provider) {
                String name = safeName(provider.getName());
                String document = safeName(provider.getDocumentNumber());
                text = document.isEmpty() ? name : name + " (" + document + ")";
            } else {
                text = value == null ? "Seleccione" : value.toString();
            }
            return defaultRenderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        });
    }

    private void refreshBrandCombo() {
        DefaultComboBoxModel<Brand> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        for (Brand brand : brandOptions) {
            model.addElement(brand);
        }
        jComboBoxBrand.setModel(model);
    }

    private void refreshCategoryCombo() {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        for (Category category : categoryOptions) {
            model.addElement(category);
        }
        jComboBoxCategory.setModel(model);
    }

    private void refreshProviderCombo() {
        DefaultComboBoxModel<Provider> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        for (Provider provider : providerOptions) {
            model.addElement(provider);
        }
        jComboBoxProvider.setModel(model);
    }

    private void handleCategorySelectionChange() {
        Category selectedCategory = (Category) jComboBoxCategory.getSelectedItem();
        loadSubcategoryOptions(selectedCategory);
    }

    private void loadSubcategoryOptions(Category category) {
        Subcategory currentSelection = (Subcategory) jComboBoxSubcategory.getSelectedItem();
        DefaultComboBoxModel<Subcategory> model = new DefaultComboBoxModel<>();
        model.addElement(null);
        for (Subcategory subcategory : subcategoryOptions) {
            if (subcategory == null) {
                continue;
            }
            if (category == null) {
                model.addElement(subcategory);
                continue;
            }
            if (subcategory.getCategory() == null) {
                continue;
            }
            if (Objects.equals(subcategory.getCategory().getId(), category.getId())) {
                model.addElement(subcategory);
            }
        }
        jComboBoxSubcategory.setModel(model);
        if (currentSelection != null) {
            selectSubcategory(currentSelection);
        } else {
            jComboBoxSubcategory.setSelectedIndex(0);
        }
    }

    private void sortBrands() {
        brandOptions.sort(Comparator.comparing(brand -> safeName(brand.getName()), String.CASE_INSENSITIVE_ORDER));
    }

    private void sortCategories() {
        categoryOptions.sort(Comparator.comparing(category -> safeName(category.getName()), String.CASE_INSENSITIVE_ORDER));
        subcategoryOptions.sort(Comparator.comparing(subcategory -> safeName(subcategory.getName()), String.CASE_INSENSITIVE_ORDER));
    }

    private void sortProviders() {
        providerOptions.sort(Comparator.comparing(provider -> safeName(provider.getName()), String.CASE_INSENSITIVE_ORDER));
    }

    private String safeName(String value) {
        return value == null ? "" : value.trim();
    }

    private <T> List<T> filterNonNull(List<T> source) {
        if (source == null) {
            return new java.util.ArrayList<>();
        }
        java.util.ArrayList<T> result = new java.util.ArrayList<>();
        for (T item : source) {
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    private void selectBrand(Brand brand) {
        selectComboItem(jComboBoxBrand, brand, (option, target) -> Objects.equals(option.getId(), target.getId()));
    }

    private void selectCategory(Category category) {
        selectComboItem(jComboBoxCategory, category, (option, target) -> Objects.equals(option.getId(), target.getId()));
    }

    private void selectSubcategory(Subcategory subcategory) {
        selectComboItem(jComboBoxSubcategory, subcategory, (option, target) -> Objects.equals(option.getId(), target.getId()));
    }

    private void selectProvider(Provider provider) {
        selectComboItem(jComboBoxProvider, provider, (option, target) -> Objects.equals(option.getId(), target.getId()));
    }

    private <T> void selectComboItem(JComboBox<T> comboBox, T target, java.util.function.BiPredicate<T, T> matcher) {
        if (target == null) {
            comboBox.setSelectedIndex(0);
            return;
        }
        ComboBoxModel<T> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            T option = model.getElementAt(i);
            if (option != null && matcher.test(option, target)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(0);
    }

    private void suggestNextProductCode() {
        if (editing) {
            return;
        }
        int nextCode = productController.findAll().stream()
                .map(Product::getCode)
                .filter(this::isNumericCode)
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1;
        if (nextCode > 0) {
            jTextFieldCode.setText(String.valueOf(nextCode));
            jTextFieldCode.selectAll();
        }
    }

    private boolean isNumericCode(String code) {
        if (code == null) {
            return false;
        }
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        if (!validateForm()) {
            return;
        }

        Product product = editing ? this.product : new Product();
        String codeValue = jTextFieldCode.getText().trim().toUpperCase();
        product.setCode(codeValue);
        product.setDescription(jTextFieldDetail.getText().trim().toUpperCase());

        Brand brand = (Brand) jComboBoxBrand.getSelectedItem();
        product.setBrand(brand);

        Category category = (Category) jComboBoxCategory.getSelectedItem();
        product.setCategory(category);

        Subcategory subcategory = (Subcategory) jComboBoxSubcategory.getSelectedItem();
        if (subcategory != null) {
            product.setSubcategory(subcategory);
            if (subcategory.getCategory() != null) {
                product.setCategory(subcategory.getCategory());
            }
        } else {
            product.setSubcategory(null);
        }

        Provider provider = (Provider) jComboBoxProvider.getSelectedItem();
        product.setProvider(provider);
        if (!jTextFieldStockQuantity.getText().isEmpty()) {
            product.setStockQuantity(Float.parseFloat(jTextFieldStockQuantity.getText()));
        }

        if (!jTextFieldCostAmount.getText().isEmpty()) {
            product.setPurchasePrice(new BigDecimal(jTextFieldCostAmount.getText()));
        }

        Float vat = 0f;
        product.setVatRate(vat);

        if (!jTextFieldGain.getText().isEmpty()) {
            product.setProfitMargin(Float.parseFloat(jTextFieldGain.getText()));
        }
        if (!jTextFieldFinanciacion.getText().isEmpty()) {
            product.setInterestRate(Float.parseFloat(jTextFieldFinanciacion.getText()));
        }

        BigDecimal cashPrice = productController.calculateCashPrice(vat, product.getPurchasePrice(), product.getProfitMargin());
        BigDecimal financedPrice = productController.calculateFinancedPrice(cashPrice, product.getInterestRate());

        product.setCashPrice(cashPrice);
        product.setFinancedPrice(financedPrice);

        product.setNotes(jTextAreaObs.getText());
        product.setEnabled(true);

        if (editing) {
            productController.update(product);
            JOptionPane.showMessageDialog(this,
                    "Producto actualizado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            productController.save(product);
            JOptionPane.showMessageDialog(this,
                    "Producto guardado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        closeAndRefreshParent();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    public boolean validateForm() {

        // Valida que el codigo no este vacio
        if (jTextFieldCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El codigo del articulo no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldCode.requestFocus();
            return (false);
        }

        // Valida que el detalle no este vacio
        if (jTextFieldDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El detalle del articulo no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldDetail.requestFocus();
            return (false);
        }

        if (jTextFieldCostAmount.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El precio del articulo no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldCostAmount.requestFocus();
            return (false);
        }

        if (jTextFieldGain.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo ganancia no puede estar vacio no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldGain.requestFocus();
            return (false);
        }

        if (jTextFieldFinanciacion.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo financiacion no puede estar vacio no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldFinanciacion.requestFocus();
            return (false);
        }

        if (jTextFieldCashPrice.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo efectivo no puede estar vacio no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldGain.requestFocus();
            return (false);
        }

        if (jComboBoxBrand.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una marca!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jComboBoxBrand.requestFocusInWindow();
            return (false);
        }

        if (jComboBoxCategory.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rubro!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jComboBoxCategory.requestFocusInWindow();
            return (false);
        }

        if (jComboBoxProvider.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jComboBoxProvider.requestFocusInWindow();
            return (false);
        }

        if (jTextFieldFinancePrice.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "El campo financiado no puede estar vacio no puede estar vacio!", "Atencion", JOptionPane.WARNING_MESSAGE);
            jTextFieldFinanciacion.requestFocus();
            return (false);
        }

        try {
            Float.parseFloat(jTextFieldCostAmount.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(this, "El campo precio debe ser numerico!", "Guardar", JOptionPane.WARNING_MESSAGE);
            jTextFieldCostAmount.requestFocus();
            return false;
        }

        try {
            Float.parseFloat(jTextFieldGain.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(this, "El campo ganancia debe ser numerico!", "Guardar", JOptionPane.WARNING_MESSAGE);
            jTextFieldGain.requestFocus();
            return false;
        }

        try {
            Float.parseFloat(jTextFieldFinanciacion.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(this, "El campo financiacion debe ser numerico!", "Guardar", JOptionPane.WARNING_MESSAGE);
            jTextFieldFinanciacion.requestFocus();
            return false;
        }

        if (!jTextFieldStockQuantity.getText().trim().isEmpty()) {
            try {
                Float.parseFloat(jTextFieldStockQuantity.getText());
            } catch (Exception NumberFormatException) {
                JOptionPane.showMessageDialog(this, "El campo cantidad debe ser numerico!", "Guardar", JOptionPane.WARNING_MESSAGE);
                jTextFieldStockQuantity.requestFocus();
                return false;
            }
        }

        if (!editing) {
            String code = jTextFieldCode.getText().trim();
            if (!code.isEmpty()) {
                Product existing = productController.findByCode(code.toUpperCase());
                if (existing != null) {
                    JOptionPane.showMessageDialog(this, "Ya existe un artículo con el código indicado!", "Guardar", JOptionPane.WARNING_MESSAGE);
                    jTextFieldCode.requestFocus();
                    jTextFieldCode.selectAll();
                    return false;
                }
            }
        }

        return true;

    }

    private void closeAndRefreshParent() {
        ProductManagementView.refreshTable();
        isOpen = false;
        dispose();
    }

    private void limpiarFormulario() {
        jTextFieldCode.setText("");
        jTextFieldDetail.setText("");
        if (jComboBoxBrand.getItemCount() > 0) {
            jComboBoxBrand.setSelectedIndex(0);
        }
        if (jComboBoxCategory.getItemCount() > 0) {
            jComboBoxCategory.setSelectedIndex(0);
            handleCategorySelectionChange();
        }
        if (jComboBoxSubcategory.getItemCount() > 0) {
            jComboBoxSubcategory.setSelectedIndex(0);
        }
        if (jComboBoxProvider.getItemCount() > 0) {
            jComboBoxProvider.setSelectedIndex(0);
        }
        jTextAreaObs.setText("");
        jTextFieldCostAmount.setText("");
        jTextFieldGain.setText("");
        jTextFieldFinanciacion.setText("");
        jTextFieldCashPrice.setText("");
        jTextFieldFinancePrice.setText("");
        jRadioButton21.setSelected(false);
        jRadioButton105.setSelected(false);
        jTextFieldStockQuantity.setText("");
        suggestNextProductCode();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton105;
    private javax.swing.JRadioButton jRadioButton21;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaObs;
    public static javax.swing.JComboBox<Brand> jComboBoxBrand;
    public static javax.swing.JTextField jTextFieldCashPrice;
    public static javax.swing.JComboBox<Category> jComboBoxCategory;
    public static javax.swing.JTextField jTextFieldCode;
    public static javax.swing.JTextField jTextFieldCostAmount;
    public static javax.swing.JTextField jTextFieldDetail;
    public static javax.swing.JTextField jTextFieldFinancePrice;
    public static javax.swing.JTextField jTextFieldFinanciacion;
    public static javax.swing.JTextField jTextFieldGain;
    public static javax.swing.JComboBox<Provider> jComboBoxProvider;
    private javax.swing.JTextField jTextFieldStockQuantity;
    public static javax.swing.JComboBox<Subcategory> jComboBoxSubcategory;
    // End of variables declaration//GEN-END:variables

}
