CREATE TABLE provider_receipt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_number VARCHAR(20) NOT NULL,
    point_of_sale VARCHAR(5) NOT NULL,
    receiver_cuit VARCHAR(20) NOT NULL,
    provider_id INT NOT NULL,
    receipt_date DATETIME NOT NULL,
    total DECIMAL(15, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_provider_receipt_provider FOREIGN KEY (provider_id) REFERENCES provider(id)
);
