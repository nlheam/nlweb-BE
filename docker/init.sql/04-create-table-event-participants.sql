create table if not exists event_participants (
    id serial primary key,
    event_id INTEGER not null references events(id) on delete cascade,
    user_id INTEGER not null references users(id) on delete cascade,
    application_status varchar(10) not null default 'PENDING'
    check (application_status in ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'WITHDRAW')),
    applied_at timestamp with time zone not null default current_timestamp,
    approved_at timestamp with time zone,
    approved_by integer references admins(id) on delete set null on update cascade,
    rejection_reason text,

    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER not null default 1,

    unique (event_id, user_id)
);

create index idx_event_participants_event_id on event_participants(event_id);
create index idx_event_participants_status on event_participants(application_status);