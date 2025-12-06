
CREATE TABLE city (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tax_condition (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO tax_condition (description, abbreviation) VALUES
('RESPONSABLE INSCRIPTO', 'RI'),
('EXENTO', 'E'),
('CONSUMIDOR FINAL', 'CF'),
('MONOTRIBUTO', 'M');

CREATE TABLE client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    city_id INT,
    address_id INT,
    address_number VARCHAR(10),
    tax_condition_id INT NOT NULL,
    document_type VARCHAR(10) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    phone VARCHAR(50),
    mobile VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_city FOREIGN KEY (city_id) REFERENCES city(id),
    CONSTRAINT fk_client_address FOREIGN KEY (address_id) REFERENCES address(id),
    CONSTRAINT fk_client_tax_condition FOREIGN KEY (tax_condition_id) REFERENCES tax_condition(id)
);

CREATE TABLE provider (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    city_id INT,
    address_id INT,
    address_number VARCHAR(10),
    tax_condition_id INT NOT NULL,
    document_type VARCHAR(10) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    phone VARCHAR(50),
    mobile VARCHAR(50),
    email VARCHAR(100),
    website VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_provider_city FOREIGN KEY (city_id) REFERENCES city(id),
    CONSTRAINT fk_provider_address FOREIGN KEY (address_id) REFERENCES address(id),
    CONSTRAINT fk_provider_tax_condition FOREIGN KEY (tax_condition_id) REFERENCES tax_condition(id)
);

CREATE TABLE brand (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE subcategory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    notes TEXT,
    category_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_subcategory_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE product (
    code VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    brand_id INT,
    purchase_price DECIMAL(15, 2) NOT NULL,
    stock_quantity FLOAT DEFAULT 0,
    profit_margin FLOAT DEFAULT 0,
    interest_rate FLOAT DEFAULT 0,
    vat_rate FLOAT DEFAULT 0,
    cash_price DECIMAL(15, 2),
    credit_price DECIMAL(15, 2),
    category_id INT,
    subcategory_id INT,
    provider_id INT,
    notes TEXT,
    in_promotion TINYINT(1) DEFAULT 0,
    enabled TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_product_subcategory FOREIGN KEY (subcategory_id) REFERENCES subcategory(id),
    CONSTRAINT fk_product_provider FOREIGN KEY (provider_id) REFERENCES provider(id)
);

CREATE TABLE bank (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enabled TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO bank (name) VALUES
    ('Banco Nación'),
    ('Banco Provincia'),
    ('Banco Galicia'),
    ('Banco Santander Río'),
    ('Banco Macro'),
    ('Banco BBVA'),
    ('Banco Patagonia'),
    ('Banco HSBC'),
    ('Banco Credicoop'),
    ('Banco Itaú'),
    ('Banco Supervielle'),
    ('Banco Comafi'),
    ('Banco Ciudad'),
    ('Banco Columbia'),
    ('Nuevo Banco del Chaco'),
    ('Nuevo Banco de Entre Ríos'),
    ('Nuevo Banco de Santa Fe');

CREATE TABLE card (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    enabled TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO card (name) VALUES
    ('Visa Banco Nación'),
    ('Mastercard Banco Nación'),
    ('Visa Banco Provincia'),
    ('Mastercard Banco Provincia'),
    ('Visa Banco de La Pampa'),
    ('Mastercard Banco de La Pampa'),
    ('Visa Banco Galicia'),
    ('Mastercard Banco Galicia'),
    ('Visa Banco Macro'),
    ('Mastercard Banco Macro'),
    ('Visa Banco Santander Río'),
    ('Mastercard Banco Santander Río'),
    ('Visa Banco BBVA'),
    ('Mastercard Banco BBVA'),
    ('Visa Banco Patagonia'),
    ('Mastercard Banco Patagonia'),
    ('Visa Banco HSBC'),
    ('Mastercard Banco HSBC');


CREATE TABLE client_receipt (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_number VARCHAR(20) NOT NULL,
    point_of_sale VARCHAR(5) NOT NULL,
    issuer_cuit VARCHAR(20) NOT NULL,
    client_id INT NOT NULL,
    receipt_date DATETIME NOT NULL,
    total DECIMAL(15, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_receipt_client FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE receipt_cheque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT NOT NULL,
    receipt_type ENUM('CLIENT', 'PROVIDER') NOT NULL,
    check_number VARCHAR(50) NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    holder_name VARCHAR(100),
    bank_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE receipt_card (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT NOT NULL,
    receipt_type ENUM('CLIENT', 'PROVIDER') NOT NULL,
    card_type ENUM('CREDIT', 'DEBIT') NOT NULL,
    card_name VARCHAR(100) NOT NULL,
    last_four_digits VARCHAR(4),
    expiration_date DATE,
    holder_name VARCHAR(100),
    bank_name VARCHAR(100),
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE receipt_transfer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT NOT NULL,
    receipt_type ENUM('CLIENT', 'PROVIDER') NOT NULL,
    origin_account VARCHAR(100),
    destination_account VARCHAR(100),
    bank_name VARCHAR(100),
    reference_number VARCHAR(100),
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE receipt_cash (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT NOT NULL,
    receipt_type ENUM('CLIENT', 'PROVIDER') NOT NULL,
    amount DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_receipt_cash_receipt FOREIGN KEY (receipt_id) REFERENCES client_receipt(id)
);

CREATE TABLE invoice_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    type VARCHAR(20) NOT NULL, -- CLIENT o PROVIDER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE provider_invoice (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(20) NOT NULL,
    point_of_sale VARCHAR(5) NOT NULL,
    description TEXT,
    provider_id INT NOT NULL,
    category_id INT NOT NULL,
    receiver_cuit VARCHAR(20) NOT NULL,

    invoice_date DATETIME NOT NULL,
    presentation_date DATETIME,

    subtotal DECIMAL(15, 2) NOT NULL,
    vat_21 DECIMAL(15, 2) DEFAULT 0.00,
    vat_105 DECIMAL(15, 2) DEFAULT 0.00,
    vat_27 DECIMAL(15, 2) DEFAULT 0.00,

    vat_perception DECIMAL(15, 2) DEFAULT 0.00,
    gross_income_perception DECIMAL(15, 2) DEFAULT 0.00,
    income_tax_perception DECIMAL(15, 2) DEFAULT 0.00,

    exempt_amount DECIMAL(15, 2) DEFAULT 0.00,
    stamp_tax DECIMAL(15, 2) DEFAULT 0.00,

    total DECIMAL(15, 2) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_provider_invoice_provider FOREIGN KEY (provider_id) REFERENCES provider(id),
    CONSTRAINT fk_provider_invoice_category FOREIGN KEY (category_id) REFERENCES invoice_category(id)
);

CREATE TABLE client_invoice (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(30) NOT NULL,
    point_of_sale VARCHAR(10) NOT NULL,
    issuer_cuit VARCHAR(15) NOT NULL,
    client_id INT NOT NULL,
    client_cuit VARCHAR(15) NOT NULL,
    invoice_type VARCHAR(30) NOT NULL,
    payment_method VARCHAR(30),
    invoice_date DATETIME NOT NULL,
    subtotal DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    discount_percent DECIMAL(5, 2),
    interest_percent DECIMAL(5, 2),
    vat_21 DECIMAL(15, 2),
    vat_105 DECIMAL(15, 2),
    vat_27 DECIMAL(15, 2),
    total DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    description TEXT,
    associated_invoice_number VARCHAR(30),
    cae VARCHAR(20),
    cae_expiration_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_invoice_client FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE client_invoice_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id INT NOT NULL,
    article_code VARCHAR(50) NOT NULL,
    article_description VARCHAR(255),
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    discount_percent DECIMAL(5, 2),
    vat_amount DECIMAL(15, 2),
    subtotal DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_invoice_detail_invoice FOREIGN KEY (invoice_id) REFERENCES client_invoice(id)
);




