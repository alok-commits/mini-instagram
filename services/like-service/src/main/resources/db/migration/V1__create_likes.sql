CREATE TABLE post_likes (
 post_id UUID NOT NULL,
 user_id VARCHAR(100) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL,
 PRIMARY KEY(post_id,user_id)
);
CREATE INDEX idx_likes_user ON post_likes(user_id);
