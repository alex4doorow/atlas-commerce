CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    sku         VARCHAR(64)    NOT NULL,
    brand       VARCHAR(64)    NOT NULL,
    name        TEXT           NOT NULL,
    description TEXT           NULL,
    image_url   TEXT           NULL,
    price       NUMERIC(19, 2) NOT NULL,
    quantity    INTEGER        NOT NULL DEFAULT 0,
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ
);

CREATE UNIQUE INDEX uq_products_sku ON products (sku);
CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_brand ON products (brand);
CREATE INDEX idx_products_active ON products (active);
