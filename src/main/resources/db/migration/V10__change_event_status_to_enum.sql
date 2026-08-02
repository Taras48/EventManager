-- V10__change_event_status_to_enum.sql
-- В PostgreSQL можно просто оставить VARCHAR, Hibernate сам мапит
-- Но добавим CHECK constraint для защиты на уровне БД:
ALTER TABLE events DROP CONSTRAINT IF EXISTS chk_event_status;
ALTER TABLE events ADD CONSTRAINT chk_event_status
    CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELED', 'COMPLETED', 'ARCHIVED'));