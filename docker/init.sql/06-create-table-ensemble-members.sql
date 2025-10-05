create table if not exists ensemble_members (
    id serial primary key,
    user_id INTEGER not null references users(id) on delete cascade,
    ensemble_id INTEGER not null references ensembles(id) on delete cascade,
    session varchar(20) not null
    check (session in ('VOCAL', 'LEAD_GUITAR', 'RHYTHM_GUITAR', 'BASS', 'DRUM', 'PIANO', 'SYNTH', 'ETC')),
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER default 1,

    unique(ensemble_id, session)
);

create index idx_ensemble_members_user_id on ensemble_members(user_id);
create index idx_ensemble_members_ensemble_id on ensemble_members(ensemble_id);
create index idx_ensemble_members_session on ensemble_members(session);