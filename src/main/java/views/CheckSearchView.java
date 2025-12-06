package views;

import controllers.CheckController;
import models.CheckInfo;
import services.CheckService;
import services.ClientReceiptService;
import services.ProviderReceiptService;
import repositories.ReceiptChequeRepository;
import repositories.ClientReceiptRepository;
import repositories.ProviderReceiptRepository;
import repositories.impl.ReceiptChequeRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import repositories.impl.ProviderReceiptRepositoryImpl;
import mappers.receipts.ReceiptChequeMapper;
import mappers.receipts.ClientReceiptMapper;
import mappers.receipts.ProviderReceiptMapper;
import configs.MyBatisConfig;
import org.apache.ibatis.session.SqlSession;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author Mariana
 */
public class CheckSearchView extends javax.swing.JInternalFrame {

    public static boolean open = false;
    private final CheckController checkController;

    public CheckSearchView() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ReceiptChequeMapper receiptChequeMapper = session.getMapper(ReceiptChequeMapper.class);
        ClientReceiptMapper clientReceiptMapper = session.getMapper(ClientReceiptMapper.class);
        ProviderReceiptMapper providerReceiptMapper = session.getMapper(ProviderReceiptMapper.class);

        ReceiptChequeRepository receiptChequeRepository = new ReceiptChequeRepositoryImpl(receiptChequeMapper);
        ClientReceiptRepository clientReceiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
        ProviderReceiptRepository providerReceiptRepository = new ProviderReceiptRepositoryImpl(providerReceiptMapper);

        ClientReceiptService clientReceiptService = new ClientReceiptService(clientReceiptRepository);
        ProviderReceiptService providerReceiptService = new ProviderReceiptService(providerReceiptRepository);
        CheckService checkService = new CheckService(receiptChequeRepository, clientReceiptService, providerReceiptService);
        checkController = new CheckController(checkService);

        open = true;
        initComponents();

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                open = false;
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                open = false;
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelProveedor = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabelFechaVto = new javax.swing.JLabel();
        jLabelMonto = new javax.swing.JLabel();
        jLabelBanco = new javax.swing.JLabel();
        jLabelCliente = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(450, 300));
        setPreferredSize(new java.awt.Dimension(450, 430));
        getContentPane().setLayout(null);

        jLabelProveedor.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabelProveedor.setForeground(new java.awt.Color(255, 0, 0));
        getContentPane().add(jLabelProveedor);
        jLabelProveedor.setBounds(140, 260, 260, 25);

        jTextField1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(160, 20, 220, 25);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(390, 20, 25, 25);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setText("Número de Cheque");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 20, 140, 25);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setText("Recibido de:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(40, 210, 90, 25);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setText("Entregado a:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 260, 90, 25);

        jButton2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton2.setText("Volver");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(150, 310, 120, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del Cheque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 12))); // NOI18N

        jLabelFechaVto.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        jLabelMonto.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        jLabelBanco.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelFechaVto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFechaVto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(20, 70, 400, 110);

        jLabelCliente.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabelCliente.setForeground(new java.awt.Color(255, 0, 0));
        getContentPane().add(jLabelCliente);
        jLabelCliente.setBounds(140, 210, 260, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void clearForm() {
        jLabelBanco.setText("");
        jLabelCliente.setText("");
        jLabelFechaVto.setText("");
        jLabelMonto.setText("");
        jLabelProveedor.setText("");
    }

    public void searchCheck() {
        try {
            clearForm();
            String checkNumber = jTextField1.getText();
            CheckInfo info = checkController.findByNumber(checkNumber);
            if (info == null) {
                JOptionPane.showMessageDialog(this, "No se encuentran cheques con ese número");
                jTextField1.setText("");
            } else {
                if (info.getBankName() != null) {
                    jLabelBanco.setText(info.getBankName());
                }
                if (info.getDueDate() != null) {
                    jLabelFechaVto.setText(info.getDueDate().toString());
                }
                if (info.getAmount() != null) {
                    jLabelMonto.setText(info.getAmount());
                }
                if (info.getClientName() != null) {
                    jLabelCliente.setText(info.getClientName());
                }
                if (info.getProviderName() != null) {
                    jLabelProveedor.setText(info.getProviderName());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CheckSearchView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        searchCheck();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        open = false;
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            searchCheck();
        }
    }//GEN-LAST:event_jTextField1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBanco;
    private javax.swing.JLabel jLabelCliente;
    private javax.swing.JLabel jLabelFechaVto;
    private javax.swing.JLabel jLabelMonto;
    private javax.swing.JLabel jLabelProveedor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
