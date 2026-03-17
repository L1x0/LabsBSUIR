import streamlit as st
from db_connection import get_db_connection, execute_query, execute_function
from datetime import date, datetime
import psycopg2
import pandas as pd

st.set_page_config(page_title="Ремонт оборудования", layout="wide")

if 'current_tab' not in st.session_state:
    st.session_state.current_tab = 'Оборудование'

def get_departments():
    return execute_query("SELECT dept_id, name FROM department ORDER BY name")

def get_employees():
    return execute_query("""
        SELECT employee_id, 
               last_name || ' ' || first_name || COALESCE(' ' || middle_name, '') as fio,
               emp_number
        FROM employee 
        ORDER BY last_name, first_name
    """)

def get_equipment_list():
    return execute_query("""
        SELECT inventory_no, name, model, status 
        FROM equipment 
        ORDER BY name
    """)

def get_repairs():
    return execute_query("""
        SELECT r.repair_id, r.inventory_no, e.name as equipment_name,
               r.submitted_date, r.status, r.repair_type
        FROM repair r
        JOIN equipment e ON e.inventory_no = r.inventory_no
        ORDER BY r.submitted_date DESC
    """)

st.title("🏢 Система учета ремонта оборудования")

tabs = st.tabs([
    "Оборудование", 
    "Перемещение", 
    "Ремонт", 
    "Сотрудники",
    "Подразделения",
    "Отчеты"
])

