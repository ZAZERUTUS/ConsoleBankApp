CREATE SCHEMA test;
CREATE TYPE operations_type AS ENUM ('CASH_IN', 'CASH_OUT', 'TRANSFER');
CREATE TYPE currency_type AS ENUM ('BYN', 'RUB', 'EUR');

CREATE TABLE test.customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

INSERT INTO test.customers (name, last_name) VALUES ('Владислав', 'Шишко'),
('Павел', 'Войтович'), ('Анатолий', 'Корнеевич'), ('Иосиф', 'Карасевич'), ('Евгений', 'Грузинович'), ('Михаил', 'Жмыхович'),
('Милана' 'Гусева'), ('Антонина' 'Бессонова'), ('Максим' 'Гончаров'), ('Адам' 'Рожков'), ('Мария' 'Иванова'),
('Мирослава' 'Белоусова'), ('Андрей' 'Наумов'), ('Даниил' 'Васильев'), ('Лев' 'Ларин'), ('Егор' 'Дроздов'),
('Елизавета' 'Самойлова'), ('Вероника' 'Михайлова'), ('Дмитрий' 'Миронов'), ('Дмитрий' 'Авдеев'), ('Артём' 'Сергеев'),
('Нина' 'Лаврентьева'), ('Давид' 'Майоров'), ('Алиса' 'Карасева'), ('Ирина' 'Дмитриева'), ('Степан' 'Сергеев');



CREATE TABLE test.banks (
    id SERIAL PRIMARY KEY,
    bank_name VARCHAR(255) NOT NULL
);

INSERT INTO test.banks (bank_name) VALUES ('Alfa bank'), ('BPS bank'), ('BNB'), ('NewLevelBank'), ('Belarusbank');



CREATE TABLE test.accounts (
    id SERIAL PRIMARY KEY,
    num VARCHAR(255) NOT NULL,
    balance FLOAT NOT NULL,
    account_currency currency_type DEFAULT 'RUB',
    bank_id INT NOT NULL,
    customer_id INT NOT NULL,
    time_created timestamp DEFAULT current_timestamp,
    time_last_interest_percent timestamp DEFAULT current_timestamp,
    FOREIGN KEY (bank_id) REFERENCES test.banks (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES test.customers (id) ON UPDATE CASCADE ON DELETE CASCADE

);

INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES
('734724832492', '43554', 2, 1), ('2d73d24724832492', '1200', 4, 5),
('283982348', '2000', 1, 1), ('dd2734724832492', '2100', 4, 6),
('786798d768798', '351', 3, 1), ('d23d2d2d2', '2300', 5, 7),
('ewdh729382', '102', 2, 2), ('c4f32f5g53323', '3500', 2, 8),
('37980274', '32432', 3, 2), ('37980274', '31000', 3, 9),
('477398ej37d9h', '3222', 2, 3), ('477398ej37d9h', '3222', 2, 3);

INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES
('ewfefwe', '1000', 2, 11, '2023-08-01 12:00:00'), ('ewfefwe', '2000', 4, 10, '2023-07-05 12:00:00'),
('f34f3f34f3', '1700.42', 4, 13, '2023-08-04 12:00:00'), ('vre43f3f3', '1000', 2, 12, '2022-08-01 12:00:00'),
('f3gfc2r4f34f', '1900.22', 2, 14, '2023-07-01 12:00:00'), ('ed23fgvrece', '1000', 2, 14, '2023-08-01 12:00:00'),
('wefwefw', '1074', 2, 15, '2022-07-21 12:00:00'), ('f4f3v2d2d', '4050', 2, 16, '2023-08-01 12:00:00'),
('23d23rfe', '1000', 3, 17, '2023-08-11 12:00:00'), ('c34d23d32dd23dv', '1950', 1, 18, '2021-05-11 12:00:00'),
('4325frfr', '120', 2, 19, '2023-01-11 12:00:00'), ('dc23f534', '2950', 1, 20, '2021-05-11 12:00:00'),
('43fcvr34f2', '1000.2', 2, 21, '2021-01-01 12:00:00'), ('34fvc1dxr3v', '5950', 1, 22, '2021-09-11 12:00:00'),
('52dcer3', '2345', 2, 23, '2022-11-01 12:00:00'), ('23f2rf42d', '2950', 1, 24, '2021-05-11 12:00:00'),
('352fcvrtfvfd34', '1454.77', 2, 23, '2022-11-09 12:00:00'), ('f432fd2f232', '1950', 1, 21, '2021-05-11 12:00:00'),
('4fg34f3f', '3444', 2, 13, '2022-05-06 12:00:00'), ('f23f4f2f', '1950', 1, 20, '2021-05-11 12:00:00'),
('345gvdewefd4f4', '1000.8', 2, 8, '2021-01-01 12:00:00'), ('45f3f2f3f', '1950', 1, 18, '2021-05-11 12:00:00'),
('g34g34tg', '3000', 2, 4, '2021-01-01 12:00:00'), ('43f22ee23e2', '5444', 1, 17, '2021-05-11 12:00:00'),
('34g433g', '1400', 2, 18, '2021-01-01 12:00:00'), ('f34fewfr23e', '3000.55', 1, 8, '2021-05-11 12:00:00'),
('g34g34g34', '2500', 2, 15, '2021-01-01 12:00:00'), ('3r32re23', '2135', 1, 7, '2021-05-11 12:00:00'),
('fg43g3g34g', '1100', 2, 22, '2021-01-01 12:00:00'), ('43r34r23r2r', '3123', 1, 5, '2021-05-11 12:00:00'),
('33243f2f2d', '1470', 2, 16, '2021-01-01 12:00:00'), ('rrf3f3r43f3', '1950', 1, 1, '2021-05-11 12:00:00');



CREATE TABLE test.transactions (
    id SERIAL PRIMARY KEY,
    account_id_from INT NOT NULL,
    account_id_to INT,
    transaction_type operations_type NOT NULL,
    sum_operation FLOAT,
    time_transaction timestamp DEFAULT current_timestamp,
    FOREIGN KEY (account_id_from) REFERENCES test.accounts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (account_id_from) REFERENCES test.accounts (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO test.transactions (account_id_from, account_id_to, transaction_type, sum_operation, time_transaction) VALUES
(1, 2, 'TRANSFER', 23.22, '2023-07-05 12:00:00'), (1, 2, 'TRANSFER', 103.22, '2023-07-15 12:00:00'),
(1, 2, 'TRANSFER', 13.22, '2023-07-25 12:00:00'), (1, 2, 'TRANSFER', 23.22, '2023-05-05 12:00:00'),
(2, 1, 'TRANSFER', 233.22, '2023-04-05 12:00:00'), (2, 1, 'TRANSFER', 1003.22, '2022-09-15 12:00:00'),
(2, 1, 'TRANSFER', 43.22, '2023-03-25 12:00:00'), (2, 1, 'TRANSFER', 73.22, '2023-02-05 12:00:00');
