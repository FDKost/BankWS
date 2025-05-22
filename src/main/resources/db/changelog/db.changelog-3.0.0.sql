--liquibase formatted sql

--changeset FDKost:1
INSERT INTO client(id, name)
VALUES (uuid_generate_v4(), 'Jeff'),
       (uuid_generate_v4(), 'Bob');
