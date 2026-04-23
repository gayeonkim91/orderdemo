create table products
(
    id         bigint  not null auto_increment,
    name       varchar(255) not null,
    price      bigint  not null,
    quantity   integer not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id)
) engine=InnoDB;

create table orders
(
    id           bigint not null auto_increment,
    order_number varchar(255) not null,
    status       varchar(255) not null,
    total_amount bigint not null,
    created_at   datetime(6) not null,
    updated_at   datetime(6) not null,
    primary key (id),
    constraint uk_orders_order_number unique (order_number)
) engine=InnoDB;

create table order_items
(
    id               bigint not null auto_increment,
    order_id         bigint not null,
    product_id       bigint not null,
    product_name     varchar(255) not null,
    order_unit_price bigint  not null,
    quantity         integer not null,
    created_at       datetime(6) not null,
    updated_at       datetime(6) not null,
    primary key (id),
    constraint fk_order_items_order foreign key (order_id) references orders (id)
) engine=InnoDB;
