create table inventories
(
    product_option_id bigint         not null unique,
    created_at        datetime(6) not null,
    id                bigint         not null auto_increment,
    updated_at        datetime(6) not null,
    quantity          integer        not null,
    primary key (id),
    constraint fk_inventories_product_option foreign key (product_option_id) references product_options (id)
)
