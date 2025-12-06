package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClientBudget {
    private Integer id;
    private Client client;
    private LocalDateTime budgetDate;
    private BigDecimal total;
    private Boolean closed;
    private String description;
    private List<ClientBudgetDetail> details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getBudgetDate() {
        return budgetDate;
    }

    public void setBudgetDate(LocalDateTime budgetDate) {
        this.budgetDate = budgetDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClientBudgetDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ClientBudgetDetail> details) {
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
