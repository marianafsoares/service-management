-- =========================
-- receipt_card  (card_name -> card_id)
-- =========================
DELIMITER $$

CREATE PROCEDURE migrate_receipt_card() 
BEGIN
  -- 1) Columna nueva (si no existe)
  IF NOT EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_card'
        AND COLUMN_NAME = 'card_id'
  ) THEN
    ALTER TABLE receipt_card ADD COLUMN card_id INT NULL AFTER card_type;
  END IF;

  -- 2) Backfill por nombre (tolerante a espacios; usa collation de la tabla para case/accents)
  UPDATE receipt_card rc
  LEFT JOIN card c
    ON TRIM(c.name) = TRIM(rc.card_name)
  SET rc.card_id = c.id
  WHERE rc.card_id IS NULL AND rc.card_name IS NOT NULL;

  -- 3) Validación: no deben quedar card_id NULL si había card_name
  IF (SELECT COUNT(*) FROM receipt_card WHERE card_id IS NULL AND card_name IS NOT NULL) > 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'receipt_card: Hay filas sin match (card_name -> card). Corregir antes de NOT NULL/FK.';
  END IF;

  -- 4) FK si no existe
  IF NOT EXISTS (
      SELECT 1
      FROM information_schema.TABLE_CONSTRAINTS
      WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_card'
        AND CONSTRAINT_TYPE = 'FOREIGN KEY'
        AND CONSTRAINT_NAME = 'fk_receipt_card_card'
  ) THEN
    ALTER TABLE receipt_card
      ADD CONSTRAINT fk_receipt_card_card FOREIGN KEY (card_id) REFERENCES card(id);
  END IF;

  -- 5) NOT NULL si todavía es nullable
  IF EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_card'
        AND COLUMN_NAME = 'card_id'
        AND IS_NULLABLE = 'YES'
  ) THEN
    ALTER TABLE receipt_card MODIFY card_id INT NOT NULL;
  END IF;

  -- 6) Drop columna vieja si existe
  SET @has_old := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_card'
      AND COLUMN_NAME = 'card_name'
  );
  IF @has_old > 0 THEN
    ALTER TABLE receipt_card DROP COLUMN card_name;
  END IF;
END$$

CALL migrate_receipt_card()$$
DROP PROCEDURE migrate_receipt_card$$

DELIMITER ;



-- =========================
-- receipt_cheque (bank_name -> bank_id)
-- =========================
DELIMITER $$

CREATE PROCEDURE migrate_receipt_cheque()
BEGIN
  -- 1) Columna nueva
  IF NOT EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_cheque'
        AND COLUMN_NAME = 'bank_id'
  ) THEN
    ALTER TABLE receipt_cheque ADD COLUMN bank_id INT NULL AFTER holder_name;
  END IF;

  -- 2) Backfill
  UPDATE receipt_cheque rc
  LEFT JOIN bank b
    ON TRIM(b.name) = TRIM(rc.bank_name)
  SET rc.bank_id = b.id
  WHERE rc.bank_id IS NULL AND rc.bank_name IS NOT NULL;

  -- 3) Validación
  IF (SELECT COUNT(*) FROM receipt_cheque WHERE bank_id IS NULL AND bank_name IS NOT NULL) > 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'receipt_cheque: Hay filas sin match (bank_name -> bank). Corregir antes de NOT NULL/FK.';
  END IF;

  -- 4) FK si no existe
  IF NOT EXISTS (
      SELECT 1
      FROM information_schema.TABLE_CONSTRAINTS
      WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_cheque'
        AND CONSTRAINT_TYPE = 'FOREIGN KEY'
        AND CONSTRAINT_NAME = 'fk_receipt_cheque_bank'
  ) THEN
    ALTER TABLE receipt_cheque
      ADD CONSTRAINT fk_receipt_cheque_bank FOREIGN KEY (bank_id) REFERENCES bank(id);
  END IF;

  -- 5) NOT NULL si corresponde
  IF EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_cheque'
        AND COLUMN_NAME = 'bank_id'
        AND IS_NULLABLE = 'YES'
  ) THEN
    ALTER TABLE receipt_cheque MODIFY bank_id INT NOT NULL;
  END IF;

  -- 6) Drop columna vieja
  SET @has_old := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_cheque'
      AND COLUMN_NAME = 'bank_name'
  );
  IF @has_old > 0 THEN
    ALTER TABLE receipt_cheque DROP COLUMN bank_name;
  END IF;
