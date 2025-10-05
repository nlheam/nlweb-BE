create table if not exists users (
    id serial primary key,
    student_id varchar(8) not null unique check (student_id ~ '^\d{8}$'),
    username varchar(30) not null,
    password varchar(255) not null,
    email varchar(100) not null unique check (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    phone varchar(20) not null unique,
    batch INTEGER not null check (batch >= 0),
    session varchar(10) not null check (session in ('VOCAL', 'GUITAR', 'BASS', 'DRUM', 'KEYBOARD', 'NONE')),
    status varchar(20) default 'PENDING' check (status in ('PENDING', 'ACTIVE', 'INACTIVE', 'REJECTED', 'SUSPENDED', 'DELETED')),
    is_vocalable boolean default false,
    is_admin boolean default false,
    last_login timestamp with time zone,
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER not null default 1
);

create index idx_users_student_id on users(student_id);
create index idx_users_email on users(email);
create index idx_users_status on users(status);
create index idx_users_batch_session on users(batch, session);
create index idx_users_is_vocalable on users(is_vocalable);