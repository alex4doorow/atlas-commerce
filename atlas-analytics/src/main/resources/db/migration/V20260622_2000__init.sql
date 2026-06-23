create table stat_daily_orders
(
    id           uuid primary key,
    stat_date    date    not null,
    orders_count bigint         not null,
    total_amount numeric(19, 2) not null
);

CREATE UNIQUE INDEX uq_stat_daily_orders_stat_date ON stat_daily_orders (stat_date);
