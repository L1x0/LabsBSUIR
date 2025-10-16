INSERT INTO departments (id, code, name, description)
VALUES (1, 'HO', 'Главный офис', 'Штаб-квартира'),
       (2, 'IT', 'ИТ отдел', 'Информационные технологии'),
       (3, 'ACC', 'Бухгалтерия', 'Финансы и учёт'),
       (4, 'STO', 'Склад', 'Склад запасных частей и расходников'),
       (5, 'RM', 'Ремонтная мастерская', 'Мастерская по ремонту техники');

INSERT INTO employees (id, last_name, first_name, middle_name, birth_date, personnel_number, gender, hire_date, phone,
                       email)
VALUES (1, 'Иванов', 'Иван', 'Ив.', '1985-04-10', 'EMP001', 'M', '2010-06-01', '+7-900-111-0001',
        'ivanov@example.local'),
       (2, 'Петрова', 'Мария', 'Сер.', '1990-11-23', 'EMP002', 'F', '2015-02-15', '+7-900-111-0002',
        'petrova@example.local'),
       (3, 'Сидоров', 'Алексей', 'П.', '1978-07-12', 'EMP003', 'M', '2005-01-10', '+7-900-111-0003',
        'sidorov@example.local'),
       (4, 'Кузнецова', 'Ольга', 'Н.', '1988-03-05', 'EMP004', 'F', '2012-09-01', '+7-900-111-0004',
        'kuznetsova@example.local'),
       (5, 'Орлов', 'Дмитрий', 'В.', '1995-12-30', 'EMP005', 'M', '2020-05-20', '+7-900-111-0005',
        'orlov@example.local'),
       (6, 'Смирнов', 'Павел', 'А.', '1982-01-22', 'EMP006', 'M', '2008-03-15', '+7-900-111-0006',
        'smirnov@example.local'),
       (7, 'Новикова', 'Елена', 'Т.', '1997-08-14', 'EMP007', 'F', '2022-01-10', '+7-900-111-0007',
        'novikova@example.local'),
       (8, 'Морозов', 'Игорь', 'К.', '1969-02-02', 'EMP008', 'M', '1990-05-01', '+7-900-111-0008',
        'morozov@example.local'),
       (9, 'Лебедева', 'Светлана', 'Р.', '1983-10-30', 'EMP009', 'F', '2009-11-01', '+7-900-111-0009',
        'lebedeva@example.local'),
       (10, 'Фёдоров', 'Максим', 'Ю.', '1992-06-18', 'EMP010', 'M', '2016-07-04', '+7-900-111-0010',
        'fedorov@example.local');

INSERT INTO employee_positions (id, employee_id, department_id, position_title, date_from, date_to)
VALUES
-- Иванов: Главный офис -> ИТ отдел
(1, 1, 1, 'Специалист по оборудованию', '2010-06-01', '2017-12-31'),
(2, 1, 2, 'Системный администратор', '2018-01-01', NULL),
-- Петрова: с 2015 в Бухгалтерии
(3, 2, 3, 'Бухгалтер', '2015-02-15', NULL),
-- Сидоров: ремонтник (мастер)
(4, 3, 5, 'Инженер по ремонту', '2006-03-01', NULL),
-- Кузнецова: HR -> Главный офис
(5, 4, 1, 'Менеджер по персоналу', '2012-09-01', NULL),
-- Орлов: ИТ отдел, младший техник
(6, 5, 2, 'Младший техник', '2020-05-20', NULL),
-- Смирнов: склад -> ремонт
(7, 6, 4, 'Кладовщик', '2008-03-15', '2019-12-31'),
(8, 6, 5, 'Старший мастер', '2020-01-01', NULL),
-- Новикова: ИТ отдел (новая)
(9, 7, 2, 'Инженер поддержки', '2022-01-10', NULL),
-- Морозов: главный инженер
(10, 8, 1, 'Главный инженер', '1990-05-01', NULL),
-- Лебедева: бухгалтер-аналитик
(11, 9, 3, 'Ведущий бухгалтер', '2009-11-01', NULL),
-- Фёдоров: ИТ отдел
(12, 10, 2, 'Сетевой инженер', '2016-07-04', NULL);

