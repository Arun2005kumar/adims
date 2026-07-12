-- =========================================================
-- V2: Seed data (demo users, athletes, tips, cases)
-- Default passwords (CHANGE AFTER FIRST LOGIN):
--   admin        / Admin@123
--   investigator / Investigator@123
--   analyst      / Analyst@123
-- =========================================================

INSERT INTO users (username, password, full_name, email, role, active) VALUES
('admin',        '$2b$10$QiX32jG8aRvqS4Yp6XSfJeGegkpU9VZBuz5NM0E3IfKs.capNsvPu', 'System Administrator', 'admin@adims.local',        'ADMIN',        true),
('investigator', '$2b$10$PlZCZjNoZMWTXdKLGbluqe.1T9ee0lwbbC/n8bnn1GPeHej/qSPcC', 'Ravi Kumar',            'investigator@adims.local', 'INVESTIGATOR', true),
('analyst',      '$2b$10$IUvhFd05/IP.b6ElmBLzH.MrdGqMeiB/K21ZES8vB0ryS5Y7Omzf2', 'Priya Sharma',          'analyst@adims.local',      'ANALYST',      true);

INSERT INTO athletes (first_name, last_name, date_of_birth, gender, nationality, sport, discipline, testing_pool, status, risk_level) VALUES
('Arjun',   'Mehta',    '1998-04-12', 'MALE',   'IN', 'Athletics',   'Sprint 100m',      'REGISTERED', 'ACTIVE', 'HIGH'),
('Sara',    'Thomas',   '1996-11-02', 'FEMALE', 'IN', 'Weightlifting','69kg',            'NATIONAL',   'ACTIVE', 'MEDIUM'),
('Vikram',  'Singh',    '2000-01-20', 'MALE',   'IN', 'Wrestling',    'Freestyle 74kg',  'REGISTERED', 'ACTIVE', 'LOW'),
('Neha',    'Verma',    '1999-07-15', 'FEMALE', 'IN', 'Swimming',     '200m Freestyle',  'NONE',       'ACTIVE', 'LOW');

INSERT INTO intelligence_tips (source_type, source_contact, athlete_id, category, description, credibility_score, status, submitted_by) VALUES
('ANONYMOUS',     NULL,                     1, 'DOPING_SUBSTANCE',      'Reported unusual supplement purchases traced to a known coach network.', 4, 'UNDER_REVIEW', NULL),
('INFORMANT',     'informant_ref_2201',     1, 'ENTOURAGE_INVOLVEMENT', 'Training partner alleges involvement of support staff in sourcing prohibited substances.', 3, 'ESCALATED', 2),
('LAB',           'NDTL Lab Liaison',       2, 'METHOD_VIOLATION',      'Atypical biological passport values flagged during routine longitudinal review.', 5, 'NEW', 3);

INSERT INTO investigation_cases (case_number, title, athlete_id, tip_id, priority, status, assigned_investigator_id, description, opened_date) VALUES
('INV-2026-0001', 'Suspected supplement trafficking - Arjun Mehta', 1, 2, 'HIGH', 'IN_PROGRESS', 2, 'Investigation opened following escalated informant tip regarding entourage involvement.', CURRENT_DATE - INTERVAL '10 days');

INSERT INTO case_notes (case_id, author_id, note) VALUES
(1, 2, 'Initial interview conducted with athlete. No immediate admission. Follow-up scheduled with support staff.'),
(1, 2, 'Financial records requested from national federation for cross-verification.');

INSERT INTO evidence (case_id, type, description, collected_by, collected_date) VALUES
(1, 'TESTIMONY', 'Signed statement from training partner corroborating informant tip.', 2, CURRENT_DATE - INTERVAL '6 days');

INSERT INTO testing_records (athlete_id, test_date, test_type, result, sample_id, lab_name) VALUES
(1, CURRENT_DATE - INTERVAL '30 days', 'OUT_OF_COMPETITION', 'NEGATIVE', 'S-100234', 'NDTL New Delhi'),
(1, CURRENT_DATE - INTERVAL '90 days', 'IN_COMPETITION',     'NEGATIVE', 'S-099871', 'NDTL New Delhi'),
(2, CURRENT_DATE - INTERVAL '45 days', 'OUT_OF_COMPETITION', 'ATYPICAL', 'S-100987', 'NDTL New Delhi');

INSERT INTO risk_assessments (athlete_id, missed_tests_count, prior_violations_count, open_tips_count, open_cases_count, risk_score, risk_level, assessed_by, notes) VALUES
(1, 0, 0, 2, 1, 72.50, 'HIGH',   3, 'Elevated due to active case and multiple credible tips.'),
(2, 1, 0, 1, 0, 48.00, 'MEDIUM', 3, 'Atypical passport reading pending further review.'),
(3, 0, 0, 0, 0, 12.00, 'LOW',    3, 'No adverse findings on record.');
