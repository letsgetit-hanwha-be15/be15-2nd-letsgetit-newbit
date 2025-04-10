-- 기존 데이터가 있으면 삭제
DELETE FROM user_role;
DELETE FROM user_info;

-- 테스트용 사용자 데이터 삽입
INSERT INTO user_info (user_id, username, nickname, email, password, created_at, updated_at, is_active) 
VALUES (1, 'testuser1', '테스트유저1', 'test1@example.com', 'password', NOW(), NOW(), true);

INSERT INTO user_info (user_id, username, nickname, email, password, created_at, updated_at, is_active) 
VALUES (2, 'testuser2', '테스트유저2', 'test2@example.com', 'password', NOW(), NOW(), true);

-- 역할 추가 (필요한 경우)
INSERT INTO user_role (user_id, role) VALUES (1, 'USER');
INSERT INTO user_role (user_id, role) VALUES (2, 'USER'); 