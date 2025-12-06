
package views.providers;


import controllers.ProviderExpenseController;
import services.ProviderExpenseService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import configs.MyBatisConfig;
import mappers.ProviderExpenseMapper;
import org.apache.ibatis.session.SqlSession;
import repositories.ProviderExpenseRepository;
import repositories.impl.ProviderExpenseRepositoryImpl;

public class ProviderExpensesListView extends javax.swing.JInternalFrame {

    public static boolean open=false;
    private final ProviderExpenseController providerExpenseController;

    public ProviderExpensesListView() {
        SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession(true);
        ProviderExpenseMapper mapper = session.getMapper(ProviderExpenseMapper.class);
        ProviderExpenseRepository repository = new ProviderExpenseRepositoryImpl(mapper);
        ProviderExpenseService service = new ProviderExpenseService(repository);
        providerExpenseController = new ProviderExpenseController(service);
        initComponents();
        open = true;
        loadCategories();

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
        jLabelFin1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setTitle("Listado Gastos");
        setMinimumSize(new java.awt.Dimension(300, 250));
        setPreferredSize(new java.awt.Dimension(300, 250));
        getContentPane().setLayout(null);

        jLabelFin.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelFin.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelFin.setText("Categoría:");
        getContentPane().add(jLabelFin);
        jLabelFin.setBounds(30, 100, 110, 30);

        jLabelInicio.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelInicio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelInicio.setText("Fecha Inicio:");
        jLabelInicio.setMaximumSize(new java.awt.Dimension(180, 180));
        jLabelInicio.setMinimumSize(new java.awt.Dimension(180, 180));
        jLabelInicio.setPreferredSize(new java.awt.Dimension(180, 180));
        getContentPane().add(jLabelInicio);
        jLabelInicio.setBounds(30, 20, 110, 30);

        jButton1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/guardar.png"))); // NOI18N
        jButton1.setText("Procesar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(80, 170, 110, 30);

        jButton2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButton2.setText("Salir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(200, 170, 110, 30);
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(150, 20, 170, 30);
        getContentPane().add(jDateChooser2);
        jDateChooser2.setBounds(150, 60, 170, 30);

        jLabelFin1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabelFin1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelFin1.setText("Fecha Fin:");
        getContentPane().add(jLabelFin1);
        jLabelFin1.setBounds(30, 60, 110, 30);

        jComboBox1.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setToolTipText("");
        getContentPane().add(jComboBox1);
        jComboBox1.setBounds(150, 100, 170, 30);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void loadCategories(){
        List<String> categories = providerExpenseController.findCategories();
        jComboBox1.removeAllItems();
        for (String category : categories) {
            jComboBox1.addItem(category);
        }
    }
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        open = false;
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        boolean valid = validateSearch();
        if (valid) {
            try {
                String cat = jComboBox1.getSelectedItem().toString();
                providerExpenseController.process(jDateChooser1.getDate(), jDateChooser2.getDate(), Integer.parseInt(cat));
                open = false;
                dispose();
            } catch (Exception ex) {
                Logger.getLogger(ProviderExpensesListView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed



    public boolean validateSearch(){

        boolean valid = true;

        if (!(jDateChooser1.getDate()==null) && !(jDateChooser2.getDate()==null)) {

            if ((jDateChooser1.getDate()==null) && !(jDateChooser2.getDate()==null)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar fecha inicio", "Atención", JOptionPane.WARNING_MESSAGE);
                valid= false;
            }else{
                if (!(jDateChooser1.getDate()==null) && (jDateChooser2.getDate()==null)) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar fecha fin", "Atención", JOptionPane.WARNING_MESSAGE);
                    valid= false;
                }
            }
                if ((jDateChooser2.getDate().before(jDateChooser1.getDate()))) {
                    JOptionPane.showMessageDialog(this, "La fecha inicio debe ser anterior a la fecha fin ", "Atención", JOptionPane.WARNING_MESSAGE);
                    valid= false;
                }else{


                    }
            }


   return valid;

    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabelFin;
    private javax.swing.JLabel jLabelFin1;
    private javax.swing.JLabel jLabelInicio;
    // End of variables declaration//GEN-END:variables

}
