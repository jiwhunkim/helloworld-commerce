create table products
(
    price          decimal(19, 2) not null,
    stock_quantity integer        not null,
    created_at     datetime(6) not null,
    id             bigint         not null auto_increment,
    updated_at     datetime(6) not null,
    name           varchar(200)   not null,
    description    TEXT,
    primary key (id)
)
