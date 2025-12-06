package views.utils;

import controllers.BankController;
import configs.MyBatisConfig;
import mappers.BankMapper;
import models.Bank;
import org.apache.ibatis.session.SqlSession;
import repositories.BankRepository;
import repositories.impl.BankRepositoryImpl;
import services.BankService;

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

public class BankManagement extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private BankController bankController;
    private List<Bank> banks = null;
    private SqlSession sqlSession;
    private boolean editing = false;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private Bank selectedBank;

    public BankManagement() throws Exception {
        sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        BankMapper bankMapper = sqlSession.getMapper(BankMapper.class);
        BankRepository bankRepository = new BankRepositoryImpl(bankMapper);
        BankService bankService = new BankService(bankRepository);
        bankController = new BankController(bankService);

        initComponents();
        isOpen = true;
        refreshTable();
    }

    public void refreshTable() {
        banks = bankController.findAll();
        DefaultTableModel model = createBankTableModel(banks);
        jTable1.setModel(model);
        rowSorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(rowSorter);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applyFilter();
        selectedBank = null;
        editing = false;
        jTable1.clearSelection();
        jTextFieldDetail.setText("");
        jTextFieldDetail.setEditable(false);
    }

    private DefaultTableModel createBankTableModel(List<Bank> bankList) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Nombre"}, 0) {
            boolean[] canEdit = new boolean[]{false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        for (Bank b : bankList) {
            Vector<Object> row = new Vector<>();
            row.add(b.getId());
            row.add(b.getName());
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

        setPreferredSize(new java.awt.Dimension(735, 405));
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
        jTextFieldSearch.setBounds(100, 50, 230, 21);

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
        jScrollPane1.setBounds(30, 90, 320, 230);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(51, 51, 51));
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(390, 30, 30, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Banco", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 16))); // NOI18N
        jPanel1.setEnabled(false);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Detalle");

        jTextFieldDetail.setEditable(false);
        jTextFieldDetail.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jTextFieldDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(380, 90, 320, 70);

        jButtonSave.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButtonSave.setText("Guardar");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSave);
        jButtonSave.setBounds(410, 180, 120, 30);

        jButtonCancel.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButtonCancel.setText("Cancelar");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonCancel);
        jButtonCancel.setBounds(540, 180, 120, 30);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(430, 30, 30, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {
        applyFilter();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = jTable1.convertRowIndexToModel(viewRow);
            selectedBank = banks.get(modelRow);
            jTextFieldDetail.setText(selectedBank.getName());
            editing = true;
            jTextFieldDetail.setEditable(true);
            jTextFieldDetail.requestFocus();
            jTextFieldDetail.selectAll();
        }
    }

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
        editing = false;
        selectedBank = null;
        jTable1.clearSelection();
        jTextFieldDetail.setEditable(true);
        jTextFieldDetail.setText("");
        jTextFieldDetail.requestFocus();
    }

    private void jButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {
        int row = jTable1.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningún banco", "Modificar", JOptionPane.OK_OPTION);
        } else {
            editing = true;
            jTextFieldDetail.setEditable(true);
            jTextFieldDetail.requestFocus();
        }
    }

    private boolean validateForm() {
        if (jTextFieldDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del banco no puede estar vacío!", "Atención", JOptionPane.WARNING_MESSAGE);
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
            Bank bank = new Bank();
            bank.setName(jTextFieldDetail.getText().toUpperCase());
            bank.setEnabled(true);
            if (editing && selectedBank != null) {
                bank.setId(selectedBank.getId());
                bankController.update(bank);
            } else {
                bankController.create(bank);
            }
            refreshTable();
            selectedBank = null;
            editing = false;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atención", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(BankManagement.class.getName()).log(Level.SEVERE, null, ex);
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
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningún banco", "Eliminar", JOptionPane.OK_OPTION);
        } else {
            int option = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar el banco seleccionado?", "Atención", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                int modelRow = jTable1.convertRowIndexToModel(viewRow);
                int id = Integer.parseInt(jTable1.getModel().getValueAt(modelRow, 0).toString());
                bankController.delete(id);
                refreshTable();
                selectedBank = null;
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
