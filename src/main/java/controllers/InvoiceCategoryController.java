package controllers;

import java.util.List;
import javax.swing.JComboBox;
import models.InvoiceCategory;
import services.InvoiceCategoryService;

public class InvoiceCategoryController {

    private final InvoiceCategoryService invoiceCategoryService;

    public InvoiceCategoryController(InvoiceCategoryService invoiceCategoryService) {
        this.invoiceCategoryService = invoiceCategoryService;
    }

    public List<InvoiceCategory> findAll() {
        return invoiceCategoryService.findAll();
    }

    public InvoiceCategory findById(int id) {
        return invoiceCategoryService.findById(id);
    }

    public void create(InvoiceCategory category) {
        invoiceCategoryService.create(category);
    }

    public void update(InvoiceCategory category) {
        invoiceCategoryService.update(category);
    }

    public void delete(int id) {
        invoiceCategoryService.delete(id);
    }

    public void loadProviderCategories(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        List<InvoiceCategory> categories = invoiceCategoryService.findProviderCategories();
        for (InvoiceCategory category : categories) {
            combo.addItem(category);
        }
    }

    public void loadClientCategories(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        List<InvoiceCategory> categories = invoiceCategoryService.findClientCategories();
        for (InvoiceCategory category : categories) {
            combo.addItem(category);
        }
    }

    public void loadAll(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("Seleccione...");
        for (InvoiceCategory category : findAll()) {
            combo.addItem(category);
        }
    }
}
