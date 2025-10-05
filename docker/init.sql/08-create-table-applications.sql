create table if not exists applications (
    id serial primary key,
    user_id INTEGER not null references users(id) on delete cascade,
    application_type varchar(20) not null check (
        application_type in ('EVENT', 'ENSEMBLE', 'SESSION', 'TIMESLOT')
    ),
    application_status varchar(20) not null check (
        application_status in ('SUCCESS', 'FAILURE', 'CONFLICT', 'CANCELLED', 'TIMEOUT', 'VALIDATION_ERROR')
    ),
    applied_to INTEGER,
    details jsonb default '{}'::jsonb,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    version INTEGER default 1,

    CONSTRAINT chk_details_json CHECK (jsonb_typeof(details) = 'object')
);

create index idx_applications_user_id on applications(user_id);
create index idx_applications_type on applications(application_type);
create index idx_applications_status on applications(application_status);
