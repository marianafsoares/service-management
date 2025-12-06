package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClientRemit {
    private Integer id;
    private Client client;
    private LocalDateTime remitDate;
    private BigDecimal total;
    private Boolean closed;
    private String description;
    private List<ClientRemitDetail> details;
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

    public LocalDateTime getRemitDate() {
        return remitDate;
    }

    public void setRemitDate(LocalDateTime remitDate) {
        this.remitDate = remitDate;
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

    public List<ClientRemitDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ClientRemitDetail> details) {
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
