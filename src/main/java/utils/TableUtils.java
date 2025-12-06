package utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.SwingConstants;

/**
 * Utility methods for working with {@link JTable} components.
 */
public class TableUtils {

    private TableUtils() {
        // utility class
    }

    /**
     * Adjusts each column of the given table to fit the preferred width of the
     * column's header and cell contents.
     *
     * @param table the table whose columns should be resized
     */
    public static void autoResizeColumns(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            // Consider header width
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComponent = headerRenderer.getTableCellRendererComponent(
                    table, tableColumn.getHeaderValue(), false, false, 0, column);
            preferredWidth = Math.max(preferredWidth, headerComponent.getPreferredSize().width);

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = cellRenderer.getTableCellRendererComponent(
                        table, table.getValueAt(row, column), false, false, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    public static void configureProductManagementViewTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setPreferredWidths(table, new int[]{120, 520, 130, 100, 100});
    }

    private static void setPreferredWidths(JTable table, int[] widths) {
        for (int i = 0; i < table.getColumnCount() && i < widths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
    }

    private static void setFixedWidths(JTable table, int[] widths) {
        for (int i = 0; i < table.getColumnCount() && i < widths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setMinWidth(widths[i]);
            column.setMaxWidth(widths[i]);
        }
    }
    
    public static void configureProductSearchViewTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setPreferredWidths(table, new int[]{120, 360, 100});
        table.setRowHeight(28);
        for (int i = 0; i < table.getColumnCount() && i < 3; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setMinWidth(column.getPreferredWidth());
        }
    }

    public static void configureClientManagementViewTable(JTable table) {
        setPreferredWidths(table, new int[]{40, 150, 100, 100});
    }

    public static void configureProviderManagementViewTable(JTable table) {
        setPreferredWidths(table, new int[]{40, 150, 100, 100});
    }

    public static void configureClientInvoiceManagementViewTable(JTable table) {
        setFixedWidths(table, new int[]{100, 100, 120, 100, 260, 160, 150});
    }

    public static void configureClientHistoryViewTable(JTable table) {
        setPreferredWidths(table, new int[]{100, 40, 150, 100, 100});
    }

    public static void configureClientInvoiceInsertViewTable(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setPreferredWidths(table, new int[]{100, 360, 90, 140, 90, 100, 180});
    }

    public static void configureClientInvoiceDetailViewTable(JTable table) {
        setFixedWidths(table, new int[]{100, 390, 100, 160, 100, 100, 250});
    }

    public static void configureClientRemitDetailViewTable(JTable table) {
        setFixedWidths(table, new int[]{100, 420, 120, 120});
    }

    public static void configureProviderHistoryViewTable(JTable table) {
        setPreferredWidths(table, new int[]{100, 40, 150, 100, 100});
    }

    public static void configureProviderInvoiceManagementViewTable(JTable table) {
        setFixedWidths(table, new int[]{60, 200, 140, 70, 140, 140, 140});
    }

    public static void applyDecimalRenderer(JTable table, int[] columns, int scale) {
        applyDecimalRenderer(table, columns, scale, RoundingMode.HALF_UP);
    }

    public static void applyDecimalRenderer(JTable table, int[] columns, int scale, RoundingMode roundingMode) {
        if (table == null || columns == null) {
            return;
        }

        TableCellRenderer renderer = new DecimalTableCellRenderer(scale, roundingMode);
        for (int columnIndex : columns) {
            if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
                table.getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
            }
        }
    }

    private static class DecimalTableCellRenderer extends DefaultTableCellRenderer {

        private final int scale;
        private final RoundingMode roundingMode;

        private DecimalTableCellRenderer(int scale, RoundingMode roundingMode) {
            this.scale = Math.max(scale, 0);
            this.roundingMode = roundingMode == null ? RoundingMode.HALF_UP : roundingMode;
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(format(value));
        }

        private String format(Object value) {
            BigDecimal decimalValue = toBigDecimal(value);
            String plain = decimalValue.setScale(scale, roundingMode).toPlainString();
            return plain.replace('.', ',');
        }

        private BigDecimal toBigDecimal(Object value) {
            if (value == null) {
                return BigDecimal.ZERO;
            }

            if (value instanceof BigDecimal) {
                return ((BigDecimal) value);
            }

            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }

            try {
                String text = value.toString().trim();
                if (text.isEmpty()) {
                    return BigDecimal.ZERO;
                }
                return new BigDecimal(text);
            } catch (NumberFormatException ex) {
                return BigDecimal.ZERO;
            }
        }
    }
}
