create table if not exists events (
    id serial primary key,
    title varchar(100) not null,
    description text,
    event_type varchar(30) not null check (event_type in (
        'EVENT_APPLICATION', 'ENSEMBLE_STUDY', 'SESSION_STUDY',
        'ENSEMBLE_APPLICATION', 'SESSION_APPLICATION',
        'TIMESLOT_APPLICATION', 'REGULAR_CONCERT', 'EXTRA_EVENT')),
    start_datetime timestamp with time zone not null,
    end_datetime timestamp with time zone not null,

    parent_event INTEGER references events(id) on delete cascade on update cascade,
    depth integer not null default 0 check (depth >= 0),
    root_event integer references events(id) on delete cascade on update cascade,

    created_by INTEGER references admins(id) on delete set null on update cascade,
    is_active boolean default true,
    is_votable boolean default false,

    max_participants INTEGER default 0 check (max_participants >= 0),
    current_participants INTEGER default 0 check (current_participants >= 0),

    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER not null default 1,

    constraint chk_datetime_range check (start_datetime < end_datetime),
    constraint chk_no_self_reference check (parent_event != id),
    constraint chk_depth_limit check (depth <= 3),
    constraint chk_parent_consistency check (
        (parent_event is null and depth = 0) or (parent_event is not null and depth > 0)
    ),
    constraint chk_max_participants check (max_participants is null or max_participants > 0),
    constraint chk_current_participants check (current_participants >= 0)
);

create index idx_events_parent_event on events(parent_event);
create index idx_events_root_event on events(root_event);
create index idx_events_active_datetime on events(is_active, start_datetime, end_datetime);
create index idx_events_type_active on events(event_type, is_active);