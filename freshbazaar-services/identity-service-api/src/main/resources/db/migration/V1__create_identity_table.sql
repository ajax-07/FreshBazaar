-- identity table and related entities

CREATE TABLE identity(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                    UUID             NOT NULL UNIQUE, -- userId from user-service
    username                   VARCHAR(255)     NOT NULL unique,
    password                   VARCHAR(255)     NOT NULL,
    credential_type            VARCHAR(20)      NOT NULL   CHECK ( credential_type IN ('EMAIL', 'PHONE')),

    -- security fields
    account_locked              BOOLEAN          NOT NULL   DEFAULT false,
    failed_login_attempts      INTEGER                     DEFAULT 0,
    last_failed_login          TIMESTAMP,
    last_successful_login      TIMESTAMP,
    password_changed_at        TIMESTAMP,
    active                     BOOLEAN          NOT NULL    DEFAULT false,

    -- audit fields
    created_at                 TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updated_at                 TIMESTAMP        NOT NULL    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_identity_credential_type ON identity(credential_type);

-- refreshed token table for long-lived sessions
CREATE TABLE refresh_token(
    id                   UUID         PRIMARY KEY      DEFAULT gen_random_uuid(),
    identity_id          UUID         NOT NULL,
    token                TEXT         NOT NULL UNIQUE,
    expires_at           TIMESTAMP    NOT NULL,
    revoked              BOOLEAN      NOT NULL         DEFAULT false,
    created_at           TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_token_identity
        FOREIGN KEY (identity_id) REFERENCES identity(id) ON DELETE CASCADE
);

-- password_reset_token table
CREATE TABLE password_reset_token(
    id                  UUID       PRIMARY KEY,
    identity_id         UUID       NOT NULL,
    token               TEXT       NOT NULL UNIQUE,
    expires_at          TIMESTAMP  NOT NULL,
    used                BOOLEAN    NOT NULL       DEFAULT false,
    created_at          TIMESTAMP  NOT NULL       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_password_reset_identity
        FOREIGN KEY (identity_id) REFERENCES identity(id) ON DELETE CASCADE
)