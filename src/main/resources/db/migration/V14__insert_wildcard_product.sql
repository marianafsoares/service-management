INSERT INTO product (
    code,
    description,
    purchase_price,
    stock_quantity,
    profit_margin,
    interest_rate,
    vat_rate,
    cash_price,
    credit_price,
    in_promotion,
    enabled
) VALUES (
    '99',
    '',
    0,
    0,
    0,
    0,
    21,
    0,
    0,
    0,
    0
)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    purchase_price = VALUES(purchase_price),
    vat_rate = VALUES(vat_rate),
    enabled = VALUES(enabled);
