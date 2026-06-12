-- Schema for the contact manager. Executed on application startup against the in-memory H2 database.
DROP TABLE IF EXISTS person;

CREATE TABLE person (
    id             INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(30)  NOT NULL,
    last_name      VARCHAR(30)  NOT NULL,
    email_address  VARCHAR(30)  NOT NULL,
    street_address VARCHAR(60)  NOT NULL,
    city           VARCHAR(30)  NOT NULL,
    state          CHAR(2)      NOT NULL,
    zip_code       CHAR(5)      NOT NULL
);
