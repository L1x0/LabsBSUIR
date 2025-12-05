\connect repair_department_db

SET search_path = public;

CREATE OR REPLACE
FUNCTION fn_em_close_previous() RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    prev_move RECORD;
BEGIN
    SELECT * INTO prev_move
    FROM equipment_movement
    WHERE inventory_no = NEW.inventory_no
      AND end_date IS NULL
    FOR UPDATE;

    IF FOUND THEN
        IF NEW.start_date < prev_move.start_date THEN
            RAISE EXCEPTION 'New movement start_date (%) is before previous movement start_date (%)', NEW.start_date, prev_move.start_date;
        END IF;

        UPDATE equipment_movement
        SET end_date = NEW.start_date
        WHERE move_id = prev_move.move_id;

        IF NEW.from_dept_id IS NULL THEN
            NEW.from_dept_id := prev_move.to_dept_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_em_close_prev ON equipment_movement;
CREATE TRIGGER trg_em_close_prev
    BEFORE INSERT
    ON equipment_movement
    FOR EACH ROW EXECUTE FUNCTION fn_em_close_previous();


CREATE OR REPLACE
FUNCTION fn_repair_update_equipment_status() RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        IF NEW.status IN ('opened','in_progress') THEN
            UPDATE equipment SET status = 'in_repair' WHERE inventory_no = NEW.inventory_no;
        ELSIF NEW.status = 'closed' OR NEW.status = 'cancelled' THEN
            UPDATE equipment SET status = 'in_service' WHERE inventory_no = NEW.inventory_no AND status = 'in_repair';
            IF NEW.actual_end_date IS NULL THEN
                NEW.actual_end_date := CURRENT_DATE;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_repair_status_to_equipment ON repair;
CREATE TRIGGER trg_repair_status_to_equipment
    BEFORE INSERT OR UPDATE ON repair
    FOR EACH ROW EXECUTE FUNCTION fn_repair_update_equipment_status();


