CREATE DATABASE IF NOT EXISTS quarkus_social;

CREATE TABLE IF NOT EXISTS USERS (
	id bigserial NOT NULL PRIMARY KEY,
	name varchar(100),
	idade integer not null
);
