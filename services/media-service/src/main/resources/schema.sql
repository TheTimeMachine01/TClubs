-- Media Service Database Schema
CREATE TABLE IF NOT EXISTS media_files (
    media_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    mime_type VARCHAR(100),
    uploader_id UUID NOT NULL,
    entity_type VARCHAR(50), -- POST, PROFILE, CLUB_BANNER
    entity_id VARCHAR(255),
    s3_key VARCHAR(500),
    minio_path VARCHAR(500),
    url VARCHAR(1000),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_media_uploader_id ON media_files(uploader_id);
CREATE INDEX IF NOT EXISTS idx_media_entity_id ON media_files(entity_id);
CREATE INDEX IF NOT EXISTS idx_media_status ON media_files(status);