with tabs[0]:
    st.header("Управление оборудованием")
    
    equipment_tab = st.radio(
        "Выберите действие:",
        ["Добавить оборудование", "Редактировать оборудование", "Списать оборудование", "Список оборудования"],
        horizontal=True
    )
    
    if equipment_tab == "Добавить оборудование":
        st.subheader("Добавление нового оборудования")
        with st.form("add_equipment_form"):
            col1, col2 = st.columns(2)
            with col1:
                inventory_no = st.text_input("Инвентарный номер *", key="add_inv_no")
                name = st.text_input("Название *", key="add_name")
                model = st.text_input("Модель", key="add_model")
                year_manufacture = st.number_input("Год выпуска", min_value=1900, max_value=date.today().year, value=None, key="add_year")
            with col2:
                acquisition_date = st.date_input("Дата приобретения", value=None, key="add_acq_date")
                status = st.selectbox("Статус", ["in_service", "in_repair", "written_off", "disposed"], index=0, key="add_status")
                depts = get_departments()
                dept_dict = {d['name']: d['dept_id'] for d in depts}
                initial_dept = st.selectbox("Начальное подразделение", ["Не указано"] + list(dept_dict.keys()), key="add_dept")
            
            submitted = st.form_submit_button("Добавить")
            if submitted:
                if not inventory_no or not name:
                    st.error("Инвентарный номер и название обязательны!")
                else:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        dept_id = dept_dict.get(initial_dept) if initial_dept != "Не указано" else None
                        cur.execute("""
                            SELECT add_equipment(%s, %s, %s, %s, %s, %s, %s)
                        """, (
                            inventory_no, name, model if model else None,
                            year_manufacture if year_manufacture else None,
                            acquisition_date if acquisition_date else None,
                            dept_id, status
                        ))
                        conn.commit()
                        st.success(f"Оборудование {inventory_no} успешно добавлено!")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
    
    elif equipment_tab == "Редактировать оборудование":
        st.subheader("Редактирование оборудования")
        equipment_list = get_equipment_list()
        if equipment_list:
            eq_dict = {f"{e['inventory_no']} - {e['name']}": e['inventory_no'] for e in equipment_list}
            selected_eq = st.selectbox("Выберите оборудование", list(eq_dict.keys()))
            
            if selected_eq:
                inv_no = eq_dict[selected_eq]
                eq_data = execute_query("SELECT * FROM equipment WHERE inventory_no = %s", (inv_no,))[0]
                
                with st.form("edit_equipment_form"):
                    col1, col2 = st.columns(2)
                    with col1:
                        name = st.text_input("Название", value=eq_data['name'], key="edit_name")
                        model = st.text_input("Модель", value=eq_data['model'] or "", key="edit_model")
                        year_manufacture = st.number_input("Год выпуска", min_value=1900, max_value=date.today().year, 
                                                          value=eq_data['year_manufacture'], key="edit_year")
                    with col2:
                        acquisition_date = st.date_input("Дата приобретения", value=eq_data['acquisition_date'], key="edit_acq_date")
                        status = st.selectbox("Статус", ["in_service", "in_repair", "written_off", "disposed"], 
                                             index=list(["in_service", "in_repair", "written_off", "disposed"]).index(eq_data['status']), 
                                             key="edit_status")
                        disposed_date = st.date_input("Дата списания", value=eq_data['disposed_date'], key="edit_disposed")
                    
                    submitted = st.form_submit_button("Сохранить изменения")
                    if submitted:
                        try:
                            conn = get_db_connection()
                            cur = conn.cursor()
                            cur.execute("""
                                SELECT edit_equipment(%s, %s, %s, %s, %s, %s, %s)
                            """, (
                                inv_no, name, model if model else None,
                                year_manufacture if year_manufacture else None,
                                acquisition_date if acquisition_date else None,
                                status, disposed_date if disposed_date else None
                            ))
                            conn.commit()
                            st.success("Оборудование успешно обновлено!")
                        except Exception as e:
                            st.error(f"Ошибка: {e}")
                        finally:
                            conn.close()
        else:
            st.info("Нет оборудования для редактирования")
    
    elif equipment_tab == "Списать оборудование":
        st.subheader("Списание оборудования")
        equipment_list = get_equipment_list()
        active_eq = [e for e in equipment_list if e['status'] not in ['written_off', 'disposed']]
        if active_eq:
            eq_dict = {f"{e['inventory_no']} - {e['name']}": e['inventory_no'] for e in active_eq}
            selected_eq = st.selectbox("Выберите оборудование для списания", list(eq_dict.keys()))
            
            with st.form("writeoff_form"):
                disposed_date = st.date_input("Дата списания", value=date.today())
                status = st.selectbox("Статус", ["written_off", "disposed"], index=0)
                reason = st.text_area("Причина списания")
                
                submitted = st.form_submit_button("Списать")
                if submitted:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT write_off_equipment(%s, %s, %s, %s)
                        """, (eq_dict[selected_eq], disposed_date, status, reason if reason else None))
                        conn.commit()
                        st.success("Оборудование успешно списано!")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
        else:
            st.info("Нет активного оборудования для списания")
    
    else:
        st.subheader("Список оборудования")
        equipment_list = get_equipment_list()
        if equipment_list:
            st.dataframe(equipment_list, use_container_width=True)
        else:
            st.info("Оборудование не найдено")

with tabs[1]:
    st.header("Перемещение оборудования между подразделениями")
    
    equipment_list = get_equipment_list()
    active_eq = [e for e in equipment_list if e['status'] not in ['written_off', 'disposed']]
    
    if active_eq:
        eq_dict = {f"{e['inventory_no']} - {e['name']}": e['inventory_no'] for e in active_eq}
        depts = get_departments()
        dept_dict = {d['name']: d['dept_id'] for d in depts}
        
        with st.form("move_equipment_form"):
            col1, col2 = st.columns(2)
            with col1:
                selected_eq = st.selectbox("Оборудование *", list(eq_dict.keys()))
                to_dept = st.selectbox("В подразделение *", list(dept_dict.keys()))
            with col2:
                start_date = st.date_input("Дата перемещения *", value=date.today())
                doc_no = st.text_input("Номер документа")
            
            submitted = st.form_submit_button("Переместить")
            if submitted:
                try:
                    conn = get_db_connection()
                    cur = conn.cursor()
                    cur.execute("""
                        SELECT move_equipment_proc(%s, %s, %s, %s, %s)
                    """, (
                        eq_dict[selected_eq], dept_dict[to_dept], start_date,
                        doc_no if doc_no else None, None
                    ))
                    move_id = cur.fetchone()[0]
                    conn.commit()
                    st.success(f"Оборудование успешно перемещено! ID перемещения: {move_id}")
                except Exception as e:
                    st.error(f"Ошибка: {e}")
                finally:
                    conn.close()
    else:
        st.info("Нет активного оборудования для перемещения")
    
    st.subheader("История перемещений")
    equipment_list_all = get_equipment_list()
    if equipment_list_all:
        eq_dict_all = {f"{e['inventory_no']} - {e['name']}": e['inventory_no'] for e in equipment_list_all}
        selected_eq_history = st.selectbox("Выберите оборудование", list(eq_dict_all.keys()), key="history_select")
        
        if selected_eq_history:
            history = execute_query("""
                SELECT em.move_id, em.start_date, em.end_date, em.doc_no,
                       d1.name as from_dept, d2.name as to_dept
                FROM equipment_movement em
                LEFT JOIN department d1 ON d1.dept_id = em.from_dept_id
                JOIN department d2 ON d2.dept_id = em.to_dept_id
                WHERE em.inventory_no = %s
                ORDER BY em.start_date DESC
            """, (eq_dict_all[selected_eq_history],))
            
            if history:
                st.dataframe(history, use_container_width=True)
            else:
                st.info("История перемещений пуста")

with tabs[2]:
    st.header("Учет ремонта техники")
    
    repair_tab = st.radio(
        "Выберите действие:",
        ["Сдать в ремонт", "Завершить ремонт", "Список ремонтов"],
        horizontal=True
    )
    
    if repair_tab == "Сдать в ремонт":
        st.subheader("Сдача техники в ремонт")
        equipment_list = get_equipment_list()
        active_eq = [e for e in equipment_list if e['status'] == 'in_service']
        
        if active_eq:
            eq_dict = {f"{e['inventory_no']} - {e['name']}": e['inventory_no'] for e in active_eq}
            employees = get_employees()
            emp_dict = {f"{e['fio']} ({e['emp_number'] or 'без номера'})": e['employee_id'] for e in employees}
            depts = get_departments()
            dept_dict = {d['name']: d['dept_id'] for d in depts}
            
            with st.form("submit_repair_form"):
                col1, col2 = st.columns(2)
                with col1:
                    selected_eq = st.selectbox("Оборудование *", list(eq_dict.keys()))
                    repair_type = st.text_input("Вид ремонта")
                    expected_end_date = st.date_input("Ожидаемая дата завершения")
                with col2:
                    handed_by = st.selectbox("Сдал в ремонт", ["Не указано"] + list(emp_dict.keys()))
                    accepted_by = st.selectbox("Принял в ремонт", ["Не указано"] + list(emp_dict.keys()))
                    workshop_dept = st.selectbox("Подразделение мастерской", ["Не указано"] + list(dept_dict.keys()))
                
                notes = st.text_area("Примечания")
                
                submitted = st.form_submit_button("Сдать в ремонт")
                if submitted:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT submit_to_repair(%s, %s, %s, %s, %s, %s, %s)
                        """, (
                            eq_dict[selected_eq],
                            repair_type if repair_type else None,
                            expected_end_date if expected_end_date else None,
                            emp_dict.get(handed_by) if handed_by != "Не указано" else None,
                            emp_dict.get(accepted_by) if accepted_by != "Не указано" else None,
                            dept_dict.get(workshop_dept) if workshop_dept != "Не указано" else None,
                            notes if notes else None
                        ))
                        repair_id = cur.fetchone()[0]
                        conn.commit()
                        st.success(f"Техника сдана в ремонт! ID ремонта: {repair_id}")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
        else:
            st.info("Нет оборудования в статусе 'in_service' для сдачи в ремонт")
    
    elif repair_tab == "Завершить ремонт":
        st.subheader("Завершение ремонта")
        repairs = get_repairs()
        active_repairs = [r for r in repairs if r['status'] in ['opened', 'in_progress']]
        
        if active_repairs:
            repair_dict = {f"#{r['repair_id']} - {r['equipment_name']} ({r['inventory_no']})": r['repair_id'] for r in active_repairs}
            selected_repair = st.selectbox("Выберите ремонт", list(repair_dict.keys()))
            
            employees = get_employees()
            emp_dict = {f"{e['fio']} ({e['emp_number'] or 'без номера'})": e['employee_id'] for e in employees}
            
            with st.form("finish_repair_form"):
                col1, col2 = st.columns(2)
                with col1:
                    actual_end_date = st.date_input("Фактическая дата завершения", value=date.today())
                    performed_by = st.selectbox("Выполнил ремонт", ["Не указано"] + list(emp_dict.keys()))
                with col2:
                    final_status = st.selectbox("Финальный статус", ["closed", "cancelled"], index=0)
                
                notes = st.text_area("Примечания")
                
                submitted = st.form_submit_button("Завершить ремонт")
                if submitted:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT finish_repair(%s, %s, %s, %s, %s)
                        """, (
                            repair_dict[selected_repair],
                            actual_end_date,
                            emp_dict.get(performed_by) if performed_by != "Не указано" else None,
                            final_status,
                            notes if notes else None
                        ))
                        conn.commit()
                        st.success("Ремонт успешно завершен!")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
        else:
            st.info("Нет активных ремонтов")
    
    else:  # Список ремонтов
        st.subheader("Список ремонтов")
        repairs = get_repairs()
        if repairs:
            st.dataframe(repairs, use_container_width=True)
        else:
            st.info("Ремонты не найдены")

# ВКЛАДКА 4: СОТРУДНИКИ
with tabs[3]:
    st.header("Управление сотрудниками")
    
    employee_tab = st.radio(
        "Выберите действие:",
        ["Добавить сотрудника", "Редактировать сотрудника", "Удалить сотрудника", 
         "Добавить историю трудоустройства", "Список сотрудников"],
        horizontal=True
    )
    
    if employee_tab == "Добавить сотрудника":
        st.subheader("Добавление нового сотрудника")
        with st.form("add_employee_form"):
            col1, col2 = st.columns(2)
            with col1:
                last_name = st.text_input("Фамилия *", key="add_emp_last")
                first_name = st.text_input("Имя *", key="add_emp_first")
                middle_name = st.text_input("Отчество", key="add_emp_middle")
                emp_number = st.text_input("Номер сотрудника", key="add_emp_num")
            with col2:
                birth_date = st.date_input("Дата рождения", value=None, key="add_emp_birth")
                gender = st.selectbox("Пол", ["Не указано", "M", "F", "O"], index=0, key="add_emp_gender")
                phone = st.text_input("Телефон", key="add_emp_phone")
                email = st.text_input("Email", key="add_emp_email")
            
            submitted = st.form_submit_button("Добавить")
            if submitted:
                if not last_name or not first_name:
                    st.error("Фамилия и имя обязательны!")
                else:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT add_employee(%s, %s, %s, %s, %s, %s, %s, %s)
                        """, (
                            last_name, first_name,
                            emp_number if emp_number else None,
                            middle_name if middle_name else None,
                            birth_date if birth_date else None,
                            gender if gender != "Не указано" else None,
                            phone if phone else None,
                            email if email else None
                        ))
                        emp_id = cur.fetchone()[0]
                        conn.commit()
                        st.success(f"Сотрудник успешно добавлен! ID: {emp_id}")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
    
    elif employee_tab == "Редактировать сотрудника":
        st.subheader("Редактирование сотрудника")
        employees = get_employees()
        if employees:
            emp_dict = {f"{e['fio']} ({e['emp_number'] or 'без номера'})": e['employee_id'] for e in employees}
            selected_emp = st.selectbox("Выберите сотрудника", list(emp_dict.keys()))
            
            if selected_emp:
                emp_id = emp_dict[selected_emp]
                emp_data = execute_query("SELECT * FROM employee WHERE employee_id = %s", (emp_id,))[0]
                
                with st.form("edit_employee_form"):
                    col1, col2 = st.columns(2)
                    with col1:
                        emp_number = st.text_input("Номер сотрудника", value=emp_data['emp_number'] or "", key="edit_emp_num")
                        last_name = st.text_input("Фамилия", value=emp_data['last_name'], key="edit_emp_last")
                        first_name = st.text_input("Имя", value=emp_data['first_name'], key="edit_emp_first")
                        middle_name = st.text_input("Отчество", value=emp_data['middle_name'] or "", key="edit_emp_middle")
                    with col2:
                        birth_date = st.date_input("Дата рождения", value=emp_data['birth_date'], key="edit_emp_birth")
                        gender_options = ["Не указано", "M", "F", "O"]
                        gender_idx = gender_options.index(emp_data['gender']) if emp_data['gender'] else 0
                        gender = st.selectbox("Пол", gender_options, index=gender_idx, key="edit_emp_gender")
                        phone = st.text_input("Телефон", value=emp_data['phone'] or "", key="edit_emp_phone")
                        email = st.text_input("Email", value=emp_data['email'] or "", key="edit_emp_email")
                    
                    submitted = st.form_submit_button("Сохранить изменения")
                    if submitted:
                        try:
                            conn = get_db_connection()
                            cur = conn.cursor()
                            cur.execute("""
                                SELECT edit_employee(%s, %s, %s, %s, %s, %s, %s, %s)
                            """, (
                                emp_id,
                                emp_number if emp_number else None,
                                last_name, first_name,
                                middle_name if middle_name else None,
                                birth_date if birth_date else None,
                                gender if gender != "Не указано" else None,
                                phone if phone else None,
                                email if email else None
                            ))
                            conn.commit()
                            st.success("Сотрудник успешно обновлен!")
                        except Exception as e:
                            st.error(f"Ошибка: {e}")
                        finally:
                            conn.close()
        else:
            st.info("Нет сотрудников для редактирования")
    
    elif employee_tab == "Удалить сотрудника":
        st.subheader("Удаление сотрудника")
        employees = get_employees()
        if employees:
            emp_dict = {f"{e['fio']} ({e['emp_number'] or 'без номера'})": e['employee_id'] for e in employees}
            selected_emp = st.selectbox("Выберите сотрудника для удаления", list(emp_dict.keys()))
            
            if st.button("Удалить", type="primary"):
                try:
                    conn = get_db_connection()
                    cur = conn.cursor()
                    cur.execute("SELECT delete_employee(%s)", (emp_dict[selected_emp],))
                    conn.commit()
                    st.success("Сотрудник успешно удален!")
                    st.rerun()
                except Exception as e:
                    st.error(f"Ошибка: {e}")
                finally:
                    conn.close()
        else:
            st.info("Нет сотрудников для удаления")
    
    elif employee_tab == "Добавить историю трудоустройства":
        st.subheader("Добавление истории трудоустройства")
        employees = get_employees()
        depts = get_departments()
        
        if employees and depts:
            emp_dict = {f"{e['fio']} ({e['emp_number'] or 'без номера'})": e['employee_id'] for e in employees}
            dept_dict = {d['name']: d['dept_id'] for d in depts}
            
            with st.form("add_employment_form"):
                col1, col2 = st.columns(2)
                with col1:
                    selected_emp = st.selectbox("Сотрудник *", list(emp_dict.keys()))
                    dept = st.selectbox("Подразделение *", list(dept_dict.keys()))
                    position = st.text_input("Должность")
                with col2:
                    start_date = st.date_input("Дата начала работы *", value=date.today())
                    end_date = st.date_input("Дата окончания работы (если уволен)", value=None)
                
                submitted = st.form_submit_button("Добавить")
                if submitted:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT add_employment_history(%s, %s, %s, %s, %s)
                        """, (
                            emp_dict[selected_emp],
                            dept_dict[dept],
                            start_date,
                            position if position else None,
                            end_date if end_date else None
                        ))
                        hist_id = cur.fetchone()[0]
                        conn.commit()
                        st.success(f"История трудоустройства добавлена! ID: {hist_id}")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
        else:
            st.info("Нет сотрудников или подразделений")
    
    else:
        st.subheader("Список сотрудников")
        employees = get_employees()
        if employees:
            st.dataframe(employees, use_container_width=True)
        else:
            st.info("Сотрудники не найдены")

with tabs[4]:
    st.header("Управление подразделениями")
    
    dept_tab = st.radio(
        "Выберите действие:",
        ["Создать подразделение", "Расформировать подразделение", "Список подразделений"],
        horizontal=True
    )
    
    if dept_tab == "Создать подразделение":
        st.subheader("Создание нового подразделения")
        with st.form("add_department_form"):
            col1, col2 = st.columns(2)
            with col1:
                name = st.text_input("Название подразделения *", key="add_dept_name")
                code = st.text_input("Код подразделения", key="add_dept_code")
            with col2:
                address = st.text_area("Адрес", key="add_dept_address")
            
            submitted = st.form_submit_button("Создать")
            if submitted:
                if not name:
                    st.error("Название подразделения обязательно!")
                else:
                    try:
                        conn = get_db_connection()
                        cur = conn.cursor()
                        cur.execute("""
                            SELECT add_department(%s, %s, %s)
                        """, (
                            name,
                            code if code else None,
                            address if address else None
                        ))
                        dept_id = cur.fetchone()[0]
                        conn.commit()
                        st.success(f"Подразделение '{name}' успешно создано! ID: {dept_id}")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                    finally:
                        conn.close()
    
    elif dept_tab == "Расформировать подразделение":
        st.subheader("Расформирование подразделения")
        st.warning("⚠️ Внимание: При расформировании подразделения все сотрудники, работающие в нем, будут удалены!")
        
        depts = get_departments()
        if depts:
            dept_dict = {f"{d['name']} (ID: {d['dept_id']})": d['dept_id'] for d in depts}
            selected_dept = st.selectbox("Выберите подразделение для расформирования", list(dept_dict.keys()))
            
            if selected_dept:
                dept_id = dept_dict[selected_dept]
                employees_in_dept = execute_query("""
                    SELECT e.employee_id, 
                           e.last_name || ' ' || e.first_name || COALESCE(' ' || e.middle_name, '') as fio,
                           eh.position
                    FROM employee e
                    JOIN employment_history eh ON eh.employee_id = e.employee_id
                    WHERE eh.dept_id = %s
                      AND (eh.end_date IS NULL OR eh.end_date >= CURRENT_DATE)
                """, (dept_id,))
                
                if employees_in_dept:
                    st.info(f"В подразделении работает {len(employees_in_dept)} сотрудник(ов):")
                    st.dataframe(employees_in_dept, use_container_width=True)
                else:
                    st.info("В подразделении нет активных сотрудников")
            
            if st.button("Расформировать подразделение", type="primary"):
                try:
                    conn = get_db_connection()
                    cur = conn.cursor()
                    cur.execute("SELECT disband_department(%s)", (dept_dict[selected_dept],))
                    conn.commit()
                    st.success("Подразделение успешно расформировано!")
                    st.rerun()
                except Exception as e:
                    st.error(f"Ошибка: {e}")
                finally:
                    conn.close()
        else:
            st.info("Нет подразделений для расформирования")
    
    else:
        st.subheader("Список подразделений")
        depts = get_departments()
        if depts:
            depts_info = execute_query("""
                SELECT d.dept_id, d.name, d.code, d.address,
                       COUNT(DISTINCT e.employee_id) as employees_count,
                       COUNT(DISTINCT em.inventory_no) as equipment_count
                FROM department d
                LEFT JOIN employment_history eh ON eh.dept_id = d.dept_id 
                    AND (eh.end_date IS NULL OR eh.end_date >= CURRENT_DATE)
                LEFT JOIN employee e ON e.employee_id = eh.employee_id
                LEFT JOIN equipment_movement em ON em.to_dept_id = d.dept_id 
                    AND (em.end_date IS NULL OR em.end_date >= CURRENT_DATE)
                GROUP BY d.dept_id, d.name, d.code, d.address
                ORDER BY d.name
            """)
            st.dataframe(depts_info, use_container_width=True)
        else:
            st.info("Подразделения не найдены")

with tabs[5]:
    st.header("Отчеты")
    
    report_type = st.selectbox(
        "Выберите отчет:",
        [
            "Количество техники по подразделению за 3 года",
            "Сотрудники подразделения",
            "Сотрудники по возрасту и полу",
            "Подразделение, сдавшее больше всего техники в ремонт"
        ]
    )
    
    if report_type == "Количество техники по подразделению за 3 года":
        st.subheader("Количество техники определенного наименования по подразделению за 3 года")
        depts = get_departments()
        
        if not depts:
            st.warning("Нет подразделений в базе данных. Сначала создайте подразделение.")
        else:
            dept_dict = {d['name']: d['dept_id'] for d in depts}
            equipment_names = execute_query("SELECT DISTINCT name FROM equipment ORDER BY name")
            eq_names = [e['name'] for e in equipment_names] if equipment_names else []
            
            if not eq_names:
                st.warning("Нет оборудования в базе данных. Сначала добавьте оборудование.")
            else:
                col1, col2 = st.columns(2)
                with col1:
                    selected_dept = st.selectbox("Подразделение", list(dept_dict.keys()))
                with col2:
                    selected_name = st.selectbox("Наименование техники", eq_names)
                
                if st.button("Сформировать отчет"):
                    if selected_dept and selected_name:
                        try:
                            dept_id = dept_dict.get(selected_dept)
                            if dept_id is None:
                                st.error("Ошибка: не удалось определить ID подразделения")
                            else:
                                conn = get_db_connection()
                                cur = conn.cursor()
                                cur.execute("""
                                    SELECT * FROM report_equipment_count_3years(%s, %s)
                                """, (dept_id, selected_name))
                                result = cur.fetchall()
                                conn.close()
                                
                                if result:
                                    df = pd.DataFrame(result, columns=['Год', 'Количество'])
                                    st.dataframe(df, use_container_width=True)
                                    st.bar_chart(df.set_index('Год'))
                                else:
                                    st.info("Данные не найдены")
                        except Exception as e:
                            st.error(f"Ошибка: {e}")
                    else:
                        st.error("Пожалуйста, выберите подразделение и наименование техники")
    
    elif report_type == "Сотрудники подразделения":
        st.subheader("Сотрудники подразделения")
        depts = get_departments()
        
        if not depts:
            st.warning("Нет подразделений в базе данных. Сначала создайте подразделение.")
        else:
            dept_dict = {d['name']: d['dept_id'] for d in depts}
            selected_dept = st.selectbox("Подразделение", list(dept_dict.keys()), key="report_dept")
            
            if st.button("Сформировать отчет", key="btn_report_dept"):
                if selected_dept:
                    try:
                        dept_id = dept_dict.get(selected_dept)
                        if dept_id is None:
                            st.error("Ошибка: не удалось определить ID подразделения")
                        else:
                            conn = get_db_connection()
                            cur = conn.cursor()
                            cur.execute("SELECT * FROM report_employees_by_dept(%s)", (dept_id,))
                            result = cur.fetchall()
                            conn.close()
                            
                            if result:
                                df = pd.DataFrame(result, columns=['ID сотрудника', 'ФИО', 'Год рождения'])
                                st.dataframe(df, use_container_width=True)
                            else:
                                st.info("Сотрудники не найдены")
                    except Exception as e:
                        st.error(f"Ошибка: {e}")
                else:
                    st.error("Пожалуйста, выберите подразделение")
    
    elif report_type == "Сотрудники по возрасту и полу":
        st.subheader("Сотрудники по возрасту и полу")
        col1, col2 = st.columns(2)
        with col1:
            age = st.number_input("Возраст", min_value=18, max_value=100, value=30)
        with col2:
            gender = st.selectbox("Пол", ["M", "F", "O"])
        
        if st.button("Сформировать отчет", key="btn_report_age"):
            if age and gender:
                try:
                    conn = get_db_connection()
                    cur = conn.cursor()
                    cur.execute("SELECT * FROM report_employees_by_age_gender(%s, %s)", (age, gender))
                    result = cur.fetchall()
                    conn.close()
                    
                    if result:
                        df = pd.DataFrame(result, columns=['ID сотрудника', 'ФИО', 'Год рождения', 'Возраст', 'Пол'])
                        st.dataframe(df, use_container_width=True)
                    else:
                        st.info("Сотрудники не найдены")
                except Exception as e:
                    st.error(f"Ошибка: {e}")
            else:
                st.error("Пожалуйста, укажите возраст и пол")
    
    else:
        st.subheader("Подразделение, сдавшее больше всего техники в ремонт")
        
        if st.button("Сформировать отчет", key="btn_report_most_repair"):
            try:
                conn = get_db_connection()
                cur = conn.cursor()
                cur.execute("SELECT * FROM report_dept_most_sent_to_repair()")
                result = cur.fetchall()
                conn.close()
                
                if result:
                    df = pd.DataFrame(result, columns=['ID подразделения', 'Название', 'Количество ремонтов'])
                    st.dataframe(df, use_container_width=True)
                else:
                    st.info("Данные не найдены")
            except Exception as e:
                st.error(f"Ошибка: {e}")

