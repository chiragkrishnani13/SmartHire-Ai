Create DATABASE IF NOT EXISTS SmartHire_AI;
USE SmartHire_AI;

CREATE TABLE users (
  user_id       INT AUTO_INCREMENT PRIMARY KEY,
  full_name     VARCHAR(100)  NOT NULL,
  email         VARCHAR(100)  NOT NULL UNIQUE,
  password_hash VARCHAR(255)  NOT NULL,
  phone         VARCHAR(15),
  resume_url    VARCHAR(500),
  ats_score     INT           DEFAULT 0,
  active_yn     INT           NOT NULL DEFAULT 0,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE companies (
  company_id    INT AUTO_INCREMENT PRIMARY KEY,
  company_name  VARCHAR(150)  NOT NULL,
  email         VARCHAR(100)  NOT NULL UNIQUE,
  password_hash VARCHAR(255)  NOT NULL,
  industry      VARCHAR(100),
  website       VARCHAR(255),
  active_yn     INT           NOT NULL DEFAULT 0,
  created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE job_postings (
  job_id           INT AUTO_INCREMENT PRIMARY KEY,
  company_id       INT          NOT NULL,
  title            VARCHAR(150) NOT NULL,
  description      TEXT,
  required_skills  TEXT,
  eligibility      TEXT,
  deadline         DATE,
  status           ENUM('open', 'closed') DEFAULT 'open',
  active_yn        INT          NOT NULL DEFAULT 0,
  created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (company_id) REFERENCES companies(company_id)
    ON DELETE CASCADE
);


CREATE TABLE competitions (
  competition_id        INT AUTO_INCREMENT PRIMARY KEY,
  company_id            INT          NOT NULL,
  title                 VARCHAR(150) NOT NULL,
  description           TEXT,
  competition_type      ENUM('hackathon', 'coding_contest', 'quiz', 'case_study') NOT NULL,
  prize_details         TEXT,
  eligibility           TEXT,
  required_skills       TEXT,
  team_size_min         INT          DEFAULT 1,
  team_size_max         INT          DEFAULT 1,
  registration_deadline DATE,
  event_date            DATE,
  duration_hours        INT,
  mode                  ENUM('online', 'offline') DEFAULT 'online',
  venue                 VARCHAR(255),
  status                ENUM('upcoming', 'ongoing', 'completed', 'cancelled') DEFAULT 'upcoming',
  active_yn             INT          NOT NULL DEFAULT 0,
  created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (company_id) REFERENCES companies(company_id)
    ON DELETE CASCADE
);

CREATE TABLE applications (
  application_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id        INT     NOT NULL,
  job_id         INT     NOT NULL,
  status         ENUM('applied', 'under_review', 'shortlisted', 'rejected') DEFAULT 'applied',
  active_yn      INT     NOT NULL DEFAULT 0,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE,
  FOREIGN KEY (job_id)  REFERENCES job_postings(job_id)
    ON DELETE CASCADE,
  UNIQUE KEY uq_user_job (user_id, job_id)
);


CREATE TABLE competition_entries (
  entry_id          INT AUTO_INCREMENT PRIMARY KEY,
  competition_id    INT          NOT NULL,
  user_id           INT          NOT NULL,
  team_name         VARCHAR(100),
  team_members      JSON,
  submission_url    VARCHAR(500),
  submission_notes  TEXT,
  score             INT          DEFAULT 0,
  Ranking              INT,
  result            ENUM('winner', 'runner_up', 'participant', 'disqualified') DEFAULT 'participant',
  certificate_url   VARCHAR(500),
  submitted_at      TIMESTAMP    NULL,
  active_yn         INT          NOT NULL DEFAULT 0,
  created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (competition_id) REFERENCES competitions(competition_id)
    ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE,
  UNIQUE KEY uq_user_competition (user_id, competition_id)
);


CREATE TABLE evaluation_scores (
  score_id       INT AUTO_INCREMENT PRIMARY KEY,
  application_id INT     NOT NULL UNIQUE,
  coding_score   INT     DEFAULT 0,
  aptitude_score INT     DEFAULT 0,
  total_score    INT     GENERATED ALWAYS AS (coding_score + aptitude_score) STORED,
  Ranking           INT,
  shortlisted    BOOLEAN DEFAULT FALSE,
  active_yn      INT     NOT NULL DEFAULT 0,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (application_id) REFERENCES applications(application_id)
    ON DELETE CASCADE
);

CREATE TABLE achievements (
  achievement_id  INT AUTO_INCREMENT PRIMARY KEY,
  user_id         INT          NOT NULL,
  title           VARCHAR(150) NOT NULL,
  description     TEXT,
  active_yn       INT          NOT NULL DEFAULT 0,
  created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE
);

