ALTER TABLE client
    ADD COLUMN subscription_amount DECIMAL(15, 2) NULL AFTER email,
    ADD COLUMN fx_billing TINYINT(1) NOT NULL DEFAULT 0 AFTER subscription_amount;
