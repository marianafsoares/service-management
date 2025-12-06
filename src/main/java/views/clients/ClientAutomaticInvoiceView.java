package views.clients;

import configs.MyBatisConfig;
import controllers.ClientController;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import mappers.ClientMapper;
import models.Client;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.impl.ClientRepositoryImpl;
import services.ClientService;

public class ClientAutomaticInvoiceView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private final ClientController clientController;
    private final DecimalFormat currencyFormat;

    public ClientAutomaticInvoiceView() throws SQLException, Exception {
        SqlSession sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientService clientService = new ClientService(clientRepository);
        clientController = new ClientController(clientService);
        currencyFormat = createCurrencyFormat();

        isOpen = true;
        initComponents();
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
    }

    private DecimalFormat createCurrencyFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        return new DecimalFormat("0.00", symbols);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDetail = new javax.swing.JTextField();
        jFormattedTextFieldAmount = new javax.swing.JFormattedTextField();
        jButtonSend = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaSummary = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Facturación automática");
        setMinimumSize(new java.awt.Dimension(500, 380));
        setPreferredSize(new java.awt.Dimension(520, 420));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Detalle de la factura");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 30, 170, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setText("Importe por defecto");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 70, 170, 20);

        jTextFieldDetail.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jTextFieldDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldDetailKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldDetail);
        jTextFieldDetail.setBounds(210, 25, 260, 30);

        jFormattedTextFieldAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextFieldAmount.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jFormattedTextFieldAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldAmountKeyPressed(evt);
            }
        });
        getContentPane().add(jFormattedTextFieldAmount);
        jFormattedTextFieldAmount.setBounds(210, 65, 120, 30);

        jButtonSend.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/facturacion.png"))); // NOI18N
        jButtonSend.setText("Enviar facturación");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSend);
        jButtonSend.setBounds(30, 120, 200, 35);

        jButtonClose.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonClose.setText("Cerrar");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonClose);
        jButtonClose.setBounds(250, 120, 150, 35);

        jTextAreaSummary.setEditable(false);
        jTextAreaSummary.setColumns(20);
        jTextAreaSummary.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jTextAreaSummary.setRows(5);
        jTextAreaSummary.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultado del envío"));
        jScrollPane1.setViewportView(jTextAreaSummary);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 180, 440, 160);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jLabel3.setText("Si el cliente tiene un monto personalizado, se usará sobre el valor por defecto.");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 95, 430, 16);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        enviarFacturacion();
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jTextFieldDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDetailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jFormattedTextFieldAmount.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldDetailKeyPressed

    private void jFormattedTextFieldAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldAmountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            enviarFacturacion();
        }
    }//GEN-LAST:event_jFormattedTextFieldAmountKeyPressed

    private void enviarFacturacion() {
        String detail = jTextFieldDetail.getText() == null ? "" : jTextFieldDetail.getText().trim();
        if (detail.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingresá un detalle para la factura.", "Bits&Bytes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal defaultAmount = parseAmount();
        if (defaultAmount == null || defaultAmount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Ingresá un importe válido.", "Bits&Bytes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Client> clients = clientController.findActiveClients();
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay clientes habilitados para facturar.", "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder summary = new StringBuilder();
        int sentCount = 0;
        int missingContact = 0;
        for (Client client : clients) {
            BigDecimal amount = resolveAmount(client, defaultAmount);
            String target = resolveContact(client);
            if (target == null) {
                summary.append("No se pudo enviar a ")
                        .append(client.getFullName())
                        .append(" (sin teléfono/email)\n");
                missingContact++;
                continue;
            }

            summary.append("Enviado a ")
                    .append(client.getFullName())
                    .append(" vía ")
                    .append(target)
                    .append(" - ")
                    .append(detail)
                    .append(" ($")
                    .append(currencyFormat.format(amount))
                    .append(")\n");
            sentCount++;
        }

        jTextAreaSummary.setText(summary.toString());

        String message = "Se procesó la facturación automática.";
        message += "\nEnvíos realizados: " + sentCount;
        if (missingContact > 0) {
            message += "\nClientes sin datos de contacto: " + missingContact;
        }
        JOptionPane.showMessageDialog(this, message, "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
    }

    private BigDecimal parseAmount() {
        try {
            Object value = jFormattedTextFieldAmount.getValue();
            if (value == null) {
                return null;
            }
            if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            }
            String text = value.toString().replace(',', '.');
            return new BigDecimal(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal resolveAmount(Client client, BigDecimal defaultAmount) {
        if (client.getSubscriptionAmount() != null && client.getSubscriptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            return client.getSubscriptionAmount();
        }
        return defaultAmount;
    }

    private String resolveContact(Client client) {
        if (client.getMobile() != null && !client.getMobile().isBlank()) {
            return "WhatsApp " + client.getMobile();
        }
        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            return "Mail " + client.getEmail();
        }
        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JFormattedTextField jFormattedTextFieldAmount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaSummary;
    private javax.swing.JTextField jTextFieldDetail;
    // End of variables declaration//GEN-END:variables
}
