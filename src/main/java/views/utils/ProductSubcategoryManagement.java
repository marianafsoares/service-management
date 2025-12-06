package views.utils;

import controllers.CategoryController;
import controllers.SubcategoryController;
import configs.MyBatisConfig;
import mappers.CategoryMapper;
import mappers.SubcategoryMapper;
import models.Category;
import models.Subcategory;
import org.apache.ibatis.session.SqlSession;
import repositories.CategoryRepository;
import repositories.SubcategoryRepository;
import repositories.impl.CategoryRepositoryImpl;
import repositories.impl.SubcategoryRepositoryImpl;
import services.CategoryService;
import services.SubcategoryService;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ProductSubcategoryManagement extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private SubcategoryController subcategoryController;
    private CategoryController categoryController;
    private List<Subcategory> subcategories = null;
    private List<Category> categories = null;
    private SqlSession sqlSession;
    private boolean editing = false;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private Subcategory selectedSubcategory;
    private boolean updatingCategorySelection = false;

    public ProductSubcategoryManagement() throws Exception {
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        SubcategoryMapper subcategoryMapper = sqlSession.getMapper(SubcategoryMapper.class);
        SubcategoryRepository subcategoryRepository = new SubcategoryRepositoryImpl(subcategoryMapper);
        SubcategoryService subcategoryService = new SubcategoryService(subcategoryRepository);
        subcategoryController = new SubcategoryController(subcategoryService);

        CategoryMapper categoryMapper = sqlSession.getMapper(CategoryMapper.class);
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(categoryMapper);
        CategoryService categoryService = new CategoryService(categoryRepository);
        categoryController = new CategoryController(categoryService);

        categories = categoryController.findAll();

        initComponents();
        loadCategories();
        isOpen = true;
        refreshTable();
    }

    public void refreshTable() {
        Category selectedCategory = getSelectedCategory();
        subcategories = subcategoryController.findAll().stream()
                .filter(s -> selectedCategory != null && s.getCategory() != null
                        && s.getCategory().getId() == selectedCategory.getId())
                .collect(Collectors.toList());
        DefaultTableModel model = createSubcategoryTableModel(subcategories);
        jTable1.setModel(model);
        rowSorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(rowSorter);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applyFilter();
        selectedSubcategory = null;
        editing = false;
        jTable1.clearSelection();
        jTextFieldDetail.setText("");
        jTextAreaObs.setText("");
        jTextFieldDetail.setEditable(false);
        jTextAreaObs.setEditable(false);
    }

    private void loadCategories() {
        updatingCategorySelection = true;
        try {
            jComboBoxCategory.removeAllItems();
            jComboBoxCategory.addItem("Seleccione...");
            if (categories != null) {
                for (Category category : categories) {
                    jComboBoxCategory.addItem(category.getName());
                }
            }
            jComboBoxCategory.setSelectedIndex(0);
        } finally {
            updatingCategorySelection = false;
        }
    }

    private Category getSelectedCategory() {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        int index = jComboBoxCategory.getSelectedIndex();
        if (index <= 0 || index > categories.size()) {
            return null;
        }
        return categories.get(index - 1);
    }

    private void selectCategoryInCombo(int categoryId) {
        if (categories == null) {
            return;
        }
        updatingCategorySelection = true;
        try {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == categoryId) {
                    jComboBoxCategory.setSelectedIndex(i + 1);
                    return;
                }
            }
            jComboBoxCategory.setSelectedIndex(0);
        } finally {
            updatingCategorySelection = false;
        }
    }

    private DefaultTableModel createSubcategoryTableModel(List<Subcategory> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Detalle"}, 0) {
            boolean[] canEdit = new boolean[]{false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (Subcategory s : list) {
            Vector<Object> row = new Vector<>();
            row.add(s.getId());
            row.add(s.getName());
            tm.addRow(row);
        }
        return tm;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonAdd = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldDetail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(790, 520));
        setPreferredSize(new java.awt.Dimension(800, 529));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 70, 50, 20);

        jTextFieldSearch.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldSearch);
        jTextFieldSearch.setBounds(90, 70, 270, 20);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo", "Descripcion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
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
        jScrollPane1.setBounds(20, 110, 340, 270);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(51, 51, 51));
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(400, 60, 30, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Subrubro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N
        jPanel1.setEnabled(false);
        jPanel1.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Detalle");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(20, 40, 100, 20);

        jTextFieldDetail.setEditable(false);
        jTextFieldDetail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jPanel1.add(jTextFieldDetail);
        jTextFieldDetail.setBounds(130, 40, 230, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setText("Observaciones");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 100, 100, 20);

        jTextAreaObs.setEditable(false);
        jTextAreaObs.setColumns(20);
        jTextAreaObs.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextAreaObs.setRows(5);
        jScrollPane2.setViewportView(jTextAreaObs);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(130, 100, 230, 120);

        jLabel6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Rubro");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(20, 70, 100, 20);

        jComboBoxCategory.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jComboBoxCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCategoryActionPerformed(evt);
            }
        });
        jPanel1.add(jComboBoxCategory);
        jComboBoxCategory.setBounds(130, 70, 230, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(380, 110, 380, 250);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(240, 400, 120, 27);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(370, 400, 130, 27);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(440, 60, 30, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {
        applyFilter();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = jTable1.convertRowIndexToModel(viewRow);
            selectedSubcategory = subcategories.get(modelRow);
            jTextFieldDetail.setText(selectedSubcategory.getName());
            jTextAreaObs.setText(selectedSubcategory.getNotes());
            if (selectedSubcategory.getCategory() != null) {
                selectCategoryInCombo(selectedSubcategory.getCategory().getId());
            }
            editing = true;
            jTextFieldDetail.setEditable(true);
            jTextAreaObs.setEditable(true);
            jTextFieldDetail.requestFocus();
            jTextFieldDetail.selectAll();
        }
    }

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
        editing = false;
        selectedSubcategory = null;
        jTable1.clearSelection();
        if (getSelectedCategory() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rubro.", "Atención", JOptionPane.WARNING_MESSAGE);
            jComboBoxCategory.requestFocus();
            return;
        }
        jTextFieldDetail.setEditable(true);
        jTextAreaObs.setEditable(true);
        jTextFieldDetail.setText("");
        jTextAreaObs.setText("");
        jTextFieldDetail.requestFocus();
    }

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningún subrubro", "Modificar", JOptionPane.OK_OPTION);
        } else {
            int modelRow = jTable1.convertRowIndexToModel(viewRow);
            selectedSubcategory = subcategories.get(modelRow);
            if (selectedSubcategory.getCategory() != null) {
                selectCategoryInCombo(selectedSubcategory.getCategory().getId());
            }
            editing = true;
            jTextFieldDetail.setEditable(true);
            jTextAreaObs.setEditable(true);
            jTextFieldDetail.requestFocus();
            jTextFieldDetail.selectAll();
        }
    }

    private boolean validateForm() {
        if (jTextFieldDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El detalle del subrubro no puede estar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTextFieldDetail.requestFocus();
            return false;
        }
        if (getSelectedCategory() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rubro.", "Atención", JOptionPane.WARNING_MESSAGE);
            jComboBoxCategory.requestFocus();
            return false;
        }
        return true;
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateForm()) {
            return;
        }
        try {
            Subcategory subcategory = new Subcategory();
            subcategory.setName(jTextFieldDetail.getText().toUpperCase());
            subcategory.setNotes(jTextAreaObs.getText().toUpperCase());
            Category category = getSelectedCategory();
            subcategory.setCategory(category);
            if (editing && selectedSubcategory != null) {
                subcategory.setId(selectedSubcategory.getId());
                subcategoryController.update(subcategory);
            } else {
                subcategoryController.create(subcategory);
            }
            refreshTable();
            selectedSubcategory = null;
            editing = false;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ProductSubcategoryManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        isOpen = false;
        sqlSession.close();
        dispose();
    }

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningún subrubro", "Eliminar", JOptionPane.OK_OPTION);
        } else {
            int option = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar el subrubro seleccionado?", "Atención", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int modelRow = jTable1.convertRowIndexToModel(viewRow);
                int id = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString());
                subcategoryController.delete(id);
                refreshTable();
                selectedSubcategory = null;
            }
        }
    }

    private void jComboBoxCategoryActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isOpen || updatingCategorySelection) {
            return;
        }
        refreshTable();
    }

    private void applyFilter() {
        if (rowSorter == null) {
            return;
        }
        String text = jTextFieldSearch.getText();
        if (text == null || text.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text.trim())));
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaObs;
    private javax.swing.JComboBox<String> jComboBoxCategory;
    private javax.swing.JTextField jTextFieldDetail;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
