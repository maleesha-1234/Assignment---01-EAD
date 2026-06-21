-- ============================================================
--  Clinic Management System - Database Schema
--  DBMS: MySQL 8.x
--  Module: EAD-1 | Batch: 25.1P
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinic_db;

-- --------------------------------------------------------
-- Table: users  (system login)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      ENUM('admin','receptionist','doctor') DEFAULT 'receptionist',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------
-- Table: doctors
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id       INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    specialization  VARCHAR(100) NOT NULL,
    contact_number  VARCHAR(20),
    email           VARCHAR(100) UNIQUE,
    available_days  VARCHAR(100),  -- e.g. "Mon,Tue,Wed"
    status          ENUM('active','inactive') DEFAULT 'active',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------
-- Table: patients
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    patient_id      INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    date_of_birth   DATE         NOT NULL,
    gender          ENUM('Male','Female','Other') NOT NULL,
    contact_number  VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT,
    blood_group     VARCHAR(5),
    medical_history TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------
-- Table: appointments  (MAJOR SCENARIO)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id  INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT NOT NULL,
    doctor_id       INT NOT NULL,
    appointment_date DATE     NOT NULL,
    appointment_time TIME     NOT NULL,
    reason          VARCHAR(255),
    status          ENUM('Pending','Confirmed','Completed','Cancelled') DEFAULT 'Pending',
    notes           TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(doctor_id)  ON DELETE CASCADE
);

-- --------------------------------------------------------
-- Table: diagnoses
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS diagnoses (
    diagnosis_id    INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id  INT NOT NULL UNIQUE,
    symptoms        TEXT,
    diagnosis       TEXT NOT NULL,
    prescription    TEXT,
    notes           TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diag_appt FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
);

-- --------------------------------------------------------
-- Table: payments
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id  INT NOT NULL UNIQUE,
    amount          DECIMAL(10,2) NOT NULL,
    payment_method  ENUM('Cash','Card','Online') DEFAULT 'Cash',
    payment_date    DATE NOT NULL,
    status          ENUM('Paid','Pending','Waived') DEFAULT 'Paid',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_appt FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Default admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('reception', 'rec123', 'receptionist');

-- Sample Doctors
INSERT INTO doctors (first_name, last_name, specialization, contact_number, email, available_days) VALUES
('Arjun',   'Perera',    'General Physician',  '0711234567', 'arjun.perera@clinic.lk',    'Mon,Tue,Wed,Thu,Fri'),
('Niluka',  'Fernando',  'Cardiologist',       '0722345678', 'niluka.fernando@clinic.lk', 'Mon,Wed,Fri'),
('Kasun',   'Silva',     'Pediatrician',       '0733456789', 'kasun.silva@clinic.lk',     'Tue,Thu,Sat'),
('Amaya',   'Jayawardena','Dermatologist',     '0744567890', 'amaya.j@clinic.lk',         'Mon,Tue,Thu');

-- Sample Patients
INSERT INTO patients (first_name, last_name, date_of_birth, gender, contact_number, email, address, blood_group, medical_history) VALUES
('Saman',  'Kumara',    '1985-03-15', 'Male',   '0751112222', 'saman@mail.com',  'Colombo 05', 'B+', 'Hypertension'),
('Nimali', 'Wickrama',  '1992-07-22', 'Female', '0762223333', 'nimali@mail.com', 'Kandy',      'O+', 'None'),
('Ruwan',  'Dissanayake','1978-11-30','Male',   '0773334444', 'ruwan@mail.com',  'Galle',      'A-', 'Diabetes Type 2'),
('Dilani', 'Rathnayake', '1995-01-10','Female', '0784445555', 'dilani@mail.com', 'Negombo',    'AB+','Asthma');

-- Sample Appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES
(1, 1, CURDATE(), '09:00:00', 'Routine checkup',       'Confirmed'),
(2, 2, CURDATE(), '10:30:00', 'Chest pain evaluation', 'Pending'),
(3, 1, CURDATE(), '11:00:00', 'Blood pressure review',  'Completed'),
(4, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:30:00', 'Child vaccination', 'Pending'),
(1, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', 'Skin rash',         'Pending');

-- Sample Diagnosis (for completed appointment)
INSERT INTO diagnoses (appointment_id, symptoms, diagnosis, prescription, notes) VALUES
(3, 'High BP reading 160/100', 'Stage 2 Hypertension', 'Amlodipine 5mg OD, Losartan 50mg OD', 'Follow-up in 2 weeks');

-- Sample Payment (for completed appointment)
INSERT INTO payments (appointment_id, amount, payment_method, payment_date, status) VALUES
(3, 1500.00, 'Cash', CURDATE(), 'Paid');
