-- V11__add_registration_status_check.sql
ALTER TABLE registrations DROP CONSTRAINT IF EXISTS chk_reg_status;
ALTER TABLE registrations ADD CONSTRAINT chk_reg_status
    CHECK (status IN ('CONFIRMED', 'CANCELED', 'WAITLIST'));