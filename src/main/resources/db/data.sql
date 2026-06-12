-- Sample data so the listing page has content on first run.
-- The database is in-memory, so this is re-applied on every startup.
INSERT INTO person (first_name, last_name, email_address, street_address, city, state, zip_code) VALUES
    ('Ada', 'Lovelace', 'ada@example.com', '12 Analytical Engine Way', 'Boston', 'MA', '02110'),
    ('Grace', 'Hopper', 'grace@example.com', '707 Compiler Court', 'Arlington', 'VA', '22201'),
    ('Alan', 'Turing', 'alan@example.com', '54 Enigma Lane', 'Princeton', 'NJ', '08540');
