ALTER TABLE client_invoice
    ADD COLUMN category_id INT NULL;

ALTER TABLE client_invoice
    ADD CONSTRAINT fk_client_invoice_category
        FOREIGN KEY (category_id) REFERENCES invoice_category(id);
