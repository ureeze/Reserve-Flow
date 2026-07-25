create table booking_provider_business_hours (
    id uuid primary key,
    booking_provider_id uuid not null references booking_providers (id),
    day_of_week smallint not null,
    opens_at time not null,
    closes_at time not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint booking_provider_business_hours_day_of_week_chk check (day_of_week between 0 and 6),
    constraint booking_provider_business_hours_closes_after_opens_chk check (closes_at > opens_at),
    constraint booking_provider_business_hours_uq unique (booking_provider_id, day_of_week, opens_at)
);

create index booking_provider_business_hours_provider_idx
    on booking_provider_business_hours (booking_provider_id, day_of_week);
