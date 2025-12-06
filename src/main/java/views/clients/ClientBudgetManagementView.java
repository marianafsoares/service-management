package views.clients;

import views.MainView;
import controllers.ClientBudgetController;
import controllers.ClientBudgetDetailController;
import controllers.ClientController;
import configs.MyBatisConfig;
import mappers.ClientBudgetDetailMapper;
import mappers.ClientBudgetMapper;
import mappers.ClientMapper;
import models.Client;
import models.ClientBudget;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientBudgetDetailRepository;
import repositories.ClientBudgetRepository;
import repositories.ClientRepository;
import repositories.impl.ClientBudgetDetailRepositoryImpl;
import repositories.impl.ClientBudgetRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import services.ClientBudgetService;
import services.ClientBudgetDetailService;
import services.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientBudgetManagementView extends javax.swing.JInternalFrame {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static ClientBudgetController budgetController;
    private static ClientController clientController;
    private ClientBudgetDetailController detailController;
    private SqlSession sqlSession;
    public static boolean isOpen = false;
    private static ClientBudgetManagementView activeInstance;
    public static List<ClientBudget> budgets = null;
    private enum BudgetFilter { OPEN, CLOSED }
    private static BudgetFilter currentFilter = BudgetFilter.OPEN;

    public ClientBudgetManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientBudgetMapper budgetMapper = sqlSession.getMapper(ClientBudgetMapper.class);
            ClientBudgetDetailMapper budgetDetailMapper = sqlSession.getMapper(ClientBudgetDetailMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientBudgetRepository budgetRepository = new ClientBudgetRepositoryImpl(budgetMapper);
            ClientBudgetDetailRepository budgetDetailRepository = new ClientBudgetDetailRepositoryImpl(budgetDetailMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientBudgetService budgetService = new ClientBudgetService(budgetRepository);
            ClientBudgetDetailService budgetDetailService = new ClientBudgetDetailService(budgetDetailRepository);
            ClientService clientService = new ClientService(clientRepository);
            budgetController = new ClientBudgetController(budgetService);
            clientController = new ClientController(clientService);
            detailController = new ClientBudgetDetailController(budgetDetailService);

            initComponents();
            budgetFilterButtonGroup.add(jRadioOpen);
            budgetFilterButtonGroup.add(jRadioClosed);
            isOpen = true;
            activeInstance = this;

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
            currentFilter = BudgetFilter.OPEN;
            reloadBudgets();
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void refreshTable() {
        ClientBudgetManagementView instance = activeInstance;
        if (instance == null) {
            return;
        }
        SwingUtilities.invokeLater(instance::reloadBudgets);
    }

    private void reloadBudgets() {
        loadBudgets(currentFilter);
    }

    private void loadBudgets(BudgetFilter filter) {
        if (budgetController == null) {
            budgets = Collections.emptyList();
            applyBudgetsToTable();
            return;
        }
        try {
            clearSessionCache();
            List<ClientBudget> loaded;
            if (filter == BudgetFilter.CLOSED) {
                loaded = budgetController.findClosed();
            } else {
                loaded = budgetController.findOpen();
            }
            budgets = loaded != null ? loaded : Collections.emptyList();
            currentFilter = filter;
            applyBudgetsToTable();
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearSessionCache() {
        if (sqlSession != null) {
            sqlSession.clearCache();
        }
    }

    private static void applyBudgetsToTable() {
        List<ClientBudget> safeBudgets = budgets != null ? budgets : Collections.emptyList();
        jTable1.setModel(createBudgetTableModel(safeBudgets));
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.revalidate();
        jTable1.repaint();
    }

    private static DefaultTableModel createBudgetTableModel(List<ClientBudget> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Fecha", "Cliente", "Total", "Cerrada"}, 0) {
            boolean[] canEdit = new boolean[]{false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : Object.class;
            }
        };

        if (list == null) {
            return tm;
        }

        for (ClientBudget b : list) {
            Vector<Object> row = new Vector<>();
            row.add(b.getId());
            row.add(formatBudgetDate(b.getBudgetDate()));
            String clientName = "";
            if (b.getClient() != null && b.getClient().getId() != null) {
                Client c = clientController.findById(b.getClient().getId());
                if (c != null) clientName = c.getFullName();
            }
            row.add(clientName);
            row.add(b.getTotal());
            row.add(b.getClosed());
            tm.addRow(row);
        }
        return tm;
    }

    private static String formatBudgetDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        budgetFilterButtonGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonEdit = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonDetail = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jRadioOpen = new javax.swing.JRadioButton();
        jRadioClosed = new javax.swing.JRadioButton();

        setMinimumSize(new java.awt.Dimension(750, 550));
        setPreferredSize(new java.awt.Dimension(750, 550));
        getContentPane().setLayout(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Fecha", "Cliente", "Total", "Cerrada"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
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
        jScrollPane1.setBounds(30, 130, 670, 260);

        jButtonEdit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modificar.png"))); // NOI18N
        jButtonEdit.setText("Modificar");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonEdit);
        jButtonEdit.setBounds(180, 30, 130, 30);

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

        jButtonDetail.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDetail.setForeground(new java.awt.Color(51, 51, 51));
        jButtonDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonDetail.setText("Ver");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDetail);
        jButtonDetail.setBounds(460, 30, 130, 30);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setForeground(new java.awt.Color(51, 51, 51));
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(260, 420, 130, 30);

        jRadioOpen.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jRadioOpen.setSelected(true);
        jRadioOpen.setText("Abiertas");
        jRadioOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioOpenActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioOpen);
        jRadioOpen.setBounds(470, 90, 80, 25);

        jRadioClosed.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jRadioClosed.setText("Cerradas");
        jRadioClosed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioClosedActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioClosed);
        jRadioClosed.setBounds(560, 90, 80, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        int i = jTable1.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ninguna fila", "Modificar", JOptionPane.OK_OPTION);
        } else if (!ClientBudgetUpsertView.open) {
            if (budgets != null && i < budgets.size()) {
                ClientBudget selected = budgets.get(i);
                if (Boolean.TRUE.equals(selected.getClosed())) {
                    JOptionPane.showMessageDialog(this,
                            "El presupuesto está cerrado y no puede modificarse.",
                            "Modificar", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            Integer id = (Integer) jTable1.getValueAt(i, 0);
            ClientBudgetUpsertView view = new ClientBudgetUpsertView(id);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        if (!ClientBudgetUpsertView.open) {
            ClientBudgetUpsertView view = new ClientBudgetUpsertView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        int i = jTable1.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ninguna fila", "Eliminar", JOptionPane.OK_OPTION);
            return;
        }
        if (budgets != null && i < budgets.size()) {
            ClientBudget selected = budgets.get(i);
            if (Boolean.TRUE.equals(selected.getClosed())) {
                JOptionPane.showMessageDialog(this,
                        "El presupuesto está cerrado y no puede eliminarse.",
                        "Eliminar", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar el presupuesto?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) jTable1.getValueAt(i, 0);
            if (detailController != null) {
                detailController.deleteByBudget(id);
            }
            budgetController.delete(id);
            refreshTable();
        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailActionPerformed
        int i = jTable1.getSelectedRow();
        if (i == -1) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ninguna fila", "Ver", JOptionPane.OK_OPTION);
        } else if (!ClientBudgetDetailView.open) {
            Integer id = (Integer) jTable1.getValueAt(i, 0);
            ClientBudgetDetailView view = new ClientBudgetDetailView(id);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }//GEN-LAST:event_jButtonDetailActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if (row != -1 && jTable1.getSelectedColumn() == 4 && budgets != null && row < budgets.size()) {
            ClientBudget b = budgets.get(row);
            Boolean previous = b.getClosed() != null ? b.getClosed() : Boolean.FALSE;
            Boolean newValue = (Boolean) jTable1.getValueAt(row, 4);
            if (Boolean.TRUE.equals(previous)) {
                jTable1.setValueAt(Boolean.TRUE, row, 4);
                JOptionPane.showMessageDialog(this,
                        "El presupuesto está cerrado y no puede modificarse.",
                        "Presupuesto", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (newValue == null || Boolean.FALSE.equals(newValue)) {
                jTable1.setValueAt(Boolean.FALSE, row, 4);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "El presupuesto se cerrará. ¿Desea continuar?", "Presupuesto",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                jTable1.setValueAt(Boolean.FALSE, row, 4);
                return;
            }
            b.setClosed(Boolean.TRUE);
            budgetController.update(b);
            refreshTable();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jRadioOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioOpenActionPerformed
        if (jRadioOpen.isSelected()) {
            loadBudgets(BudgetFilter.OPEN);
        }
    }//GEN-LAST:event_jRadioOpenActionPerformed

    private void jRadioClosedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioClosedActionPerformed
        if (jRadioClosed.isSelected()) {
            loadBudgets(BudgetFilter.CLOSED);
        }
    }//GEN-LAST:event_jRadioClosedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup budgetFilterButtonGroup;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JRadioButton jRadioClosed;
    private javax.swing.JRadioButton jRadioOpen;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

