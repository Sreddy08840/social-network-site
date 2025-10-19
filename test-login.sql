-- Test Login Query
-- Run this in MySQL to check if the user exists and password hash is correct

USE social_network;

-- Check all users
SELECT user_id, name, email, LEFT(password, 30) as password_hash 
FROM users;

-- Check specific user
SELECT user_id, name, email, password 
FROM users 
WHERE email = 'john@example.com';

-- Expected password hash for 'password123':
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
