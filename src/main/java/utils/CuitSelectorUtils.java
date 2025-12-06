package utils;

import configs.AppConfig;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JComboBox;

public final class CuitSelectorUtils {

    private static final String CUITS_PROPERTY = "company.cuits";

    private CuitSelectorUtils() {
    }

    public static void populateCuits(JComboBox<String> comboBox) {
        if (comboBox == null) {
            return;
        }
        Object previousSelection = comboBox.getSelectedItem();
        comboBox.removeAllItems();
        String cuits = AppConfig.get(CUITS_PROPERTY, "");
        if (cuits != null && !cuits.isBlank()) {
            Set<String> unique = new LinkedHashSet<>();
            for (String raw : cuits.split(",")) {
                String trimmed = raw.trim();
                if (!trimmed.isEmpty()) {
                    String formatted = DocumentValidator.formatCuit(trimmed);
                    unique.add(formatted);
                }
            }
            for (String value : unique) {
                comboBox.addItem(value);
            }
        }
        if (previousSelection != null) {
            selectCuit(comboBox, previousSelection.toString());
        }
        if (comboBox.getItemCount() > 0 && comboBox.getSelectedIndex() == -1) {
            comboBox.setSelectedIndex(0);
        }
    }

    public static void selectCuit(JComboBox<String> comboBox, String cuit) {
        if (comboBox == null || cuit == null || cuit.isBlank()) {
            return;
        }
        String normalized = DocumentValidator.normalizeCuit(cuit);
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            if (normalized.equals(DocumentValidator.normalizeCuit(item))) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public static String getSelectedCuit(JComboBox<?> comboBox) {
        if (comboBox == null) {
            return null;
        }
        Object selected = comboBox.getSelectedItem();
        if (selected == null) {
            return null;
        }
        String value = selected.toString();
        String normalized = DocumentValidator.normalizeCuit(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }
}
