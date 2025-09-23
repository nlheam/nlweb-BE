create table if not exists ensemble_participants (
    id serial primary key,
    user_id INTEGER not null references users(id) on delete cascade,
    ensemble_id INTEGER not null references ensembles(id) on delete cascade,
    session_type varchar(20) not null
    check (session_type in ('VOCAL', 'LEAD_GUITAR', 'RHYTHM_GUITAR', 'BASS', 'DRUM', 'PIANO', 'SYNTH', 'ETC')),
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    version INTEGER default 1,

    unique(ensemble_id, session_type)
);

create index idx_ensemble_participants_user_id on ensemble_participants(user_id);
create index idx_ensemble_participants_ensemble_id on ensemble_participants(ensemble_id);
create index idx_ensemble_participants_session_type on ensemble_participants(session_type);