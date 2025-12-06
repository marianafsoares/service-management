package views.providers;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;
import models.Provider;

public class ProviderTableCellRenderer extends DefaultTableCellRenderer {

    private static final Color INACTIVE_BACKGROUND = new Color(245, 245, 245);
    private static final Color INACTIVE_FOREGROUND = Color.GRAY;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int alignment = value instanceof Number ? SwingConstants.TRAILING : SwingConstants.LEADING;
        if (column == table.getColumnCount() - 1) {
            alignment = SwingConstants.TRAILING;
        }
        setHorizontalAlignment(alignment);

        if (component instanceof JLabel) {
            ((JLabel) component).setText(formatValue(value, column));
        }

        boolean inactive = isProviderInactive(table, row);

        if (inactive) {
            if (isSelected) {
                component.setForeground(table.getSelectionForeground());
                component.setBackground(table.getSelectionBackground());
            } else {
                component.setForeground(INACTIVE_FOREGROUND);
                component.setBackground(INACTIVE_BACKGROUND);
            }
        } else {
            if (isSelected) {
                component.setForeground(table.getSelectionForeground());
                component.setBackground(table.getSelectionBackground());
            } else {
                component.setForeground(table.getForeground());
                component.setBackground(table.getBackground());
            }
        }

        return component;
    }

    private String formatValue(Object value, int column) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDateTime) {
            return formatDate(((LocalDateTime) value).toLocalDate());
        }
        if (value instanceof LocalDate) {
            return formatDate((LocalDate) value);
        }
        if (value instanceof Date) {
            Date date = (Date) value;
            return formatDate(date.toInstant().atZone(DEFAULT_ZONE).toLocalDate());
        }
        if (value instanceof BigDecimal) {
            return formatAmount((BigDecimal) value);
        }
        return value.toString();
    }

    private String formatDate(LocalDate date) {
        return date != null ? DATE_FORMATTER.format(date) : "";
    }

    private String formatAmount(BigDecimal amount) {
        return amount != null ? amount.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private boolean isProviderInactive(JTable table, int row) {
        Object property = table.getClientProperty("providersList");
        if (!(property instanceof List<?>)) {
            return false;
        }
        List<?> providers = (List<?>) property;
        if (row < 0 || row >= providers.size()) {
            return false;
        }
        Object providerObj = providers.get(row);
        if (!(providerObj instanceof Provider)) {
            return false;
        }
        Provider provider = (Provider) providerObj;
        return !provider.isActive();
    }
}
