CREATE TABLE customers
(
    id         UUID PRIMARY KEY,
    first_name TEXT      NOT NULL,
    last_name  TEXT      NOT NULL,
    email      TEXT      NOT NULL,
    phone      TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

ALTER TABLE customers
    ADD CONSTRAINT uk_customers_email
        UNIQUE (email);

CREATE INDEX idx_customers_last_name
    ON customers (last_name);

CREATE INDEX idx_customers_phone
    ON customers (phone);