package views.providers;

import controllers.ProviderController;
import controllers.ProviderInvoiceController;
import configs.MyBatisConfig;
import mappers.ProviderMapper;
import mappers.ProviderInvoiceMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.ProviderRepository;
import repositories.ProviderInvoiceRepository;
import repositories.impl.ProviderRepositoryImpl;
import repositories.impl.ProviderInvoiceRepositoryImpl;
import services.ProviderService;
import services.ProviderInvoiceService;
import utils.InvoiceTypeUtils;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProviderInvoiceDetailView extends javax.swing.JInternalFrame {

    public static boolean isOpen = false;
    private ProviderController providerController;
    private ProviderInvoiceController providerInvoiceController;
    private SqlSession sqlSession;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ProviderInvoiceDetailView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ProviderMapper providerMapper = sqlSession.getMapper(ProviderMapper.class);
            ProviderInvoiceMapper providerInvoiceMapper = sqlSession.getMapper(ProviderInvoiceMapper.class);
            ProviderRepository providerRepository = new ProviderRepositoryImpl(providerMapper);
            ProviderInvoiceRepository invoiceRepository = new ProviderInvoiceRepositoryImpl(providerInvoiceMapper);
            ProviderService providerService = new ProviderService(providerRepository);
            ProviderInvoiceService invoiceService = new ProviderInvoiceService(invoiceRepository);
            this.providerController = new ProviderController(providerService);
            this.providerInvoiceController = new ProviderInvoiceController(invoiceService);

            initComponents();
            isOpen = true;

            models.ProviderInvoice invoice = null;

            if (ProviderHistoryView.isOpen) {
                int i = ProviderHistoryView.jTable1.getSelectedRow();
                invoice = ProviderHistoryView.getInvoiceAtRow(i);
            } else if (ProviderInvoiceManagementView.isOpen) {
                int i = ProviderInvoiceManagementView.jTable1.getSelectedRow();
                int id = Integer.parseInt(ProviderInvoiceManagementView.jTable1.getValueAt(i, 0).toString());
                invoice = providerInvoiceController.findById(id);
            }

            if (invoice != null) {
                jLabelTipoCompro.setText(InvoiceTypeUtils.toDisplayValue(invoice.getInvoiceType()));
                jLabelNumeroCompro.setText(formatInvoiceCode(invoice.getPointOfSale(), invoice.getInvoiceNumber()));
                jLabelFecha.setText(formatInvoiceDate(invoice.getInvoiceDate()));
                jLabelTotal.setText(String.valueOf(invoice.getTotal()));
                jLabelSubTotal.setText(String.valueOf(invoice.getSubtotal()));
                jLabelIva21.setText(String.valueOf(invoice.getVat21()));
                jLabelIva105.setText(String.valueOf(invoice.getVat105()));
                jLabelIva27.setText(String.valueOf(invoice.getVat27()));
                jLabelPIIBB.setText(String.valueOf(invoice.getGrossIncomePerception()));
                jLabelPIva.setText(String.valueOf(invoice.getVatPerception()));
                jLabelPG.setText(String.valueOf(invoice.getIncomeTaxPerception()));
                jLabelNG.setText(String.valueOf(invoice.getExemptAmount()));
                jLabelIBU.setText(String.valueOf(invoice.getStampTax()));

                models.Provider provider = providerController.findById(invoice.getProvider().getId());
                jLabelProveedor.setText(provider.getId() + " " + provider.getName());
                jLabelLocalidad.setText(provider.getCity() != null ? provider.getCity().getName() : "");
                String address = provider.getAddress() != null ? provider.getAddress().getName() : "";
                jLabelDomicilio.setText(address + " " + provider.getAddressNumber());
            }

        } catch (Exception ex) {
            Logger.getLogger(ProviderInvoiceDetailView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String formatInvoiceDate(java.time.LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.toLocalDate().format(DATE_FORMATTER);
    }

    private String formatInvoiceCode(String pointOfSale, String number) {
        String pos = padNumeric(pointOfSale, 4);
        String num = padNumeric(number, 8);
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

    private String padNumeric(String value, int length) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "";
        }
        if (digits.length() >= length) {
            return digits;
        }
        return String.format("%" + length + "s", digits).replace(' ', '0');
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelProveedor = new javax.swing.JLabel();
        jLabelDomicilio = new javax.swing.JLabel();
        jLabelNumeroCompro = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabelSubTotal = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabelIBU = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabelLocalidad = new javax.swing.JLabel();
        jLabelTipoCompro = new javax.swing.JLabel();
        jLabelFecha = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabelIva21 = new javax.swing.JLabel();
        jLabelIva105 = new javax.swing.JLabel();
        jLabelIva27 = new javax.swing.JLabel();
        jLabelPIva = new javax.swing.JLabel();
        jLabelPIIBB = new javax.swing.JLabel();
        jLabelPG = new javax.swing.JLabel();
        jLabelNG = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(850, 620));
        setPreferredSize(new java.awt.Dimension(850, 620));
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Proveedor");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(40, 60, 125, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Localidad");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 90, 125, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Domicilio");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 120, 125, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Fecha");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(410, 90, 125, 20);

        jLabel10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Tipo Comprobante");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(410, 120, 125, 20);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Comprobante Numero");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(660, 30, 160, 20);

        jLabelProveedor.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelProveedor);
        jLabelProveedor.setBounds(180, 60, 180, 20);

        jLabelDomicilio.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDomicilio);
        jLabelDomicilio.setBounds(190, 120, 220, 20);

        jLabelNumeroCompro.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelNumeroCompro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelNumeroCompro);
        jLabelNumeroCompro.setBounds(660, 60, 160, 20);

        jButton5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton5.setText("Volver");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);
        jButton5.setBounds(350, 470, 130, 30);
        getContentPane().add(jLabel3);
        jLabel3.setBounds(280, 60, 130, 20);

        jLabelSubTotal.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        jLabelSubTotal.setForeground(new java.awt.Color(0, 204, 51));
        getContentPane().add(jLabelSubTotal);
        jLabelSubTotal.setBounds(620, 200, 150, 30);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(50, 160, 770, 10);

        jLabel9.setBackground(new java.awt.Color(255, 102, 0));
        jLabel9.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 102, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Percepcion IVA");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(610, 270, 140, 30);

        jLabelIBU.setBackground(new java.awt.Color(255, 102, 0));
        jLabelIBU.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelIBU.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelIBU);
        jLabelIBU.setBounds(610, 420, 140, 30);

        jLabel15.setBackground(new java.awt.Color(255, 102, 0));
        jLabel15.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 102, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("IVA 10.5");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(260, 270, 140, 30);

        jLabel16.setBackground(new java.awt.Color(255, 102, 0));
        jLabel16.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 102, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("IVA 27");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(440, 270, 140, 30);

        jLabel17.setBackground(new java.awt.Color(255, 102, 0));
        jLabel17.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 102, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("IVA B.U.");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(610, 380, 140, 30);

        jLabel18.setBackground(new java.awt.Color(255, 102, 0));
        jLabel18.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 102, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Percepcion IIBB");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(80, 380, 140, 30);

        jLabel19.setBackground(new java.awt.Color(255, 102, 0));
        jLabel19.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 102, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Perc. Ganancias");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(260, 380, 140, 30);

        jLabel20.setBackground(new java.awt.Color(255, 102, 0));
        jLabel20.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 102, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("No Gravado");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(440, 380, 140, 30);

        jLabel23.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel23.setText("Subtotal");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(530, 200, 100, 30);

        jLabelLocalidad.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelLocalidad);
        jLabelLocalidad.setBounds(180, 90, 150, 20);

        jLabelTipoCompro.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelTipoCompro);
        jLabelTipoCompro.setBounds(560, 120, 150, 20);

        jLabelFecha.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelFecha);
        jLabelFecha.setBounds(560, 90, 100, 20);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        jLabel5.setText("Importe Total");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(80, 200, 180, 30);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 0, 24)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 204, 51));
        getContentPane().add(jLabelTotal);
        jLabelTotal.setBounds(240, 200, 180, 30);

        jLabel12.setBackground(new java.awt.Color(255, 102, 0));
        jLabel12.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 102, 0));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("IVA 21");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(80, 270, 140, 30);

        jLabelIva21.setBackground(new java.awt.Color(255, 102, 0));
        jLabelIva21.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelIva21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelIva21);
        jLabelIva21.setBounds(80, 310, 140, 30);

        jLabelIva105.setBackground(new java.awt.Color(255, 102, 0));
        jLabelIva105.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelIva105.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelIva105);
        jLabelIva105.setBounds(260, 310, 140, 30);

        jLabelIva27.setBackground(new java.awt.Color(255, 102, 0));
        jLabelIva27.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelIva27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelIva27);
        jLabelIva27.setBounds(440, 310, 140, 30);

        jLabelPIva.setBackground(new java.awt.Color(255, 102, 0));
        jLabelPIva.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelPIva.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelPIva);
        jLabelPIva.setBounds(610, 310, 140, 30);

        jLabelPIIBB.setBackground(new java.awt.Color(255, 102, 0));
        jLabelPIIBB.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelPIIBB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelPIIBB);
        jLabelPIIBB.setBounds(80, 420, 140, 30);

        jLabelPG.setBackground(new java.awt.Color(255, 102, 0));
        jLabelPG.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelPG.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelPG);
        jLabelPG.setBounds(260, 420, 140, 30);

        jLabelNG.setBackground(new java.awt.Color(255, 102, 0));
        jLabelNG.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabelNG.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabelNG);
        jLabelNG.setBounds(440, 420, 140, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        isOpen = false;
        dispose();

}//GEN-LAST:event_jButton5ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    public static javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    public static javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabelDomicilio;
    public static javax.swing.JLabel jLabelFecha;
    private javax.swing.JLabel jLabelIBU;
    private javax.swing.JLabel jLabelIva105;
    private javax.swing.JLabel jLabelIva21;
    private javax.swing.JLabel jLabelIva27;
    public static javax.swing.JLabel jLabelLocalidad;
    private javax.swing.JLabel jLabelNG;
    public static javax.swing.JLabel jLabelNumeroCompro;
    private javax.swing.JLabel jLabelPG;
    private javax.swing.JLabel jLabelPIIBB;
    private javax.swing.JLabel jLabelPIva;
    public static javax.swing.JLabel jLabelProveedor;
    private javax.swing.JLabel jLabelSubTotal;
    public static javax.swing.JLabel jLabelTipoCompro;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

}
