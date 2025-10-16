CREATE TABLE IF NOT EXISTS departments
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50) UNIQUE,
    name        TEXT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS employees
(
    id               SERIAL PRIMARY KEY,
    last_name        TEXT NOT NULL,
    first_name       TEXT NOT NULL,
    middle_name      TEXT,
    birth_date       DATE,
    personnel_number VARCHAR(50) UNIQUE,
    gender           VARCHAR(10) CHECK (gender IN ('M', 'F', 'Other')) DEFAULT 'Other',
    hire_date        DATE,
    fired_date       DATE,
    phone            TEXT,
    email            TEXT
);

CREATE TABLE IF NOT EXISTS employee_positions
(
    id             SERIAL PRIMARY KEY,
    employee_id    INT  NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    department_id  INT  NOT NULL REFERENCES departments (id) ON DELETE RESTRICT,
    position_title TEXT NOT NULL,
    date_from      DATE NOT NULL,
    date_to        DATE,
    CONSTRAINT emp_pos_dates CHECK (date_to IS NULL OR date_from <= date_to)
);

CREATE TABLE IF NOT EXISTS equipment
(
    id               SERIAL PRIMARY KEY,
    inventory_number VARCHAR(100) UNIQUE NOT NULL,
    name             TEXT                NOT NULL,
    model            TEXT,
    manufacture_year INT CHECK (manufacture_year >= 1900 AND
                                manufacture_year <= EXTRACT(YEAR FROM CURRENT_DATE)::INT + 1),
    purchase_date    DATE,
    status           VARCHAR(30)         NOT NULL DEFAULT 'operational',
    note             TEXT
);

CREATE TABLE IF NOT EXISTS equipment_movements
(
    id                  SERIAL PRIMARY KEY,
    equipment_id        INT  NOT NULL REFERENCES equipment (id) ON DELETE CASCADE,
    department_id       INT  NOT NULL REFERENCES departments (id) ON DELETE RESTRICT,
    movement_doc_number VARCHAR(100),
    movement_date       DATE NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS write_offs
(
    id                  SERIAL PRIMARY KEY,
    equipment_id        INT  NOT NULL REFERENCES equipment (id) ON DELETE CASCADE,
    writeoff_date       DATE NOT NULL,
    reason              TEXT,
    writeoff_doc_number VARCHAR(100),
    created_by          INT REFERENCES employees (id),
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS invoices
(
    id             SERIAL PRIMARY KEY,
    invoice_number VARCHAR(100) UNIQUE NOT NULL,
    supplier       TEXT,
    invoice_date   DATE                NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS spare_parts
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE,
    name TEXT NOT NULL,
    unit TEXT
);

CREATE TABLE IF NOT EXISTS invoice_items
(
    id            SERIAL PRIMARY KEY,
    invoice_id    INT            NOT NULL REFERENCES invoices (id) ON DELETE CASCADE,
    spare_part_id INT            NOT NULL REFERENCES spare_parts (id) ON DELETE RESTRICT,
    quantity      NUMERIC(10, 2) NOT NULL CHECK (quantity > 0),
    unit_price    NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0)
);

CREATE TABLE IF NOT EXISTS repair_orders
(
    id                           SERIAL PRIMARY KEY,
    repair_number                VARCHAR(100) UNIQUE,
    equipment_id                 INT  NOT NULL REFERENCES equipment (id) ON DELETE CASCADE,
    date_submitted               DATE NOT NULL,
    repair_type                  VARCHAR(100),
    expected_finish_date         DATE,
    submitted_by_employee_id     INT REFERENCES employees (id),
    submitted_by_personnel_num   VARCHAR(50),
    accepted_by_employee_id      INT REFERENCES employees (id),
    repair_performer_employee_id INT REFERENCES employees (id),
    performer_position           TEXT,
    status                       VARCHAR(30)              DEFAULT 'open',
    note                         TEXT,
    created_at                   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS repair_spare_parts
(
    id              SERIAL PRIMARY KEY,
    repair_order_id INT            NOT NULL REFERENCES repair_orders (id) ON DELETE CASCADE,
    spare_part_id   INT            NOT NULL REFERENCES spare_parts (id) ON DELETE RESTRICT,
    invoice_item_id INT            REFERENCES invoice_items (id) ON DELETE SET NULL,
    quantity        NUMERIC(10, 2) NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0)
);

CREATE INDEX IF NOT EXISTS idx_eq_invnum ON equipment (inventory_number);
CREATE INDEX IF NOT EXISTS idx_eq_mov_equip ON equipment_movements (equipment_id, movement_date);
CREATE INDEX IF NOT EXISTS idx_rep_by_equip ON repair_orders (equipment_id);
CREATE INDEX IF NOT EXISTS idx_emp_positions_emp ON employee_positions (employee_id, date_from, date_to);
CREATE INDEX IF NOT EXISTS idx_invoice_items_spare ON invoice_items (spare_part_id);
