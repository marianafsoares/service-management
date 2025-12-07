package views.clients;

import controllers.ClientController;
import controllers.ClientInvoiceController;
import controllers.ClientInvoiceDetailController;
import configs.MyBatisConfig;
import mappers.ClientInvoiceMapper;
import mappers.ClientInvoiceDetailMapper;
import mappers.ClientMapper;
import models.ClientInvoice;
import models.ClientInvoiceDetail;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientInvoiceRepository;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.ClientInvoiceDetailRepository;
import repositories.impl.ClientInvoiceDetailRepositoryImpl;
import repositories.ClientRepository;
import repositories.impl.ClientRepositoryImpl;
import services.ClientInvoiceService;
import services.ClientInvoiceDetailService;
import services.ClientService;
import services.afip.AfipPdfException;
import services.afip.AfipPdfService;
import services.reports.ClientInvoiceManualPrintService;
import services.reports.ManualInvoicePrintException;
import views.clients.ClientInvoiceManagementView;
import views.clients.ClientHistoryView;
import views.MainView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import utils.Constants;
import utils.InvoiceTypeUtils;
import utils.TableUtils;

public class ClientInvoiceDetailView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ClientInvoiceController invoiceController;
    private ClientInvoiceDetailController detailController;
    private ClientController clientController;
    private SqlSession sqlSession;
    private AfipPdfService afipPdfService;
    private ClientInvoiceManualPrintService manualPrintService;
    private ClientInvoice invoice;
    private List<ClientInvoiceDetail> invoiceDetails;

    public ClientInvoiceDetailView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientInvoiceMapper invoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
            ClientInvoiceDetailMapper detailMapper = sqlSession.getMapper(ClientInvoiceDetailMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientInvoiceRepository invoiceRepository = new ClientInvoiceRepositoryImpl(invoiceMapper);
            ClientInvoiceDetailRepository detailRepository = new ClientInvoiceDetailRepositoryImpl(detailMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientInvoiceService invoiceService = new ClientInvoiceService(invoiceRepository);
            ClientInvoiceDetailService detailService = new ClientInvoiceDetailService(detailRepository);
            ClientService clientService = new ClientService(clientRepository);
            invoiceController = new ClientInvoiceController(invoiceService);
            detailController = new ClientInvoiceDetailController(detailService);
            clientController = new ClientController(clientService);
            afipPdfService = new AfipPdfService();
            manualPrintService = new ClientInvoiceManualPrintService();

            initComponents();
            isOpen = true;
            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                }

                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                }
            });

            ClientInvoice selectedInvoice = null;
            if (ClientHistoryView.isOpen) {
                int i = ClientHistoryView.jTable1.getSelectedRow();
                selectedInvoice = findInvoiceFromHistorySelection(i);
            } else if (ClientInvoiceManagementView.isOpen) {
                int i = ClientInvoiceManagementView.jTable1.getSelectedRow();
                if (i >= 0 && ClientInvoiceManagementView.invoices != null) {
                    selectedInvoice = ClientInvoiceManagementView.invoices.get(i);
                }
            }

            if (selectedInvoice != null) {
                this.invoice = selectedInvoice;
                initializeClientData();
                jLabelInvoiceType.setText(InvoiceTypeUtils.toDisplayValue(invoice.getInvoiceType()));
                jLabelInvoiceNumber.setText(String.format("%s-%s", invoice.getPointOfSale(), invoice.getInvoiceNumber()));
                jLabelDate.setText(formatDate(invoice.getInvoiceDate()));
                jLabelClientName.setText(buildClientLabel(invoice));

                invoiceDetails = detailController.findByInvoice(invoice.getId());
                if (invoiceDetails == null) {
                    invoiceDetails = new ArrayList<>();
                }
                invoice.setDetails(invoiceDetails);
                jTable1.setModel(createDetailTableModel(invoiceDetails));

                jLabelSubtotal.setText(format(invoice.getSubtotal()));
                jLabelIva21.setText(format(invoice.getVat21()));
                jLabelIva105.setText(format(invoice.getVat105()));
                jLabelTotal.setText(format(invoice.getTotal()));
            }

            setModelTable();
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeClientData() {
        if (invoice == null) {
            return;
        }
        try {
            if (invoice.getClient() != null && invoice.getClient().getId() != null) {
                invoice.setClient(clientController.findById(invoice.getClient().getId()));
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.WARNING,
                    "No se pudo cargar la información del cliente", ex);
        }
    }

    private String buildClientLabel(ClientInvoice invoice) {
        if (invoice == null || invoice.getClient() == null || invoice.getClient().getFullName() == null
                || invoice.getClient().getFullName().trim().isEmpty()) {
            String fallback = ClientHistoryView.isOpen ? ClientHistoryView.jLabelCliente.getText() : "";
            return fallback == null || fallback.trim().isEmpty() ? "" : String.format("Cliente: %s", fallback.trim());
        }
        return String.format("Cliente: %s", invoice.getClient().getFullName());
    }

    private String formatDate(java.time.LocalDateTime date) {
        return date == null ? "" : DATE_FORMATTER.format(date);
    }

    private String format(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.toString() : value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    private void setModelTable() {
        TableUtils.configureClientInvoiceDetailViewTable(jTable1);
    }

    private DefaultTableModel createDetailTableModel(List<ClientInvoiceDetail> details) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"CodigoArticulo", "Descripcion", "Cantidad", "Neto Gravado", "Bonificacion", "Subtotal"}, 0) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        Vector row;
        for (ClientInvoiceDetail d : details) {
            row = new Vector();
            row.add(d.getArticleCode());
            row.add(d.getArticleDescription());
            row.add(d.getQuantity());
            row.add(d.getUnitPrice());
            row.add(d.getDiscountPercent());
            row.add(d.getSubtotal());
            tm.addRow(row);
        }
        return tm;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelSubtotal = new javax.swing.JLabel();
        jLabelIva21 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabelIva105 = new javax.swing.JLabel();
        jButtonPrint = new javax.swing.JButton();
        jLabelInvoiceType = new javax.swing.JLabel();
        jLabelInvoiceNumber = new javax.swing.JLabel();
        jLabelDate = new javax.swing.JLabel();
        jButtonReturn = new javax.swing.JButton();
        jLabelClientName = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1050, 650));
        setPreferredSize(new java.awt.Dimension(1050, 650));
        getContentPane().setLayout(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Cantidad", "Neto Gravado", "Bonificacion", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(40, 90, 940, 280);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Subtotal:");

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Iva 21:");

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Total:");

        jLabelSubtotal.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N

        jLabelIva21.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 204, 51));

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Iva 10.5:");

        jLabelIva105.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .addComponent(jLabelIva21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabelIva105, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelIva21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelIva105, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(710, 390, 280, 170);

        jButtonPrint.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/listado.png"))); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrint);
        jButtonPrint.setBounds(430, 400, 150, 30);

        jLabelInvoiceType.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabelInvoiceType);
        jLabelInvoiceType.setBounds(50, 30, 110, 20);

        jLabelInvoiceNumber.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabelInvoiceNumber);
        jLabelInvoiceNumber.setBounds(170, 30, 150, 20);

        jLabelDate.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabelDate);
        jLabelDate.setBounds(720, 40, 250, 20);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(260, 400, 150, 30);

        jLabelClientName.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabelClientName);
        jLabelClientName.setBounds(50, 60, 260, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        if (invoice == null) {
            JOptionPane.showMessageDialog(this, "No hay una factura seleccionada para imprimir", "Imprimir",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (invoiceDetails != null) {
                invoice.setDetails(invoiceDetails);
            }
            if (isBudget(invoice)) {
                manualPrintService.printBudget(invoice);
                return;
            }

            boolean hasCae = invoice.getCae() != null && !invoice.getCae().trim().isEmpty();
            if (!hasCae) {
                int option = JOptionPane.showConfirmDialog(this,
                        "El comprobante no tiene un CAE registrado. ¿Desea imprimirlo de todas formas?",
                        "Imprimir", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            ClientInvoice associatedInvoice = findAssociatedInvoice(invoice);
            afipPdfService.generateAndPrint(invoice, associatedInvoice);
        } catch (ManualInvoicePrintException ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Imprimir", JOptionPane.WARNING_MESSAGE);
        } catch (AfipPdfException ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "AFIP", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Ocurrió un error al intentar imprimir la factura", "Imprimir",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        isOpen = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private ClientInvoice findInvoiceFromHistorySelection(int row) {
        if (row < 0 || ClientHistoryView.invoices == null) {
            return null;
        }
        try {
            Object numberValue = ClientHistoryView.jTable1.getValueAt(row, 2);
            String numberStr = numberValue != null ? numberValue.toString() : "";
            String[] parts = numberStr.split("-");
            String pointOfSale = normalizeDigits(parts.length > 0 ? parts[0] : "");
            String invoiceNumber = normalizeDigits(parts.length > 1 ? parts[1] : "");

            for (ClientInvoice inv : ClientHistoryView.invoices) {
                if (inv == null) {
                    continue;
                }
                String invPos = normalizeDigits(inv.getPointOfSale());
                String invNumber = normalizeDigits(inv.getInvoiceNumber());
                if (pointOfSale.equals(invPos) && invoiceNumber.equals(invNumber)) {
                    return inv;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.WARNING,
                    "No se pudo obtener la factura seleccionada del historial", ex);
        }
        return null;
    }

    private boolean isBudget(ClientInvoice invoice) {
        if (invoice == null) {
            return false;
        }
        String type = InvoiceTypeUtils.toStorageValue(invoice.getInvoiceType());
        return Constants.PRESUPUESTO_ABBR.equalsIgnoreCase(type);
    }

    private String normalizeDigits(String value) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("[^0-9]", "");
        return digits.replaceFirst("^0+(?!$)", "");
    }

    private ClientInvoice findAssociatedInvoice(ClientInvoice invoice) {
        if (invoice == null) {
            return null;
        }
        String associatedNumber = invoice.getAssociatedInvoiceNumber();
        if (associatedNumber == null || associatedNumber.trim().isEmpty()) {
            return null;
        }
        try {
            String associatedType = InvoiceTypeUtils.findAssociatedInvoiceType(invoice.getInvoiceType());
            return invoiceController.findByPointOfSaleAndNumber(
                    invoice.getPointOfSale(), associatedNumber.trim(), associatedType);
        } catch (Exception ex) {
            Logger.getLogger(ClientInvoiceDetailView.class.getName()).log(Level.WARNING,
                    "No se pudo obtener el comprobante asociado", ex);
            return null;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelClientName;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelInvoiceNumber;
    private javax.swing.JLabel jLabelInvoiceType;
    private javax.swing.JLabel jLabelIva105;
    private javax.swing.JLabel jLabelIva21;
    private javax.swing.JLabel jLabelSubtotal;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
