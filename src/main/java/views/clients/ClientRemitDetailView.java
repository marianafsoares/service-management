package views.clients;

import controllers.ClientRemitController;
import controllers.ClientRemitDetailController;
import configs.MyBatisConfig;
import mappers.ClientRemitDetailMapper;
import mappers.ClientRemitMapper;
import models.ClientRemit;
import models.ClientRemitDetail;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRemitDetailRepository;
import repositories.ClientRemitRepository;
import repositories.impl.ClientRemitDetailRepositoryImpl;
import repositories.impl.ClientRemitRepositoryImpl;
import services.ClientRemitDetailService;
import services.ClientRemitService;
import services.reports.ClientRemitPrintService;
import services.reports.ClientRemitPrintService.RemitPrintException;
import utils.TableUtils;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRemitDetailView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private ClientRemitController remitController;
    private ClientRemitDetailController detailController;
    private SqlSession sqlSession;
    private final ClientRemitPrintService remitPrintService = new ClientRemitPrintService();
    private ClientRemit remit;
    private List<ClientRemitDetail> remitDetails = Collections.emptyList();

    public ClientRemitDetailView(int remitId) {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientRemitMapper remitMapper = sqlSession.getMapper(ClientRemitMapper.class);
            ClientRemitDetailMapper detailMapper = sqlSession.getMapper(ClientRemitDetailMapper.class);
            ClientRemitRepository remitRepository = new ClientRemitRepositoryImpl(remitMapper);
            ClientRemitDetailRepository detailRepository = new ClientRemitDetailRepositoryImpl(detailMapper);
            ClientRemitService remitService = new ClientRemitService(remitRepository);
            ClientRemitDetailService detailService = new ClientRemitDetailService(detailRepository);
            remitController = new ClientRemitController(remitService);
            detailController = new ClientRemitDetailController(detailService);

            initComponents();
            isOpen = true;

            remit = remitController.findById(remitId);
            if (remit != null && remit.getRemitDate() != null) {
                jLabel9.setText(remit.getRemitDate().toLocalDate().toString());
            }
            remitDetails = detailController.findByRemit(remitId);
            jTable1.setModel(createDetailTableModel(remitDetails));
            setModelTable();
            updateSummary();

            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(ClientRemitDetailView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setModelTable() {
        TableUtils.configureClientRemitDetailViewTable(jTable1);
    }

    private DefaultTableModel createDetailTableModel(List<ClientRemitDetail> details) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Descripcion", "Cantidad", "Precio"}, 0) {
            boolean[] canEdit = new boolean[]{false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        Vector row;
        for (ClientRemitDetail d : details) {
            row = new Vector();
            row.add(d.getProductCode());
            row.add(d.getDescription());
            row.add(d.getQuantity());
            row.add(d.getPrice());
            tm.addRow(row);
        }
        return tm;
    }

    private void updateSummary() {
        BigDecimal subtotal = calculateSubtotal();
        jLabel17.setText(formatAmount(subtotal));
        jLabel18.setText(formatAmount(BigDecimal.ZERO));
        jLabel20.setText(formatAmount(subtotal));
    }

    private BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        List<ClientRemitDetail> details = remitDetails != null ? remitDetails : Collections.emptyList();
        for (ClientRemitDetail detail : details) {
            if (detail == null) {
                continue;
            }
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            BigDecimal quantity = detail.getQuantity() != null
                    ? new BigDecimal(Float.toString(detail.getQuantity())) : BigDecimal.ZERO;
            subtotal = subtotal.add(price.multiply(quantity));
        }
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0 && remit != null && remit.getTotal() != null) {
            return remit.getTotal();
        }
        return subtotal;
    }

    private String formatAmount(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButtonImprimir = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(800, 620));
        setPreferredSize(new java.awt.Dimension(800, 620));
        getContentPane().setLayout(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Cantidad", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 80, 750, 320);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Subtotal:");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(10, 20, 70, 20);

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Iva:");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(10, 50, 70, 20);

        jLabel17.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jPanel2.add(jLabel17);
        jLabel17.setBounds(90, 20, 130, 20);

        jLabel18.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(90, 50, 130, 20);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Total:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(10, 100, 70, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 3, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 153, 51));
        jPanel2.add(jLabel20);
        jLabel20.setBounds(90, 100, 150, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(470, 410, 250, 130);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton1.setText("Volver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(280, 440, 120, 30);

        jLabel9.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabel9);
        jLabel9.setBounds(30, 30, 200, 20);

        jButtonImprimir.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonImprimir.setText("Imprimir");
        jButtonImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImprimirActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonImprimir);
        jButtonImprimir.setBounds(140, 440, 120, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImprimirActionPerformed
        try {
            remitPrintService.print(remit, remitDetails);
        } catch (RemitPrintException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Imprimir", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ClientRemitDetailView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo imprimir el remito", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonImprimirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonImprimir;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    public static javax.swing.JLabel jLabel17;
    public static javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    public static javax.swing.JLabel jLabel20;
    public static javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

