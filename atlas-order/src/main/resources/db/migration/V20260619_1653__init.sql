create table orders
(
    id           uuid primary key,
    order_number varchar(64)    not null unique,
    status       varchar(30)    not null,
    total_amount numeric(19, 2) not null,
    created_at   timestamptz    not null,
    updated_at   timestamptz    not null
);
CREATE UNIQUE INDEX uq_orders_order_number ON orders (order_number);
CREATE INDEX idx_orders_status ON orders (status);

create table order_items
(
    id           uuid primary key,
    order_id     uuid           not null,
    product_id   uuid           not null,
    sku          varchar(64)    not null,
    product_name TEXT           not null,
    price        numeric(19, 2) not null,
    quantity     integer        not null,
    line_amount  numeric(19, 2) not null,

    constraint fk_order_items_order foreign key (order_id) references orders (id)
);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);

