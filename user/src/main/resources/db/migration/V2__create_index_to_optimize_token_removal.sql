SET search_path TO user_schema;

-- Optimizes finding/deleting tokens for a specific user (Logout / Account Deletion)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON user_schema.refresh_tokens(user_id);

-- Optimizes the background cleanup job (e.g., DELETE FROM refresh_tokens WHERE expires_at < NOW())
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON user_schema.refresh_tokens(expires_at);