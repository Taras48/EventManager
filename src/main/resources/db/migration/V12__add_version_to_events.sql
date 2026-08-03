-- V12__add_version_to_events.sql
ALTER TABLE events ADD COLUMN version BIGINT NOT NULL DEFAULT 0;