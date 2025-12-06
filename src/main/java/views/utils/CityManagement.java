package views.utils;

import controllers.CityController;
import configs.MyBatisConfig;
import mappers.CityMapper;
import models.City;
import org.apache.ibatis.session.SqlSession;
import repositories.CityRepository;
import repositories.impl.CityRepositoryImpl;
import services.CityService;

import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CityManagement extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private CityController cityController;
    private List<City> cities = null;
    private SqlSession sqlSession;
    private boolean editing = false;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private City selectedCity;

    public CityManagement() throws Exception {
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        CityMapper cityMapper = sqlSession.getMapper(CityMapper.class);
        CityRepository cityRepository = new CityRepositoryImpl(cityMapper);
        CityService cityService = new CityService(cityRepository);
        cityController = new CityController(cityService);

        initComponents();
        isOpen = true;
        refreshTable();
    }

    public void refreshTable() {
        cities = cityController.findAll();
        DefaultTableModel model = createCityTableModel(cities);
        jTable1.setModel(model);
        rowSorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(rowSorter);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applyFilter();
        selectedCity = null;
        editing = false;
        jTable1.clearSelection();
        jTextFieldDetail.setText("");
        jTextFieldDetail.setEditable(false);
    }

    private DefaultTableModel createCityTableModel(List<City> cityList) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Nombre"}, 0) {
            boolean[] canEdit = new boolean[]{false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (City c : cityList) {
            Vector<Object> row = new Vector<>();
            row.add(c.getId());
            row.add(c.getName());
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
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(660, 400));
        setPreferredSize(new java.awt.Dimension(660, 400));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 50, 70, 20);

        jTextFieldSearch.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });
        getContentPane().add(jTextFieldSearch);
        jTextFieldSearch.setBounds(100, 50, 170, 21);

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

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 90, 280, 230);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(51, 51, 51));
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(340, 40, 30, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ciudad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Nombre");

        jTextFieldDetail.setEditable(false);
        jTextFieldDetail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jTextFieldDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(330, 90, 290, 90);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(350, 200, 120, 30);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Volver");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(490, 200, 120, 30);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(380, 40, 30, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {
        applyFilter();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = jTable1.convertRowIndexToModel(viewRow);
            selectedCity = cities.get(modelRow);
            jTextFieldDetail.setText(selectedCity.getName());
            editing = true;
            jTextFieldDetail.setEditable(true);
            jTextFieldDetail.requestFocus();
            jTextFieldDetail.selectAll();
        }
    }

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
        editing = false;
        jTable1.clearSelection();
        selectedCity = null;
        jTextFieldDetail.setEditable(true);
        jTextFieldDetail.setText("");
        jTextFieldDetail.requestFocus();
    }

    private boolean validateForm() {
        if (jTextFieldDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la ciudad no puede estar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
            jTextFieldDetail.requestFocus();
            return false;
        }
        return true;
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateForm()) {
            return;
        }
        try {
            City city = new City();
            city.setName(jTextFieldDetail.getText().toUpperCase());
            if (editing && selectedCity != null) {
                city.setId(selectedCity.getId());
                cityController.update(city);
            } else {
                cityController.create(city);
            }
            refreshTable();
            applyFilter();
            selectedCity = null;
            jTextFieldDetail.setEditable(false);
            editing = false;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(CityManagement.class.getName()).log(Level.SEVERE, null, ex);
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
            JOptionPane.showMessageDialog(this, "No ha seleccionado ninguna ciudad", "Eliminar", JOptionPane.OK_OPTION);
        } else {
            int option = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar la ciudad seleccionada?", "Atención", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int modelRow = jTable1.convertRowIndexToModel(viewRow);
                int id = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString());
                cityController.delete(id);
                refreshTable();
                applyFilter();
                selectedCity = null;
            }
        }
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

    private javax.swing.ImageIcon loadIcon(String resourcePath) {
        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            Logger.getLogger(CityManagement.class.getName()).log(Level.WARNING,
                    "No se pudo cargar el icono en la ruta {0}", resourcePath);
            return new javax.swing.ImageIcon();
        }
        return new javax.swing.ImageIcon(resource);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldDetail;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