INSERT INTO equipment (id, inventory_number, name, model, manufacture_year, purchase_date, status, note)
VALUES (1, 'INV-0001', 'Ноутбук Lenovo', 'ThinkPad T490', 2019, '2019-08-10', 'operational', 'Выдан сотруднику'),
       (2, 'INV-0002', 'Принтер HP', 'LaserJet Pro M428', 2017, '2018-03-12', 'in_repair',
        'Отправлен в ремонт 2025-06-15'),
       (3, 'INV-0003', 'Сервер Dell', 'PowerEdge R640', 2018, '2018-11-01', 'in_repair', 'Капитальный ремонт'),
       (4, 'INV-0004', 'Монитор Samsung', 'S24F350', 2020, '2020-05-05', 'operational', NULL),
       (5, 'INV-0005', 'МФУ Canon', 'MF644C', 2016, '2016-09-20', 'operational', NULL),
       (6, 'INV-0006', 'Копировальный аппарат Ricoh', 'MP C3004', 2015, '2015-04-10', 'operational', NULL),
       (7, 'INV-0007', 'ПК рабочий', 'Custom-Office-1', 2021, '2021-02-01', 'in_repair', 'Сдача в ремонт 2025-07-01'),
       (8, 'INV-0008', 'Маршрутизатор Cisco', 'RV340', 2019, '2019-09-15', 'operational', NULL),
       (9, 'INV-0009', 'Ноутбук ASUS', 'VivoBook 15', 2022, '2022-10-01', 'operational', NULL),
       (10, 'INV-0010', 'Принтер Xerox', 'WorkCentre 6027', 2010, '2010-06-20', 'written_off', 'Списан 2024-12-01');

INSERT INTO equipment_movements (id, equipment_id, department_id, movement_doc_number, movement_date, created_at)
VALUES (1, 1, 2, 'MV-2019-001', '2019-08-10', '2019-08-10 09:00:00+03'),
       (2, 2, 3, 'MV-2018-005', '2018-03-12', '2018-03-12 10:00:00+03'), -- принтер в бухгалтерии
       (3, 3, 1, 'MV-2018-011', '2018-11-01', '2018-11-01 11:00:00+03'),
       (4, 4, 2, 'MV-2020-004', '2020-05-05', '2020-05-05 12:00:00+03'),
       (5, 5, 3, 'MV-2016-002', '2016-09-20', '2016-09-20 09:30:00+03'),
       (6, 6, 4, 'MV-2015-010', '2015-04-10', '2015-04-10 10:00:00+03'),
       (7, 7, 2, 'MV-2021-002', '2021-02-01', '2021-02-01 09:00:00+03'),
       (8, 8, 2, 'MV-2019-020', '2019-09-15', '2019-09-15 09:00:00+03'),
       (9, 9, 2, 'MV-2022-015', '2022-10-01', '2022-10-01 09:00:00+03'),
       (10, 10, 3, 'MV-2010-001', '2010-06-20', '2010-06-20 09:00:00+03'),
-- дополнительные перемещения: принтер из бухгалтерии в ремонт мастерскую 2025-06-15
       (11, 2, 5, 'MV-2025-06-15', '2025-06-15', '2025-06-15 08:30:00+03'),
-- сервер переведён в ремонтную мастерскую
       (12, 3, 5, 'MV-2025-06-10', '2025-06-10', '2025-06-10 09:00:00+03'),
-- ПК рабочий сдан 2025-07-01 в ремонтную
       (13, 7, 5, 'MV-2025-07-01', '2025-07-01', '2025-07-01 10:00:00+03'),
-- несколько перемещений для демонстрации: ноутбук INV-0001 был в ИТ с 2019, затем временно в головном офисе
       (14, 1, 1, 'MV-2022-05-20', '2022-05-20', '2022-05-20 09:00:00+03'),
       (15, 1, 2, 'MV-2023-01-10', '2023-01-10', '2023-01-10 09:00:00+03');

INSERT INTO write_offs (id, equipment_id, writeoff_date, reason, writeoff_doc_number, created_by, created_at)
VALUES (1, 10, '2024-12-01', 'Износ и неисправность, восстановлению не подлежит', 'WO-2024-12-01', 8,
        '2024-12-01 15:00:00+03');

INSERT INTO invoices (id, invoice_number, supplier, invoice_date, created_at)
VALUES (1, 'INV-2024-03-15', 'ООО Ресурс', '2024-03-15', '2024-03-15 10:00:00+03'),
       (2, 'INV-2025-06-10', 'Поставщик Партнёр', '2025-06-10', '2025-06-10 11:00:00+03');

