package views;

import models.receipts.ReceiptCard;
import models.receipts.ReceiptCash;
import models.receipts.ReceiptCheque;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptRetention;
import models.receipts.ReceiptTransfer;
import models.receipts.ReceiptType;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiptDetailView extends javax.swing.JInternalFrame {

    private static final Set<ReceiptType> OPEN_TYPES = EnumSet.noneOf(ReceiptType.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Logger LOGGER = Logger.getLogger(ReceiptDetailView.class.getName());

    private final ReceiptDetailData detailData;
    private final PrintAction printAction;

    public ReceiptDetailView(ReceiptDetailData detailData) {
        this(detailData, null);
    }

    public ReceiptDetailView(ReceiptDetailData detailData, PrintAction printAction) {
        this.detailData = detailData;
        this.printAction = printAction;
        initComponents();
        configureTables();
        configureTextAreas();
        configurePrintButton();
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                markClosed();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                markClosed();
            }
        });
        markOpen();
        loadReceipt();
    }

    public static boolean isOpen(ReceiptType type) {
        synchronized (OPEN_TYPES) {
            return OPEN_TYPES.contains(type);
        }
    }

    private void markOpen() {
        if (detailData != null && detailData.getType() != null) {
            synchronized (OPEN_TYPES) {
                OPEN_TYPES.add(detailData.getType());
            }
        }
    }

    private void markClosed() {
        if (detailData != null && detailData.getType() != null) {
            synchronized (OPEN_TYPES) {
                OPEN_TYPES.remove(detailData.getType());
            }
        }
    }

    private void clearTable(DefaultTableModel model) {
        model.setRowCount(0);
    }

    private String formatCode(String pointOfSale, String number) {
        String pos = safeString(pointOfSale);
        String num = safeString(number);
        if (pos.isEmpty() && num.isEmpty()) {
            return "";
        }
        if (pos.isEmpty()) {
            return num;
        }
        if (num.isEmpty()) {
            return pos;
        }
        return pos + "-" + num;
    }

    private String formatAmount(BigDecimal amount) {
        BigDecimal value = amount != null ? amount : BigDecimal.ZERO;
        return value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    private String safeString(String value) {
        return value != null ? value : "";
    }

    private String translateCardType(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().toUpperCase();
        if ("CREDIT".equals(normalized)) {
            return "Crédito";
        }
        if ("DEBIT".equals(normalized)) {
            return "Débito";
        }
        return value;
    }

    private void configureTables() {
        configureTable(jTableCards, jScrollPane2, new int[]{120, 220, 140, 120});
        configureTable(jTableCheques, jScrollPane1, new int[]{140, 220, 220, 180, 140});
        configureTable(jTableTransferencias, jScrollPane4, new int[]{160, 180, 160, 180, 220, 140});
        configureTable(jTableRetenciones, jScrollPaneRetenciones, new int[]{360, 120});
    }

    private void configureTable(JTable table, JScrollPane scrollPane, int[] columnWidths) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private void configureTextAreas() {
        jTextAreaDescription.setEditable(false);
        jTextAreaDescription.setLineWrap(true);
        jTextAreaDescription.setWrapStyleWord(true);
    }

    private void configurePrintButton() {
        if (printAction == null) {
            jButtonPrint.setVisible(false);
        } else {
            jButtonPrint.setVisible(true);
        }
    }

    private void loadReceipt() {
        jLabel7.setText("");
        jLabel8.setText("");
        jLabel9.setText("");
        jLabelTotal.setText(formatAmount(null));
        jLabelCash.setText(formatAmount(null));
        jTextAreaDescription.setText("");

        clearTable((DefaultTableModel) jTableCards.getModel());
        clearTable((DefaultTableModel) jTableCheques.getModel());
        clearTable((DefaultTableModel) jTableTransferencias.getModel());
        clearTable((DefaultTableModel) jTableRetenciones.getModel());

        if (detailData == null) {
            return;
        }

        ReceiptType type = detailData.getType();
        if (type != null) {
            jLabel7.setText(type == ReceiptType.CLIENT ? "RC" : "RP");
        }
        jLabel8.setText(formatCode(detailData.getPointOfSale(), detailData.getReceiptNumber()));
        if (detailData.getReceiptDate() != null) {
            jLabel9.setText(detailData.getReceiptDate().toLocalDate().format(DATE_FORMATTER));
        }
       
        jLabelTotal.setText(formatAmount(detailData.getTotal()));

        ReceiptCash cash = detailData.getCashPayment();
        if (cash != null) {
            jLabelCash.setText(formatAmount(cash.getAmount()));
        }

        DefaultTableModel cardModel = (DefaultTableModel) jTableCards.getModel();
        for (ReceiptCard card : detailData.getCardPayments()) {
            cardModel.addRow(new Object[]{
                    translateCardType(card.getCardType()),
                    safeString(card.getCardName()),
                    safeString(card.getLastFourDigits()),
                    formatAmount(card.getAmount())
            });
        }

        DefaultTableModel chequeModel = (DefaultTableModel) jTableCheques.getModel();
        for (ReceiptCheque cheque : detailData.getChequePayments()) {
            chequeModel.addRow(new Object[]{
                    safeString(cheque.getCheckNumber()),
                    safeString(cheque.getHolderName()),
                    safeString(cheque.getBankName()),
                    cheque.getDueDate() != null ? cheque.getDueDate().format(DATE_FORMATTER) : "",
                    formatAmount(cheque.getAmount())
            });
        }

        DefaultTableModel transferModel = (DefaultTableModel) jTableTransferencias.getModel();
        for (ReceiptTransfer transfer : detailData.getTransferPayments()) {
            transferModel.addRow(new Object[]{
                    safeString(transfer.getOriginBankName()),
                    safeString(transfer.getOriginAccount()),
                    safeString(transfer.getDestinationBankName()),
                    safeString(transfer.getDestinationAccount()),
                    safeString(transfer.getReference()),
                    formatAmount(transfer.getAmount())
            });
        }

        DefaultTableModel retentionModel = (DefaultTableModel) jTableRetenciones.getModel();
        BigDecimal retentionTotal = BigDecimal.ZERO;
        for (ReceiptRetention retention : detailData.getRetentionPayments()) {
            retentionModel.addRow(new Object[]{
                    safeString(retention.getDescription()),
                    formatAmount(retention.getAmount())
            });
            if (retention.getAmount() != null) {
                retentionTotal = retentionTotal.add(retention.getAmount());
            }
        }

        jTextAreaDescription.setText(safeString(detailData.getNotes()));
        jTextAreaDescription.setCaretPosition(0);

    }

     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelCash = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableCards = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCheques = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPaneRetenciones = new javax.swing.JScrollPane();
        jTableRetenciones = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableTransferencias = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();
        jButtonReturn = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(780, 550));
        setPreferredSize(new java.awt.Dimension(780, 550));
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Total");

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(80, 420, 200, 50);

        jLabel7.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 20, 70, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabel8);
        jLabel8.setBounds(90, 20, 150, 20);

        jLabel9.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabel9);
        jLabel9.setBounds(470, 20, 150, 20);

        jPanel3.setLayout(null);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Efectivo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel5.setLayout(null);

        jLabelCash.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jPanel5.add(jLabelCash);
        jLabelCash.setBounds(30, 100, 210, 25);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setText("Monto:");
        jPanel5.add(jLabel11);
        jLabel11.setBounds(30, 70, 90, 25);

        jPanel3.add(jPanel5);
        jPanel5.setBounds(20, 20, 260, 180);

        jTabbedPane1.addTab("Efectivo", jPanel3);

        jPanel4.setLayout(null);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tarjeta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel7.setLayout(null);

        jTableCards.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tipo", "Tarjeta", "Numero", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableCards);

        jPanel7.add(jScrollPane2);
        jScrollPane2.setBounds(10, 50, 590, 120);

        jPanel4.add(jPanel7);
        jPanel7.setBounds(20, 20, 660, 220);

        jTabbedPane1.addTab("Tarjeta", jPanel4);

        jPanel9.setLayout(null);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cheque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel6.setLayout(null);

        jTableCheques.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Titular", "Banco", "Vencimiento", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableCheques);

        jPanel6.add(jScrollPane1);
        jScrollPane1.setBounds(10, 50, 640, 110);

        jPanel9.add(jPanel6);
        jPanel6.setBounds(10, 10, 660, 200);

        jTabbedPane1.addTab("Cheque", jPanel9);

        jPanel10.setLayout(null);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Retenciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel8.setLayout(null);

        jTableRetenciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripción", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
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
        jScrollPaneRetenciones.setViewportView(jTableRetenciones);

        jPanel8.add(jScrollPaneRetenciones);
        jScrollPaneRetenciones.setBounds(30, 50, 480, 170);

        jPanel10.add(jPanel8);
        jPanel8.setBounds(30, 20, 560, 260);

        jTabbedPane1.addTab("Retenciones", jPanel10);

        jPanel11.setLayout(null);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transferencia", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 24))); // NOI18N
        jPanel12.setLayout(null);

        jTableTransferencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bco Origen", "Cta Origen", "Bco Destino", "Cta Destino", "Referencia", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableTransferencias);

        jPanel12.add(jScrollPane4);
        jScrollPane4.setBounds(20, 40, 630, 110);

        jPanel11.add(jPanel12);
        jPanel12.setBounds(10, 10, 660, 200);

        jTabbedPane1.addTab("Transferencias", jPanel11);

        jPanel13.setLayout(null);

        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setRows(5);
        jScrollPane3.setViewportView(jTextAreaDescription);

        jPanel13.add(jScrollPane3);
        jScrollPane3.setBounds(70, 40, 540, 150);

        jTabbedPane1.addTab("Anotaciones", jPanel13);

        getContentPane().add(jTabbedPane1);
        jTabbedPane1.setBounds(30, 60, 690, 340);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(520, 430, 110, 30);

        jButtonPrint.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrint);
        jButtonPrint.setBounds(390, 430, 120, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        if (printAction == null) {
            return;
        }
        try {
            printAction.print(detailData);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "No se pudo imprimir el recibo", ex);
            String message = ex.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "No se pudo imprimir el recibo";
            }
            JOptionPane.showMessageDialog(this, message, "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCash;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneRetenciones;
    private javax.swing.JTabbedPane jTabbedPane1;
    public static javax.swing.JTable jTableCards;
    public static javax.swing.JTable jTableCheques;
    javax.swing.JTable jTableRetenciones;
    public static javax.swing.JTable jTableTransferencias;
    private javax.swing.JTextArea jTextAreaDescription;
    // End of variables declaration//GEN-END:variables

    @FunctionalInterface
    public interface PrintAction {
        void print(ReceiptDetailData detailData) throws Exception;
    }
}
