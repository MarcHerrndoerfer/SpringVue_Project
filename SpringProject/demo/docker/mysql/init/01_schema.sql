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
