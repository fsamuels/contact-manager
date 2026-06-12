CREATE TABLE person (
    person_id   INTEGER       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name  VARCHAR(30)   NOT NULL,
    last_name   VARCHAR(30)   NOT NULL,
    email       VARCHAR(30)   NOT NULL,
    street      VARCHAR(60)   NOT NULL,
    city        VARCHAR(30)   NOT NULL,
    state       CHAR(2)       NOT NULL,
    zip         CHAR(5)       NOT NULL
);
