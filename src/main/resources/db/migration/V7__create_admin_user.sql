-- V7__create_admin_user.sql
-- Пароль: admin123 (BCrypt хэш)
INSERT INTO users (email, name, password, role, created_at)
VALUES (
    'admin@eventmanager.com',
    'Administrator',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    CURRENT_TIMESTAMP
);