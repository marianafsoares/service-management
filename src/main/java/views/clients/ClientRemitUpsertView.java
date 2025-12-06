package views.clients;

import controllers.ClientController;
import controllers.ClientRemitController;
import controllers.ClientRemitDetailController;
import controllers.ProductController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientRemitDetailMapper;
import mappers.ClientRemitMapper;
import mappers.ProductMapper;
import models.Client;
import models.ClientRemit;
import models.ClientRemitDetail;
import models.Product;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRemitDetailRepository;
import repositories.ClientRemitRepository;
import repositories.ClientRepository;
import repositories.ProductRepository;
import repositories.impl.ClientRemitDetailRepositoryImpl;
import repositories.impl.ClientRemitRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import services.ClientRemitDetailService;
import services.ClientRemitService;
import services.ClientService;
import services.ProductService;
import utils.DocumentValidator;
import views.MainView;
import views.clients.ClientManagementView;
import views.clients.ClientRemitManagementView;
import views.products.ProductSearchView;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRemitUpsertView extends javax.swing.JInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(ClientRemitUpsertView.class.getName());

    private ClientRemitController remitController;
    private ClientRemitDetailController detailController;
    private ClientController clientController;
    private ProductController productController;
    private SqlSession sqlSession;
    public static boolean isOpen = false;
    private Integer remitId;
    private TableModelListener detailTableModelListener;
    private TableModel detailTableModel;
    private boolean updatingDetailTotals;

    public ClientRemitUpsertView(Integer remitId) {
        this(remitId, null);
    }

    public ClientRemitUpsertView(Client client) {
        this(null, client);
    }

    public ClientRemitUpsertView(Integer remitId, Client client) {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientRemitMapper remitMapper = sqlSession.getMapper(ClientRemitMapper.class);
            ClientRemitDetailMapper detailMapper = sqlSession.getMapper(ClientRemitDetailMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            ClientRemitRepository remitRepository = new ClientRemitRepositoryImpl(remitMapper);
            ClientRemitDetailRepository detailRepository = new ClientRemitDetailRepositoryImpl(detailMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ProductRepository productRepository = new ProductRepositoryImpl(productMapper);
            ClientRemitService remitService = new ClientRemitService(remitRepository);
            ClientRemitDetailService detailService = new ClientRemitDetailService(detailRepository);
            ClientService clientService = new ClientService(clientRepository);
            ProductService productService = new ProductService(productRepository);
            remitController = new ClientRemitController(remitService);
            detailController = new ClientRemitDetailController(detailService);
            clientController = new ClientController(clientService);
            productController = new ProductController(productService);

            initComponents();
            isOpen = true;
            this.remitId = remitId;

            jTable1.setModel(createEmptyModel());
            configureDetailTable();
            jDateChooser1.setDate(new java.util.Date());
            jLabelTotal.setText("0");

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

            if (client != null) {
                if (client.getId() != null) {
                    loadClient(client.getId(), false);
                } else {
                    applyClientToForm(client);
                }
            }

            if (remitId != null) {
                loadRemitData(remitId);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientRemitUpsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadClient(int id) {
        loadClient(id, true);
    }

    private void loadClient(int id, boolean showWarnings) {
        try {
            Client client = clientController.findById(id);
            if (client == null) {
                if (showWarnings) {
                    JOptionPane.showMessageDialog(this, "No se ha encontrado cliente!", "Cliente", JOptionPane.WARNING_MESSAGE);
                }
                clearClientDetails();
                return;
            }
            if (showWarnings && !client.isActive()) {
                JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Cliente", JOptionPane.WARNING_MESSAGE);
            }
            applyClientToForm(client);
        } catch (Exception ex) {
            Logger.getLogger(ClientRemitUpsertView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearClientDetails() {
        jTextFieldCodigo.setText("");
        jLabelCliente.setText("");
        jLabelLocalidad.setText("");
        jLabelDomicilio.setText("");
        jLabelCondicion.setText("");
        jLabelNumeroTipo.setText("");
    }

    private void applyClientToForm(Client client) {
        if (client == null) {
            clearClientDetails();
            return;
        }
        Integer id = client.getId();
        jTextFieldCodigo.setText(id != null ? id.toString() : "");
        String name = client.getFullName();
        jLabelCliente.setText(name != null ? name : "");
        jLabelLocalidad.setText(client.getCity() != null ? client.getCity().getName() : "");
        StringBuilder addressBuilder = new StringBuilder();
        if (client.getAddress() != null && client.getAddress().getName() != null) {
            addressBuilder.append(client.getAddress().getName());
        }
        if (client.getAddressNumber() != null && !client.getAddressNumber().trim().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(" ");
            }
            addressBuilder.append(client.getAddressNumber().trim());
        }
        jLabelDomicilio.setText(addressBuilder.toString());
        jLabelCondicion.setText(client.getTaxCondition() != null ? client.getTaxCondition().getName() : "");
        String documentNumber = client.getDocumentNumber();
        String formattedDocument = DocumentValidator.formatCuit(documentNumber);
        if (client.getDocumentType() != null && !client.getDocumentType().trim().isEmpty()) {
            String type = client.getDocumentType().trim();
            if (formattedDocument != null && !formattedDocument.isEmpty()) {
                jLabelNumeroTipo.setText(type + " " + formattedDocument);
            } else {
                jLabelNumeroTipo.setText(type);
            }
        } else {
            jLabelNumeroTipo.setText(formattedDocument != null ? formattedDocument : "");
        }
    }

    private void loadRemitData(int id) throws Exception {
        ClientRemit remit = remitController.findById(id);
        if (remit != null && remit.getClient() != null) {
            loadClient(remit.getClient().getId(), false);
            if (remit.getRemitDate() != null) {
                jDateChooser1.setDate(java.util.Date.from(remit.getRemitDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            jTextArea1.setText(remit.getDescription());
        }
        List<ClientRemitDetail> details = detailController.findByRemit(id);
        jTable1.setModel(createDetailModel(details));
        configureDetailTable();
        recalculateTotal();
    }

    private DefaultTableModel createEmptyModel() {
        return new DefaultTableModel(new String[]{"Codigo", "Descripcion", "Cantidad", "Precio", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1) {
                    Object codeValue = getValueAt(row, 0);
                    return codeValue != null && "99".equals(codeValue.toString().trim());
                }
                return column == 0 || column == 2 || column == 3;
            }
        };
    }

    private DefaultTableModel createDetailModel(List<ClientRemitDetail> details) {
        DefaultTableModel tm = createEmptyModel();
        Vector row;
        for (ClientRemitDetail d : details) {
            row = new Vector();
            row.add(d.getProductCode());
            row.add(d.getDescription());
            row.add(d.getQuantity());
            row.add(d.getPrice());
            BigDecimal price = d.getPrice() != null ? d.getPrice() : BigDecimal.ZERO;
            Float quantity = d.getQuantity();
            BigDecimal total = quantity != null
                    ? price.multiply(new BigDecimal(Float.toString(quantity)))
                    : BigDecimal.ZERO;
            row.add(total);
            tm.addRow(row);
        }
        return tm;
    }

    private void recalculateTotal() {
        if (jTable1 == null) {
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        updatingDetailTotals = true;
        try {
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                Object qtyObj = jTable1.getValueAt(i, 2);
                Object priceObj = jTable1.getValueAt(i, 3);
                if (qtyObj != null && priceObj != null) {
                    try {
                        BigDecimal qty = new BigDecimal(qtyObj.toString());
                        BigDecimal price = new BigDecimal(priceObj.toString());
                        BigDecimal sub = price.multiply(qty);
                        jTable1.setValueAt(sub, i, 4);
                        total = total.add(sub);
                    } catch (NumberFormatException e) {
                        // ignore invalid numbers
                    }
                }
            }
        } finally {
            updatingDetailTotals = false;
        }
        jLabelTotal.setText(total.toString());
    }

    private void configureDetailTable() {
        if (jTable1 == null) {
            return;
        }
        jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jTable1.setSurrendersFocusOnKeystroke(true);
        installNumericEditor(2);
        installNumericEditor(3);
        attachDetailTableListener();
    }

    private void installNumericEditor(int columnIndex) {
        if (jTable1.getColumnModel().getColumnCount() <= columnIndex) {
            return;
        }
        JTextField field = new JTextField();
        field.setHorizontalAlignment(JTextField.RIGHT);
        DefaultCellEditor editor = new DefaultCellEditor(field);
        editor.setClickCountToStart(1);
        editor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                SwingUtilities.invokeLater(() -> recalculateTotal());
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                // no-op
            }
        });
        TableColumn column = jTable1.getColumnModel().getColumn(columnIndex);
        column.setCellEditor(editor);
    }

    private void attachDetailTableListener() {
        if (jTable1 == null) {
            return;
        }
        if (detailTableModelListener == null) {
            detailTableModelListener = event -> {
                if (updatingDetailTotals) {
                    return;
                }
                int type = event.getType();
                if (type == TableModelEvent.UPDATE) {
                    int column = event.getColumn();
                    if (column == TableModelEvent.ALL_COLUMNS || column == 2 || column == 3) {
                        recalculateTotal();
                    }
                } else if (type == TableModelEvent.INSERT || type == TableModelEvent.DELETE) {
                    recalculateTotal();
                }
            };
        }
        TableModel model = jTable1.getModel();
        if (detailTableModel == model) {
            return;
        }
        if (detailTableModel != null) {
            detailTableModel.removeTableModelListener(detailTableModelListener);
        }
        detailTableModel = model;
        detailTableModel.addTableModelListener(detailTableModelListener);
    }

    private void commitRemitTableEdits() {
        if (jTable1 != null && jTable1.isEditing()) {
            TableCellEditor editor = jTable1.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    private boolean validateRemitRows() {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            Object codeObj = jTable1.getValueAt(i, 0);
            String code = codeObj != null ? codeObj.toString().trim() : "";
            if (code.isEmpty()) {
                continue;
            }
            Object qtyObj = jTable1.getValueAt(i, 2);
            Object priceObj = jTable1.getValueAt(i, 3);
            Object subtotalObj = jTable1.getValueAt(i, 4);
            if (qtyObj == null || priceObj == null || subtotalObj == null) {
                JOptionPane.showMessageDialog(this,
                        "Complete cantidad, precio y subtotal en la fila " + (i + 1),
                        "Guardar",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            try {
                BigDecimal quantity = new BigDecimal(qtyObj.toString());
                BigDecimal price = new BigDecimal(priceObj.toString());
                BigDecimal subtotal = new BigDecimal(subtotalObj.toString());
                if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a cero en la fila " + (i + 1),
                            "Guardar",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                BigDecimal expectedSubtotal = price.multiply(quantity);
                if (!normalize(expectedSubtotal).equals(normalize(subtotal))) {
                    JOptionPane.showMessageDialog(this,
                            "El subtotal no coincide con Cantidad x Precio en la fila " + (i + 1),
                            "Guardar",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Valores numéricos inválidos en la fila " + (i + 1),
                        "Guardar",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : normalized;
    }

    private void handleClientSelectedFromSearch(Client client) {
        if (client == null) {
            return;
        }
        applyClientToForm(client);
        if (client.getId() != null) {
            jTextFieldCodigo.setText(client.getId().toString());
        }
        jTextFieldCodigo.requestFocusInWindow();
    }

    private boolean addProductToRemit(Product product) {
        if (product == null) {
            return false;
        }
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        String code = product.getCode() != null ? product.getCode().trim() : "";
        if (!code.isEmpty()) {
            int existingRow = findProductRow(model, code);
            if (existingRow >= 0) {
                JOptionPane.showMessageDialog(this,
                        "El artículo ya se encuentra en el remito. Modifique la cantidad manualmente si es necesario.",
                        "Artículo duplicado",
                        JOptionPane.INFORMATION_MESSAGE);
                jTable1.changeSelection(existingRow, 2, false, false);
                jTable1.requestFocusInWindow();
                return true;
            }
        }

        Vector<Object> row = new Vector<>();
        row.add(product.getCode());
        row.add(product.getDescription());
        row.add(Float.valueOf(1f));
        BigDecimal price = product.getCashPrice() != null ? product.getCashPrice() : BigDecimal.ZERO;
        row.add(price);
        row.add(price);
        model.addRow(row);
        int newRow = model.getRowCount() - 1;
        if (newRow >= 0) {
            jTable1.changeSelection(newRow, 2, false, false);
            jTable1.requestFocusInWindow();
        }
        recalculateTotal();
        return true;
    }

    private int findProductRow(DefaultTableModel model, String code) {
        if (model == null || code == null) {
            return -1;
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            Object rowCodeObj = model.getValueAt(i, 0);
            if (rowCodeObj == null) {
                continue;
            }
            String rowCode = rowCodeObj.toString().trim();
            if (code.equalsIgnoreCase(rowCode)) {
                return i;
            }
        }
        return -1;
    }

    private Float parseFloat(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void addEmptyRow() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        Vector<Object> row = new Vector<>();
        row.add("99");
        row.add("");
        row.add(Float.valueOf(1f));
        row.add(BigDecimal.ZERO);
        row.add(BigDecimal.ZERO);
        model.addRow(row);
        int newRow = model.getRowCount() - 1;
        if (newRow >= 0) {
            jTable1.changeSelection(newRow, 1, false, false);
            jTable1.requestFocusInWindow();
        }
    }

    private void handleProductSelectedFromSearch(Product product) {
        if (addProductToRemit(product)) {
            jTextField1.setText("");
            jTextField1.requestFocusInWindow();
        }
    }

    private void searchProductByCode(String code) {
        if (code == null) {
            return;
        }
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if ("99".equals(trimmed)) {
            addEmptyRow();
            jTextField1.setText("");
            return;
        }
        try {
            Product product = productController.findByCode(trimmed);
            if (product == null) {
                JOptionPane.showMessageDialog(this, "El artículo no existe", "Producto", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            handleProductSelectedFromSearch(product);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo buscar el artículo", "Producto", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedRow() {
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            ((DefaultTableModel) jTable1.getModel()).removeRow(row);
            recalculateTotal();
        }
    }

    private void restoreStockForDetails(List<ClientRemitDetail> details) {
        if (details == null || productController == null) {
            return;
        }
        for (ClientRemitDetail detail : details) {
            adjustStockForDetail(detail, true);
        }
    }

    private boolean adjustStockForDetail(ClientRemitDetail detail, boolean increase) {
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
            if (increase) {
                productController.increaseStock(trimmedCode, quantity);
                return true;
            }
            return productController.decreaseStock(trimmedCode, quantity);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al ajustar el stock para el artículo {0}", trimmedCode);
            LOGGER.log(Level.FINE, "Detalle del error", ex);
            return false;
        }
    }

    private void saveRemit() {
        try {
            commitRemitTableEdits();
            String clientIdValue = jTextFieldCodigo.getText() != null ? jTextFieldCodigo.getText().trim() : "";
            if (clientIdValue.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ClientRemit remit = new ClientRemit();
            if (remitId != null) remit.setId(remitId);
            int clientId = Integer.parseInt(clientIdValue);
            Client client = clientController.findById(clientId);
            if (client == null) {
                JOptionPane.showMessageDialog(this, "El cliente seleccionado ya no existe", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!client.isActive()) {
                JOptionPane.showMessageDialog(this, "El cliente está deshabilitado", "Guardar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            remit.setClient(client);
            remit.setRemitDate(jDateChooser1.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            remit.setDescription(jTextArea1.getText());
            String totalValue = jLabelTotal.getText() != null ? jLabelTotal.getText().trim() : "";
            remit.setTotal(new BigDecimal(totalValue.isEmpty() ? "0" : totalValue));

            if (!validateRemitRows()) {
                return;
            }

            List<ClientRemitDetail> previousDetails = Collections.emptyList();
            if (remitId != null) {
                previousDetails = detailController.findByRemit(remitId);
            }

            if (remitId == null) {
                remitController.save(remit);
                remitId = remit.getId();
            } else {
                remitController.update(remit);
                restoreStockForDetails(previousDetails);
                detailController.deleteByRemit(remitId);
            }

            List<String> stockWarnings = new ArrayList<>();
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                Object codeObj = jTable1.getValueAt(i, 0);
                Object quantityObj = jTable1.getValueAt(i, 2);
                Object priceObj = jTable1.getValueAt(i, 3);
                Object subtotalObj = jTable1.getValueAt(i, 4);

                if (codeObj == null || quantityObj == null || priceObj == null || subtotalObj == null) {
                    continue;
                }

                String code = codeObj.toString().trim();
                if (code.isEmpty()) {
                    continue;
                }

                ClientRemitDetail d = new ClientRemitDetail();
                d.setRemit(remit);
                d.setProductCode(code);
                d.setDescription(String.valueOf(jTable1.getValueAt(i, 1)));
                BigDecimal quantity = new BigDecimal(quantityObj.toString());
                d.setQuantity(quantity.floatValue());
                BigDecimal price = new BigDecimal(priceObj.toString());
                d.setPrice(price);
                detailController.save(d);

                if (!adjustStockForDetail(d, false)) {
                    stockWarnings.add(code);
                }
            }

            if (!stockWarnings.isEmpty()) {
                String articles = String.join(", ", stockWarnings);
                JOptionPane.showMessageDialog(this,
                        "No se pudo descontar stock para los artículos: " + articles,
                        "Stock",
                        JOptionPane.WARNING_MESSAGE);
            }

            JOptionPane.showMessageDialog(this, "Remito guardado", "Guardar", JOptionPane.INFORMATION_MESSAGE);
            ClientRemitManagementView.refreshTable();
            dispose();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar remito", ex);
            JOptionPane.showMessageDialog(this, "Error al guardar", "Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        recalculateTotal();
        saveRemit();
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        int option = JOptionPane.showConfirmDialog(this,
                "Se descartarán los cambios del remito. ¿Desea continuar?",
                "Cancelar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
        removeSelectedRow();
    }

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        recalculateTotal();
    }

    @Override
    public void dispose() {
        isOpen = false;
        if (sqlSession != null) {
            sqlSession.close();
            sqlSession = null;
        }
        super.dispose();
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!ClientManagementView.isOpen) {
            ClientManagementView view = new ClientManagementView();
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
        ClientManagementView.setClientSelectionListener(this::handleClientSelectedFromSearch, true);
        ClientManagementView.bringToFront();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (productController == null) {
            JOptionPane.showMessageDialog(this, "No se pudo inicializar la búsqueda de artículos", "Producto", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ProductSearchView.isOpen) {
            ProductSearchView view = new ProductSearchView(productController, this::handleProductSelectedFromSearch);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        } else {
            ProductSearchView.updateSelectionListener(this::handleProductSelectedFromSearch);
            ProductSearchView.bringToFront();
        }
    }

    private void jTextFieldCodigoActionPerformed(java.awt.event.ActionEvent evt) {
        String value = jTextFieldCodigo.getText();
        if (value == null || value.trim().isEmpty()) {
            clearClientDetails();
            return;
        }
        try {
            int clientId = Integer.parseInt(value.trim());
            loadClient(clientId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El código de cliente no es válido", "Cliente", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void jTextFieldCodigoKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextFieldCodigoActionPerformed(null);
        }
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
        searchProductByCode(jTextField1.getText());
    }

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedRow();
        }
    }

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {
        recalculateTotal();
    }

    private void jTable1KeyTyped(java.awt.event.KeyEvent evt) {
        SwingUtilities.invokeLater(this::recalculateTotal);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jTextFieldCodigo = new javax.swing.JTextField();
        jLabelCliente = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelDomicilio = new javax.swing.JLabel();
        jLabelLocalidad = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jLabelCondicion = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabelNumeroTipo = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(850, 630));
        setSize(new java.awt.Dimension(850, 630));
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Cod Cliente");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(40, 20, 125, 20);

        jTextFieldCodigo.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTextFieldCodigo.setPreferredSize(new java.awt.Dimension(40, 20));
        jTextFieldCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCodigoActionPerformed(evt);
            }
        });
        jTextFieldCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCodigoKeyPressed(evt);
            }
        });
        getContentPane().add(jTextFieldCodigo);
        jTextFieldCodigo.setBounds(190, 20, 40, 20);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(240, 20, 25, 25);

        jLabelCliente.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        jLabelCliente.setMinimumSize(new java.awt.Dimension(270, 20));
        jLabelCliente.setPreferredSize(new java.awt.Dimension(270, 20));
        getContentPane().add(jLabelCliente);
        jLabelCliente.setBounds(280, 20, 270, 20);

        jLabel7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Localidad");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(40, 50, 125, 20);

        jLabelLocalidad.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelLocalidad);
        jLabelLocalidad.setBounds(190, 50, 150, 20);

        jLabel4.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Domicilio");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 80, 125, 20);

        jLabelDomicilio.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelDomicilio);
        jLabelDomicilio.setBounds(190, 80, 150, 20);

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Dni/Cuit");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(40, 110, 125, 20);

        jLabelNumeroTipo.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelNumeroTipo);
        jLabelNumeroTipo.setBounds(190, 110, 150, 20);

        jLabel11.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Condicion");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(40, 140, 125, 20);

        jLabelCondicion.setFont(new java.awt.Font("Calibri", 3, 12)); // NOI18N
        getContentPane().add(jLabelCondicion);
        jLabelCondicion.setBounds(190, 140, 150, 20);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Fecha");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(340, 50, 125, 20);

        jDateChooser1.setPreferredSize(new java.awt.Dimension(120, 22));
        getContentPane().add(jDateChooser1);
        jDateChooser1.setBounds(480, 50, 120, 22);

        jLabel13.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Buscar Articulo");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(30, 210, 125, 20);

        jTextField1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(170, 210, 340, 22);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(520, 210, 25, 25);

        jTable1.setFont(new java.awt.Font("Calibri", 0, 11)); // NOI18N
        jTable1.setModel(createEmptyModel());
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTable1KeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(30, 250, 760, 130);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(30, 390, 760, 80);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(null);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Total:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(40, 20, 70, 20);

        jLabelTotal.setFont(new java.awt.Font("Calibri", 3, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(0, 99, 51));
        jPanel2.add(jLabelTotal);
        jLabelTotal.setBounds(120, 20, 180, 20);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(460, 500, 310, 60);

        jButton9.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar item.png"))); // NOI18N
        jButton9.setText("Borrar Item");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton9);
        jButton9.setBounds(110, 500, 130, 30);

        jButton10.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/borrar items.png"))); // NOI18N
        jButton10.setText("Borrar todo");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton10);
        jButton10.setBounds(250, 500, 130, 30);

        jButton7.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ok.png"))); // NOI18N
        jButton7.setText("Guardar");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7);
        jButton7.setBounds(110, 540, 130, 30);

        jButton8.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cancelar.png"))); // NOI18N
        jButton8.setText("Cancelar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);
        jButton8.setBounds(250, 540, 130, 30);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButton10;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelCliente;
    private javax.swing.JLabel jLabelCondicion;
    private javax.swing.JLabel jLabelDomicilio;
    private javax.swing.JLabel jLabelLocalidad;
    private javax.swing.JLabel jLabelNumeroTipo;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldCodigo;
    // End of variables declaration//GEN-END:variables
}

