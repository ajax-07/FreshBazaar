CREATE TABLE user_credential
(
    id               UUID         NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    created_by       VARCHAR(255) NOT NULL,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by VARCHAR(255),
    username         VARCHAR(255) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL,
    CONSTRAINT pk_user_credential PRIMARY KEY (id)
);

ALTER TABLE user_credential
    ADD CONSTRAINT uc_user_credential_username UNIQUE (username);