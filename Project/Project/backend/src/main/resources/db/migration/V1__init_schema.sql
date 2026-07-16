-- ============================================================
-- CertifyPro - Initial schema (MySQL 8)
-- 21 tables. Enums enforced via VARCHAR + CHECK constraints
-- (MySQL 8.0.16+ enforces CHECK; robust with Hibernate @Enumerated(STRING)).
-- FKs declared at table level (MySQL ignores inline column REFERENCES).
-- ============================================================

-- 1. User --------------------------------------------------------------
CREATE TABLE users (
    user_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    role           VARCHAR(30)  NOT NULL,
    email          VARCHAR(180) NOT NULL UNIQUE,
    phone          VARCHAR(20),
    password_hash  VARCHAR(255) NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'Active',
    CONSTRAINT chk_user_role   CHECK (role IN ('Candidate','CentreAdmin','ExamController','Evaluator','CertificationOfficer','Admin')),
    CONSTRAINT chk_user_status CHECK (status IN ('Active','Inactive','Suspended'))
);
CREATE INDEX idx_users_role   ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- 2. AuditLog ----------------------------------------------------------
CREATE TABLE audit_log (
    audit_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,
    action     VARCHAR(50)  NOT NULL,
    module     VARCHAR(80)  NOT NULL,
    entity_id  VARCHAR(80),
    `timestamp` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
CREATE INDEX idx_audit_user   ON audit_log(user_id);
CREATE INDEX idx_audit_module ON audit_log(module);
CREATE INDEX idx_audit_time   ON audit_log(`timestamp`);

-- 3. CertificationProgram ---------------------------------------------
CREATE TABLE certification_program (
    program_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    program_name         VARCHAR(180) NOT NULL,
    body                 VARCHAR(180),
    level                VARCHAR(20)  NOT NULL,
    eligibility_criteria TEXT,
    exam_fee             DECIMAL(10,2),
    validity_years       INT,
    max_attempts         INT,
    status               VARCHAR(20)  NOT NULL DEFAULT 'Active',
    CONSTRAINT chk_program_level  CHECK (level IN ('Foundation','Associate','Professional','Fellow')),
    CONSTRAINT chk_program_status CHECK (status IN ('Active','Discontinued'))
);
CREATE INDEX idx_program_status ON certification_program(status);

-- 4. GradingScale ------------------------------------------------------
CREATE TABLE grading_scale (
    grade_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    program_id     BIGINT NOT NULL,
    grade_letter   VARCHAR(30) NOT NULL,
    min_percentage INT NOT NULL,
    max_percentage INT NOT NULL,
    is_passing     BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_grading_program FOREIGN KEY (program_id) REFERENCES certification_program(program_id)
);
CREATE INDEX idx_grading_program ON grading_scale(program_id);

-- 5. Candidate ---------------------------------------------------------
CREATE TABLE candidate (
    candidate_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                 BIGINT,
    name                    VARCHAR(150) NOT NULL,
    date_of_birth           DATE,
    gender                  VARCHAR(20),
    email                   VARCHAR(180),
    phone                   VARCHAR(20),
    address                 VARCHAR(400),
    highest_qualification   VARCHAR(150),
    professional_experience TEXT,
    employer_name           VARCHAR(180),
    status                  VARCHAR(20) NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_candidate_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_candidate_status CHECK (status IN ('Active','Suspended','Debarred'))
);
CREATE INDEX idx_candidate_user ON candidate(user_id);

-- 6. ProgramEnrolment --------------------------------------------------
CREATE TABLE program_enrolment (
    enrolment_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id       BIGINT NOT NULL,
    program_id         BIGINT NOT NULL,
    enrolment_date     DATE,
    eligibility_status VARCHAR(25) NOT NULL DEFAULT 'PendingVerification',
    attempts_used      INT NOT NULL DEFAULT 0,
    max_attempts       INT,
    status             VARCHAR(20) NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_enrolment_candidate FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    CONSTRAINT fk_enrolment_program   FOREIGN KEY (program_id)   REFERENCES certification_program(program_id),
    CONSTRAINT chk_enrolment_elig   CHECK (eligibility_status IN ('Eligible','Ineligible','PendingVerification')),
    CONSTRAINT chk_enrolment_status CHECK (status IN ('Active','Completed','Lapsed','Withdrawn'))
);
CREATE INDEX idx_enrolment_candidate ON program_enrolment(candidate_id);
CREATE INDEX idx_enrolment_program   ON program_enrolment(program_id);

-- 7. ExamWindow --------------------------------------------------------
CREATE TABLE exam_window (
    window_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    program_id            BIGINT NOT NULL,
    exam_name             VARCHAR(180) NOT NULL,
    start_date            DATE,
    end_date              DATE,
    registration_deadline DATE,
    result_date           DATE,
    status                VARCHAR(20) NOT NULL DEFAULT 'Upcoming',
    CONSTRAINT fk_window_program FOREIGN KEY (program_id) REFERENCES certification_program(program_id),
    CONSTRAINT chk_window_status CHECK (status IN ('Upcoming','Open','Closed','ResultsPublished'))
);
CREATE INDEX idx_window_program ON exam_window(program_id);
CREATE INDEX idx_window_status  ON exam_window(status);

-- 8. TestCentre --------------------------------------------------------
CREATE TABLE test_centre (
    centre_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    centre_name    VARCHAR(180) NOT NULL,
    city           VARCHAR(120),
    address        VARCHAR(400),
    capacity       INT NOT NULL DEFAULT 0,
    contact_person VARCHAR(150),
    status         VARCHAR(20) NOT NULL DEFAULT 'Active',
    CONSTRAINT chk_centre_status CHECK (status IN ('Active','Inactive','Blacklisted'))
);

-- 9. InvigilatorAssignment --------------------------------------------
CREATE TABLE invigilator_assignment (
    assignment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    window_id     BIGINT NOT NULL,
    centre_id     BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    room_number   VARCHAR(20),
    status        VARCHAR(20) NOT NULL DEFAULT 'Assigned',
    CONSTRAINT fk_invig_window FOREIGN KEY (window_id) REFERENCES exam_window(window_id),
    CONSTRAINT fk_invig_centre FOREIGN KEY (centre_id) REFERENCES test_centre(centre_id),
    CONSTRAINT fk_invig_user   FOREIGN KEY (user_id)   REFERENCES users(user_id),
    CONSTRAINT chk_invig_status CHECK (status IN ('Assigned','Confirmed','Absent'))
);
CREATE INDEX idx_invig_window ON invigilator_assignment(window_id);
CREATE INDEX idx_invig_centre ON invigilator_assignment(centre_id);
CREATE INDEX idx_invig_user   ON invigilator_assignment(user_id);

-- 10. SeatAllocation ---------------------------------------------------
CREATE TABLE seat_allocation (
    allocation_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id       BIGINT NOT NULL,
    window_id          BIGINT NOT NULL,
    centre_id          BIGINT NOT NULL,
    room_number        VARCHAR(20),
    seat_number        VARCHAR(20),
    hall_ticket_number VARCHAR(50) UNIQUE,
    status             VARCHAR(20) NOT NULL DEFAULT 'Allocated',
    CONSTRAINT fk_seat_candidate FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    CONSTRAINT fk_seat_window    FOREIGN KEY (window_id)    REFERENCES exam_window(window_id),
    CONSTRAINT fk_seat_centre    FOREIGN KEY (centre_id)    REFERENCES test_centre(centre_id),
    CONSTRAINT chk_seat_status CHECK (status IN ('Allocated','Confirmed','Cancelled','NoShow'))
);
CREATE INDEX idx_seat_candidate ON seat_allocation(candidate_id);
CREATE INDEX idx_seat_window    ON seat_allocation(window_id);
CREATE INDEX idx_seat_centre    ON seat_allocation(centre_id);

-- 11. Question ---------------------------------------------------------
CREATE TABLE question (
    question_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    program_id    BIGINT NOT NULL,
    topic_tag     VARCHAR(120),
    difficulty    VARCHAR(10) NOT NULL,
    question_text TEXT NOT NULL,
    type          VARCHAR(20) NOT NULL,
    marks         INT NOT NULL DEFAULT 0,
    created_by_id BIGINT,
    status        VARCHAR(20) NOT NULL DEFAULT 'Active',
    CONSTRAINT fk_question_program FOREIGN KEY (program_id)    REFERENCES certification_program(program_id),
    CONSTRAINT fk_question_creator FOREIGN KEY (created_by_id) REFERENCES users(user_id),
    CONSTRAINT chk_question_diff   CHECK (difficulty IN ('Easy','Medium','Hard')),
    CONSTRAINT chk_question_type   CHECK (type IN ('MCQ','Descriptive','CaseStudy','Practical')),
    CONSTRAINT chk_question_status CHECK (status IN ('Active','Retired','UnderReview'))
);
CREATE INDEX idx_question_program    ON question(program_id);
CREATE INDEX idx_question_difficulty ON question(difficulty);
CREATE INDEX idx_question_type       ON question(type);

-- 12. QuestionPaper ----------------------------------------------------
CREATE TABLE question_paper (
    paper_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    window_id        BIGINT NOT NULL,
    program_id       BIGINT NOT NULL,
    paper_code       VARCHAR(50) NOT NULL UNIQUE,
    total_marks      INT NOT NULL DEFAULT 0,
    duration         INT,
    instructions_ref TEXT,
    created_by_id    BIGINT,
    status           VARCHAR(20) NOT NULL DEFAULT 'Draft',
    CONSTRAINT fk_paper_window  FOREIGN KEY (window_id)     REFERENCES exam_window(window_id),
    CONSTRAINT fk_paper_program FOREIGN KEY (program_id)    REFERENCES certification_program(program_id),
    CONSTRAINT fk_paper_creator FOREIGN KEY (created_by_id) REFERENCES users(user_id),
    CONSTRAINT chk_paper_status CHECK (status IN ('Draft','Finalised','Distributed','Archived'))
);
CREATE INDEX idx_paper_window  ON question_paper(window_id);
CREATE INDEX idx_paper_program ON question_paper(program_id);

-- 13. PaperQuestion ----------------------------------------------------
CREATE TABLE paper_question (
    paper_question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id          BIGINT NOT NULL,
    question_id       BIGINT NOT NULL,
    sequence_order    INT,
    marks_allocated   INT,
    CONSTRAINT fk_pq_paper    FOREIGN KEY (paper_id)    REFERENCES question_paper(paper_id),
    CONSTRAINT fk_pq_question FOREIGN KEY (question_id) REFERENCES question(question_id)
);
CREATE INDEX idx_pq_paper    ON paper_question(paper_id);
CREATE INDEX idx_pq_question ON paper_question(question_id);

-- 14. ScriptAllocation -------------------------------------------------
CREATE TABLE script_allocation (
    script_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    allocation_id   BIGINT NOT NULL,
    evaluator_id    BIGINT NOT NULL,
    paper_id        BIGINT NOT NULL,
    allocation_date DATE,
    status          VARCHAR(20) NOT NULL DEFAULT 'Assigned',
    CONSTRAINT fk_script_allocation FOREIGN KEY (allocation_id) REFERENCES seat_allocation(allocation_id),
    CONSTRAINT fk_script_evaluator  FOREIGN KEY (evaluator_id)  REFERENCES users(user_id),
    CONSTRAINT fk_script_paper      FOREIGN KEY (paper_id)      REFERENCES question_paper(paper_id),
    CONSTRAINT chk_script_status CHECK (status IN ('Assigned','UnderEvaluation','MarksSubmitted'))
);
CREATE INDEX idx_script_allocation ON script_allocation(allocation_id);
CREATE INDEX idx_script_evaluator  ON script_allocation(evaluator_id);
CREATE INDEX idx_script_paper      ON script_allocation(paper_id);

-- 15. MarksEntry -------------------------------------------------------
CREATE TABLE marks_entry (
    marks_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    script_id      BIGINT NOT NULL,
    evaluator_id   BIGINT NOT NULL,
    marks_awarded  INT NOT NULL,
    entry_date     DATE,
    verified_by_id BIGINT,
    status         VARCHAR(20) NOT NULL DEFAULT 'Draft',
    CONSTRAINT fk_marks_script    FOREIGN KEY (script_id)      REFERENCES script_allocation(script_id),
    CONSTRAINT fk_marks_evaluator FOREIGN KEY (evaluator_id)   REFERENCES users(user_id),
    CONSTRAINT fk_marks_verifier  FOREIGN KEY (verified_by_id) REFERENCES users(user_id),
    CONSTRAINT chk_marks_status CHECK (status IN ('Draft','Submitted','Verified','Moderated'))
);
CREATE INDEX idx_marks_script    ON marks_entry(script_id);
CREATE INDEX idx_marks_evaluator ON marks_entry(evaluator_id);

-- 16. CandidateResult --------------------------------------------------
CREATE TABLE candidate_result (
    result_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id   BIGINT NOT NULL,
    window_id      BIGINT NOT NULL,
    program_id     BIGINT NOT NULL,
    total_marks    INT,
    marks_obtained INT,
    percentage     FLOAT,
    grade          VARCHAR(30),
    outcome        VARCHAR(20),
    published_date DATE,
    status         VARCHAR(20) NOT NULL DEFAULT 'Draft',
    CONSTRAINT fk_result_candidate FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    CONSTRAINT fk_result_window    FOREIGN KEY (window_id)    REFERENCES exam_window(window_id),
    CONSTRAINT fk_result_program   FOREIGN KEY (program_id)   REFERENCES certification_program(program_id),
    CONSTRAINT chk_result_outcome CHECK (outcome IN ('Pass','Fail','Absent','Withheld')),
    CONSTRAINT chk_result_status  CHECK (status IN ('Draft','Published','UnderReEvaluation','Revised'))
);
CREATE INDEX idx_result_candidate ON candidate_result(candidate_id);
CREATE INDEX idx_result_window    ON candidate_result(window_id);
CREATE INDEX idx_result_program   ON candidate_result(program_id);

-- 17. ReEvaluationRequest ---------------------------------------------
CREATE TABLE re_evaluation_request (
    request_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    result_id    BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    request_date DATE,
    reason       TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'Submitted',
    CONSTRAINT fk_reeval_result    FOREIGN KEY (result_id)    REFERENCES candidate_result(result_id),
    CONSTRAINT fk_reeval_candidate FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    CONSTRAINT chk_reeval_status CHECK (status IN ('Submitted','UnderReview','Resolved'))
);
CREATE INDEX idx_reeval_result    ON re_evaluation_request(result_id);
CREATE INDEX idx_reeval_candidate ON re_evaluation_request(candidate_id);

-- 18. Certificate ------------------------------------------------------
CREATE TABLE certificate (
    certificate_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id       BIGINT NOT NULL,
    program_id         BIGINT NOT NULL,
    certificate_number VARCHAR(60) NOT NULL UNIQUE,
    issued_date        DATE,
    valid_until        DATE,
    issued_by_id       BIGINT,
    status             VARCHAR(20) NOT NULL DEFAULT 'Valid',
    CONSTRAINT fk_certificate_candidate FOREIGN KEY (candidate_id) REFERENCES candidate(candidate_id),
    CONSTRAINT fk_certificate_program   FOREIGN KEY (program_id)   REFERENCES certification_program(program_id),
    CONSTRAINT fk_certificate_issuer    FOREIGN KEY (issued_by_id) REFERENCES users(user_id),
    CONSTRAINT chk_certificate_status CHECK (status IN ('Valid','Expired','Revoked','Suspended'))
);
CREATE INDEX idx_certificate_candidate ON certificate(candidate_id);
CREATE INDEX idx_certificate_program   ON certificate(program_id);

-- 19. RenewalApplication ----------------------------------------------
CREATE TABLE renewal_application (
    renewal_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_id       BIGINT NOT NULL,
    candidate_id         BIGINT NOT NULL,
    cpd_points_submitted INT,
    application_date     DATE,
    reviewed_by_id       BIGINT,
    new_valid_until      DATE,
    status               VARCHAR(20) NOT NULL DEFAULT 'Submitted',
    CONSTRAINT fk_renewal_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(certificate_id),
    CONSTRAINT fk_renewal_candidate   FOREIGN KEY (candidate_id)   REFERENCES candidate(candidate_id),
    CONSTRAINT fk_renewal_reviewer    FOREIGN KEY (reviewed_by_id) REFERENCES users(user_id),
    CONSTRAINT chk_renewal_status CHECK (status IN ('Submitted','UnderReview','Approved','Rejected'))
);
CREATE INDEX idx_renewal_certificate ON renewal_application(certificate_id);
CREATE INDEX idx_renewal_candidate   ON renewal_application(candidate_id);

-- 20. ExaminationReport ------------------------------------------------
CREATE TABLE examination_report (
    report_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    scope          VARCHAR(20) NOT NULL,
    metrics        JSON,
    generated_date DATE,
    CONSTRAINT chk_report_scope CHECK (scope IN ('Program','Window','Centre','Period'))
);

-- 21. Notification -----------------------------------------------------
CREATE TABLE notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    message         TEXT NOT NULL,
    category        VARCHAR(20) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'Unread',
    created_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_notification_category CHECK (category IN ('Registration','Exam','Result','Certificate','Renewal')),
    CONSTRAINT chk_notification_status   CHECK (status IN ('Unread','Read','Dismissed'))
);
CREATE INDEX idx_notification_user ON notification(user_id);
