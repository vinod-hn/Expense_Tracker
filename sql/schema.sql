CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2)      NOT NULL CHECK (amount>0),
    description VARCHAR(120)  NOT NULL,
    category    VARCHAR(40)   NOT NULL,
    expense_date DATE         NOT NULL,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
