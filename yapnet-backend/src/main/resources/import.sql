-- Add 'title' column to posts if not exists (for dev/test)
ALTER TABLE posts ADD COLUMN IF NOT EXISTS title VARCHAR(120) NOT NULL DEFAULT 'Untitled';

-- Create roles
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');

-- Create admin user
INSERT INTO users (username, password, email, enabled) 
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@example.com', true);

-- Create adam user
INSERT INTO users (username, password, email, enabled) 
VALUES ('adam', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'adam@apple.net', true);

-- Get IDs of admin user and ADMIN role
SELECT id INTO @admin_user_id FROM users WHERE username = 'admin';
SELECT id INTO @admin_role_id FROM roles WHERE name = 'ADMIN';

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id) VALUES (@admin_user_id, @admin_role_id);

-- Get IDs of adam user and USER role
SELECT id INTO @adam_user_id FROM users WHERE username = 'adam';
SELECT id INTO @user_role_id FROM roles WHERE name = 'USER';

-- Assign user role to adam user
INSERT INTO user_roles (user_id, role_id) VALUES (@adam_user_id, @user_role_id);

-- Sample posts with titles
INSERT INTO posts (id, title, content, created_at, updated_at, likes, user_id) VALUES
  (random_uuid(), 'Welcome to YapNet', 'This is the first post!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, (SELECT id FROM users LIMIT 1)),
  (random_uuid(), 'Second Post', 'Another example post.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, (SELECT id FROM users LIMIT 1));