END$$

CALL migrate_receipt_cheque()$$
DROP PROCEDURE migrate_receipt_cheque$$

DELIMITER ;



-- =========================
-- receipt_transfer (origin_bank_name/destination_bank_name -> *_bank_id)
-- =========================
DELIMITER $$

CREATE PROCEDURE migrate_receipt_transfer()
BEGIN
  -- 1) Columnas nuevas
  IF NOT EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND COLUMN_NAME = 'origin_bank_id'
  ) THEN
    ALTER TABLE receipt_transfer ADD COLUMN origin_bank_id INT NULL AFTER origin_account;
  END IF;

  IF NOT EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND COLUMN_NAME = 'destination_bank_id'
  ) THEN
    ALTER TABLE receipt_transfer ADD COLUMN destination_bank_id INT NULL AFTER destination_account;
  END IF;

  -- 2) Backfill origen
  UPDATE receipt_transfer rt
  LEFT JOIN bank b
    ON TRIM(b.name) = TRIM(rt.origin_bank_name)
  SET rt.origin_bank_id = b.id
  WHERE rt.origin_bank_id IS NULL AND rt.origin_bank_name IS NOT NULL;

  -- 3) Backfill destino
  UPDATE receipt_transfer rt
  LEFT JOIN bank b
    ON TRIM(b.name) = TRIM(rt.destination_bank_name)
  SET rt.destination_bank_id = b.id
  WHERE rt.destination_bank_id IS NULL AND rt.destination_bank_name IS NOT NULL;

  -- 4) Validación de pendientes
  IF (SELECT COUNT(*) FROM receipt_transfer WHERE origin_bank_id IS NULL AND origin_bank_name IS NOT NULL) > 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'receipt_transfer: Falta mapear origin_bank_name -> bank.';
  END IF;

  IF (SELECT COUNT(*) FROM receipt_transfer WHERE destination_bank_id IS NULL AND destination_bank_name IS NOT NULL) > 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'receipt_transfer: Falta mapear destination_bank_name -> bank.';
  END IF;

  -- 5) FKs si no existen
  IF NOT EXISTS (
      SELECT 1
      FROM information_schema.TABLE_CONSTRAINTS
      WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND CONSTRAINT_TYPE = 'FOREIGN KEY'
        AND CONSTRAINT_NAME = 'fk_receipt_transfer_origin_bank'
  ) THEN
    ALTER TABLE receipt_transfer
      ADD CONSTRAINT fk_receipt_transfer_origin_bank
        FOREIGN KEY (origin_bank_id) REFERENCES bank(id);
  END IF;

  IF NOT EXISTS (
      SELECT 1
      FROM information_schema.TABLE_CONSTRAINTS
      WHERE CONSTRAINT_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND CONSTRAINT_TYPE = 'FOREIGN KEY'
        AND CONSTRAINT_NAME = 'fk_receipt_transfer_destination_bank'
  ) THEN
    ALTER TABLE receipt_transfer
      ADD CONSTRAINT fk_receipt_transfer_destination_bank
        FOREIGN KEY (destination_bank_id) REFERENCES bank(id);
  END IF;

  -- 6) NOT NULL si corresponde
  IF EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND COLUMN_NAME = 'origin_bank_id'
        AND IS_NULLABLE = 'YES'
  ) THEN
    ALTER TABLE receipt_transfer MODIFY origin_bank_id INT NOT NULL;
  END IF;

  IF EXISTS (
      SELECT 1 FROM information_schema.COLUMNS
      WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 'receipt_transfer'
        AND COLUMN_NAME = 'destination_bank_id'
        AND IS_NULLABLE = 'YES'
  ) THEN
    ALTER TABLE receipt_transfer MODIFY destination_bank_id INT NOT NULL;
  END IF;

  -- 7) Drop columnas viejas si existen
  SET @has_old1 := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_transfer'
      AND COLUMN_NAME = 'origin_bank_name'
  );
  IF @has_old1 > 0 THEN
    ALTER TABLE receipt_transfer DROP COLUMN origin_bank_name;
  END IF;

  SET @has_old2 := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_transfer'
      AND COLUMN_NAME = 'destination_bank_name'
  );
  IF @has_old2 > 0 THEN
    ALTER TABLE receipt_transfer DROP COLUMN destination_bank_name;
  END IF;
END$$

CALL migrate_receipt_transfer()$$
DROP PROCEDURE migrate_receipt_transfer$$

DELIMITER ;
