create table if not exists applications (
    id serial primary key,
    user_id INTEGER not null references users(id) on delete cascade,
    application_type varchar(20) not null check (
        application_type in ('EVENT', 'ENSEMBLE', 'SESSION', 'TIMESLOT')
    ),
    application_status varchar(20) not null check (
        application_status in ('SUCCESS', 'FAILURE', 'CONFLICT', 'CANCELLED', 'TIMEOUT', 'VALIDATION_ERROR')
    ),
    event_id INTEGER references events(id) on delete set null,
    ensemble_id INTEGER references events(id) on delete set null,
    timeslot_id INTEGER references timeslots(id) on delete set null,
    details jsonb default '{}'::jsonb,
    error_code varchar(50),
    error_message text, -- 사용자 친화적 메시지
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    version INTEGER default 1,

    CONSTRAINT chk_reference_consistency CHECK (
        (application_type = 'EVENT' AND event_id IS NOT NULL) OR
        (application_type = 'ENSEMBLE' AND ensemble_id IS NOT NULL AND event_id IS NOT NULL) OR
        (application_type = 'SESSION' AND ensemble_id IS NOT NULL) OR
        (application_type = 'TIMESLOT' AND timeslot_id IS NOT NULL AND ensemble_id IS NOT NULL)
    ),

    CONSTRAINT chk_details_json CHECK (jsonb_typeof(details) = 'object')
);

create index idx_applications_user_id on applications(user_id);
create index idx_applications_type on applications(application_type);
create index idx_applications_status on applications(application_status);
