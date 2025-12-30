CREATE TABLE test_table
(
    id               UUID PRIMARY KEY NOT NULL,
    username         VARCHAR(255) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL
);
