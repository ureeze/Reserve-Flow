create table booking_providers (
    id uuid primary key,
    name varchar(255) not null,
    provider_type varchar(20) not null,
    location_text varchar(255) not null,
    timezone varchar(100) not null default 'Asia/Seoul',
    max_party_size integer not null,
    status varchar(20) not null default 'ACTIVE',
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint booking_providers_provider_type_chk
        check (provider_type in ('RESTAURANT', 'CINEMA', 'HOSPITAL', 'SALON', 'CLASS', 'ROOM')),
    constraint booking_providers_max_party_size_chk check (max_party_size > 0),
    constraint booking_providers_status_chk check (status in ('ACTIVE', 'INACTIVE'))
);
