package utils;

import net.sf.jasperreports.view.JasperViewer;

import javax.swing.WindowConstants;
import java.awt.Frame;

/**
 * Utility helpers to display {@link JasperViewer} instances without them
 * being minimised unexpectedly. JasperViewer opens as a regular
 * {@link java.awt.Frame}, so we can force its state to normal and bring it to
 * the front to make sure the report is visible to the user.
 */
public final class JasperViewerUtils {

    private JasperViewerUtils() {
        // Utility class
    }

    public static void showViewer(JasperViewer viewer) {
        showViewer(viewer, null);
    }

    public static void showViewer(JasperViewer viewer, String title) {
        if (viewer == null) {
            return;
        }
        if (title != null && !title.isEmpty()) {
            viewer.setTitle(title);
        }

        viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        int currentState = viewer.getExtendedState();
        viewer.setExtendedState((currentState & ~Frame.ICONIFIED) | Frame.NORMAL);

        viewer.setVisible(true);
        viewer.toFront();
        viewer.requestFocus();
    }
}
