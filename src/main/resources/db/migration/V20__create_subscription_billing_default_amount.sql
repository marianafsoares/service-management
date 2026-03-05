CREATE TABLE subscription_billing_default_amount (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(15, 2) NOT NULL,
    invoice_type VARCHAR(10) NOT NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    enabled_singleton TINYINT GENERATED ALWAYS AS (CASE WHEN enabled = 1 THEN 1 ELSE NULL END) STORED,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_subscription_billing_default_amount_enabled (enabled_singleton)
);

INSERT INTO subscription_billing_default_amount (amount, invoice_type, enabled)
VALUES (67538.00, 'FC', 1);
