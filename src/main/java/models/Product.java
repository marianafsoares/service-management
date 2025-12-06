
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private String code;
    private String description;
    private Brand brand;
    private BigDecimal purchasePrice; //precio de compra
    private Float stockQuantity;
    private Float profitMargin;
    private Float interestRate;
    private Float vatRate;
    private BigDecimal cashPrice;
    private BigDecimal financedPrice;
    private Category category;
    private Subcategory subcategory;
    private Provider provider;
    private String notes;
    private Boolean inPromotion;
    private Boolean enabled;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Float getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Float stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Float getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(Float profitMargin) {
        this.profitMargin = profitMargin;
    }

    public Float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Float interestRate) {
        this.interestRate = interestRate;
    }

    public Float getVatRate() {
        return vatRate;
    }

    public void setVatRate(Float vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(BigDecimal cashPrice) {
        this.cashPrice = cashPrice;
    }

    public BigDecimal getFinancedPrice() {
        return financedPrice;
    }

    public void setFinancedPrice(BigDecimal financedPrice) {
        this.financedPrice = financedPrice;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getInPromotion() {
        return inPromotion;
    }

    public void setInPromotion(Boolean inPromotion) {
        this.inPromotion = inPromotion;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
