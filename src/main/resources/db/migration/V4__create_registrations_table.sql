CREATE TABLE registrations (
    id            BIGSERIAL PRIMARY KEY,
    event_id      BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',

    CONSTRAINT fk_reg_event
        FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_reg_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_event_user
        UNIQUE (event_id, user_id)
);

-- Индекс для быстрого поиска регистраций по событию
CREATE INDEX idx_registrations_event_id ON registrations(event_id);
CREATE INDEX idx_registrations_user_id ON registrations(user_id);