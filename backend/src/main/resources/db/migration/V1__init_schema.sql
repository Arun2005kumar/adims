-- =========================================================
-- Anti-Doping Intelligence & Investigation Management System
-- V1: Initial schema
-- =========================================================

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    role            VARCHAR(20)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN','INVESTIGATOR','ANALYST'))
);

CREATE TABLE athletes (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(20),
    nationality     VARCHAR(100),
    sport           VARCHAR(100) NOT NULL,
    discipline      VARCHAR(100),
    testing_pool    VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    risk_level      VARCHAR(20)  NOT NULL DEFAULT 'LOW',
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT chk_athletes_testing_pool CHECK (testing_pool IN ('REGISTERED','NATIONAL','NONE')),
    CONSTRAINT chk_athletes_status CHECK (status IN ('ACTIVE','RETIRED','SUSPENDED')),
    CONSTRAINT chk_athletes_risk_level CHECK (risk_level IN ('LOW','MEDIUM','HIGH','CRITICAL'))
);

CREATE TABLE intelligence_tips (
    id              BIGSERIAL PRIMARY KEY,
    source_type     VARCHAR(20)  NOT NULL,
    source_contact  VARCHAR(255),
    athlete_id      BIGINT REFERENCES athletes(id) ON DELETE SET NULL,
    category        VARCHAR(30)  NOT NULL,
    description     TEXT         NOT NULL,
    credibility_score INTEGER  NOT NULL DEFAULT 1,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    submitted_by    BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT chk_tips_source_type CHECK (source_type IN ('ANONYMOUS','INFORMANT','WHISTLEBLOWER','LAB','OTHER')),
    CONSTRAINT chk_tips_category CHECK (category IN ('DOPING_SUBSTANCE','TRAFFICKING','METHOD_VIOLATION','WHEREABOUTS_FAILURE','ENTOURAGE_INVOLVEMENT','OTHER')),
    CONSTRAINT chk_tips_status CHECK (status IN ('NEW','UNDER_REVIEW','ESCALATED','CLOSED','DISMISSED')),
    CONSTRAINT chk_tips_credibility CHECK (credibility_score BETWEEN 1 AND 5)
);

CREATE TABLE investigation_cases (
    id                      BIGSERIAL PRIMARY KEY,
    case_number             VARCHAR(30) NOT NULL UNIQUE,
    title                   VARCHAR(255) NOT NULL,
    athlete_id              BIGINT REFERENCES athletes(id) ON DELETE SET NULL,
    tip_id                  BIGINT REFERENCES intelligence_tips(id) ON DELETE SET NULL,
    priority                VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status                  VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    assigned_investigator_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    description             TEXT,
    opened_date             DATE NOT NULL DEFAULT CURRENT_DATE,
    closed_date             DATE,
    outcome                 TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_cases_priority CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    CONSTRAINT chk_cases_status CHECK (status IN ('OPEN','IN_PROGRESS','PENDING_REVIEW','CLOSED','ARCHIVED'))
);

CREATE TABLE evidence (
    id              BIGSERIAL PRIMARY KEY,
    case_id         BIGINT NOT NULL REFERENCES investigation_cases(id) ON DELETE CASCADE,
    type            VARCHAR(30) NOT NULL,
    description     TEXT NOT NULL,
    file_reference  VARCHAR(500),
    collected_by    BIGINT REFERENCES users(id) ON DELETE SET NULL,
    collected_date  DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_evidence_type CHECK (type IN ('DOCUMENT','TESTIMONY','LAB_RESULT','FINANCIAL_RECORD','COMMUNICATION_LOG','OTHER'))
);

CREATE TABLE case_notes (
    id          BIGSERIAL PRIMARY KEY,
    case_id     BIGINT NOT NULL REFERENCES investigation_cases(id) ON DELETE CASCADE,
    author_id   BIGINT NOT NULL REFERENCES users(id),
    note        TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE risk_assessments (
    id                      BIGSERIAL PRIMARY KEY,
    athlete_id              BIGINT NOT NULL REFERENCES athletes(id) ON DELETE CASCADE,
    assessment_date         DATE NOT NULL DEFAULT CURRENT_DATE,
    missed_tests_count      INT NOT NULL DEFAULT 0,
    prior_violations_count  INT NOT NULL DEFAULT 0,
    open_tips_count         INT NOT NULL DEFAULT 0,
    open_cases_count        INT NOT NULL DEFAULT 0,
    risk_score              NUMERIC(5,2) NOT NULL,
    risk_level              VARCHAR(20) NOT NULL,
    assessed_by             BIGINT REFERENCES users(id) ON DELETE SET NULL,
    notes                   TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_risk_level CHECK (risk_level IN ('LOW','MEDIUM','HIGH','CRITICAL'))
);

CREATE TABLE testing_records (
    id          BIGSERIAL PRIMARY KEY,
    athlete_id  BIGINT NOT NULL REFERENCES athletes(id) ON DELETE CASCADE,
    test_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    test_type   VARCHAR(30) NOT NULL,
    result      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    sample_id   VARCHAR(100),
    lab_name    VARCHAR(150),
    notes       TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_testing_type CHECK (test_type IN ('IN_COMPETITION','OUT_OF_COMPETITION')),
    CONSTRAINT chk_testing_result CHECK (result IN ('NEGATIVE','POSITIVE','PENDING','ATYPICAL'))
);

CREATE TABLE audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   BIGINT,
    details     TEXT,
    ip_address  VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- Indexes for common lookups / filters
CREATE INDEX idx_athletes_sport ON athletes(sport);
CREATE INDEX idx_athletes_status ON athletes(status);
CREATE INDEX idx_athletes_risk_level ON athletes(risk_level);

CREATE INDEX idx_tips_athlete ON intelligence_tips(athlete_id);
CREATE INDEX idx_tips_status ON intelligence_tips(status);
CREATE INDEX idx_tips_category ON intelligence_tips(category);

CREATE INDEX idx_cases_athlete ON investigation_cases(athlete_id);
CREATE INDEX idx_cases_status ON investigation_cases(status);
CREATE INDEX idx_cases_priority ON investigation_cases(priority);
CREATE INDEX idx_cases_assignee ON investigation_cases(assigned_investigator_id);

CREATE INDEX idx_evidence_case ON evidence(case_id);
CREATE INDEX idx_case_notes_case ON case_notes(case_id);
CREATE INDEX idx_risk_athlete ON risk_assessments(athlete_id);
CREATE INDEX idx_testing_athlete ON testing_records(athlete_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
