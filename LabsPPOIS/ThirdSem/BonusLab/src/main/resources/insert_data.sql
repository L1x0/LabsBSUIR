-- Вставка данных в таблицу client
INSERT INTO clients (name, age, order_id)
VALUES ('Ivan Petrov', 25, 1),
       ('Anna Ivanova', 32, 2),
       ('Sergey Kuznetsov', 40, 3);

-- Вставка данных в таблицу employee
INSERT INTO employees (name, age, salary, experience)
VALUES ('Nikolai Smirnov', 35, 50000, 10),
       ('Maria Sokolova', 28, 45000, 5),
       ('Olga Fedorova', 45, 70000, 15);

-- Вставка данных в таблицу orders
INSERT INTO orders (client_id, product, destination_country)
VALUES (1, 'Electronics', 'Russia'),
       (2, 'Furniture', 'Kazakhstan'),
       (3, 'Clothing', 'Belarus');

-- Вставка данных в таблицу truck
INSERT INTO trucks (color, model, engine, max_speed, weight, max_weight, number_of_axes)
VALUES ('Red', 'Volvo FH', 'Diesel', 120, 8000, 18000, 4),
       ('Blue', 'Scania R500', 'Diesel', 110, 7500, 17500, 4),
       ('Green', 'MAN TGX', 'Diesel', 100, 7800, 16000, 4);
