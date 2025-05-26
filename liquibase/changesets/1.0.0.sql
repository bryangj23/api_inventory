-- Liquibase Formatted SQL

-- Changeset brayan.guardo:1
CREATE TABLE products (
    id                      bigserial           primary key,
    name                    varchar(150) not null,
    description             varchar(255),
    quantity                integer                             not null,
    registered_by_user_id   bigint                              not null,
    modified_by_user_id     bigint,
    active                  boolean   default true              not null,
    created_at              timestamp default current_timestamp not null,
    updated_at              timestamp default current_timestamp,
    constraint unique_products_name unique (name)
);


create table product_movements (
    id                      bigserial           primary key,
    product_id              bigint                              not null,
    movement_type           varchar(25)                         not null,
    quantity                integer                             not null,
    user_id                 bigint                              not null,
    description             varchar(255),
    active                  boolean   default true              not null,
    created_at              timestamp default current_timestamp not null,
    updated_at              timestamp default current_timestamp,
    constraint fk_products_productmovements foreign key (product_id) references products(id)
);