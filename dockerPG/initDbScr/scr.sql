CREATE SCHEMA test;
CREATE TYPE operations_type AS ENUM ('CASH_IN', 'CASH_OUT', 'TRANSFER');
CREATE TYPE currency_type AS ENUM ('BYN', 'RUB', 'EUR');

CREATE TABLE test.customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

INSERT INTO test.customers (name, last_name) VALUES ('Владислав', 'Шишко'),
('Павел', 'Войтович'), ('Анатолий', 'Корнеевич'), ('Иосиф', 'Карасевич'), ('Евгений', 'Грузинович'), ('Михаил', 'Жмыхович');



CREATE TABLE test.banks (
    id SERIAL PRIMARY KEY,
    bank_name VARCHAR(255) NOT NULL
);

INSERT INTO test.banks (bank_name) VALUES ('Alfa bank'), ('BPS bank'), ('BNB'), ('NewLevelBank'), ('Belarusbank');



CREATE TABLE test.accounts (
    id SERIAL PRIMARY KEY,
    num VARCHAR(255) NOT NULL,
    balance FLOAT NOT NULL,
    currency_account currency_type DEFAULT 'RUB',
    bank_id INT NOT NULL,
    customer_id INT NOT NULL,
    time_created timestamp DEFAULT current_timestamp,
    time_last_interest_percent timestamp DEFAULT current_timestamp,
    FOREIGN KEY (bank_id) REFERENCES test.banks (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES test.customers (id) ON UPDATE CASCADE ON DELETE CASCADE

);

INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('734724832492', '43554', 2, 1);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('283982348', '2000', 1, 1);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('786798d768798', '35', 3, 1);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('ewdh729382', '1', 2, 2);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('23789328dj32', '44444', 3, 3);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('37980274', '32432', 3, 2);
INSERT INTO test.accounts (num, balance, bank_id, customer_id) VALUES ('477398ej37d9h', '3222', 2, 3);
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('ewfefwe', '1000', 2, 3, '2023-08-01 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('477398ej37d9h', '1000', 2, 3, '2023-08-04 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('47739832dej37d9h', '1000', 2, 3, '2023-07-01 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('wefwefw', '1000', 2, 3, '2023-07-21 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('23d23rfe', '1000', 2, 3, '2023-07-11 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('4325frfr', '1000', 2, 3, '2023-01-21 12:00:00');
INSERT INTO test.accounts (num, balance, bank_id, customer_id, time_created) VALUES ('23edweddeed', '1000', 2, 3, '2021-01-01 12:00:00');





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