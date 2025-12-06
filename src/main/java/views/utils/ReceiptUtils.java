package views.utils;

import controllers.BankController;
import controllers.CardController;
import java.text.Normalizer;
import java.util.Locale;
import javax.swing.JComboBox;
import models.Bank;
import models.Card;

/**
 * Utility methods used across receipt views for loading combo boxes.
 */
public final class ReceiptUtils {
    private ReceiptUtils() {}

    public static void loadBanks(JComboBox<Object> combo, BankController bankController) {
        combo.removeAllItems();
        combo.addItem(new ComboBoxItem<>(null, "Seleccione.."));
        for (Bank b : bankController.findAll()) {
            combo.addItem(new ComboBoxItem<>(b.getId(), b.getName()));
        }
        combo.setSelectedIndex(0);
    }

    public static void loadCards(JComboBox<Object> combo, CardController cardController) {
        combo.removeAllItems();
        combo.addItem(new ComboBoxItem<>(null, "Seleccione.."));
        for (Card c : cardController.findAll()) {
            if (Boolean.TRUE.equals(c.getEnabled()) || c.getEnabled() == null) {
                combo.addItem(new ComboBoxItem<>(c.getId(), c.getName()));
            }
        }
        combo.setSelectedIndex(0);
    }

    public static String normalizeCardType(Object value) {
        if (value == null) {
            return null;
        }
        String raw = value.toString().trim();
        if (raw.isEmpty()) {
            return null;
        }
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        if (normalized.startsWith("CRED")) {
            return "CREDIT";
        }
        if (normalized.startsWith("DEB")) {
            return "DEBIT";
        }
        return normalized;
    }
}
