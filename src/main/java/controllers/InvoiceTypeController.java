package controllers;

import java.util.List;
import javax.swing.JComboBox;
import models.InvoiceType;
import services.InvoiceTypeService;

public class InvoiceTypeController {

    private final InvoiceTypeService invoiceTypeService;

    public InvoiceTypeController(InvoiceTypeService invoiceTypeService) {
        this.invoiceTypeService = invoiceTypeService;
    }

    public void loadTypes(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        List<InvoiceType> types = invoiceTypeService.findAll();
        for (InvoiceType type : types) {
            combo.addItem(type.getDescription());
        }
    }
}
