create table if not exists timeslots (
    id serial primary key,
    ensemble_id INTEGER not null references ensembles(id) on delete cascade,
    day_of_week varchar(10) not null check (
        day_of_week in ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')
    ),
    start_time time not null,
    end_time time not null,
    excluded_dates JSONB default '[]'::jsonb,
    is_active boolean not null default true,
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER not null default 1,

    constraint chk_time_order check (start_time < end_time),
    constraint chk_duration_range check (
        extract(epoch from (end_time - start_time)) >= 1800 and
        extract(epoch from (end_time - start_time)) <= 5400
    ),
    constraint chk_excluded_dates_format check (jsonb_typeof(excluded_dates) = 'array')
);

create index idx_timeslots_ensemble_id on timeslots(ensemble_id);
create index idx_timeslots_day_of_week on timeslots(day_of_week);
create index idx_timeslots_excluded_dates on timeslots using gin (excluded_dates);