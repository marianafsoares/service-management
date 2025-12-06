package services.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ClientInvoice;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import utils.JasperViewerUtils;

/**
 * Generates the sales VAT book report.
 */
public class ReportSalesVatBook {

    public Path print(List<ClientInvoice> invoices, Date start, Date end, Path outputDirectory, String issuerCuit) throws JRException {
        JasperReport report = JasperReportFactory.loadReport("/reports/LibroIvaVentas.jrxml");
        Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters(issuerCuit));
        parameters.put("tituloLibro", "Libro I.V.A Ventas");
        parameters.put("pagina", 0);

        ReportSalesVatBookDataSource.detailsList = invoices;
        ReportSalesVatBookDataSource.configurePeriod(start, end);
        ReportSalesVatBookDataSource dataSource = new ReportSalesVatBookDataSource();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), "Libro I.V.A Ventas");
        Path pdfPath = outputDirectory.resolve("libro-iva-ventas.pdf");
        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException ex) {
            throw new JRException("No se pudo crear el directorio de salida para el libro IVA de ventas.", ex);
        }
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath.toString());
        return pdfPath;
    }
}

