create table notifications
(
    id         bigint auto_increment
        primary key,
    type       enum ('email', 'sms')               not null,
    subject    varchar(255)                        null,
    content    text                                not null,
    recipient  varchar(255)                        not null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null
);

create index idx_created_at
    on notifications (created_at);

create index idx_recipient
    on notifications (recipient);

create index idx_type_recipient
    on notifications (type, recipient);