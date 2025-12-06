package views.utils;

import java.util.Objects;

/**
 * Simple combo box item wrapper that keeps an internal value and a label to display.
 *
 * @param <T> value type stored in the combo box.
 */
public class ComboBoxItem<T> {
    private final T value;
    private final String label;

    public ComboBoxItem(T value, String label) {
        this.value = value;
        this.label = label;
    }

    public T getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComboBoxItem<?> other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
