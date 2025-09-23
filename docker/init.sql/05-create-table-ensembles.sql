create table if not exists ensembles (
    id serial primary key,
    event_id INTEGER not null references events(id) on delete cascade,
    artist varchar(100) not null check (length(trim(artist)) > 0),
    title varchar(100) not null check (length(trim(title)) > 0),
    notes text,
    is_active boolean default true,
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER default 1,
    unique(event_id, artist, title)
);

create index idx_ensembles_event_id on ensembles(event_id);