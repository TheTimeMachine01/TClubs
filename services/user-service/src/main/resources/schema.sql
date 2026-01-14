CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY, -- Auto-incrementing primary key for PostgreSQL
    user_id VARCHAR(255) UNIQUE NOT NULL, -- The userId from common.models.User (e.g., UUID string)
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255),
    roles VARCHAR(255), -- Storing roles as a comma-separated string (e.g., "ROLE_USER,ROLE_ADMIN")
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);