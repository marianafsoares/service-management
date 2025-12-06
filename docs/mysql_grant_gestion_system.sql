-- Run this script with a privileged MySQL user, for example:
--   mysql -u root -p < docs/mysql_grant_gestion_system.sql
-- Before running, replace the placeholders APP_USER and APP_PASSWORD
-- with the account you want to grant full access to gestion_system.

CREATE DATABASE IF NOT EXISTS gestion_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Replace APP_USER and APP_PASSWORD with your desired credentials.
CREATE USER IF NOT EXISTS 'APP_USER'@'localhost' IDENTIFIED BY 'APP_PASSWORD';

-- If the application connects from another host, repeat the CREATE USER and
-- GRANT statements with the appropriate host (for example, '%').
GRANT ALL PRIVILEGES ON gestion_system.* TO 'APP_USER'@'localhost';

-- MySQL 8 actualiza los privilegios automáticamente cuando se crean usuarios
-- o se otorgan permisos, por lo que no es necesario ejecutar FLUSH PRIVILEGES.
