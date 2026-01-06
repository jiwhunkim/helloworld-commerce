create table product_options
(
    product_id     bigint         not null,
    created_at     datetime(6) not null,
    id             bigint         not null auto_increment,
    updated_at     datetime(6) not null,
    name           varchar(200)   not null,
    description    TEXT,
    primary key (id),
    constraint fk_product_options_product foreign key (product_id) references products (id)
)
