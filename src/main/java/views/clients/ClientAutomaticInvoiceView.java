package views.clients;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import configs.AppConfig;
import configs.MyBatisConfig;
import controllers.ClientController;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.SwingWorker;
import mappers.ClientInvoiceDetailMapper;
import mappers.ClientInvoiceMapper;
import mappers.ClientMapper;
import mappers.receipts.ClientReceiptMapper;
import models.ClientInvoice;
import models.Client;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRepository;
import repositories.impl.ClientRepositoryImpl;
import repositories.ClientInvoiceRepository;
import repositories.ClientInvoiceDetailRepository;
import repositories.ClientReceiptRepository;
import repositories.impl.ClientInvoiceRepositoryImpl;
import repositories.impl.ClientInvoiceDetailRepositoryImpl;
import repositories.impl.ClientReceiptRepositoryImpl;
import services.ClientService;
import services.EmailService;
import services.SubscriptionBillingService;
import services.afip.AfipAuthorizationException;
import services.afip.AfipPdfException;
import services.afip.AfipPdfService;
import java.util.stream.Collectors;

public class ClientAutomaticInvoiceView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private final ClientController clientController;
    private final SubscriptionBillingService subscriptionBillingService;
    private final EmailService emailService;
    private final AfipPdfService afipPdfService;
    private final DecimalFormat currencyFormat;
    private final String defaultInvoiceType;
    private final List<GeneratedInvoiceData> generatedInvoices;

    public ClientAutomaticInvoiceView() throws SQLException, Exception {
        SqlSession sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
        ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
        ClientInvoiceMapper clientInvoiceMapper = sqlSession.getMapper(ClientInvoiceMapper.class);
        ClientInvoiceRepository clientInvoiceRepository = new ClientInvoiceRepositoryImpl(clientInvoiceMapper);
        ClientInvoiceDetailMapper clientInvoiceDetailMapper = sqlSession.getMapper(ClientInvoiceDetailMapper.class);
        ClientInvoiceDetailRepository clientInvoiceDetailRepository = new ClientInvoiceDetailRepositoryImpl(
                clientInvoiceDetailMapper);
        ClientReceiptMapper clientReceiptMapper = sqlSession.getMapper(ClientReceiptMapper.class);
        ClientReceiptRepository clientReceiptRepository = new ClientReceiptRepositoryImpl(clientReceiptMapper);
        ClientService clientService = new ClientService(clientRepository, clientInvoiceRepository,
                clientReceiptRepository);
        clientController = new ClientController(clientService);
        subscriptionBillingService = new SubscriptionBillingService(clientRepository, clientInvoiceRepository,
                clientInvoiceDetailRepository, clientReceiptRepository);
        emailService = new EmailService();
        afipPdfService = new AfipPdfService();
        currencyFormat = createCurrencyFormat();
        defaultInvoiceType = AppConfig.get("subscription.invoice.type.default", null);
        generatedInvoices = new ArrayList<>();

        isOpen = true;
        initComponents();
        setDefaultValues();
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
        jButtonGenerate = new javax.swing.JButton();
        jButtonSend = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaSummary = new javax.swing.JTextArea();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Facturación automática");
        setMinimumSize(new java.awt.Dimension(760, 460));
        setPreferredSize(new java.awt.Dimension(820, 520));
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
        jTextFieldDetail.setBounds(210, 25, 380, 30);

        jFormattedTextFieldAmount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextFieldAmount.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jFormattedTextFieldAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFormattedTextFieldAmountKeyPressed(evt);
            }
        });
        getContentPane().add(jFormattedTextFieldAmount);
        jFormattedTextFieldAmount.setBounds(210, 65, 120, 30);

        jButtonGenerate.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonGenerate.setText("Generar facturas");
        jButtonGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonGenerate);
        jButtonGenerate.setBounds(30, 120, 190, 35);

        jButtonSend.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonSend.setText("Enviar facturas");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSend);
        jButtonSend.setBounds(230, 120, 190, 35);

        jButtonClose.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonClose.setText("Cerrar");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonClose);
        jButtonClose.setBounds(430, 120, 150, 35);

        jTextAreaSummary.setEditable(false);
        jTextAreaSummary.setColumns(20);
        jTextAreaSummary.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jTextAreaSummary.setRows(5);
        jTextAreaSummary.setBorder(javax.swing.BorderFactory.createTitledBorder("Progreso"));
        jScrollPane1.setViewportView(jTextAreaSummary);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 180, 740, 230);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        generarFacturas();
    }//GEN-LAST:event_jButtonGenerateActionPerformed

    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        enviarFacturas();
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jTextFieldDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDetailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jFormattedTextFieldAmount.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldDetailKeyPressed

    private void jFormattedTextFieldAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextFieldAmountKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            generarFacturas();
        }
    }//GEN-LAST:event_jFormattedTextFieldAmountKeyPressed

    private void generarFacturas() {
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

        generatedInvoices.clear();
        jTextAreaSummary.setText("");
        appendToSummary("Iniciando generación de facturas...");
        jButtonGenerate.setEnabled(false);
        jButtonSend.setEnabled(false);

        new GenerationWorker(clients, defaultAmount, detail).execute();
    }

    private void enviarFacturas() {
        if (generatedInvoices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero generá las facturas antes de enviarlas.", "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<GeneratedInvoiceData> invoicesToSend = generatedInvoices.stream()
                .filter(GeneratedInvoiceData::hasEmail)
                .collect(Collectors.toList());

        if (invoicesToSend.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay facturas con email configurado para enviar.", "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jTextAreaSummary.setText("");
        appendToSummary("Iniciando envío de facturas...");
        jButtonGenerate.setEnabled(false);
        jButtonSend.setEnabled(false);

        new SendInvoicesWorker(invoicesToSend).execute();
    }

    private boolean sendInvoiceEmail(Client client, ClientInvoice invoice, BigDecimal amount, String detail,
            BigDecimal previousBalance, File pdfAttachment) {
        String subject = "Factura " + formatInvoiceDisplay(invoice);
        BigDecimal safePrevBalance = previousBalance != null ? previousBalance : BigDecimal.ZERO;
        BigDecimal newBalance = safePrevBalance.add(amount);

        StringBuilder body = new StringBuilder()
                .append("<p>Hola ")
                .append(client.getFullName())
                .append(",</p>")
                .append("<p>Te enviamos la factura generada el ")
                .append(invoice.getInvoiceDate() != null
                        ? DateTimeFormatter.ofPattern("dd/MM/yyyy").format(invoice.getInvoiceDate())
                        : "hoy")
                .append(".</p>")
                .append("<p>Detalle: ")
                .append(detail)
                .append("<br/>Total: $")
                .append(currencyFormat.format(amount))
                .append("</p>");

        if (safePrevBalance.compareTo(BigDecimal.ZERO) != 0) {
            body.append("<p><strong style=\"color:#cc0000;\">Saldo anterior: $")
                    .append(currencyFormat.format(safePrevBalance))
                    .append("</strong></p>");
        }
        if (safePrevBalance.compareTo(BigDecimal.ZERO) > 0) {
            body.append("<p><strong>Tenías un saldo deudor. Con esta factura tu nuevo saldo es de $")
                    .append(currencyFormat.format(newBalance))
                    .append(".</strong></p>");
        }

        body.append("<p>Adjuntamos los datos para el pago en un PDF separado.</p>")
                .append("<p>Por favor enviar comprobante al teléfono: 2392519656.</p>")
                .append("<p>Gracias.<br/>")
                .append(AppConfig.get("company.name", ""))
                .append("</p>");

        List<File> attachments = new ArrayList<>();
        File paymentData = buildPaymentDataPdf();
        if (paymentData != null && paymentData.exists()) {
            attachments.add(paymentData);
        }
        if (pdfAttachment != null && pdfAttachment.exists()) {
            attachments.add(pdfAttachment);
        }

        return emailService.sendEmail(client.getEmail(), subject, body.toString(), attachments, true);
    }

    private File buildPaymentDataPdf() {
        try {
            File pdfFile = File.createTempFile("datos-pago-", ".pdf");
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.open();
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font textFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("Datos para el pago", titleFont));
            document.add(new Paragraph("", textFont));

            addPaymentSection(document, sectionFont, textFont, "Caja Ahorro Pesos Galicia", new String[]{
                "Número de cuenta 4006250-2 393-5",
                "CBU 0070393030004006250255",
                "DNI 32862872",
                "Alias de CBU MARIANA.SOARES"
            });

            addPaymentSection(document, sectionFont, textFont, "Caja Ahorro Pesos Banco Provincia", new String[]{
                "Número de Cuenta 6715-502610/5",
                "CBU 0140373003671550261059",
                "CBU Alias MARIANA.SOARES.P"
            });

            addPaymentSection(document, sectionFont, textFont, "Cuenta DNI", new String[]{
                "DNI 32862872"
            });

            document.close();
            return pdfFile;
        } catch (DocumentException | IOException ex) {
            return null;
        }
    }

    private void addPaymentSection(Document document, Font sectionFont, Font textFont, String title, String[] lines)
            throws DocumentException {
        document.add(new Paragraph(title, sectionFont));
        for (String line : lines) {
            document.add(new Paragraph(line, textFont));
        }
        document.add(new Paragraph(" ", textFont));
    }

    private String formatInvoiceDisplay(ClientInvoice invoice) {
        if (invoice == null) {
            return "";
        }
        String posDigits = sanitizeDigits(invoice.getPointOfSale());
        String numberDigits = sanitizeDigits(invoice.getInvoiceNumber());
        return leftPad(posDigits, 4) + "-" + leftPad(numberDigits, 8);
    }

    private String sanitizeDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }

    private String leftPad(String value, int size) {
        String digits = value == null ? "" : value.trim();
        if (digits.length() > size) {
            digits = digits.substring(digits.length() - size);
        }
        return String.format("%" + size + "s", digits).replace(' ', '0');
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

    private void appendToSummary(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        if (!text.endsWith("\n")) {
            text = text + "\n";
        }
        jTextAreaSummary.append(text);
        jTextAreaSummary.setCaretPosition(jTextAreaSummary.getDocument().getLength());
    }

    private class GenerationWorker extends SwingWorker<GenerationResult, String> {

        private final List<Client> clients;
        private final BigDecimal defaultAmount;
        private final String detail;

        public GenerationWorker(List<Client> clients, BigDecimal defaultAmount, String detail) {
            this.clients = clients;
            this.defaultAmount = defaultAmount;
            this.detail = detail;
        }

        @Override
        protected GenerationResult doInBackground() {
            GenerationResult result = new GenerationResult();
            for (Client client : clients) {
                BigDecimal amount = resolveAmount(client, defaultAmount);
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    publish("Factura para " + client.getFullName() + " no generada (importe inválido)");
                    result.skipped++;
                    continue;
                }
                BigDecimal previousBalance = resolveBalance(client.getId());
                try {
                    ClientInvoice invoice = subscriptionBillingService.generateInvoiceForClient(client, LocalDate.now(),
                            defaultInvoiceType, amount, detail);
                    if (invoice == null) {
                        publish("Factura para " + client.getFullName() + " no generada (importe no válido)");
                        result.skipped++;
                        continue;
                    }
                    File pdfAttachment = generateInvoicePdf(invoice);
                    generatedInvoices.add(new GeneratedInvoiceData(client, invoice, amount, detail, previousBalance, pdfAttachment));
                    publish("Factura " + formatInvoiceDisplay(invoice) + " cliente " + client.getFullName() + " autorizada");
                    result.created++;
                } catch (AfipAuthorizationException ex) {
                    publish("Factura para " + client.getFullName() + " no autorizada: " + ex.getMessage());
                    result.rejectedByAfip++;
                } catch (Exception ex) {
                    publish("Error al generar factura para " + client.getFullName() + ": " + ex.getMessage());
                    result.failed++;
                }
            }
            return result;
        }

        @Override
        protected void process(List<String> chunks) {
            chunks.forEach(ClientAutomaticInvoiceView.this::appendToSummary);
        }

        @Override
        protected void done() {
            jButtonGenerate.setEnabled(true);
            try {
                GenerationResult result = get();
                appendToSummary("Generación de facturas finalizada.");
                StringBuilder message = new StringBuilder("Se procesó la generación de facturas automáticas.");
                message.append("\nFacturas autorizadas: ").append(result.created);
                if (result.rejectedByAfip > 0) {
                    message.append("\nRechazadas por AFIP: ").append(result.rejectedByAfip);
                }
                if (result.skipped > 0) {
                    message.append("\nSaltadas por importe inválido: ").append(result.skipped);
                }
                if (result.failed > 0) {
                    message.append("\nErrores al generar: ").append(result.failed);
                }
                JOptionPane.showMessageDialog(ClientAutomaticInvoiceView.this, message.toString(), "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
                jButtonSend.setEnabled(!generatedInvoices.isEmpty());
            } catch (Exception ex) {
                appendToSummary("No se pudo completar la generación de facturas: " + ex.getMessage());
            }
        }
    }

    private class SendInvoicesWorker extends SwingWorker<SendResult, String> {

        private final List<GeneratedInvoiceData> invoicesToSend;

        public SendInvoicesWorker(List<GeneratedInvoiceData> invoicesToSend) {
            this.invoicesToSend = invoicesToSend;
        }

        @Override
        protected SendResult doInBackground() {
            SendResult result = new SendResult();
            for (GeneratedInvoiceData data : invoicesToSend) {
                publish("Enviando factura " + formatInvoiceDisplay(data.invoice) + " a " + data.client.getEmail());
                boolean sent = sendInvoiceEmail(data.client, data.invoice, data.amount, data.detail, data.previousBalance, data.pdfAttachment);
                if (sent) {
                    publish("Factura " + formatInvoiceDisplay(data.invoice) + " enviada a " + data.client.getEmail());
                    result.sent++;
                } else {
                    publish("No se pudo enviar la factura " + formatInvoiceDisplay(data.invoice) + " a " + data.client.getEmail());
                    result.failed++;
                }
            }
            return result;
        }

        @Override
        protected void process(List<String> chunks) {
            chunks.forEach(ClientAutomaticInvoiceView.this::appendToSummary);
        }

        @Override
        protected void done() {
            jButtonGenerate.setEnabled(true);
            jButtonSend.setEnabled(!generatedInvoices.isEmpty());
            try {
                SendResult result = get();
                appendToSummary("Envío de facturas finalizado.");
                StringBuilder message = new StringBuilder("Se procesó el envío de facturas por mail.");
                message.append("\nFacturas enviadas: ").append(result.sent);
                if (result.failed > 0) {
                    message.append("\nEnvios fallidos: ").append(result.failed);
                }
                JOptionPane.showMessageDialog(ClientAutomaticInvoiceView.this, message.toString(), "Bits&Bytes", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                appendToSummary("No se pudo completar el envío de facturas: " + ex.getMessage());
            }
        }
    }

    private static class GeneratedInvoiceData {

        private final Client client;
        private final ClientInvoice invoice;
        private final BigDecimal amount;
        private final String detail;
        private final BigDecimal previousBalance;
        private final File pdfAttachment;

        public GeneratedInvoiceData(Client client, ClientInvoice invoice, BigDecimal amount, String detail,
                BigDecimal previousBalance, File pdfAttachment) {
            this.client = client;
            this.invoice = invoice;
            this.amount = amount;
            this.detail = detail;
            this.previousBalance = previousBalance;
            this.pdfAttachment = pdfAttachment;
        }

        public boolean hasEmail() {
            return client.getEmail() != null && !client.getEmail().isBlank();
        }
    }

    private static class GenerationResult {

        private int created;
        private int rejectedByAfip;
        private int skipped;
        private int failed;
    }

    private static class SendResult {

        private int sent;
        private int failed;
    }

    private BigDecimal resolveBalance(Integer clientId) {
        try {
            BigDecimal balance = clientController.getBalance(clientId);
            return balance != null ? balance : BigDecimal.ZERO;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private File generateInvoicePdf(ClientInvoice invoice) {
        try {
            File exported = afipPdfService.findExistingPdf(invoice);
            if (exported != null && exported.exists()) {
                return exported;
            }
            return afipPdfService.generatePdfFile(invoice, null, false);
        } catch (AfipPdfException ex) {
            return null;
        }
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

    private void setDefaultValues() {
        jTextFieldDetail.setText(buildDefaultDetail());
        BigDecimal defaultAmount = resolveDefaultAmount();
        if (defaultAmount.compareTo(BigDecimal.ZERO) > 0) {
            jFormattedTextFieldAmount.setValue(defaultAmount);
        }
        jButtonSend.setEnabled(false);
    }

    private String buildDefaultDetail() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("es", "ES"));
        String formatted = formatter.format(now);
        String capitalized = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        return "Abono sistema " + capitalized;
    }

    private BigDecimal resolveDefaultAmount() {
        String configured = AppConfig.get("subscription.amount.default", "0");
        try {
            return new BigDecimal(configured.trim());
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonGenerate;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JFormattedTextField jFormattedTextFieldAmount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaSummary;
    private javax.swing.JTextField jTextFieldDetail;
    // End of variables declaration//GEN-END:variables
}
