package services.reports;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 * Utility class responsible for compiling JasperReports templates on demand
 * and caching the compiled results to avoid repeated compilation work.
 */
public final class JasperReportFactory {

    private static final Map<String, JasperReport> CACHE = new ConcurrentHashMap<>();

    private JasperReportFactory() {
        // Utility class
    }

    /**
     * Loads and compiles the report located at the given classpath resource if it
     * has not been compiled yet. The compiled report is cached for subsequent
     * calls.
     *
     * @param jrxmlResource the classpath location of the jrxml file.
     * @return the compiled {@link JasperReport}
     * @throws JRException if the resource cannot be found or if compilation fails.
     */
    public static JasperReport loadReport(String jrxmlResource) throws JRException {
        JasperReport report = CACHE.get(jrxmlResource);
        if (report != null) {
            return report;
        }

        try (InputStream stream = JasperReportFactory.class.getResourceAsStream(jrxmlResource)) {
            if (stream == null) {
                throw new JRException("Report template not found: " + jrxmlResource);
            }

            JasperReport compiled = JasperCompileManager.compileReport(stream);
            CACHE.put(jrxmlResource, compiled);
            return compiled;
        } catch (IOException e) {
            throw new JRException("Unable to load report template: " + jrxmlResource, e);
        }
    }
}
