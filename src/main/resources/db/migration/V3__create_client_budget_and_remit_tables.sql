CREATE TABLE client_budget (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    budget_date TIMESTAMP NOT NULL,
    total DECIMAL(15,2) DEFAULT 0,
    closed TINYINT(1) DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_budget_client FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE client_budget_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    budget_id INT NOT NULL,
    product_code VARCHAR(20) NOT NULL,
    quantity FLOAT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_budget_detail_budget FOREIGN KEY (budget_id) REFERENCES client_budget(id),
    CONSTRAINT fk_client_budget_detail_product FOREIGN KEY (product_code) REFERENCES product(code)
);

CREATE TABLE client_remit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    remit_date TIMESTAMP NOT NULL,
    total DECIMAL(15,2) DEFAULT 0,
    closed TINYINT(1) DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_remit_client FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE client_remit_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remit_id INT NOT NULL,
    product_code VARCHAR(20) NOT NULL,
    quantity FLOAT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_client_remit_detail_remit FOREIGN KEY (remit_id) REFERENCES client_remit(id),
    CONSTRAINT fk_client_remit_detail_product FOREIGN KEY (product_code) REFERENCES product(code)
);
