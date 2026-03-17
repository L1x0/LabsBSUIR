CREATE USER admin_user WITH PASSWORD 'dev';

CREATE DATABASE repair_department_db OWNER admin_user;

GRANT ALL PRIVILEGES ON DATABASE repair_department_db TO admin_user;

\connect repair_department_db

GRANT USAGE ON SCHEMA public TO admin_user;
GRANT CREATE ON SCHEMA public TO admin_user;
ALTER SCHEMA public OWNER TO admin_user;
