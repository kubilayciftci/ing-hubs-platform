INSERT INTO customer (id, name, surname, credit_limit, used_credit_limit) VALUES (1, 'Kubilay', 'Çiftci', 10000, 0);
INSERT INTO customer (id, name, surname, credit_limit, used_credit_limit) VALUES (2, 'Merve', 'Çetinkaya', 15000, 0);

INSERT INTO users (id, username, password, role, customer_id) VALUES (1, 'admin', '$2a$10$EB.GpfGgUmTlp.hRZxuLHeX3QFCHAAdcX9bVMXwzlye01VnKdaVAu', 'ADMIN', NULL);
INSERT INTO users (id, username, password, role, customer_id) VALUES (2, 'kubilay', '$2a$10$IW0VFH2wio3M/ElO35.JaOS3AWZRMsiL9k.Dd9jHmZ7zLA10/j/pm', 'CUSTOMER', 1);
INSERT INTO users (id, username, password, role, customer_id) VALUES (3, 'merve', '$2a$10$IW0VFH2wio3M/ElO35.JaOS3AWZRMsiL9k.Dd9jHmZ7zLA10/j/pm', 'CUSTOMER', 2);
