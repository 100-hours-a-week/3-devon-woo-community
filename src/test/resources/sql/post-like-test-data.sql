-- 테스트 회원 데이터
INSERT INTO member (id, email, password, nickname, profile_image_url, status, last_login_at)
VALUES
    (1, 'test1@example.com', 'password123!', 'tester1', 'https://example.com/profile1.jpg', 'ACTIVE', NULL),
    (2, 'test2@example.com', 'password123!', 'tester2', 'https://example.com/profile2.jpg', 'ACTIVE', NULL);

-- 테스트 게시글 데이터
INSERT INTO post (id, member_id, title, content, views_count, like_count, is_deleted, created_at, updated_at)
VALUES (1, 1, 'Test Post', 'Test Content', 0, 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
