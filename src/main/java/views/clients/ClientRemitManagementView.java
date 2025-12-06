package views.clients;

import controllers.ClientController;
import controllers.ClientRemitController;
import controllers.ClientRemitDetailController;
import controllers.ProductController;
import configs.MyBatisConfig;
import mappers.ClientMapper;
import mappers.ClientRemitMapper;
import mappers.ClientRemitDetailMapper;
import mappers.ProductMapper;
import models.Client;
import models.ClientRemit;
import models.ClientRemitDetail;
import models.Product;
import org.apache.ibatis.session.SqlSession;
import repositories.ClientRemitRepository;
import repositories.ClientRepository;
import repositories.ClientRemitDetailRepository;
import repositories.ProductRepository;
import repositories.impl.ClientRemitRepositoryImpl;
import repositories.impl.ClientRepositoryImpl;
import repositories.impl.ClientRemitDetailRepositoryImpl;
import repositories.impl.ProductRepositoryImpl;
import services.ClientRemitService;
import services.ClientService;
import services.ClientRemitDetailService;
import services.ProductService;
import services.reports.ClientRemitPrintService;
import services.reports.ClientRemitPrintService.RemitPrintException;
import views.MainView;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRemitManagementView extends javax.swing.JInternalFrame {

    private static final Logger LOGGER = Logger.getLogger(ClientRemitManagementView.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private enum RemitFilter { OPEN, CLOSED }

    private ClientRemitController remitController;
    private ClientRemitDetailController detailController;
    private ClientController clientController;
    private ProductController productController;
    private SqlSession sqlSession;
    private final ClientRemitPrintService remitPrintService = new ClientRemitPrintService();
    public static boolean isOpen = false;
    public static List<ClientRemit> remits;
    private static ClientRemitManagementView activeInstance;
    private RemitFilter currentFilter = RemitFilter.OPEN;
    private Client selectedClient;
    private static RemitInvoiceContext pendingInvoiceContext;
    private javax.swing.ButtonGroup remitFilterButtonGroup;

    public ClientRemitManagementView() {
        try {
            sqlSession = MyBatisConfig.getSqlSessionFactory().openSession(true);
            ClientRemitMapper remitMapper = sqlSession.getMapper(ClientRemitMapper.class);
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);
            ClientRemitDetailMapper detailMapper = sqlSession.getMapper(ClientRemitDetailMapper.class);
            ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);
            ClientRemitRepository remitRepository = new ClientRemitRepositoryImpl(remitMapper);
            ClientRepository clientRepository = new ClientRepositoryImpl(clientMapper);
            ClientRemitDetailRepository detailRepository = new ClientRemitDetailRepositoryImpl(detailMapper);
            ProductRepository productRepository = new ProductRepositoryImpl(productMapper);
            ClientRemitService remitService = new ClientRemitService(remitRepository);
            ClientService clientService = new ClientService(clientRepository);
            ClientRemitDetailService detailService = new ClientRemitDetailService(detailRepository);
            ProductService productService = new ProductService(productRepository);
            remitController = new ClientRemitController(remitService);
            detailController = new ClientRemitDetailController(detailService);
            clientController = new ClientController(clientService);
            productController = new ProductController(productService);
            initComponents();
            remitFilterButtonGroup = new javax.swing.ButtonGroup();
            remitFilterButtonGroup.add(jRadioOpen);
            remitFilterButtonGroup.add(jRadioClosed);
            jRadioOpen.setSelected(true);
            isOpen = true;
            activeInstance = this;
            addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                }

                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    isOpen = false;
                    activeInstance = null;
                }
            });
            remits = Collections.emptyList();
            updateClientLabels();
            currentFilter = RemitFilter.OPEN;
            updateFilterButtons(currentFilter);
            reloadRemits();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void updateTable(List<ClientRemit> list) {
        List<ClientRemit> safeList = list != null ? list : Collections.emptyList();
        jTable1.setModel(createTableModel(safeList));
        jTable1.setRowSelectionAllowed(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.revalidate();
        jTable1.repaint();
    }

    private void reloadRemits() {
        clearSessionCache();
        RemitFilter filter = currentFilter != null ? currentFilter : RemitFilter.OPEN;
        updateFilterButtons(filter);
        loadRemits(filter);
    }

    private void clearSessionCache() {
        if (sqlSession != null) {
            sqlSession.clearCache();
        }
    }

    private void loadRemits(RemitFilter filter) {
        updateFilterButtons(filter);
        List<ClientRemit> loaded = Collections.emptyList();
        if (remitController != null) {
            try {
                if (selectedClient != null && selectedClient.getId() != null) {
                    loaded = remitController.findByClient(selectedClient.getId());
                } else {
                    loaded = remitController.findAll();
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "No se pudieron cargar los remitos", ex);
                loaded = Collections.emptyList();
            }
        }
        List<ClientRemit> filtered = filterRemits(loaded, filter);
        remits = filtered;
        currentFilter = filter;
        updateTable(filtered);
    }

    private List<ClientRemit> filterRemits(List<ClientRemit> source, RemitFilter filter) {
        List<ClientRemit> safeSource = source != null ? source : Collections.emptyList();
        if (filter == null) {
            return new ArrayList<>(safeSource);
        }
        List<ClientRemit> result = new ArrayList<>();
        boolean closedFilter = filter == RemitFilter.CLOSED;
        for (ClientRemit remit : safeSource) {
            if (remit == null) {
                continue;
            }
            Boolean closed = remit.getClosed();
            if (closedFilter) {
                if (Boolean.TRUE.equals(closed)) {
                    result.add(remit);
                }
            } else if (!Boolean.TRUE.equals(closed)) {
                result.add(remit);
            }
        }
        return result;
    }

    private void updateClientLabels() {
        if (jLabelCodCliente == null || jLabel2 == null) {
            return;
        }
        if (selectedClient == null) {
            jLabelCodCliente.setText("");
            jLabel2.setText("");
            return;
        }
        Integer id = selectedClient.getId();
        jLabelCodCliente.setText(id != null ? id.toString() : "");
        String name = selectedClient.getFullName();
        jLabel2.setText(name != null ? name : "");
    }

    public void setSelectedClient(Client client) {
        selectedClient = client;
        updateClientLabels();
        reloadRemits();
    }

    private void bringToFrontInternal() {
        try {
            if (isIcon()) {
                setIcon(false);
            }
            setSelected(true);
        } catch (PropertyVetoException ex) {
            LOGGER.log(Level.FINE, "No se pudo seleccionar la vista de remitos", ex);
        }
        toFront();
        requestFocus();
    }

    public static void bringToFront() {
        ClientRemitManagementView instance = activeInstance;
        if (instance == null) {
            return;
        }
        SwingUtilities.invokeLater(instance::bringToFrontInternal);
    }

    public static void setActiveClient(Client client) {
        ClientRemitManagementView instance = activeInstance;
        if (instance == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            instance.setSelectedClient(client);
            instance.bringToFrontInternal();
        });
    }

    private void restoreStock(List<ClientRemitDetail> details) {
        if (details == null || productController == null) {
            return;
        }
        for (ClientRemitDetail detail : details) {
            if (detail == null) {
                continue;
            }
            String code = detail.getProductCode();
            if (code == null) {
                continue;
            }
            String trimmedCode = code.trim();
            if (trimmedCode.isEmpty() || "99".equals(trimmedCode)) {
                continue;
            }
            Float quantity = detail.getQuantity();
            if (quantity == null || quantity <= 0f) {
                continue;
            }
            try {
                productController.increaseStock(trimmedCode, quantity);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE,
                        "Error al devolver stock para el artículo {0}", trimmedCode);
                LOGGER.log(Level.FINE, null, ex);
            }
        }
    }

    private DefaultTableModel createTableModel(List<ClientRemit> list) {
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"Codigo", "Fecha", "Cantidad de Articulos", "Total", "Cerrada"}, 0) {
            private final Class<?>[] columnTypes = new Class[]{Integer.class, Object.class, Integer.class, Float.class, Boolean.class};
            private final boolean[] canEdit = new boolean[]{false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };

        List<ClientRemit> safeList = list != null ? list : Collections.emptyList();
        for (ClientRemit remit : safeList) {
            if (remit == null) {
                continue;
            }
            Vector<Object> row = new Vector<>();
            row.add(remit.getId());
            row.add(formatRemitDate(remit.getRemitDate()));
            row.add(calculateArticleCount(remit));
            row.add(remit.getTotal() != null ? remit.getTotal().floatValue() : null);
            row.add(Boolean.TRUE.equals(remit.getClosed()));
            tm.addRow(row);
        }
        return tm;
    }

    private String formatRemitDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private int calculateArticleCount(ClientRemit remit) {
        List<ClientRemitDetail> details = loadRemitDetails(remit);
        int total = 0;
        for (ClientRemitDetail detail : details) {
            if (detail == null) {
                continue;
            }
            Float quantity = detail.getQuantity();
            if (quantity != null) {
                total += Math.round(quantity);
            }
        }
        return total;
    }

    private List<ClientRemitDetail> loadRemitDetails(ClientRemit remit) {
        if (remit == null || remit.getId() == null) {
            return Collections.emptyList();
        }
        List<ClientRemitDetail> details = remit.getDetails();
        if (details != null && !details.isEmpty()) {
            return details;
        }
        if (detailController == null) {
            return Collections.emptyList();
        }
        clearSessionCache();
        try {
            List<ClientRemitDetail> loadedDetails = detailController.findByRemit(remit.getId());
            return loadedDetails != null ? loadedDetails : Collections.emptyList();
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudieron cargar los detalles del remito {0}", remit.getId());
            LOGGER.log(Level.FINE, null, ex);
            return Collections.emptyList();
        }
    }

    private void updateFilterButtons(RemitFilter filter) {
        if (filter == RemitFilter.CLOSED) {
            jRadioClosed.setSelected(true);
        } else {
            jRadioOpen.setSelected(true);
        }
    }

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
        if (!ClientRemitUpsertView.isOpen) {
            ClientRemitUpsertView view = selectedClient != null
                    ? new ClientRemitUpsertView(selectedClient)
                    : new ClientRemitUpsertView((Integer) null);
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        int i = jTable1.getSelectedRow();
        if (i == -1 || remits == null || i >= remits.size()) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun remito", "Modificar", JOptionPane.OK_OPTION);
            return;
        }
        ClientRemit remit = remits.get(i);
        if (!ClientRemitUpsertView.isOpen) {
            ClientRemitUpsertView view = new ClientRemitUpsertView(remit.getId());
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        int i = jTable1.getSelectedRow();
        if (i == -1 || remits == null || i >= remits.size()) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun remito", "Eliminar", JOptionPane.OK_OPTION);
            return;
        }
        ClientRemit remit = remits.get(i);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Esta seguro que desea eliminar el remito seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<ClientRemitDetail> details = Collections.emptyList();
            if (detailController != null) {
                try {
                    details = detailController.findByRemit(remit.getId());
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudieron cargar los detalles del remito a eliminar", ex);
                }
            }
            restoreStock(details);
            boolean detailsDeleted = true;
            if (detailController != null) {
                try {
                    detailController.deleteByRemit(remit.getId());
                } catch (Exception ex) {
                    detailsDeleted = false;
                    LOGGER.log(Level.WARNING, "No se pudieron eliminar los detalles del remito {0}", remit.getId());
                    LOGGER.log(Level.FINE, null, ex);
                    JOptionPane.showMessageDialog(this,
                            "No se pudieron eliminar los detalles del remito.",
                            "Eliminar",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if (detailsDeleted && remitController != null) {
                try {
                    remitController.delete(remit.getId());
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "No se pudo eliminar el remito {0}", remit.getId());
                    LOGGER.log(Level.FINE, null, ex);
                    JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar el remito seleccionado.",
                            "Eliminar",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            reloadRemits();
        }
    }

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {
        int i = jTable1.getSelectedRow();
        if (i == -1 || remits == null || i >= remits.size()) {
            JOptionPane.showMessageDialog(this, "No ha seleccionado ningun remito", "Ver", JOptionPane.OK_OPTION);
            return;
        }
        ClientRemit remit = remits.get(i);
        if (!ClientRemitDetailView.isOpen) {
            ClientRemitDetailView view = new ClientRemitDetailView(remit.getId());
            MainView.jDesktopPane1.add(view);
            view.setVisible(true);
        }
    }

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {
        List<ClientRemit> remitsToProcess = getRemitsMarkedForClosing();
        if (remitsToProcess.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar al menos un remito abierto para cerrar.",
                    "Cerrar",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        handleRemitClosure(remitsToProcess);
    }

    private void handleRemitClosure(List<ClientRemit> remitsToProcess) {
        Object[] options = {"Facturar", "Solo cerrar", "Cancelar"};
        int choice = JOptionPane.showOptionDialog(this,
                "¿Qué desea hacer con los remitos seleccionados?",
                "Cerrar remitos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.CLOSED_OPTION || choice == 2) {
            return;
        }

        if (choice == 0) {
            RemitInvoiceContext context = buildInvoiceContext(remitsToProcess);
            if (context == null) {
                return;
            }
            if (!closeRemits(remitsToProcess)) {
                JOptionPane.showMessageDialog(this,
                        "No se pudieron cerrar todos los remitos seleccionados.",
                        "Cerrar",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            pendingInvoiceContext = context;
            openInvoiceForRemits();
            reloadRemits();
            return;
        }

        boolean closed = closeRemits(remitsToProcess);
        if (closed) {
            JOptionPane.showMessageDialog(this,
                    "Los remitos seleccionados se cerraron correctamente.",
                    "Cerrar",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cerrar todos los remitos seleccionados.",
                    "Cerrar",
                    JOptionPane.WARNING_MESSAGE);
        }
        reloadRemits();
    }

    private List<ClientRemit> getRemitsMarkedForClosing() {
        if (remits == null || jTable1 == null) {
            return new ArrayList<>();
        }
        Map<Integer, ClientRemit> unique = new LinkedHashMap<>();
        int rowCount = jTable1.getRowCount();
        for (int row = 0; row < rowCount; row++) {
            Object value = jTable1.getValueAt(row, 4);
            if (!(value instanceof Boolean) || !((Boolean) value)) {
                continue;
            }
            if (row >= remits.size()) {
                continue;
            }
            ClientRemit remit = remits.get(row);
            if (remit == null || remit.getId() == null || Boolean.TRUE.equals(remit.getClosed())) {
                continue;
            }
            unique.put(remit.getId(), remit);
        }
        return new ArrayList<>(unique.values());
    }

    private boolean closeRemits(List<ClientRemit> remitsToClose) {
        if (remitsToClose == null || remitsToClose.isEmpty()) {
            return true;
        }
        boolean success = true;
        for (ClientRemit remit : remitsToClose) {
            if (remit == null || remit.getId() == null) {
                continue;
            }
            try {
                remit.setClosed(true);
                remitController.update(remit);
            } catch (Exception ex) {
                success = false;
                LOGGER.log(Level.WARNING, "No se pudo cerrar el remito {0}", remit.getId());
                LOGGER.log(Level.FINE, null, ex);
            }
        }
        return success;
    }

    private RemitInvoiceContext buildInvoiceContext(List<ClientRemit> remitsToInvoice) {
        if (remitsToInvoice == null || remitsToInvoice.isEmpty()) {
            return null;
        }

        Map<String, ClientRemitDetail> aggregated = new LinkedHashMap<>();
        List<ClientRemitDetail> manualDetails = new ArrayList<>();
        Client client = null;

        for (ClientRemit remit : remitsToInvoice) {
            if (remit == null || remit.getId() == null) {
                continue;
            }
            Client resolvedClient = ensureClientLoaded(remit);
            if (resolvedClient == null || resolvedClient.getId() == null) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo cargar el cliente del remito " + remit.getId(),
                        "Cerrar",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (client == null) {
                client = resolvedClient;
            } else if (!Objects.equals(client.getId(), resolvedClient.getId())) {
                JOptionPane.showMessageDialog(this,
                        "Los remitos seleccionados pertenecen a distintos clientes.",
                        "Cerrar",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            List<ClientRemitDetail> details = loadRemitDetails(remit);
            for (ClientRemitDetail detail : details) {
                if (detail == null) {
                    continue;
                }
                float quantity = safeQuantity(detail.getQuantity());
                if (quantity <= 0f) {
                    continue;
                }
                String code = detail.getProductCode();
                String trimmedCode = code != null ? code.trim() : "";
                if (trimmedCode.isEmpty()) {
                    continue;
                }
                if ("99".equals(trimmedCode)) {
                    manualDetails.add(copyManualDetail(detail));
                    continue;
                }

                ClientRemitDetail aggregatedDetail = aggregated.get(trimmedCode);
                if (aggregatedDetail == null) {
                    aggregatedDetail = new ClientRemitDetail();
                    aggregatedDetail.setProductCode(trimmedCode);
                    Product product = loadProduct(trimmedCode);
                    aggregatedDetail.setDescription(resolveDescription(product, detail));
                    aggregatedDetail.setPrice(resolvePrice(product, detail));
                    aggregatedDetail.setQuantity(0f);
                    aggregated.put(trimmedCode, aggregatedDetail);
                }

                Float current = aggregatedDetail.getQuantity();
                float updated = (current != null ? current : 0f) + quantity;
                aggregatedDetail.setQuantity(updated);
            }
        }

        List<ClientRemitDetail> combined = new ArrayList<>(aggregated.values());
        combined.addAll(manualDetails);
        if (combined.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Los remitos seleccionados no contienen artículos para facturar.",
                    "Cerrar",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new RemitInvoiceContext(client, combined);
    }

    private Client ensureClientLoaded(ClientRemit remit) {
        if (remit == null) {
            return null;
        }
        Client client = remit.getClient();
        if (client != null && client.getId() != null) {
            return client;
        }
        try {
            ClientRemit refreshed = remitController.findById(remit.getId());
            return refreshed != null ? refreshed.getClient() : null;
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo recargar el remito {0}", remit.getId());
            LOGGER.log(Level.FINE, null, ex);
            return null;
        }
    }

    private Product loadProduct(String code) {
        if (productController == null || code == null || code.trim().isEmpty()) {
            return null;
        }
        try {
            return productController.findByCode(code);
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "No se pudo cargar el artículo {0}", code);
            LOGGER.log(Level.FINE, null, ex);
            return null;
        }
    }

    private String resolveDescription(Product product, ClientRemitDetail detail) {
        if (product != null && product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
            return product.getDescription();
        }
        String description = detail != null ? detail.getDescription() : null;
        return description != null ? description : "";
    }

    private BigDecimal resolvePrice(Product product, ClientRemitDetail detail) {
        if (product != null && product.getCashPrice() != null) {
            return product.getCashPrice();
        }
        if (detail != null && detail.getPrice() != null) {
            return detail.getPrice();
        }
        return BigDecimal.ZERO;
    }

    private ClientRemitDetail copyManualDetail(ClientRemitDetail detail) {
        ClientRemitDetail copy = new ClientRemitDetail();
        if (detail != null) {
            copy.setProductCode(detail.getProductCode());
            copy.setDescription(detail.getDescription());
            copy.setQuantity(detail.getQuantity());
            copy.setPrice(resolvePrice(null, detail));
        }
        return copy;
    }

    private float safeQuantity(Float value) {
        return value != null ? value : 0f;
    }

    private void openInvoiceForRemits() {
        if (!ClientInvoiceInsertView.isOpen) {
            try {
                ClientInvoiceInsertView view = new ClientInvoiceInsertView();
                MainView.jDesktopPane1.add(view);
                view.setVisible(true);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "No se pudo abrir la facturación para los remitos seleccionados", ex);
                JOptionPane.showMessageDialog(this,
                        "No se pudo abrir la facturación para los remitos seleccionados.",
                        "Facturar",
                        JOptionPane.ERROR_MESSAGE);
                pendingInvoiceContext = null;
            }
        } else {
            ClientInvoiceInsertView.reloadRemitContext();
        }
    }

    static RemitInvoiceContext consumePendingInvoiceContext() {
        RemitInvoiceContext context = pendingInvoiceContext;
        pendingInvoiceContext = null;
        return context;
    }

    static final class RemitInvoiceContext {
        private final Client client;
        private final List<ClientRemitDetail> details;

        RemitInvoiceContext(Client client, List<ClientRemitDetail> details) {
            this.client = client;
            this.details = details != null
                    ? Collections.unmodifiableList(new ArrayList<>(details))
                    : Collections.emptyList();
        }

        Client getClient() {
            return client;
        }

        List<ClientRemitDetail> getDetails() {
            return details;
        }
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        isOpen = false;
        dispose();
    }

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        reloadRemits();
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        reloadRemits();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        // No action required
    }

    public static void refreshTable() {
        ClientRemitManagementView instance = activeInstance;
        if (instance == null) {
            return;
        }
        SwingUtilities.invokeLater(instance::reloadRemits);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonUpdate = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonDetail = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabelCodCliente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jRadioClosed = new javax.swing.JRadioButton();
        jRadioOpen = new javax.swing.JRadioButton();

        setMinimumSize(new java.awt.Dimension(866, 482));
        setPreferredSize(new java.awt.Dimension(800, 550));
        getContentPane().setLayout(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Fecha", "Cantidad de Articulos", "Total", "Cerrada"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(70, 150, 670, 260);

        jButtonUpdate.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/modificar.png"))); // NOI18N
        jButtonUpdate.setText("Modificar");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonUpdate);
        jButtonUpdate.setBounds(180, 30, 130, 30);

        jButtonAdd.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonAdd.setForeground(new java.awt.Color(51, 51, 51));
        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/agregar.png"))); // NOI18N
        jButtonAdd.setText("Agregar");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonAdd);
        jButtonAdd.setBounds(40, 30, 130, 30);

        jButtonDelete.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/restar.png"))); // NOI18N
        jButtonDelete.setText("Eliminar");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDelete);
        jButtonDelete.setBounds(320, 30, 130, 30);

        jButtonClose.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cobranza.png"))); // NOI18N
        jButtonClose.setText("Cerrar");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonClose);
        jButtonClose.setBounds(600, 30, 130, 30);

        jButtonDetail.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButtonDetail.setForeground(new java.awt.Color(51, 51, 51));
        jButtonDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar.png"))); // NOI18N
        jButtonDetail.setText("Ver");
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDetail);
        jButtonDetail.setBounds(460, 30, 130, 30);

        jButton6.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jButton6.setForeground(new java.awt.Color(51, 51, 51));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/volver.png"))); // NOI18N
        jButton6.setText("Volver");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);
        jButton6.setBounds(310, 440, 130, 30);

        jLabelCodCliente.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        getContentPane().add(jLabelCodCliente);
        jLabelCodCliente.setBounds(40, 90, 90, 20);

        jLabel2.setFont(new java.awt.Font("Calibri", 3, 14)); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(140, 90, 170, 20);

        jRadioClosed.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jRadioClosed.setText("Cerradas");
        jRadioClosed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioClosedActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioClosed);
        jRadioClosed.setBounds(630, 110, 80, 25);

        jRadioOpen.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jRadioOpen.setSelected(true);
        jRadioOpen.setText("Abiertas");
        jRadioOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioOpenActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioOpen);
        jRadioOpen.setBounds(540, 110, 80, 25);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioClosedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioClosedActionPerformed
        if (jRadioClosed.isSelected()) {
            loadRemits(RemitFilter.CLOSED);
        }
    }//GEN-LAST:event_jRadioClosedActionPerformed

    private void jRadioOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioOpenActionPerformed
        if (jRadioOpen.isSelected()) {
            loadRemits(RemitFilter.OPEN);
        }
    }//GEN-LAST:event_jRadioOpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonUpdate;
    public static javax.swing.JLabel jLabel2;
    public static javax.swing.JLabel jLabelCodCliente;
    private javax.swing.JRadioButton jRadioClosed;
    private javax.swing.JRadioButton jRadioOpen;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

