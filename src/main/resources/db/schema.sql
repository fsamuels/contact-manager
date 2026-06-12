-- Schema for the contact manager database. Primary keys are UUIDs assigned
-- by the application (Hibernate @UuidGenerator); seed data supplies explicit
-- values.
CREATE TABLE person (
    person_id      UUID PRIMARY KEY,
    first_name     VARCHAR(30) NOT NULL,
    last_name      VARCHAR(30) NOT NULL,
    email_address  VARCHAR(30) NOT NULL,
    street_address VARCHAR(60) NOT NULL,
    city           VARCHAR(30) NOT NULL,
    state          CHAR(2)     NOT NULL,
    zip_code       CHAR(5)     NOT NULL
);

-- Notes attached to a person. Notes are soft-deleted: the deleted flag is
-- set instead of removing the row. Hard-deleting a person cascades to its
-- notes (including soft-deleted ones).
CREATE TABLE note (
    note_id    UUID PRIMARY KEY,
    person_id  UUID NOT NULL REFERENCES person (person_id) ON DELETE CASCADE,
    note_text  VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);
