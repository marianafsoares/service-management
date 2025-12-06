package services.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.ProviderInvoice;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import utils.pyAfip.AfipManagement;
import utils.JasperViewerUtils;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Generates the expense report using JasperReports.
 */
public class ReportExpenses {

    public void print(List<ProviderInvoice> invoices, String category) throws JRException {
        JasperReport report = JasperReportFactory.loadReport("/reports/ListadoGastos.jrxml");
        Map<String, Object> parameters = new HashMap<>(ReportParameterFactory.createBaseParameters());

        parameters.put("tituloLibro", "Categoría: " + category);

        ReportExpensesDataSource.detailsList = invoices;
        ReportExpensesDataSource dataSource = new ReportExpensesDataSource();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        JasperViewerUtils.showViewer(new JasperViewer(jasperPrint, false), "Listado de gastos");

        JasperExportManager.exportReportToPdfFile(jasperPrint,
                AfipManagement.EXPORT_BASE_PATH + "listadoGastos-" + category + ".pdf");
    }
}
