-- Insert departments
INSERT INTO departments (name) VALUES
('Emergency'),
('ICU'),
('Surgery'),
('Pediatrics');

-- Insert test user (for creating shifts - using first registered user or admin)
-- Note: We'll use ID 1 (created on first registration)

-- Insert sample shifts (will work after at least 1 user is created)
-- These will be created when a user registers
INSERT INTO shifts (department_id, title, start_at, end_at, required_staff, created_by) VALUES
(1, 'Early Shift', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR, 3, 1),
(1, 'Late Shift', DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR, DATE_ADD(NOW(), INTERVAL 2 DAY), 2, 1),
(1, 'Night Shift', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR, 1, 1),
(2, 'ICU Early', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR, 4, 1),
(2, 'ICU Late', DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR, DATE_ADD(NOW(), INTERVAL 2 DAY), 3, 1),
(3, 'Surgery Block', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 6 HOUR, 5, 1),
(3, 'Surgery Evening', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 6 HOUR, 3, 1),
(4, 'Pediatrics Morning', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 8 HOUR, 2, 1),
(4, 'Pediatrics Afternoon', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR, 2, 1);
