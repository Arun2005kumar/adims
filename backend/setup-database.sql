-- =========================================================
-- Run this ONCE, before starting the backend for the first time,
-- as the PostgreSQL superuser (usually "postgres").
--
-- Windows (Command Prompt), from your PostgreSQL "bin" folder
-- (e.g. C:\Program Files\PostgreSQL\16\bin):
--   psql -U postgres -f "C:\path\to\setup-database.sql"
-- It will prompt for the postgres superuser password.
--
-- Or paste the 4 statements below into pgAdmin's Query Tool
-- (connected to the default "postgres" database) and run them.
-- =========================================================

DROP DATABASE IF EXISTS antidoping_investigation;
DROP USER IF EXISTS adims_user;

CREATE USER adims_user WITH PASSWORD 'adims_pass';
CREATE DATABASE antidoping_investigation OWNER adims_user;
GRANT ALL PRIVILEGES ON DATABASE antidoping_investigation TO adims_user;
