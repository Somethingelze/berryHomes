CREATE SCHEMA IF NOT EXISTS berryhomes;

CREATE TABLE IF NOT EXISTS berryhomes.admins
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    login            VARCHAR(50)  NOT NULL UNIQUE,
    password_hash    VARCHAR(100) NOT NULL,
    role             VARCHAR(20)  NOT NULL,
    failed_attempts   INTEGER,
    account_non_locked BOOLEAN,
    lock_Time         TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 1. ТАБЛИЦА ПРОЕКТОВ (Строго по полям вашего Java-класса Project)
CREATE TABLE IF NOT EXISTS berryhomes.projects
(
    id                  UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    address             VARCHAR(255) NOT NULL,
    city_zip            VARCHAR(100) NOT NULL,

    -- Финансовые показатели под ручной ввод
    purchase_price      VARCHAR(50),
    monthly_rent        VARCHAR(50),
    renovation_budget   VARCHAR(50),
    est_noi_annual      VARCHAR(50),
    total_investment    VARCHAR(50),
    cash_on_cash_return VARCHAR(50),
    est_payback         VARCHAR(50),

    created_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ,
    deleted_at          TIMESTAMPTZ
);

-- 2. ТАБЛИЦА ИЗОБРАЖЕНИЙ ПРОЕКТОВ ( project_images )
CREATE TABLE IF NOT EXISTS berryhomes.project_images
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID         NOT NULL,
    file_path  VARCHAR(550) NOT NULL,
    sort_order INT              DEFAULT 0,
    CONSTRAINT fk_project_images_project FOREIGN KEY (project_id)
        REFERENCES berryhomes.projects (id) ON DELETE CASCADE
);

-- 3. ТАБЛИЦА ДОКУМЕНТОВ ПРОЕКТОВ ( project_documents )
CREATE TABLE IF NOT EXISTS berryhomes.project_documents
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID,
    file_path  VARCHAR(550) NOT NULL,
    CONSTRAINT fk_project_documents_project FOREIGN KEY (project_id)
        REFERENCES berryhomes.projects (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS berryhomes.contacts
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    phone      VARCHAR(30),
    type       TEXT,
    status     TEXT,
    message    TEXT,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP

        CONSTRAINT type_chk CHECK (type IN ('TENANT', 'HOMEOWNER', 'INVESTOR')),
    CONSTRAINT status_chk CHECK (status IN ('NEW', 'IN_PROGRESS', 'CLOSED'))
);

CREATE TABLE IF NOT EXISTS berryhomes.settings
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    setting_key   VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT
);

CREATE INDEX IF NOT EXISTS idx_contacts_email ON berryhomes.contacts (email);
CREATE INDEX IF NOT EXISTS idx_contacts_phone ON berryhomes.contacts (phone);
CREATE INDEX IF NOT EXISTS idx_contacts_name ON berryhomes.contacts (name);
CREATE INDEX IF NOT EXISTS idx_images_project_id ON berryhomes.project_images (project_id);
CREATE INDEX IF NOT EXISTS idx_documents_project_id ON berryhomes.project_documents (project_id);