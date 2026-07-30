CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    event_date  TIMESTAMP,
    capacity    INTEGER NOT NULL DEFAULT 0,
    status      VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    location_id BIGINT,

    CONSTRAINT fk_event_location
        FOREIGN KEY (location_id) REFERENCES locations(id)
);