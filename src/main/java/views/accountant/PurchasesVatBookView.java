package views.accountant;

import controllers.VatBookController;
import configs.MyBatisConfig;
import mappers.VatBookMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.VatBookRepository;
import repositories.impl.VatBookRepositoryImpl;
import services.VatBookGenerationResult;
import services.VatBookService;
import services.reports.ReportPurchasesVatBook;
import services.reports.ReportSalesVatBook;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import utils.CuitSelectorUtils;

public class PurchasesVatBookView extends javax.swing.JInternalFrame {

    public static boolean open=false;
    private final VatBookController vatBookController;

    public PurchasesVatBookView() throws Exception {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        VatBookMapper mapper = session.getMapper(VatBookMapper.class);
        VatBookRepository repository = new VatBookRepositoryImpl(mapper);
        ReportSalesVatBook reportSales = new ReportSalesVatBook();
        ReportPurchasesVatBook reportPurchases = new ReportPurchasesVatBook();
        VatBookService service = new VatBookService(repository, reportSales, reportPurchases);
        vatBookController = new VatBookController(service);
        initComponents();
        CuitSelectorUtils.populateCuits(jComboBoxInvoiceCuit);
        setDefaultPeriod();
        open = true;

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

    private void setDefaultPeriod() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate start = previousMonth.atDay(1);
        LocalDate end = previousMonth.atEndOfMonth();
        ZoneId zoneId = ZoneId.systemDefault();
        Date startDate = Date.from(start.atStartOfDay(zoneId).toInstant());
        Date endDate = Date.from(end.atStartOfDay(zoneId).toInstant());
        jDateChooser1.setDate(startDate);
        jDateChooser2.setDate(endDate);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelFin = new javax.swing.JLabel();
        jLabelInicio = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxInvoiceCuit = new javax.swing.JComboBox();

        setTitle("Libro I.V.A Compras");
        setMinimumSize(new java.awt.Dimension(300, 250));
        setPreferredSize(new java.awt.Dimension(350, 350));
        getContentPane().setLayout(null);

        jLabelFin.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelFin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFin.setText("Fecha Fin");
        getContentPane().add(jLabelFin);
        jLabelFin.setBounds(80, 90, 170, 20);

        jLabelInicio.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelInicio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInicio.setText("Fecha Inicio");
        jLabelInicio.setMaximumSize(new java.awt.Dimension(180, 180));
        jLabelInicio.setMinimumSize(new java.awt.Dimension(180, 180));
        jLabelInicio.setPreferredSize(new java.awt.Dimension(180, 180));
        getContentPane().add(jLabelInicio);
        jLabelInicio.setBounds(80, 30, 170, 20);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/guardar.png"))); // NOI18N
        jButton1.setText("Procesar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(50, 230, 110, 30);

        jButton2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButton2.setText("Salir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(170, 230, 110, 30);
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(80, 60, 170, 22);
        getContentPane().add(jDateChooser2);
        jDateChooser2.setBounds(80, 120, 170, 22);

        jLabel5.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Cuit");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(80, 150, 170, 20);

        jComboBoxInvoiceCuit.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        getContentPane().add(jComboBoxInvoiceCuit);
        jComboBoxInvoiceCuit.setBounds(80, 180, 170, 21);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        open = false;
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (validateSearch()) {
            try {
                String issuerCuit = CuitSelectorUtils.getSelectedCuit(jComboBoxInvoiceCuit);
                if (issuerCuit == null) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar un CUIT emisor", "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Optional<VatBookGenerationResult> result = vatBookController.processPurchases(jDateChooser1.getDate(), jDateChooser2.getDate(), issuerCuit);
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontraron comprobantes de compras en el período indicado.",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                VatBookGenerationResult generationResult = result.get();
                JOptionPane.showMessageDialog(this,
                        "Libro IVA generado en:\n" + generationResult.getVatBookDirectory().toAbsolutePath()
                                + "\nArchivos CITI generados en:\n" + generationResult.getCitiDirectory().toAbsolutePath(),
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(PurchasesVatBookView.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this,
                        ex.getMessage() == null ? "Error al generar el libro IVA." : ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            open = false;
            dispose();
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    

    public boolean validateSearch(){

        boolean valid = true;

        if (!(jDateChooser1.getDate()==null) && !(jDateChooser2.getDate()==null)) {

            if ((jDateChooser1.getDate()==null) && !(jDateChooser2.getDate()==null)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar fecha inicio", "Atención", JOptionPane.WARNING_MESSAGE);
                valid = false;
            }else{
                if (!(jDateChooser1.getDate()==null) && (jDateChooser2.getDate()==null)) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar fecha fin", "Atención", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }
            }
                if ((jDateChooser2.getDate().before(jDateChooser1.getDate()))) {
                    JOptionPane.showMessageDialog(this, "La fecha inicio debe ser anterior a la fecha fin ", "Atención", JOptionPane.WARNING_MESSAGE);
                    valid = false;
                }else{


                    }
            }

        if (CuitSelectorUtils.getSelectedCuit(jComboBoxInvoiceCuit) == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un CUIT emisor", "Atención", JOptionPane.WARNING_MESSAGE);
            valid = false;
        }


   return valid;

    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    public static javax.swing.JComboBox jComboBoxInvoiceCuit;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelFin;
    private javax.swing.JLabel jLabelInicio;
    // End of variables declaration//GEN-END:variables

}
