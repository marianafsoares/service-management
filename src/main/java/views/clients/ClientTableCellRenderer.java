package views.clients;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import models.Client;

public class ClientTableCellRenderer extends DefaultTableCellRenderer {

    private static final Color INACTIVE_BACKGROUND = new Color(245, 245, 245);
    private static final Color INACTIVE_FOREGROUND = Color.GRAY;
    private static final Color OPEN_REMIT_BACKGROUND = new Color(255, 249, 196);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int alignment = value instanceof Number ? SwingConstants.TRAILING : SwingConstants.LEADING;
        if (column == table.getColumnCount() - 1) {
            alignment = SwingConstants.TRAILING;
        }
        setHorizontalAlignment(alignment);

        boolean hasOpenRemits = hasClientOpenRemits(table, row);
        boolean inactive = isClientInactive(table, row);

        if (isSelected) {
            component.setForeground(table.getSelectionForeground());
            component.setBackground(table.getSelectionBackground());
        } else if (hasOpenRemits) {
            component.setForeground(table.getForeground());
            component.setBackground(OPEN_REMIT_BACKGROUND);
        } else if (inactive) {
            component.setForeground(INACTIVE_FOREGROUND);
            component.setBackground(INACTIVE_BACKGROUND);
        } else {
            component.setForeground(table.getForeground());
            component.setBackground(table.getBackground());
        }

        return component;
    }

    private boolean isClientInactive(JTable table, int row) {
        Object property = table.getClientProperty("clientsList");
        if (!(property instanceof List<?>)) {
            return false;
        }
        List<?> clients = (List<?>) property;
        if (row < 0 || row >= clients.size()) {
            return false;
        }
        Object clientObj = clients.get(row);
        if (!(clientObj instanceof Client)) {
            return false;
        }
        Client client = (Client) clientObj;
        return !client.isActive();
    }

    private boolean hasClientOpenRemits(JTable table, int row) {
        Object property = table.getClientProperty("clientsList");
        if (!(property instanceof List<?>)) {
            return false;
        }
        List<?> clients = (List<?>) property;
        if (row < 0 || row >= clients.size()) {
            return false;
        }
        Object clientObj = clients.get(row);
        if (!(clientObj instanceof Client)) {
            return false;
        }
        Client client = (Client) clientObj;
        return client.hasOpenRemits();
    }
}
