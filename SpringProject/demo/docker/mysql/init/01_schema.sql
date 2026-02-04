CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR
(120) NOT NULL,
  email VARCHAR
(190) NOT NULL UNIQUE,
  password_hash VARCHAR
(255) NOT NULL,
  role VARCHAR
(30) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE departments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR
(120) NOT NULL UNIQUE
);

CREATE TABLE staff_profiles (
  user_id BIGINT PRIMARY KEY,
  phone VARCHAR(30),
  department_id BIGINT NULL,
  bio VARCHAR(500),
  CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_profile_department FOREIGN KEY (department_id) REFERENCES departments (id)
);

CREATE TABLE staff_availability (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  day_of_week VARCHAR(10) NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_availability_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE sick_leaves (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  note VARCHAR(255),
  CONSTRAINT fk_sick_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE shifts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_id BIGINT NOT NULL,
  title VARCHAR
(120) NOT NULL,
  start_at DATETIME NOT NULL,
  end_at DATETIME NOT NULL,
  required_staff INT NOT NULL DEFAULT 1,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_shifts_department FOREIGN KEY
(department_id) REFERENCES departments
(id),
  CONSTRAINT fk_shifts_created_by FOREIGN KEY
(created_by) REFERENCES users
(id)
);

CREATE TABLE shift_assignments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shift_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status VARCHAR
(20) NOT NULL,
  requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  decided_at TIMESTAMP NULL,
  decided_by BIGINT NULL,
  CONSTRAINT fk_assign_shift FOREIGN KEY
(shift_id) REFERENCES shifts
(id),
  CONSTRAINT fk_assign_user FOREIGN KEY
(user_id) REFERENCES users
(id),
  CONSTRAINT fk_assign_decider FOREIGN KEY
(decided_by) REFERENCES users
(id),
  UNIQUE KEY uq_shift_user
(shift_id, user_id)
);

-- Seed data for local testing
-- Password for all seed users: password
INSERT INTO users (id, name, email, password_hash, role, active)
VALUES
  (1, 'Dr. Maya Malik', 'maya.malik@hospital.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5S2h64Vt0e.9e8u6Q9xKxR1uQW1Wm', 'DOCTOR', TRUE),
  (2, 'Nurse Lea Fischer', 'lea.fischer@hospital.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5S2h64Vt0e.9e8u6Q9xKxR1uQW1Wm', 'NURSE', TRUE),
  (3, 'Admin Jan Klein', 'jan.klein@hospital.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5S2h64Vt0e.9e8u6Q9xKxR1uQW1Wm', 'ADMIN', TRUE);

INSERT INTO departments (id, name)
VALUES
  (1, 'Emergency'),
  (2, 'ICU'),
  (3, 'Surgery'),
  (4, 'Pediatrics');

INSERT INTO shifts (id, department_id, title, start_at, end_at, required_staff, created_by)
VALUES
  (1, 1, 'Early Shift', '2026-02-05 06:00:00', '2026-02-05 14:00:00', 4, 3),
  (2, 2, 'Late Shift', '2026-02-05 14:00:00', '2026-02-05 22:00:00', 3, 3),
  (3, 3, 'Night Shift', '2026-02-05 22:00:00', '2026-02-06 06:00:00', 2, 3),
  (4, 4, 'Day Shift', '2026-02-06 08:00:00', '2026-02-06 16:00:00', 3, 3);

INSERT INTO shift_assignments (id, shift_id, user_id, status)
VALUES
  (1, 1, 1, 'assigned'),
  (2, 2, 2, 'assigned');

INSERT INTO staff_profiles (user_id, phone, department_id, bio)
VALUES
  (1, '+49 4321 100 200', 1, 'Emergency medicine focus.'),
  (2, '+49 4321 100 201', 2, 'ICU and night coverage.');

INSERT INTO staff_availability (user_id, day_of_week, status)
VALUES
  (1, 'Mon', 'AVAILABLE'),
  (1, 'Wed', 'AVAILABLE'),
  (1, 'Fri', 'AVAILABLE'),
  (2, 'Tue', 'LIMITED'),
  (2, 'Thu', 'LIMITED'),
  (2, 'Sat', 'LIMITED'),
  (3, 'Mon', 'AVAILABLE'),
  (3, 'Tue', 'AVAILABLE'),
  (3, 'Wed', 'AVAILABLE');

INSERT INTO sick_leaves (user_id, start_date, end_date, note)
VALUES
  (2, '2026-02-04', '2026-02-06', 'Flu');
