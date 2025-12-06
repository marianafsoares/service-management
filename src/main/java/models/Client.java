package models;


import java.time.LocalDateTime;
import utils.DocumentValidator;
import java.math.BigDecimal;


public class Client {
    private Integer id;
    private String fullName;
    private City city;
    
    private Address address;
    private String addressNumber;
    
    private TaxCondition taxCondition;
    private String documentType;
    private String documentNumber;
    
    private String phone;
    private String mobile;
    private String email;

    private BigDecimal subscriptionAmount;
    private boolean fxBilling;

    private boolean active = true;
    private boolean hasOpenRemits;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public TaxCondition getTaxCondition() {
        return taxCondition;
    }

    public void setTaxCondition(TaxCondition taxCondition) {
        this.taxCondition = taxCondition;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = DocumentValidator.normalizeCuit(documentNumber);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    public boolean isFxBilling() {
        return fxBilling;
    }

    public void setFxBilling(boolean fxBilling) {
        this.fxBilling = fxBilling;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean hasOpenRemits() {
        return hasOpenRemits;
    }

    public void setHasOpenRemits(boolean hasOpenRemits) {
        this.hasOpenRemits = hasOpenRemits;
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
