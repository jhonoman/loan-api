CREATE TABLE loans (
    id             UUID PRIMARY KEY,
    user_id        VARCHAR(255)   NOT NULL,
    mrp            NUMERIC(19, 2) NOT NULL,
    dp             NUMERIC(19, 2) NOT NULL,
    vehicle_year   INTEGER        NOT NULL,
    police_number  VARCHAR(50)    NOT NULL,
    machine_number VARCHAR(100)   NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    created_at     TIMESTAMP      NOT NULL,
    updated_at     TIMESTAMP      NOT NULL,
    CONSTRAINT uq_loans_user_police_number UNIQUE (user_id, police_number)
);

CREATE INDEX idx_loans_user_id ON loans (user_id);
