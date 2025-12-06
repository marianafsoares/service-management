package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import utils.DocumentValidator;

public class ProviderInvoice {

    private Integer id; 
    private String invoiceNumber;
    private String pointOfSale; 
    private String description; 

    private Provider provider;
    private InvoiceCategory category;

    private String receiverCuit;

    private String invoiceType;

    private LocalDateTime invoiceDate;
    private LocalDateTime presentationDate;

    private BigDecimal subtotal;

    private BigDecimal vat21;
    private BigDecimal vat105;
    private BigDecimal vat27;

    private BigDecimal vatPerception;
    private BigDecimal grossIncomePerception; // Percepción Ingresos Brutos
    private BigDecimal incomeTaxPerception; // Percepción Ganancias

    private BigDecimal exemptAmount; // No gravado
    private BigDecimal stampTax; // Impuesto de sellos

    private BigDecimal total;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public InvoiceCategory getCategory() {
        return category;
    }

    public void setCategory(InvoiceCategory category) {
        this.category = category;
    }

    public String getReceiverCuit() {
        return receiverCuit;
    }

    public void setReceiverCuit(String receiverCuit) {
        this.receiverCuit = DocumentValidator.normalizeCuit(receiverCuit);
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDateTime getPresentationDate() {
        return presentationDate;
    }

    public void setPresentationDate(LocalDateTime presentationDate) {
        this.presentationDate = presentationDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getVat21() {
        return vat21;
    }

    public void setVat21(BigDecimal vat21) {
        this.vat21 = vat21;
    }

    public BigDecimal getVat105() {
        return vat105;
    }

    public void setVat105(BigDecimal vat105) {
        this.vat105 = vat105;
    }

    public BigDecimal getVat27() {
        return vat27;
    }

    public void setVat27(BigDecimal vat27) {
        this.vat27 = vat27;
    }

    public BigDecimal getVatPerception() {
        return vatPerception;
    }

    public void setVatPerception(BigDecimal vatPerception) {
        this.vatPerception = vatPerception;
    }

    public BigDecimal getGrossIncomePerception() {
        return grossIncomePerception;
    }

    public void setGrossIncomePerception(BigDecimal grossIncomePerception) {
        this.grossIncomePerception = grossIncomePerception;
    }

    public BigDecimal getIncomeTaxPerception() {
        return incomeTaxPerception;
    }

    public void setIncomeTaxPerception(BigDecimal incomeTaxPerception) {
        this.incomeTaxPerception = incomeTaxPerception;
    }

    public BigDecimal getExemptAmount() {
        return exemptAmount;
    }

    public void setExemptAmount(BigDecimal exemptAmount) {
        this.exemptAmount = exemptAmount;
    }

    public BigDecimal getStampTax() {
        return stampTax;
    }

    public void setStampTax(BigDecimal stampTax) {
        this.stampTax = stampTax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}