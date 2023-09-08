create schema if not exists watering_flowers;
create sequence if not exists watering_flowers.hibernate_sequence start 1 increment 1;

create table if not exists watering_flowers.telegram_accounts
(
    id           bigint  not null primary key,
    chat_id      bigint  not null,
    username     varchar not null,
    created_date timestamptz,
    updated_date timestamptz,
    subscribed   boolean not null,
    settings     jsonb
);

create table if not exists watering_flowers.notifications
(
    id                     bigint       not null primary key,
    title                  varchar(255) not null,
    created_date           timestamptz,
    updated_date           timestamptz,
    last_notification_date timestamptz,
    next_notification_date timestamptz,
    archived               boolean      not null,
    telegram_accounts_id   bigint       not null references watering_flowers.telegram_accounts (id)
);