INSERT INTO spare_parts (id, code, name, unit)
VALUES (1, 'SP-001', 'Блок питания 65W', 'шт'),
       (2, 'SP-002', 'Картридж HP CF230A', 'шт'),
       (3, 'SP-003', 'Жёсткий диск 1TB SATA', 'шт'),
       (4, 'SP-004', 'Материнская плата (модель X)', 'шт');

INSERT INTO invoice_items (id, invoice_id, spare_part_id, quantity, unit_price)
VALUES (1, 1, 2, 5, 3500.00),  -- картриджи в 2024
       (2, 1, 1, 3, 4200.00),  -- блоки питания
       (3, 2, 3, 2, 7500.00),  -- диски в 2025
       (4, 2, 4, 1, 25000.00), -- материнская плата
       (5, 2, 1, 2, 4500.00);
-- блоки питания в 2025 (другая цена)

INSERT INTO repair_orders (id, repair_number, equipment_id, date_submitted, repair_type, expected_finish_date,
                           submitted_by_employee_id, submitted_by_personnel_num, accepted_by_employee_id,
                           repair_performer_employee_id, performer_position, status, note, created_at)
VALUES (1, 'R-2025-06-15-001', 2, '2025-06-15', 'Текущий', '2025-06-20', 2, 'EMP002', 3, 3, 'Инженер по ремонту',
        'in_progress', 'Принтер не печатает, замена фьюзера', '2025-06-15 09:00:00+03'),
       (2, 'R-2025-06-10-001', 3, '2025-06-10', 'Капитальный', '2025-07-10', 1, 'EMP001', 3, 6, 'Старший мастер',
        'in_progress', 'Сервер перезагружается, требуется диагностика', '2025-06-10 09:30:00+03'),
       (3, 'R-2025-07-01-001', 7, '2025-07-01', 'Текущий', '2025-07-05', 10, 'EMP010', 3, 3, 'Инженер по ремонту',
        'closed', 'Не включается, заменён блок питания', '2025-07-01 10:00:00+03'),
       (4, 'R-2025-08-20-001', 5, '2025-08-20', 'Текущий', '2025-08-30', 9, 'EMP009', 4, 6, 'Старший мастер', 'open',
        'Проблема с подачей бумаги', '2025-08-20 11:00:00+03');


INSERT INTO repair_spare_parts (id, repair_order_id, spare_part_id, invoice_item_id, quantity, unit_price)
VALUES (1, 1, 2, 1, 1, 3500.00), -- картридж использован в ремонте принтера R-2025-06-15-001
       (2, 2, 3, 3, 1, 7500.00), -- диск для сервера R-2025-06-10-001
       (3, 3, 1, 2, 1, 4200.00), -- блок питания для ПК R-2025-07-01-001 (цена из invoice 1)
       (4, 4, 1, 5, 1, 4500.00);
-- блок питания для МФУ R-2025-08-20-001 (цена из invoice 2)

INSERT INTO write_offs (id, equipment_id, writeoff_date, reason, writeoff_doc_number, created_by, created_at)
VALUES (2, 6, '2022-11-10', 'Списан после пожара в помещении', 'WO-2022-11-10', 8, '2022-11-10 16:00:00+03');

SELECT setval(pg_get_serial_sequence('departments', 'id'), (SELECT MAX(id) FROM departments));
SELECT setval(pg_get_serial_sequence('employees', 'id'), (SELECT MAX(id) FROM employees));
SELECT setval(pg_get_serial_sequence('employee_positions', 'id'), (SELECT MAX(id) FROM employee_positions));
SELECT setval(pg_get_serial_sequence('equipment', 'id'), (SELECT MAX(id) FROM equipment));
SELECT setval(pg_get_serial_sequence('equipment_movements', 'id'), (SELECT MAX(id) FROM equipment_movements));
SELECT setval(pg_get_serial_sequence('write_offs', 'id'), (SELECT MAX(id) FROM write_offs));
SELECT setval(pg_get_serial_sequence('invoices', 'id'), (SELECT MAX(id) FROM invoices));
SELECT setval(pg_get_serial_sequence('spare_parts', 'id'), (SELECT MAX(id) FROM spare_parts));
SELECT setval(pg_get_serial_sequence('invoice_items', 'id'), (SELECT MAX(id) FROM invoice_items));
SELECT setval(pg_get_serial_sequence('repair_orders', 'id'), (SELECT MAX(id) FROM repair_orders));
SELECT setval(pg_get_serial_sequence('repair_spare_parts', 'id'), (SELECT MAX(id) FROM repair_spare_parts));
