-- Создаем таблицу clients
CREATE TABLE clients
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    age      INT,
    order_id INT
);

-- Создаем таблицу employees
CREATE TABLE employees
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    age        INT,
    salary     NUMERIC(10, 2),
    experience INT
);

-- Создаем таблицу trucks
CREATE TABLE trucks
(
    id             SERIAL PRIMARY KEY,
    color          VARCHAR(50),
    model          VARCHAR(100),
    engine         VARCHAR(100),
    max_speed      INT,
    weight         INT,
    max_weight     INT,
    number_of_axes INT
);

-- Создаем таблицу orders, ссылающуюся на таблицу clients
CREATE TABLE orders
(
    id                  SERIAL PRIMARY KEY,
    client_id           INT REFERENCES clients (id),
    product             VARCHAR(100),
    destination_country VARCHAR(100)
);
