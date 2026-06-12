-- Seed data so the listing is populated on first run. Safe to remove for an empty system.
INSERT INTO person (first_name, last_name, email_address, street_address, city, state, zip_code) VALUES
    ('Ada',   'Lovelace', 'ada@example.com',   '12 Analytical Way', 'London',    'NY', '10001'),
    ('Alan',  'Turing',   'alan@example.com',  '1936 Enigma Rd',    'Manchester','CA', '90210'),
    ('Grace', 'Hopper',   'grace@example.com', '360 Cobol Blvd',    'Arlington', 'VA', '22201');
