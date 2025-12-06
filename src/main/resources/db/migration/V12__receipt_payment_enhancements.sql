ALTER TABLE receipt_transfer
    CHANGE COLUMN bank_name origin_bank_name VARCHAR(100),
    ADD COLUMN destination_bank_name VARCHAR(100) AFTER origin_bank_name,
    CHANGE COLUMN reference_number reference VARCHAR(100);

CREATE TABLE receipt_retention (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT NOT NULL,
    receipt_type ENUM('CLIENT', 'PROVIDER') NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
