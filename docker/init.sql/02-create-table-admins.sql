create table if not exists admins (
    id serial primary key,
    user_id INTEGER not null unique references users(id) on delete cascade on update cascade,
    role varchar(20) not null check (length(trim(role)) > 0),
    appointment_reason text,
    appointed_by integer references users(id) on delete set null on update cascade,
    appointed_by_name varchar(30), --기록용 이름
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER not null default 1
);