-- 기존 데이터가 있으면 삭제
DELETE FROM report;

-- 게시글 신고 데이터 삽입
INSERT INTO report (report_id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at)
VALUES (1, 1, 1, NULL, 1, '스팸성 게시글입니다', 'SUBMITTED', NOW(), NULL);

INSERT INTO report (report_id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at)
VALUES (2, 2, 1, NULL, 2, '욕설이 포함된 게시글입니다', 'SUBMITTED', NOW(), NULL);

INSERT INTO report (report_id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at)
VALUES (3, 1, 2, NULL, 3, '부적절한 내용이 있습니다', 'UNDER_REVIEW', NOW(), NOW());

-- 댓글 신고 데이터 삽입
INSERT INTO report (report_id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at)
VALUES (4, 2, NULL, 1, 2, '욕설이 포함된 댓글입니다', 'SUBMITTED', NOW(), NULL);

INSERT INTO report (report_id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at)
VALUES (5, 1, NULL, 2, 4, '저작권 침해 내용이 있습니다', 'COMPLETED', NOW(), NOW()); 