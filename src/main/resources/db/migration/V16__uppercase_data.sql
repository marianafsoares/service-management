UPDATE address
SET name = UPPER(TRIM(name))
WHERE name IS NOT NULL;

UPDATE bank
SET name = UPPER(TRIM(name))
WHERE name IS NOT NULL;

UPDATE brand
SET name = UPPER(TRIM(name))
WHERE name IS NOT NULL;

UPDATE card
SET name = UPPER(TRIM(name))
WHERE name IS NOT NULL;

UPDATE category
SET name = UPPER(TRIM(name)),
    notes = UPPER(TRIM(notes))
WHERE name IS NOT NULL OR notes IS NOT NULL;

UPDATE city
SET name = UPPER(TRIM(name))
WHERE name IS NOT NULL;

UPDATE invoice_category
SET description = UPPER(TRIM(description))
WHERE description IS NOT NULL;

UPDATE subcategory
SET name = UPPER(TRIM(name)),
    notes = UPPER(TRIM(notes))
WHERE name IS NOT NULL OR notes IS NOT NULL;



