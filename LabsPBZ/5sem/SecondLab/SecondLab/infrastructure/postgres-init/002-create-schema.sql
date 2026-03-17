\connect repair_department_db

CREATE TYPE equipment_status AS ENUM ('in_service', 'in_repair', 'written_off', 'disposed');
CREATE TYPE repair_status AS ENUM ('opened', 'in_progress', 'closed', 'cancelled');
CREATE TYPE stock_movement_type AS ENUM ('RECEIPT','CONSUMPTION','RETURN','ADJUSTMENT');

CREATE TABLE department
(
    dept_id SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    code    VARCHAR(50),
    address TEXT,
    CONSTRAINT department_name_unique UNIQUE (name)
);

CREATE TABLE equipment
(
    inventory_no     VARCHAR(64) PRIMARY KEY,
    name             VARCHAR(255)     NOT NULL,
    model            VARCHAR(255),
    year_manufacture INT CHECK (year_manufacture IS NULL OR
                                (year_manufacture >= 1900 AND year_manufacture <= EXTRACT(YEAR FROM CURRENT_DATE))),
    acquisition_date DATE,
    status           equipment_status NOT NULL DEFAULT 'in_service',
    disposed_date    DATE
);

CREATE TABLE equipment_movement
(
    move_id      BIGSERIAL PRIMARY KEY,
    inventory_no VARCHAR(64) NOT NULL REFERENCES equipment (inventory_no) ON DELETE CASCADE,
    from_dept_id INT         REFERENCES department (dept_id) ON DELETE SET NULL,
    to_dept_id   INT         NOT NULL REFERENCES department (dept_id),
    start_date   DATE        NOT NULL,
    end_date     DATE,
    doc_no       VARCHAR(128),
    CONSTRAINT em_unique_inventory_start UNIQUE (inventory_no, start_date),
    CONSTRAINT em_dates_check CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE employee
(
    employee_id SERIAL PRIMARY KEY,
    emp_number  VARCHAR(50) UNIQUE,
    last_name   VARCHAR(150) NOT NULL,
    first_name  VARCHAR(150) NOT NULL,
    middle_name VARCHAR(150),
    birth_date  DATE,
    gender      CHAR(1) CHECK (gender IN ('M', 'F', 'O')),
    phone       VARCHAR(50),
    email       VARCHAR(255)
);

CREATE TABLE employment_history
(
    hist_id     BIGSERIAL PRIMARY KEY,
    employee_id INT  NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
    dept_id     INT  NOT NULL REFERENCES department (dept_id) ON DELETE RESTRICT,
    position    VARCHAR(200),
    start_date  DATE NOT NULL,
    end_date    DATE,
    CONSTRAINT eh_dates_check CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE spare_part
(
    part_id     SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    part_code   VARCHAR(100) UNIQUE,
    unit        VARCHAR(50),
    description TEXT
);

CREATE TABLE invoice
(
    invoice_id    SERIAL PRIMARY KEY,
    invoice_no    VARCHAR(100) UNIQUE,
    date_received DATE NOT NULL,
    supplier      VARCHAR(255),
    total_amount  NUMERIC(14, 2) CHECK (total_amount >= 0)
);

CREATE TABLE repair
(
    repair_id           BIGSERIAL PRIMARY KEY,
    inventory_no        VARCHAR(64)   NOT NULL REFERENCES equipment (inventory_no) ON DELETE CASCADE,
    submitted_date      DATE          NOT NULL DEFAULT CURRENT_DATE,
    repair_type         VARCHAR(200),
    expected_end_date   DATE,
    actual_end_date     DATE,
    handed_by_emp_id    INT REFERENCES employee (employee_id),
    accepted_by_emp_id  INT REFERENCES employee (employee_id),
    performed_by_emp_id INT REFERENCES employee (employee_id),
    notes               TEXT,
    status              repair_status NOT NULL DEFAULT 'opened',
    CONSTRAINT repair_dates_check CHECK (
        (expected_end_date IS NULL OR expected_end_date >= submitted_date)
            AND (actual_end_date IS NULL OR actual_end_date >= submitted_date)
        )
);

CREATE TABLE repair_part
(
    rp_id        BIGSERIAL PRIMARY KEY,
    repair_id    BIGINT         NOT NULL REFERENCES repair (repair_id) ON DELETE CASCADE,
    part_id      INT            NOT NULL REFERENCES spare_part (part_id) ON DELETE RESTRICT,
    invoice_id   INT            REFERENCES invoice (invoice_id) ON DELETE SET NULL,
    quantity     INT            NOT NULL CHECK (quantity > 0), -- запрошено
    qty_reserved INT            NOT NULL DEFAULT 0 CHECK (qty_reserved >= 0),
    qty_consumed INT            NOT NULL DEFAULT 0 CHECK (qty_consumed >= 0),
    unit_price   NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0),
    total_price  NUMERIC(16, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED
);

CREATE TABLE sparepart_stock
(
    stock_id      BIGSERIAL PRIMARY KEY,
    part_id       INT  NOT NULL REFERENCES spare_part (part_id) ON DELETE CASCADE,
    invoice_id    INT  REFERENCES invoice (invoice_id) ON DELETE SET NULL,
    batch_no      VARCHAR(100),
    qty_received  INT  NOT NULL CHECK (qty_received >= 0),
    qty_reserved  INT  NOT NULL DEFAULT 0 CHECK (qty_reserved >= 0),
    qty_available INT  NOT NULL CHECK (qty_available >= 0),
    unit_cost     NUMERIC(14, 2),
    location      VARCHAR(100),
    received_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date   DATE,
    notes         TEXT
);

CREATE TABLE stock_movement
(
    movement_id   BIGSERIAL PRIMARY KEY,
    stock_id      BIGINT                      REFERENCES sparepart_stock (stock_id) ON DELETE SET NULL,
    part_id       INT                         NOT NULL REFERENCES spare_part (part_id) ON DELETE CASCADE,
    repair_id     BIGINT                      REFERENCES repair (repair_id) ON DELETE SET NULL,
    invoice_id    INT                         REFERENCES invoice (invoice_id) ON DELETE SET NULL,
    movement_type stock_movement_type         NOT NULL,
    quantity      INT                         NOT NULL CHECK (quantity > 0),
    unit_cost     NUMERIC(14, 2),
    movement_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    employee_id   INT                         REFERENCES employee (employee_id) ON DELETE SET NULL,
    doc_no        VARCHAR(128),
    notes         TEXT
);

CREATE INDEX idx_equipment_movement_inventory_start ON equipment_movement (inventory_no, start_date);
CREATE INDEX idx_equipment_movement_to_dept_start ON equipment_movement (to_dept_id, start_date);
CREATE INDEX idx_repair_inventory ON repair (inventory_no);
CREATE INDEX idx_repair_submitted ON repair (submitted_date);
CREATE INDEX idx_emp_history_emp ON employment_history (employee_id, start_date);
CREATE INDEX idx_repair_part_part ON repair_part (part_id);
CREATE INDEX idx_invoice_date ON invoice (date_received);
CREATE INDEX idx_sparepart_stock_part_available ON sparepart_stock (part_id, qty_available, received_date);
CREATE INDEX idx_sparepart_stock_invoice ON sparepart_stock (invoice_id);
CREATE INDEX idx_stock_movement_part ON stock_movement (part_id);
CREATE INDEX idx_stock_movement_repair ON stock_movement (repair_id);

-- Выдача прав доступа для admin_user
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO admin_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO admin_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO admin_user;