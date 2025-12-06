package services.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ProviderInvoice;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import utils.JasperViewerUtils;

/**
 * Generates the purchase VAT book report.
 */
public class ReportPurchasesVatBook {

    public Path print(List<ProviderInvoice> invoices, Date start, Date end, Path outputDirectory, String issuerCuit) throws JRException {
        JasperReport report = JasperReportFactory.loadReport("/reports/LibroIvaCompras.jrxml");
        Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters(issuerCuit));
        parameters.put("tituloLibro", "Libro I.V.A Compras");
        parameters.put("pagina", 0);

        ReportPurchasesVatBookDataSource.detailsList = invoices;
        ReportPurchasesVatBookDataSource.configurePeriod(start, end);
        ReportPurchasesVatBookDataSource dataSource = new ReportPurchasesVatBookDataSource();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), "Libro I.V.A Compras");
        Path pdfPath = outputDirectory.resolve("libro-iva-compras.pdf");
        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException ex) {
            throw new JRException("No se pudo crear el directorio de salida para el libro IVA de compras.", ex);
        }
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath.toString());
        return pdfPath;
    }
}