CREATE OR REPLACE
FUNCTION add_equipment(
    p_inventory_no VARCHAR,
    p_name VARCHAR,
    p_model VARCHAR,
    p_year_manufacture INT DEFAULT NULL,
    p_acquisition_date DATE DEFAULT NULL,
    p_initial_dept_id INT DEFAULT NULL,
    p_status equipment_status DEFAULT 'in_service'
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
    IF p_inventory_no IS NULL OR p_name IS NULL THEN
        RAISE EXCEPTION 'inventory_no and name are required';
    END IF;

    INSERT INTO equipment(inventory_no, name, model, year_manufacture, acquisition_date, status)
    VALUES (p_inventory_no, p_name, p_model, p_year_manufacture, p_acquisition_date, p_status);

    IF p_initial_dept_id IS NOT NULL THEN
        INSERT INTO equipment_movement(inventory_no, from_dept_id, to_dept_id, start_date)
        VALUES (p_inventory_no, NULL, p_initial_dept_id, COALESCE(p_acquisition_date, CURRENT_DATE));
    END IF;
END;
$$;

CREATE OR REPLACE
FUNCTION edit_equipment(
    p_inventory_no VARCHAR,
    p_name VARCHAR DEFAULT NULL,
    p_model VARCHAR DEFAULT NULL,
    p_year_manufacture INT DEFAULT NULL,
    p_acquisition_date DATE DEFAULT NULL,
    p_status equipment_status DEFAULT NULL,
    p_disposed_date DATE DEFAULT NULL
) RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    e RECORD;
BEGIN
    SELECT * INTO e FROM equipment WHERE inventory_no = p_inventory_no FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Equipment % not found', p_inventory_no;
    END IF;

    IF p_disposed_date IS NOT NULL AND (p_status IS NULL AND e.status NOT IN ('written_off','disposed')) THEN
        RAISE EXCEPTION 'Cannot set disposed_date for equipment % without status written_off/disposed', p_inventory_no;
    END IF;

    UPDATE equipment
    SET name = COALESCE(p_name, name),
        model = COALESCE(p_model, model),
        year_manufacture = COALESCE(p_year_manufacture, year_manufacture),
        acquisition_date = COALESCE(p_acquisition_date, acquisition_date),
        status = COALESCE(p_status, status),
        disposed_date = COALESCE(p_disposed_date, disposed_date)
    WHERE inventory_no = p_inventory_no;
END;
$$;

CREATE OR REPLACE
FUNCTION write_off_equipment(
    p_inventory_no VARCHAR,
    p_disposed_date DATE DEFAULT CURRENT_DATE,
    p_new_status equipment_status DEFAULT 'written_off',
    p_reason TEXT DEFAULT NULL
) RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    r_count INT;
BEGIN
    PERFORM 1 FROM equipment WHERE inventory_no = p_inventory_no;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Equipment % not found', p_inventory_no;
    END IF;

    SELECT COUNT(*) INTO r_count FROM repair WHERE inventory_no = p_inventory_no AND status IN ('opened','in_progress');
    IF r_count > 0 THEN
        RAISE EXCEPTION 'Cannot write off equipment %: there is an open repair', p_inventory_no;
    END IF;

    UPDATE equipment
    SET status = p_new_status,
        disposed_date = p_disposed_date
    WHERE inventory_no = p_inventory_no;
END;
$$;

CREATE OR REPLACE
FUNCTION move_equipment_proc(
    p_inventory_no VARCHAR,
    p_to_dept_id INT,
    p_start_date DATE,
    p_doc_no VARCHAR DEFAULT NULL,
    p_from_dept_id INT DEFAULT NULL
) RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
    new_move_id BIGINT;
BEGIN
    IF p_inventory_no IS NULL OR p_to_dept_id IS NULL OR p_start_date IS NULL THEN
        RAISE EXCEPTION 'inventory_no, to_dept_id and start_date are required';
    END IF;

    INSERT INTO equipment_movement(inventory_no, from_dept_id, to_dept_id, start_date, doc_no)
    VALUES (p_inventory_no, p_from_dept_id, p_to_dept_id, p_start_date, p_doc_no)
    RETURNING move_id INTO new_move_id;

    RETURN new_move_id;
END;
$$;

CREATE OR REPLACE
FUNCTION submit_to_repair(
    p_inventory_no VARCHAR,
    p_repair_type VARCHAR DEFAULT NULL,
    p_expected_end_date DATE DEFAULT NULL,
    p_handed_by_emp_id INT DEFAULT NULL,
    p_accepted_by_emp_id INT DEFAULT NULL,
    p_workshop_dept_id INT DEFAULT NULL,
    p_notes TEXT DEFAULT NULL
) RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
    existing_open INT;
    new_repair_id BIGINT;
BEGIN
    IF p_inventory_no IS NULL THEN
        RAISE EXCEPTION 'inventory_no is required';
    END IF;

    PERFORM 1 FROM equipment WHERE inventory_no = p_inventory_no;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Equipment % not found', p_inventory_no;
    END IF;

    IF (SELECT status FROM equipment WHERE inventory_no = p_inventory_no) IN ('written_off','disposed') THEN
        RAISE EXCEPTION 'Cannot submit equipment % to repair: it is written_off/disposed', p_inventory_no;
    END IF;

    SELECT COUNT(*) INTO existing_open FROM repair WHERE inventory_no = p_inventory_no AND status IN ('opened','in_progress');
    IF existing_open > 0 THEN
        RAISE EXCEPTION 'There is already an open/in_progress repair for equipment %', p_inventory_no;
    END IF;

    INSERT INTO repair(inventory_no, submitted_date, repair_type, expected_end_date, handed_by_emp_id, accepted_by_emp_id, notes, status)
    VALUES (p_inventory_no, CURRENT_DATE, p_repair_type, p_expected_end_date, p_handed_by_emp_id, p_accepted_by_emp_id, p_notes, 'opened')
    RETURNING repair_id INTO new_repair_id;

    UPDATE equipment SET status = 'in_repair' WHERE inventory_no = p_inventory_no;

    IF p_workshop_dept_id IS NOT NULL THEN
        PERFORM move_equipment_proc(p_inventory_no, p_workshop_dept_id, CURRENT_DATE, 'repair_transfer', NULL);
    END IF;

    RETURN new_repair_id;
END;
$$;

CREATE OR REPLACE
FUNCTION finish_repair(
    p_repair_id BIGINT,
    p_actual_end_date DATE DEFAULT CURRENT_DATE,
    p_performed_by_emp_id INT DEFAULT NULL,
    p_final_status repair_status DEFAULT 'closed',
    p_notes TEXT DEFAULT NULL
) RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    r RECORD;
BEGIN
    SELECT * INTO r FROM repair WHERE repair_id = p_repair_id FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Repair % not found', p_repair_id;
    END IF;

    UPDATE repair
    SET actual_end_date = COALESCE(p_actual_end_date, actual_end_date),
        performed_by_emp_id = COALESCE(p_performed_by_emp_id, performed_by_emp_id),
        status = p_final_status,
        notes = COALESCE(p_notes, notes)
    WHERE repair_id = p_repair_id;

    IF p_final_status IN ('closed','cancelled') THEN
        UPDATE equipment SET status = 'in_service' WHERE inventory_no = r.inventory_no AND status = 'in_repair';
    END IF;
END;
$$;

CREATE OR REPLACE
FUNCTION add_employee(
    p_last_name   VARCHAR,
    p_first_name  VARCHAR,
    p_emp_number  VARCHAR DEFAULT NULL,
    p_middle_name VARCHAR DEFAULT NULL,
    p_birth_date  DATE    DEFAULT NULL,
    p_gender      CHAR    DEFAULT NULL,
    p_phone       VARCHAR DEFAULT NULL,
    p_email       VARCHAR DEFAULT NULL
) RETURNS INT LANGUAGE plpgsql AS $$
DECLARE
    new_id INT;
BEGIN
    IF p_last_name IS NULL OR p_first_name IS NULL THEN
        RAISE EXCEPTION 'last_name and first_name are required';
    END IF;

    INSERT INTO employee(emp_number, last_name, first_name, middle_name, birth_date, gender, phone, email)
    VALUES (p_emp_number, p_last_name, p_first_name, p_middle_name, p_birth_date, p_gender, p_phone, p_email)
    RETURNING employee_id INTO new_id;

    RETURN new_id;
END;
$$;


CREATE OR REPLACE
FUNCTION edit_employee(
    p_employee_id INT,
    p_emp_number VARCHAR DEFAULT NULL,
    p_last_name VARCHAR DEFAULT NULL,
    p_first_name VARCHAR DEFAULT NULL,
    p_middle_name VARCHAR DEFAULT NULL,
    p_birth_date DATE DEFAULT NULL,
    p_gender CHAR DEFAULT NULL,
    p_phone VARCHAR DEFAULT NULL,
    p_email VARCHAR DEFAULT NULL
) RETURNS VOID LANGUAGE plpgsql AS $$
BEGIN
    PERFORM 1 FROM employee WHERE employee_id = p_employee_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Employee % not found', p_employee_id;
    END IF;

    UPDATE employee
    SET emp_number = COALESCE(p_emp_number, emp_number),
        last_name = COALESCE(p_last_name, last_name),
        first_name = COALESCE(p_first_name, first_name),
        middle_name = COALESCE(p_middle_name, middle_name),
        birth_date = COALESCE(p_birth_date, birth_date),
        gender = COALESCE(p_gender, gender),
        phone = COALESCE(p_phone, phone),
        email = COALESCE(p_email, email)
    WHERE employee_id = p_employee_id;
END;
$$;

CREATE OR REPLACE
FUNCTION delete_employee(p_employee_id INT) RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    hcnt INT;
    rcnt INT;
BEGIN
    SELECT COUNT(*) INTO hcnt FROM employment_history WHERE employee_id = p_employee_id;
    IF hcnt > 0 THEN
        RAISE EXCEPTION 'Cannot delete employee %: there are employment_history rows', p_employee_id;
    END IF;

    SELECT COUNT(*) INTO rcnt FROM repair WHERE handed_by_emp_id = p_employee_id OR accepted_by_emp_id = p_employee_id OR performed_by_emp_id = p_employee_id;
    IF rcnt > 0 THEN
        RAISE EXCEPTION 'Cannot delete employee %: referenced in repairs', p_employee_id;
    END IF;

    DELETE FROM employee WHERE employee_id = p_employee_id;
END;
$$;

CREATE OR REPLACE
FUNCTION add_employment_history(
    p_employee_id INT,
    p_dept_id INT,
    p_start_date DATE,
    p_position VARCHAR DEFAULT NULL,
    p_end_date DATE DEFAULT NULL
) RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
    new_hist_id BIGINT;
BEGIN
    IF p_employee_id IS NULL OR p_dept_id IS NULL OR p_start_date IS NULL THEN
        RAISE EXCEPTION 'employee_id, dept_id and start_date are required';
    END IF;

    INSERT INTO employment_history(employee_id, dept_id, position, start_date, end_date)
    VALUES (p_employee_id, p_dept_id, p_position, p_start_date, p_end_date)
    RETURNING hist_id INTO new_hist_id;

    RETURN new_hist_id;
END;
$$;

CREATE OR REPLACE
FUNCTION report_equipment_count_3years(p_dept_id INT, p_name VARCHAR)
RETURNS TABLE (year INT, cnt BIGINT) LANGUAGE plpgsql AS $$
DECLARE
    cur_year INT := EXTRACT(YEAR FROM CURRENT_DATE)::INT;
    y INT;
BEGIN
    IF p_dept_id IS NULL OR p_name IS NULL OR p_name = '' THEN
        RAISE EXCEPTION 'dept_id and name are required';
    END IF;

    FOR y IN cur_year-2..cur_year LOOP
        RETURN QUERY
        SELECT y AS year,
               COUNT(DISTINCT e.inventory_no) AS cnt
        FROM equipment e
        JOIN equipment_movement em ON em.inventory_no = e.inventory_no
        WHERE e.name = p_name
          AND em.to_dept_id = p_dept_id
          AND em.start_date <= make_date(y,12,31)
          AND (em.end_date IS NULL OR em.end_date >= make_date(y,1,1));
    END LOOP;
END;
$$;

CREATE OR REPLACE
FUNCTION report_employees_by_dept(p_dept_id INT)
RETURNS TABLE(employee_id INT, fio TEXT, birth_year INT) LANGUAGE plpgsql AS $$
BEGIN
    IF p_dept_id IS NULL THEN
        RAISE EXCEPTION 'dept_id is required';
    END IF;

    RETURN QUERY
    SELECT e.employee_id,
           (e.last_name || ' ' || e.first_name || COALESCE(' '||e.middle_name,'')) AS fio,
           CASE WHEN e.birth_date IS NOT NULL THEN EXTRACT(YEAR FROM e.birth_date)::INT ELSE NULL END AS birth_year
    FROM employee e
    JOIN employment_history h ON h.employee_id = e.employee_id
    WHERE h.dept_id = p_dept_id
      AND (h.end_date IS NULL OR h.end_date >= CURRENT_DATE)
    ORDER BY e.last_name, e.first_name;
END;
$$;

CREATE OR REPLACE
FUNCTION report_employees_by_age_gender(p_age INT, p_gender CHAR)
RETURNS TABLE(employee_id INT, fio TEXT, birth_year INT, age INT, gender CHAR) LANGUAGE plpgsql AS $$
BEGIN
    IF p_age IS NULL OR p_gender IS NULL OR p_gender = '' THEN
        RAISE EXCEPTION 'age and gender are required';
    END IF;

    RETURN QUERY
    SELECT e.employee_id,
           (e.last_name || ' ' || e.first_name || COALESCE(' '||e.middle_name,'')) AS fio,
           CASE WHEN e.birth_date IS NOT NULL THEN EXTRACT(YEAR FROM e.birth_date)::INT ELSE NULL END AS birth_year,
           CASE WHEN e.birth_date IS NOT NULL THEN FLOOR(EXTRACT(YEAR FROM age(CURRENT_DATE, e.birth_date)))::INT ELSE NULL END AS age,
           e.gender
    FROM employee e
    WHERE e.birth_date IS NOT NULL
      AND FLOOR(EXTRACT(YEAR FROM age(CURRENT_DATE, e.birth_date)))::INT = p_age
      AND e.gender = p_gender
    ORDER BY e.last_name;
END;
$$;

CREATE OR REPLACE
FUNCTION report_dept_most_sent_to_repair()
RETURNS TABLE(dept_id INT, dept_name TEXT, repairs_count BIGINT) LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    WITH repair_locations AS (
        SELECT r.repair_id, r.inventory_no, r.submitted_date,
               em.to_dept_id AS dept_id
        FROM repair r
        JOIN LATERAL (
            SELECT * FROM equipment_movement em
            WHERE em.inventory_no = r.inventory_no
              AND em.start_date <= r.submitted_date
              AND (em.end_date IS NULL OR em.end_date >= r.submitted_date)
            ORDER BY em.start_date DESC
            LIMIT 1
        ) em ON true
    )
    SELECT d.dept_id, d.name::TEXT, COUNT(rl.repair_id) AS repairs_count
    FROM repair_locations rl
    JOIN department d ON d.dept_id = rl.dept_id
    GROUP BY d.dept_id, d.name
    ORDER BY repairs_count DESC
    LIMIT 1;
END;
$$;

CREATE OR REPLACE
FUNCTION fn_em_no_noop() RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.from_dept_id IS NOT NULL AND NEW.from_dept_id = NEW.to_dept_id THEN
        RAISE EXCEPTION 'Cannot move equipment % to the same department %', NEW.inventory_no, NEW.to_dept_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_em_no_noop ON equipment_movement;
CREATE TRIGGER trg_em_no_noop
    BEFORE INSERT
    ON equipment_movement
    FOR EACH ROW EXECUTE FUNCTION fn_emаа_no_noop();

CREATE OR REPLACE
FUNCTION fn_dept_restrict_delete() RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    cnt INT;
BEGIN
    SELECT COUNT(*) INTO cnt FROM equipment_movement WHERE to_dept_id = OLD.dept_id OR from_dept_id = OLD.dept_id;
    IF cnt > 0 THEN
        RAISE EXCEPTION 'Cannot delete department %: there are equipment movements referencing it', OLD.dept_id;
    END IF;

    SELECT COUNT(*) INTO cnt FROM employment_history WHERE dept_id = OLD.dept_id;
    IF cnt > 0 THEN
        RAISE EXCEPTION 'Cannot delete department %: there are employment_history rows', OLD.dept_id;
    END IF;

    RETURN OLD;
END;
$$;

DROP TRIGGER IF EXISTS trg_dept_restrict_delete ON department;
CREATE TRIGGER trg_dept_restrict_delete
    BEFORE DELETE
    ON department
    FOR EACH ROW EXECUTE FUNCTION fn_dept_restrict_delete();

CREATE OR REPLACE
FUNCTION add_department(
    p_name VARCHAR,
    p_code VARCHAR DEFAULT NULL,
    p_address TEXT DEFAULT NULL
) RETURNS INT LANGUAGE plpgsql AS $$
DECLARE
    new_dept_id INT;
BEGIN
    IF p_name IS NULL OR p_name = '' THEN
        RAISE EXCEPTION 'Department name is required';
    END IF;

    INSERT INTO department(name, code, address)
    VALUES (p_name, p_code, p_address)
    RETURNING dept_id INTO new_dept_id;

    RETURN new_dept_id;
END;
$$;

CREATE OR REPLACE
FUNCTION disband_department(p_dept_id INT) RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    emp_rec RECORD;
    emp_count INT := 0;
    deleted_count INT := 0;
BEGIN
    IF p_dept_id IS NULL THEN
        RAISE EXCEPTION 'dept_id is required';
    END IF;

    PERFORM 1 FROM department WHERE dept_id = p_dept_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Department % not found', p_dept_id;
    END IF;

    FOR emp_rec IN
        SELECT DISTINCT e.employee_id
        FROM employee e
        JOIN employment_history eh ON eh.employee_id = e.employee_id
        WHERE eh.dept_id = p_dept_id
          AND (eh.end_date IS NULL OR eh.end_date >= CURRENT_DATE)
    LOOP
        emp_count := emp_count + 1;
        
        DELETE FROM employment_history 
        WHERE employee_id = emp_rec.employee_id 
          AND dept_id = p_dept_id;
        
        IF NOT EXISTS (
            SELECT 1 FROM employment_history 
            WHERE employee_id = emp_rec.employee_id
        ) AND NOT EXISTS (
            SELECT 1 FROM repair 
            WHERE handed_by_emp_id = emp_rec.employee_id 
               OR accepted_by_emp_id = emp_rec.employee_id 
               OR performed_by_emp_id = emp_rec.employee_id
        ) THEN
            DELETE FROM employee WHERE employee_id = emp_rec.employee_id;
            deleted_count := deleted_count + 1;
        END IF;
    END LOOP;

    DELETE FROM employment_history WHERE dept_id = p_dept_id;

    IF EXISTS (
        SELECT 1 FROM equipment_movement 
        WHERE to_dept_id = p_dept_id OR from_dept_id = p_dept_id
    ) THEN
        RAISE EXCEPTION 'Cannot disband department %: there are equipment movements referencing it', p_dept_id;
    END IF;

    DELETE FROM department WHERE dept_id = p_dept_id;
END;
$$;
