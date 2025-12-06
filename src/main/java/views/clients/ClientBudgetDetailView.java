package views.clients;

import controllers.ClientBudgetController;
import controllers.ClientBudgetDetailController;
import controllers.ClientController;
import controllers.ClientRemitController;
import controllers.ClientRemitDetailController;
import controllers.ProductController;
import configs.MyBatisConfig;
import mappers.ClientBudgetDetailMapper;
import mappers.ClientBudgetMapper;
import mappers.ClientMapper;
import mappers.ClientRemitDetailMapper;
import mappers.ClientRemitMapper;
import mappers.ProductMapper;
import models.Client;
import models.ClientBudget;
import models.ClientBudgetDetail;
import models.ClientRemit;
import models.ClientRemitDetail;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientBudgetDetailRepository;
import repositories.ClientBudgetRepository;
import repositories.ClientRepository;
import repositories.ClientRemitDetailRepository;
import repositories.ClientRemitRepository;
import repositories.ProductRepository;
import repositories.impl.ClientBudgetDetailRepositoryImpl;
import repositories.impl.ClientBudgetRepositoryImpl;
import repositories.impl.ClientRemitDetailRepositoryImpl;
import repositories.impl.ClientRemitRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import services.ClientBudgetDetailService;
import services.ClientBudgetService;
import services.ClientRemitDetailService;
import services.ClientRemitService;
import services.ProductService;
import services.ClientService;
import services.reports.JasperReportFactory;
import services.reports.ReportParameterFactory;
import utils.Constants;
import utils.DocumentValidator;
import utils.JasperViewerUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientBudgetDetailView extends javax.swing.JInternalFrame {

    private ClientBudgetController budgetController;
    private ClientBudgetDetailController detailController;
    private ClientController clientController;
    private ClientRemitController remitController;
    private ClientRemitDetailController remitDetailController;
    private ProductController productController;
    private SqlSession sqlSession;
    public static boolean open = false;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ClientBudget budget;
    private List<ClientBudgetDetail> budgetDetails = Collections.emptyList();

    public ClientBudgetDetailView(int budgetId) {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientBudgetMapper budgetMapper = sqlSession.getMapper(ClientBudgetMapper.class);
            ClientBudgetDetailMapper detailMapper = sqlSession.getMapper(ClientBudgetDetailMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientRemitMapper remitMapper = sqlSession.getMapper(ClientRemitMapper.class);
            ClientRemitDetailMapper remitDetailMapper = sqlSession.getMapper(ClientRemitDetailMapper.class);
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            ClientBudgetRepository budgetRepository = new ClientBudgetRepositoryImpl(budgetMapper);
            ClientBudgetDetailRepository detailRepository = new ClientBudgetDetailRepositoryImpl(detailMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientRemitRepository remitRepository = new ClientRemitRepositoryImpl(remitMapper);
            ClientRemitDetailRepository remitDetailRepository = new ClientRemitDetailRepositoryImpl(remitDetailMapper);
            ProductRepository productRepository = new ProductRepositoryImpl(productMapper);
            ClientBudgetService budgetService = new ClientBudgetService(budgetRepository);
            ClientBudgetDetailService detailService = new ClientBudgetDetailService(detailRepository);
            ClientService clientService = new ClientService(clientRepository);
            ClientRemitService remitService = new ClientRemitService(remitRepository);
            ClientRemitDetailService remitDetailService = new ClientRemitDetailService(remitDetailRepository);
            ProductService productService = new ProductService(productRepository);
            budgetController = new ClientBudgetController(budgetService);
            detailController = new ClientBudgetDetailController(detailService);
            clientController = new ClientController(clientService);
            remitController = new ClientRemitController(remitService);
            remitDetailController = new ClientRemitDetailController(remitDetailService);
            productController = new ProductController(productService);

            initComponents();
            open = true;

            budget = budgetController.findById(budgetId);
            if (budget != null && budget.getClient() != null && budget.getClient().getId() != null) {
                Client fullClient = clientController.findById(budget.getClient().getId());
                if (fullClient != null) {
                    budget.setClient(fullClient);
                }
            }
            if (budget != null) {
                jLabelDate.setText(formatBudgetDate(budget.getBudgetDate()));
                BigDecimal total = budget.getTotal() != null ? budget.getTotal() : BigDecimal.ZERO;
                jLabelTotal.setText(total.toPlainString());
                jTextAreaObs.setText(budget.getDescription());
                if (Boolean.TRUE.equals(budget.getClosed())) {
                    jButtonConvertToRemit.setEnabled(false);
                }
            }

            budgetDetails = detailController.findByBudget(budgetId);
            jTable1.setModel(createDetailTableModel(budgetDetails));
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetDetailView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DefaultTableModel createDetailTableModel(List<ClientBudgetDetail> list) {
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Codigo", "Descripcion", "Cantidad", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        List<ClientBudgetDetail> safeList = list != null ? list : Collections.emptyList();
        for (ClientBudgetDetail d : safeList) {
            Vector<Object> row = new Vector<>();
            row.add(d.getProductCode());
            row.add(d.getDescription());
            row.add(d.getQuantity());
            row.add(d.getPrice());
            tm.addRow(row);
        }
        return tm;
    }

    private String formatBudgetDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private BigDecimal calculateBudgetTotal() {
        List<ClientBudgetDetail> details = budgetDetails != null ? budgetDetails : Collections.emptyList();
        BigDecimal total = BigDecimal.ZERO;
        for (ClientBudgetDetail detail : details) {
            if (detail == null) {
                continue;
            }
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            BigDecimal quantity = toBigDecimal(detail.getQuantity());
            total = total.add(price.multiply(quantity));
        }
        if (total.compareTo(BigDecimal.ZERO) <= 0 && budget != null && budget.getTotal() != null) {
            return budget.getTotal();
        }
        return total;
    }

    private BigDecimal toBigDecimal(Float value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(Float.toString(value));
    }

    private boolean adjustStockForRemitDetail(ClientRemitDetail detail) {
        if (detail == null || productController == null) {
            return true;
        }
        String code = detail.getProductCode();
        if (code == null) {
            return true;
        }
        String trimmedCode = code.trim();
        if (trimmedCode.isEmpty() || "99".equals(trimmedCode)) {
            return true;
        }
        Float quantity = detail.getQuantity();
        if (quantity == null || quantity <= 0f) {
            return true;
        }
        try {
            return productController.decreaseStock(trimmedCode, quantity);
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetDetailView.class.getName()).log(Level.SEVERE,
                    "No se pudo descontar stock para el artículo " + trimmedCode, ex);
            return false;
        }
    }

    private String buildRemitDescription() {
        if (budget == null) {
            return "";
        }
        StringBuilder description = new StringBuilder("Generado desde presupuesto");
        if (budget.getId() != null) {
            description.append(" #").append(String.format("%08d", budget.getId()));
        }
        if (budget.getDescription() != null && !budget.getDescription().isBlank()) {
            description.append(" - ").append(budget.getDescription().trim());
        }
        return description.toString();
    }

    private String formatRemitNumber(ClientRemit remit) {
        if (remit == null || remit.getId() == null) {
            return "";
        }
        return String.format("%08d", remit.getId());
    }

    private List<Map<String, ?>> buildBudgetReportData() {
        if (budget == null) {
            return Collections.emptyList();
        }

        List<Map<String, ?>> rows = new ArrayList<>();
        List<ClientBudgetDetail> details = budgetDetails != null ? budgetDetails : Collections.emptyList();

        String clientName = budget.getClient() != null ? safeString(budget.getClient().getFullName()) : "";
        String address = buildAddress();
        String locality = budget.getClient() != null && budget.getClient().getCity() != null
                ? safeString(budget.getClient().getCity().getName()) : "";
        String condition = budget.getClient() != null && budget.getClient().getTaxCondition() != null
                ? safeString(budget.getClient().getTaxCondition().getName()) : "";
        String clientNumber = budget.getClient() != null && budget.getClient().getId() != null
                ? budget.getClient().getId().toString() : "";
        String cuit = budget.getClient() != null
                ? DocumentValidator.formatCuit(budget.getClient().getDocumentNumber()) : "";
        String formattedDate = budget.getBudgetDate() != null
                ? budget.getBudgetDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
        String formattedNumber = budget.getId() != null ? String.format("%08d", budget.getId()) : "";
        String total = formatAmount(budget.getTotal());
        String observations = safeString(budget.getDescription());

        if (details.isEmpty()) {
            rows.add(buildBudgetRow(null, total, formattedDate, formattedNumber, clientName, address,
                    locality, condition, clientNumber, cuit, observations));
        } else {
            for (ClientBudgetDetail detail : details) {
                rows.add(buildBudgetRow(detail, total, formattedDate, formattedNumber, clientName, address,
                        locality, condition, clientNumber, cuit, observations));
            }
        }
        return rows;
    }

    private Map<String, Object> buildBudgetRow(ClientBudgetDetail detail, String total, String formattedDate,
            String formattedNumber, String clientName, String address, String locality,
            String condition, String clientNumber, String cuit, String observations) {

        Map<String, Object> row = new HashMap<>();
        row.put("tipoComprobante", Constants.PRESUPUESTO);
        row.put("cliente", clientName);
        row.put("direccion", address);
        row.put("localidad", locality);
        row.put("condicion", condition);
        row.put("nroCliente", clientNumber);
        row.put("numero", formattedNumber);
        row.put("fecha", formattedDate);
        row.put("cuit", cuit);
        row.put("total", total);
        row.put("observaciones", observations);

        if (detail != null) {
            row.put("codArticulo", safeString(detail.getProductCode()));
            row.put("detalle", safeString(detail.getDescription()));
            row.put("cantidad", formatQuantity(detail.getQuantity()));
            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
            row.put("precio", formatAmount(price));
            BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity() != null ? detail.getQuantity() : 0f);
            row.put("parcial", formatAmount(price.multiply(quantity)));
        } else {
            row.put("codArticulo", "");
            row.put("detalle", "");
            row.put("cantidad", "");
            row.put("precio", "");
            row.put("parcial", "");
        }
        return row;
    }

    private String buildAddress() {
        if (budget == null || budget.getClient() == null || budget.getClient().getAddress() == null) {
            return "";
        }
        String street = safeString(budget.getClient().getAddress().getName());
        String number = safeString(budget.getClient().getAddressNumber());
        if (street.isEmpty()) {
            return number;
        }
        if (number.isEmpty()) {
            return street;
        }
        return street + " " + number;
    }

    private String formatAmount(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatQuantity(Float quantity) {
        if (quantity == null) {
            return "";
        }
        BigDecimal bd = new BigDecimal(Float.toString(quantity));
        BigDecimal normalized = bd.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString();
    }

    private Map<String, Object> createReportParameters() {
        return new HashMap<>(ReportParameterFactory.createBaseParameters());
    }

    private JasperReport loadReport(String resource) throws JRException {
        return JasperReportFactory.loadReport(resource);
    }

    private void printBudget(String reportResource, Map<String, Object> additionalParameters, String viewerTitle) {
        if (budget == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el presupuesto solicitado", "Imprimir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Map<String, ?>> data = buildBudgetReportData();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay información para imprimir", "Imprimir", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            JasperReport report = loadReport(reportResource);
            Map<String, Object> parameters = createReportParameters();
            if (additionalParameters != null) {
                parameters.putAll(additionalParameters);
            }
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), viewerTitle);
        } catch (JRException ex) {
            Logger.getLogger(ClientBudgetDetailView.class.getName()).log(Level.SEVERE, "Error al generar el reporte", ex);
            JOptionPane.showMessageDialog(this, "No se pudo generar el informe", "Imprimir", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString();
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
        jButtonReturn = new javax.swing.JButton();
        jLabelDate = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jButtonPrint = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaObs = new javax.swing.JTextArea();
        jButtonConvertToRemit = new javax.swing.JButton();
        jButtonPrintProForm = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(690, 590));
        setPreferredSize(new java.awt.Dimension(690, 610));
        getContentPane().setLayout(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descripcion", "Medida", "Cantidad", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Short.class, java.lang.Integer.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(40, 90, 600, 210);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Subtotal:");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(40, 20, 70, 20);

        jLabel16.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Iva:");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(40, 50, 70, 20);

        jLabel17.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jPanel2.add(jLabel17);
        jLabel17.setBounds(120, 20, 80, 20);

        jLabel18.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(120, 50, 80, 20);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Total:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(40, 100, 70, 20);

        jLabel20.setFont(new java.awt.Font("Calibri", 3, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 153, 51));
        jPanel2.add(jLabel20);
        jLabel20.setBounds(120, 100, 100, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(960, 470, 250, 130);

        jButtonReturn.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButtonReturn.setText("Volver");
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonReturn);
        jButtonReturn.setBounds(530, 510, 110, 30);

        jLabelDate.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        getContentPane().add(jLabelDate);
        jLabelDate.setBounds(480, 30, 150, 20);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Total");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(30, 20, 70, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 204, 51));
        jPanel1.add(jLabelTotal);
        jLabelTotal.setBounds(110, 20, 130, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(380, 430, 260, 60);

        jButtonPrint.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonPrint.setText("Imprimir");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrint);
        jButtonPrint.setBounds(110, 380, 110, 30);

        jTextAreaObs.setEditable(false);
        jTextAreaObs.setColumns(20);
        jTextAreaObs.setRows(5);
        jScrollPane2.setViewportView(jTextAreaObs);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(40, 310, 600, 50);

        jButtonConvertToRemit.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonConvertToRemit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/remitoCliente.png"))); // NOI18N
        jButtonConvertToRemit.setText("Pasar a Remito");
        jButtonConvertToRemit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConvertToRemitActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonConvertToRemit);
        jButtonConvertToRemit.setBounds(420, 380, 170, 30);

        jButtonPrintProForm.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonPrintProForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/imprimir.png"))); // NOI18N
        jButtonPrintProForm.setText("Imprimir Pro-Forma");
        jButtonPrintProForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintProFormActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonPrintProForm);
        jButtonPrintProForm.setBounds(230, 380, 180, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        open = false;
        dispose();
    }//GEN-LAST:event_jButtonReturnActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        printBudget("/reports/ImpresionPresupuesto.jrxml", null, "Presupuesto");
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jButtonConvertToRemitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConvertToRemitActionPerformed
        if (budget == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el presupuesto seleccionado", "Remito", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (budget.getClient() == null || budget.getClient().getId() == null) {
            JOptionPane.showMessageDialog(this, "El presupuesto no tiene un cliente asociado", "Remito", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Se generará un remito a partir de este presupuesto. ¿Desea continuar?",
                "Remito", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Client client = clientController.findById(budget.getClient().getId());
            if (client == null) {
                JOptionPane.showMessageDialog(this, "El cliente asociado ya no existe", "Remito", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ClientRemit remit = new ClientRemit();
            remit.setClient(client);
            remit.setRemitDate(LocalDateTime.now());
            remit.setDescription(buildRemitDescription());
            remit.setClosed(Boolean.FALSE);
            BigDecimal total = calculateBudgetTotal();
            remit.setTotal(total);
            remitController.save(remit);

            List<ClientBudgetDetail> sourceDetails = budgetDetails != null ? budgetDetails : Collections.emptyList();
            List<ClientRemitDetail> remitItems = new ArrayList<>();
            for (ClientBudgetDetail detail : sourceDetails) {
                if (detail == null) {
                    continue;
                }
                ClientRemitDetail remitDetail = new ClientRemitDetail();
                remitDetail.setRemit(remit);
                remitDetail.setProductCode(detail.getProductCode());
                remitDetail.setDescription(detail.getDescription());
                remitDetail.setQuantity(detail.getQuantity());
                remitDetail.setPrice(detail.getPrice());
                remitItems.add(remitDetail);
            }
            if (remitItems.isEmpty()) {
                ClientRemitDetail emptyDetail = new ClientRemitDetail();
                emptyDetail.setRemit(remit);
                emptyDetail.setDescription(budget.getDescription());
                emptyDetail.setQuantity(0f);
                emptyDetail.setPrice(total);
                remitItems.add(emptyDetail);
            }
            List<String> stockWarnings = new ArrayList<>();
            for (ClientRemitDetail detail : remitItems) {
                remitDetailController.save(detail);
                if (!adjustStockForRemitDetail(detail)) {
                    String code = detail.getProductCode();
                    if (code != null && !code.trim().isEmpty()) {
                        stockWarnings.add(code.trim());
                    }
                }
            }

            if (!stockWarnings.isEmpty()) {
                String articles = String.join(", ", stockWarnings);
                JOptionPane.showMessageDialog(this,
                        "No se pudo descontar stock para los artículos: " + articles,
                        "Stock",
                        JOptionPane.WARNING_MESSAGE);
            }

            budget.setClosed(Boolean.TRUE);
            budgetController.update(budget);
            jButtonConvertToRemit.setEnabled(false);

            if (ClientBudgetManagementView.isOpen) {
                ClientBudgetManagementView.refreshTable();
            }
            ClientRemitManagementView.refreshTable();

            String remitNumber = formatRemitNumber(remit);
            String message = remitNumber.isEmpty()
                    ? "Se generó el remito correctamente."
                    : "Se generó el remito N° " + remitNumber + ".";
            JOptionPane.showMessageDialog(this, message, "Remito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(ClientBudgetDetailView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo generar el remito", "Remito", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonConvertToRemitActionPerformed

    private void jButtonPrintProFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintProFormActionPerformed
        BigDecimal total = budget != null && budget.getTotal() != null ? budget.getTotal() : BigDecimal.ZERO;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subtotal", total.floatValue());
        parameters.put("iva", 0f);
        printBudget("/reports/FacturaProforma.jrxml", parameters, "Factura Pro-Forma");
    }//GEN-LAST:event_jButtonPrintProFormActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConvertToRemit;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonPrintProForm;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    public static javax.swing.JLabel jLabel17;
    public static javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    public static javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    public static javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaObs;
    // End of variables declaration//GEN-END:variables
}

