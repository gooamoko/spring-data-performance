CREATE TABLE IF NOT EXISTS messages (
    msg_id UUID,
    msg_phone varchar(20) not null,
    msg_message varchar(255) not null,
    msg_create_ts timestamp not null default current_timestamp(),
    CONSTRAINT messages_pk primary key (msg_id)
);