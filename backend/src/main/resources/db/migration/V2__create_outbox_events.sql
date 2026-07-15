create table outbox_events (
    id uuid primary key,
    aggregate_type varchar(100) not null,
    aggregate_id uuid not null,
    event_type varchar(100) not null,
    topic varchar(200) not null,
    message_key varchar(200) not null,
    schema_version integer not null default 1,
    payload jsonb not null,
    status varchar(20) not null default 'PENDING',
    retry_count integer not null default 0,
    available_at timestamp with time zone not null default current_timestamp,
    published_at timestamp with time zone,
    last_error text,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint outbox_events_schema_version_chk check (schema_version > 0),
    constraint outbox_events_retry_count_chk check (retry_count >= 0),
    constraint outbox_events_status_chk check (status in ('PENDING', 'PUBLISHED', 'FAILED'))
);

create index outbox_events_aggregate_idx
    on outbox_events (aggregate_type, aggregate_id, created_at desc);

create index outbox_events_pending_idx
    on outbox_events (status, available_at, created_at);
