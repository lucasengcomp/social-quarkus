CREATE DATABASE IF NOT EXISTS quarkus_social;

CREATE TABLE IF NOT EXISTS USERS (
	id bigserial NOT NULL PRIMARY KEY,
	name varchar(100),
	age integer not null
);

CREATE TABLE IF NOT EXISTS POSTS (
	id bigserial NOT NULL PRIMARY KEY,
	post_text varchar(150) not null,
	date_time timestamp not null,
	user_id bigint not null references USERS(id)
);
