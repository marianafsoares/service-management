package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import utils.DocumentValidator;
import models.InvoiceCategory;

public class ClientInvoice {
    private Integer id;
    private String invoiceNumber;           
    private String pointOfSale;           
    private String issuerCuit;

    private Client client;
    
    private String invoiceType;             
    private String paymentMethod;          

    private LocalDateTime invoiceDate;
    private BigDecimal subtotal;
    private BigDecimal discountPercent;
    private BigDecimal interestPercent;
    private BigDecimal vat21;
    private BigDecimal vat105;
    private BigDecimal vat27;
    private BigDecimal total;

    private String description;
    private String associatedInvoiceNumber;

    private String cae;
    private LocalDateTime caeExpirationDate;

    private InvoiceCategory category;

    private List<ClientInvoiceDetail> details;

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

    public String getIssuerCuit() {
        return issuerCuit;
    }

    public void setIssuerCuit(String issuerCuit) {
        this.issuerCuit = DocumentValidator.normalizeCuit(issuerCuit);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getInterestPercent() {
        return interestPercent;
    }

    public void setInterestPercent(BigDecimal interestPercent) {
        this.interestPercent = interestPercent;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssociatedInvoiceNumber() {
        return associatedInvoiceNumber;
    }

    public void setAssociatedInvoiceNumber(String associatedInvoiceNumber) {
        this.associatedInvoiceNumber = associatedInvoiceNumber;
    }

    public InvoiceCategory getCategory() {
        return category;
    }

    public void setCategory(InvoiceCategory category) {
        this.category = category;
    }

    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDateTime getCaeExpirationDate() {
        return caeExpirationDate;
    }

    public void setCaeExpirationDate(LocalDateTime caeExpirationDate) {
        this.caeExpirationDate = caeExpirationDate;
    }

    public List<ClientInvoiceDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ClientInvoiceDetail> details) {
        this.details = details;
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

