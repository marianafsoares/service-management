package views.clients;

import controllers.ClientController;
import controllers.ClientReceiptController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientInvoiceMapper;
import mappers.receipts.ClientReceiptMapper;
import models.Client;
import models.receipts.ClientReceipt;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.ClientInvoiceRepository;
import repositories.ClientReceiptRepository;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import services.ClientService;
import services.ClientReceiptService;
import views.MainView;
import views.ReceiptDetailView;
import views.utils.ReceiptDetailLoader;
import models.receipts.ReceiptDetailData;
import models.receipts.ReceiptType;
import services.reports.ClientReceiptPrintService;
import services.reports.ClientReceiptPrintService.ReceiptPrintException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.awt.Cursor;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientReceiptManagementView extends javax.swing.JInternalFrame {

    private ClientReceiptController receiptController;
    private ClientController clientController;
    private SqlSession sqlSession;
    private final ClientReceiptPrintService receiptPrintService = new ClientReceiptPrintService();
    private final ZoneId zoneId = ZoneId.systemDefault();
    public static boolean isOpen = false;
    public static List<ClientReceipt> receipts = new ArrayList<>();
    private List<ClientReceipt> allReceipts = new ArrayList<>();
    private final AtomicInteger backgroundTaskCount = new AtomicInteger(0);
    private boolean receiptsLoading = false;
    private boolean detailLoading = false;

    public ClientReceiptManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientReceiptMapper receiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
            ClientInvoiceMapper invoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
            ClientReceiptRepository receiptRepository = new ClientReceiptRepositoryImpl(receiptMapper);
            ClientInvoiceRepository clientInvoiceRepository = new ClientInvoiceRepositoryImpl(invoiceMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientReceiptService receiptService = new ClientReceiptService(receiptRepository);
            ClientService clientService = new ClientService(clientRepository, clientInvoiceRepository, receiptRepository);
            receiptController = new ClientReceiptController(receiptService);
            clientController = new ClientController(clientService);
            initComponents();
            configureDateFilters();
            jButtonSearch.addActionListener(evt -> onSearch());
            updateButtonStates();
            isOpen = true;

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
            loadReceipts();
        } catch (Exception ex) {
            Logger.getLogger(ClientReceiptManagementView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTable(List<ClientReceipt> list) {
        jTable1.setModel(createTableModel(list));
    }

    private DefaultTableModel createTableModel(List<ClientReceipt> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Fecha", "Numero", "Codigo Cliente", "Nombre Cliente", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        Vector row;
        for (ClientReceipt r : list) {
            row = new Vector();
            row.add(r.getReceiptDate());
            row.add(String.format("%s-%s", r.getPointOfSale(), r.getReceiptNumber()));
            Integer clientId = r.getClient() != null ? r.getClient().getId() : null;
            row.add(clientId);
            String name = "";
            if (clientId != null) {
                Client c = clientController.findById(clientId);
                if (c != null) name = c.getFullName();
            }
            row.add(name);
            row.add(r.getTotal());
            tm.addRow(row);
        }
        return tm;
    }

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {
        applyFilter();
    }

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {
        isOpen = false;
        dispose();
    }

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {
        int i = jTable1.getSelectedRow();
        if (i == -1 || receipts == null || i >= receipts.size()) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun recibo", "Ver", JOptionPane.OK_OPTION);
            return;
        }
        if (ReceiptDetailView.isOpen(ReceiptType.CLIENT) || detailLoading) {
            return;
        }
        ClientReceipt receipt = receipts.get(i);
        detailLoading = true;
        updateButtonStates();
        startBackgroundTask();
        new SwingWorker<DetailLoadResult, Void>() {
            @Override
            protected DetailLoadResult doInBackground() {
                ReceiptDetailData detailData = ReceiptDetailLoader.loadClientReceipt(sqlSession, receipt, clientController);
                Client client = null;
                if (receipt.getClient() != null && receipt.getClient().getId() != null) {
                    client = clientController.findById(receipt.getClient().getId());
                }
                return new DetailLoadResult(receipt, client, detailData);
            }

            @Override
            protected void done() {
                try {
                    DetailLoadResult result = get();
                    if (result != null && !ReceiptDetailView.isOpen(ReceiptType.CLIENT)) {
                        final ClientReceipt selectedReceipt = result.receipt();
                        final Client selectedClient = result.client();
                        ReceiptDetailView.PrintAction printAction = data -> {
                            try {
                                Client resolvedClient = selectedClient;
                                if (resolvedClient == null && selectedReceipt.getClient() != null && selectedReceipt.getClient().getId() != null) {
                                    resolvedClient = clientController.findById(selectedReceipt.getClient().getId());
                                }
                                receiptPrintService.print(selectedReceipt, resolvedClient, data);
                            } catch (ReceiptPrintException ex) {
                                throw ex;
                            } catch (Exception ex) {
                                throw new ReceiptPrintException("No se pudo generar el recibo", ex);
                            }
                        };
                        ReceiptDetailView view = new ReceiptDetailView(result.detailData(), printAction);
                        MainView.jDesktopPane1.add(view);
                        view.setVisible(true);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ClientReceiptManagementView.class.getName()).log(Level.SEVERE,
                            "No se pudo cargar el detalle del recibo", ex);
                    JOptionPane.showMessageDialog(ClientReceiptManagementView.this,
                            "No se pudo cargar el detalle del recibo",
                            "Ver", JOptionPane.ERROR_MESSAGE);
                } finally {
                    detailLoading = false;
                    updateButtonStates();
                    finishBackgroundTask();
                }
            }
        }.execute();
    }

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int i = jTable1.getSelectedRow();
        if (i == -1 || receipts == null || i >= receipts.size()) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun recibo", "Eliminar", JOptionPane.OK_OPTION);
            return;
        }
        ClientReceipt receipt = receipts.get(i);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Esta seguro que desea eliminar el recibo seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            receiptController.delete(receipt.getId());
            loadReceipts();
        }
    }

    private void loadReceipts() {
        receiptsLoading = true;
        updateButtonStates();
        startBackgroundTask();
        new SwingWorker<List<ClientReceipt>, Void>() {
            @Override
            protected List<ClientReceipt> doInBackground() {
                return receiptController.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<ClientReceipt> found = get();
                    allReceipts = found != null ? new ArrayList<>(found) : new ArrayList<>();
                } catch (Exception ex) {
                    Logger.getLogger(ClientReceiptManagementView.class.getName()).log(Level.SEVERE,
                            "No se pudieron cargar los recibos", ex);
                    allReceipts = new ArrayList<>();
                }
                applyFilter();
                receiptsLoading = false;
                updateButtonStates();
                finishBackgroundTask();
            }
        }.execute();
    }

    private void applyFilter() {
        List<ClientReceipt> source = allReceipts != null ? new ArrayList<>(allReceipts) : new ArrayList<>();
        List<ClientReceipt> filteredByDate = filterBySelectedDates(source);

        String text = jTextField1.getText() != null ? jTextField1.getText().toLowerCase() : "";
        List<ClientReceipt> filtered;
        if (text.trim().isEmpty()) {
            filtered = filteredByDate;
        } else {
            filtered = filteredByDate.stream().filter(r -> {
                String number = String.format("%s-%s", r.getPointOfSale(), r.getReceiptNumber()).toLowerCase();
                String name = "";
                if (r.getClient() != null) {
                    Client c = clientController.findById(r.getClient().getId());
                    if (c != null) {
                        name = c.getFullName().toLowerCase();
                    }
                }
                return number.contains(text) || name.contains(text);
            }).collect(Collectors.toList());
        }

        receipts = filtered;
        updateTable(receipts);
    }

    private List<ClientReceipt> filterBySelectedDates(List<ClientReceipt> source) {
        LocalDate from = getSelectedFromDate();
        LocalDate to = getSelectedToDate();
        if (from == null || to == null || to.isBefore(from)) {
            return source;
        }
        return source.stream()
                .filter(receipt -> {
                    LocalDateTime receiptDate = receipt.getReceiptDate();
                    if (receiptDate == null) {
                        return false;
                    }
                    LocalDate date = receiptDate.toLocalDate();
                    return (!date.isBefore(from)) && (!date.isAfter(to));
                })
                .collect(Collectors.toList());
    }

    private void configureDateFilters() {
        jDateChooserFrom.setDate(null);
        jDateChooserTo.setDate(null);
    }

    private void onSearch() {
        if (!validateSelectedDates(true)) {
            return;
        }
        applyFilter();
    }

    private boolean validateSelectedDates(boolean showMessage) {
        LocalDate from = getSelectedFromDate();
        LocalDate to = getSelectedToDate();
        if (from == null || to == null) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar ambas fechas para buscar",
                        "Buscar", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
        if (to.isBefore(from)) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        "La fecha 'Hasta' debe ser posterior o igual a la fecha 'Desde'",
                        "Buscar", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
        return true;
    }

    private Date toDate(LocalDate date) {
        return date != null ? Date.from(date.atStartOfDay(zoneId).toInstant()) : null;
    }

    private LocalDate getSelectedFromDate() {
        Date date = jDateChooserFrom.getDate();
        return date != null ? Instant.ofEpochMilli(date.getTime()).atZone(zoneId).toLocalDate() : null;
    }

    private LocalDate getSelectedToDate() {
        Date date = jDateChooserTo.getDate();
        return date != null ? Instant.ofEpochMilli(date.getTime()).atZone(zoneId).toLocalDate() : null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonReturn = new javax.swing.JButton();
        jButtonDetail = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonSearch = new javax.swing.JButton();
        jDateChooserFrom = new com.toedter.calendar.JDateChooser();
        jDateChooserTo = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(850, 610));
        setPreferredSize(new java.awt.Dimension(850, 610));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel1.setText("Buscar");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 140, 44, 20);

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(80, 140, 470, 22);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Fecha", "Numero ", "Codigo Cliente", "Nombre Cliente", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 180, 730, 330);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(630, 110, 110, 30);

        jButtonDetail.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonDetail.setText("Ver");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDetail);
        jButtonDetail.setBounds(630, 30, 110, 30);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.setText("Eliminar");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(630, 70, 110, 30);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buscar Comprobantes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 16))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jPanel1.setLayout(null);

        jButtonSearch.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jPanel1.add(jButtonSearch);
        jButtonSearch.setBounds(480, 40, 25, 25);
        jPanel1.add(jDateChooserFrom);
        jDateChooserFrom.setBounds(100, 40, 150, 22);
        jPanel1.add(jDateChooserTo);
        jDateChooserTo.setBounds(320, 40, 150, 22);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Desde");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 40, 70, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Hasta");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(250, 40, 60, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(20, 20, 590, 100);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSearch;
    private com.toedter.calendar.JDateChooser jDateChooserFrom;
    private com.toedter.calendar.JDateChooser jDateChooserTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    private void startBackgroundTask() {
        if (backgroundTaskCount.getAndIncrement() == 0) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    private void finishBackgroundTask() {
        int remaining = backgroundTaskCount.decrementAndGet();
        if (remaining <= 0) {
            backgroundTaskCount.set(0);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateButtonStates() {
        boolean busy = receiptsLoading || detailLoading;
        jButtonSearch.setEnabled(!receiptsLoading);
        jButtonDelete.setEnabled(!busy);
        jButtonDetail.setEnabled(!busy);
    }

    private static class DetailLoadResult {
        private final ClientReceipt receipt;
        private final Client client;
        private final ReceiptDetailData detailData;

        private DetailLoadResult(ClientReceipt receipt, Client client, ReceiptDetailData detailData) {
            this.receipt = receipt;
            this.client = client;
            this.detailData = detailData;
        }

        private ClientReceipt receipt() {
            return receipt;
        }

        private Client client() {
            return client;
        }

        private ReceiptDetailData detailData() {
            return detailData;
        }
    }
}
