CREATE TABLE posts (
 id UUID PRIMARY KEY,
 author_id VARCHAR(100) NOT NULL,
 caption VARCHAR(2200) NOT NULL,
 image_url VARCHAR(2048),
 status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE','DELETED')),
 version BIGINT NOT NULL,
 created_at TIMESTAMPTZ NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_posts_author_created ON posts (author_id,created_at);
CREATE INDEX idx_posts_active_recent ON posts (created_at DESC,id DESC) WHERE status='ACTIVE';
