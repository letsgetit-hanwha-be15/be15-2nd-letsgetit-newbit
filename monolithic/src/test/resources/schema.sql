-- -----------------------------------------------------------------
-- 외래 키 제약 조건을 무시하여 테이블 삭제
-- -----------------------------------------------------------------
SET
    FOREIGN_KEY_CHECKS = 0;

-- 재생성을 위해 테이블 모두 삭제
DROP TABLE IF EXISTS `attachment`;

DROP TABLE IF EXISTS `cancel_reason`;

DROP TABLE IF EXISTS `coffeechat`;

DROP TABLE IF EXISTS `coffeeletter`;

DROP TABLE IF EXISTS `column`;

DROP TABLE IF EXISTS `column_purchase_history`;

DROP TABLE IF EXISTS `column_request`;

DROP TABLE IF EXISTS `comment`;

DROP TABLE IF EXISTS `content_has_tag`;

DROP TABLE IF EXISTS `diamond_history`;

DROP TABLE IF EXISTS `diamond_payment`;

DROP TABLE IF EXISTS `diamond_refund`;

DROP TABLE IF EXISTS `job`;

DROP TABLE IF EXISTS `login_history`;

DROP TABLE IF EXISTS `mentor`;

DROP TABLE IF EXISTS `monthly_settlement_history`;

DROP TABLE IF EXISTS `notification`;

DROP TABLE IF EXISTS `notification_type`;

DROP TABLE IF EXISTS `point_history`;

DROP TABLE IF EXISTS `point_type`;

DROP TABLE IF EXISTS `post`;

DROP TABLE IF EXISTS `post_category`;

DROP TABLE IF EXISTS `report`;

DROP TABLE IF EXISTS `report_type`;

DROP TABLE IF EXISTS `request_time`;

DROP TABLE IF EXISTS `review`;

DROP TABLE IF EXISTS `series`;

DROP TABLE IF EXISTS `sale_history`;

DROP TABLE IF EXISTS `subscription_list`;

DROP TABLE IF EXISTS `suspension`;

DROP TABLE IF EXISTS `tag`;

DROP TABLE IF EXISTS `techstack`;

DROP TABLE IF EXISTS `user`;

DROP TABLE IF EXISTS `user_and_techstack`;

DROP TABLE IF EXISTS `work_experience`;

DROP TABLE IF EXISTS `like`;

-- 외래 키 제약 조건 다시 활성화
SET
    FOREIGN_KEY_CHECKS = 1;

-- -----------------< CREATE문 시작 >-----------------
CREATE TABLE
    `attachment`
(
    `attachment_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '첨부파일ID',
    `url`           VARCHAR(2048)                       NOT NULL COMMENT 'URL',
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `post_id`       BIGINT COMMENT '게시글ID',
    `column_id`     BIGINT COMMENT '칼럼ID',
    PRIMARY KEY (`attachment_id`),
    CHECK (
        (
            post_id IS NOT NULL
                AND column_id IS NULL
            )
            OR (
            post_id IS NULL
                AND column_id IS NOT NULL
            )
        )
) COMMENT = '첨부파일';

CREATE TABLE
    `cancel_reason`
(
    `cancel_reason_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '취소사유ID',
    `reason`           VARCHAR(100)                        NOT NULL COMMENT '취소사유',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`cancel_reason_id`),
    UNIQUE KEY (`reason`)
) COMMENT = '취소사유';

CREATE TABLE
    `coffeechat`
(
    `coffeechat_id`         BIGINT                                                                              NOT NULL AUTO_INCREMENT COMMENT '커피챗ID',
    `progress_status`       ENUM ('IN_PROGRESS', 'PAYMENT_WAITING', 'COFFEECHAT_WAITING', 'COMPLETE', 'CANCEL') NOT NULL COMMENT '진행상태',
    `request_message`       VARCHAR(255)                                                                        NOT NULL COMMENT '신청메시지',
    `confirmed_schedule`    TIMESTAMP COMMENT '커피챗확정일시',
    `ended_at`              TIMESTAMP COMMENT '커피챗종료일시',
    `created_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                 NOT NULL COMMENT '생성일시',
    `updated_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                 NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `purchase_confirmed_at` TIMESTAMP COMMENT '구매확정일시',
    `sale_confirmed_at`     TIMESTAMP COMMENT '판매확정일시',
    `purchase_quantity`     INT                                                                                 NOT NULL COMMENT '구매수량',
    `mentee_id`             BIGINT                                                                              NOT NULL COMMENT '멘티ID',
    `cancel_reason_id`      BIGINT COMMENT '취소사유ID',
    `mentor_id`             BIGINT                                                                              NOT NULL COMMENT '멘토ID',
    PRIMARY KEY (`coffeechat_id`),
    CHECK (purchase_quantity > 0)
) COMMENT = '커피챗';

CREATE TABLE
    `coffeeletter`
(
    `coffeeletter_id`  BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '커피레터ID',
    `content`          VARCHAR(255)                        NOT NULL COMMENT '커피레터내용',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `coffeechat_id`    BIGINT                              NOT NULL COMMENT '커피챗ID',
    `receiver_user_id` BIGINT COMMENT '수신자ID',
    `sender_user_id`   BIGINT COMMENT '발신자ID',
    PRIMARY KEY (`coffeeletter_id`)
) COMMENT = '커피레터';

CREATE TABLE
    `column`
(
    `column_id`     BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '칼럼ID',
    `title`         VARCHAR(255)                        NOT NULL COMMENT '제목',
    `content`       TEXT                                NOT NULL COMMENT '내용',
    `is_public`     BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '공개여부',
    `price`         INT                                 NOT NULL COMMENT '칼럼가격',
    `like_count`    INT                                 NOT NULL COMMENT '좋아요수',
    `thumbnail_url` VARCHAR(2048) COMMENT '대표이미지URL',
    `approved_at`   TIMESTAMP COMMENT '승인일시',
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `deleted_at`    TIMESTAMP COMMENT '삭제일시',
    `series_id`     BIGINT COMMENT '시리즈ID',
    `mentor_id`     BIGINT COMMENT '멘토ID',
    PRIMARY KEY (`column_id`)
) COMMENT = '칼럼';

CREATE TABLE
    `column_purchase_history`
(
    `column_purchase_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '칼럼구매내역ID',
    `column_id`          BIGINT                              NOT NULL COMMENT '칼럼ID',
    `price`              INT                                 NOT NULL COMMENT '구매가격',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`            BIGINT                              NOT NULL COMMENT '회원ID',
    PRIMARY KEY (`column_purchase_id`)
) COMMENT = '칼럼구매내역';

CREATE TABLE
    `column_request`
(
    `column_request_id`     BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '칼럼요청ID',
    `request_type`          ENUM ('CREATE', 'UPDATE', 'DELETE') NOT NULL COMMENT '요청유형',
    `is_approved`           BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '승인여부',
    `updated_title`         VARCHAR(255) COMMENT '수정제목',
    `updated_content`       TEXT COMMENT '수정내용',
    `updated_price`         INT COMMENT '수정가격',
    `updated_thumbnail_url` VARCHAR(2048) COMMENT '수정대표이미지URL',
    `rejected_reason`       VARCHAR(100) COMMENT '반려사유',
    `created_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `admin_user_id`         BIGINT                              NOT NULL COMMENT '관리자ID',
    `column_id`             BIGINT                              NOT NULL COMMENT '칼럼ID',
    PRIMARY KEY (`column_request_id`)
) COMMENT = '칼럼요청';

CREATE TABLE
    `comment`
(
    `comment_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '댓글ID',
    `content`      VARCHAR(500)                        NOT NULL COMMENT '댓글내용',
    `report_count` INT       DEFAULT 0                 NOT NULL COMMENT '신고개수',
    `created_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `deleted_at`   TIMESTAMP                           NOT NULL COMMENT '삭제일시',
    `user_id`      BIGINT                              NOT NULL COMMENT '회원ID',
    `post_id`      BIGINT                              NOT NULL COMMENT '게시글ID',
    PRIMARY KEY (`comment_id`)
) COMMENT = '댓글';

CREATE TABLE
    `content_has_tag`
(
    `content_has_tag_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '컨텐츠별태그ID',
    `post_id`            BIGINT COMMENT '게시글ID',
    `tag_id`             BIGINT                              NOT NULL COMMENT '태그ID',
    `column_id`          BIGINT COMMENT '칼럼ID',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`content_has_tag_id`),
    UNIQUE KEY (`post_id`, `tag_id`),
    UNIQUE KEY (`column_id`, `tag_id`),
    CHECK (
        (
            post_id IS NOT NULL
                AND column_id IS NULL
            )
            OR (
            post_id IS NULL
                AND column_id IS NOT NULL
            )
        )
) COMMENT = '컨텐츠별태그';

CREATE TABLE
    `diamond_history`
(
    `diamond_history_id` BIGINT                                            NOT NULL AUTO_INCREMENT COMMENT '다이아내역ID',
    `service_type`       ENUM ('COFFEECHAT', 'COLUMN', 'MENTOR_AUTHORITY') NOT NULL COMMENT '서비스유형',
    `service_id`         BIGINT                                            NOT NULL COMMENT '서비스ID',
    `increase_amount`    INT COMMENT '증가량',
    `decrease_amount`    INT COMMENT '감소량',
    `balance`            INT                                               NOT NULL COMMENT '잔여다이아',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP               NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP               NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`            BIGINT                                            NOT NULL COMMENT '회원ID',
    PRIMARY KEY (`diamond_history_id`),
    CHECK (balance >= 0),
    CHECK (
        (
            increase_amount IS NULL
                AND decrease_amount IS NOT NULL
            )
            OR (
            increase_amount IS NOT NULL
                AND decrease_amount IS NULL
            )
        )
) COMMENT = '다이아내역';

CREATE TABLE
    `diamond_payment`
(
    `diamond_payment_id`         BIGINT                               NOT NULL AUTO_INCREMENT COMMENT '결제ID',
    `payment_amount`             DECIMAL(12, 2)                       NOT NULL COMMENT '결제금액',
    `payment_method`             ENUM ('CARD', 'VIRTUAL_ACCOUNT')     NOT NULL COMMENT '결제수단',
    `payment_gateway_id`         ENUM ('TOSS')                        NOT NULL COMMENT 'PG사',
    `payment_gateway_payment_id` BIGINT                               NOT NULL COMMENT 'PG사 결제ID',
    `is_refund`                  BOOLEAN   DEFAULT FALSE              NOT NULL COMMENT '환불여부',
    `payment_status`             ENUM ('APPROVE', 'REJECT', 'CANCEL') NOT NULL COMMENT '결제상태',
    `created_at`                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '생성일시',
    `updated_at`                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP  NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`                    BIGINT                               NOT NULL COMMENT '회원ID',
    `charge_amount`              INT                                  NOT NULL COMMENT '충전개수',
    PRIMARY KEY (`diamond_payment_id`)
) COMMENT = '다이아결제';

CREATE TABLE
    `diamond_refund`
(
    `diamond_refund_id`  BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '다이아환불ID',
    `content`            VARCHAR(255)                        NOT NULL COMMENT '환불요청내용',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `diamond_payment_id` BIGINT                              NOT NULL COMMENT '결제ID',
    PRIMARY KEY (`diamond_refund_id`),
    UNIQUE KEY (`content`)
) COMMENT = '다이아환불';

CREATE TABLE
    `job`
(
    `job_id`     BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '직종ID',
    `job_name`   VARCHAR(50)                         NOT NULL COMMENT '직종이름',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`job_id`),
    UNIQUE KEY (`job_name`)
) COMMENT = '직종';

CREATE TABLE
    `like`
(
    `like_id`    BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '좋아요ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `is_delete`  BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '삭제여부',
    `user_id`    BIGINT                              NOT NULL COMMENT '회원ID',
    `post_id`    BIGINT COMMENT '게시글ID',
    `column_id`  BIGINT COMMENT '칼럼ID',
    PRIMARY KEY (`like_id`),
    UNIQUE KEY (`user_id`, `post_id`),
    UNIQUE KEY (`user_id`, `column_id`),
    CHECK (
        (
            post_id IS NOT NULL
                AND column_id IS NULL
            )
            OR (
            post_id IS NULL
                AND column_id IS NOT NULL
            )
        )
) COMMENT = '좋아요';

CREATE TABLE
    `login_history`
(
    `login_history_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '로그인이력ID',
    `user_id`          BIGINT                              NOT NULL COMMENT '회원ID',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`login_history_id`)
) COMMENT = '로그인이력';

CREATE TABLE
    `mentor`
(
    `mentor_id`      BIGINT                                  NOT NULL AUTO_INCREMENT COMMENT '멘토ID',
    `temperature`    DECIMAL(4, 1) DEFAULT 36.5              NOT NULL COMMENT '온도',
    `preferred_time` VARCHAR(255) COMMENT '커피챗선호시간대',
    `is_active`      BOOLEAN       DEFAULT TRUE              NOT NULL COMMENT '커피챗활동여부',
    `price`          DECIMAL(12, 2)                          NOT NULL COMMENT '커피챗가격',
    `created_at`     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`        BIGINT                                  NOT NULL COMMENT '회원ID',
    PRIMARY KEY (`mentor_id`),
    CHECK (temperature BETWEEN 0 AND 100),
    CHECK (price > 0)
) COMMENT = '멘토';

CREATE TABLE
    `monthly_settlement_history`
(
    `monthly_settlement_history_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '월별정산내역ID',
    `settlement_year`               INT                                 NOT NULL COMMENT '정산연도',
    `settlement_month`              INT                                 NOT NULL COMMENT '정산월',
    `settlement_amount`             DECIMAL(12, 2)                      NOT NULL COMMENT '정산액',
    `settled_at`                    TIMESTAMP                           NOT NULL COMMENT '정산일시',
    `created_at`                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`                    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `mentor_id`                     BIGINT                              NOT NULL COMMENT '멘토ID',
    PRIMARY KEY (`monthly_settlement_history_id`),
    CHECK (settlement_month BETWEEN 1 AND 12)
) COMMENT = '월별정산내역';

CREATE TABLE
    `notification`
(
    `notification_id`      BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '알림ID',
    `content`              VARCHAR(255) COMMENT '알림내용',
    `is_read`              BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '읽음여부',
    `created_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`              BIGINT                              NOT NULL COMMENT '회원ID',
    `notification_type_id` BIGINT                              NOT NULL COMMENT '알림유형ID',
    PRIMARY KEY (`notification_id`)
) COMMENT = '알림';

CREATE TABLE
    `notification_type`
(
    `notification_type_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '알림유형ID',
    `notification_type_name` VARCHAR(50)                         NOT NULL COMMENT '알림유형이름',
    `created_at`             TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`             TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`notification_type_id`),
    UNIQUE KEY (`notification_type_name`)
) COMMENT = '알림유형';

CREATE TABLE
    `point_history`
(
    `point_history_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '포인트내역ID',
    `service_id`       BIGINT COMMENT '서비스ID',
    `balance`          INT                                 NOT NULL COMMENT '잔여 포인트',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `point_type_id`    BIGINT                              NOT NULL COMMENT '포인트유형ID',
    `user_id`          BIGINT                              NOT NULL COMMENT '회원ID',
    PRIMARY KEY (`point_history_id`),
    CHECK (balance >= 0)
) COMMENT = '포인트내역';

CREATE TABLE
    `point_type`
(
    `point_type_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '포인트유형ID',
    `point_type_name` VARCHAR(50)                         NOT NULL COMMENT '포인트유형이름',
    `increase_amount` INT COMMENT '증가량',
    `decrease_amount` INT COMMENT '감소량',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`point_type_id`),
    UNIQUE KEY (`point_type_name`),
    CHECK (
        (
            increase_amount IS NULL
                AND decrease_amount IS NOT NULL
            )
            OR (
            increase_amount IS NOT NULL
                AND decrease_amount IS NULL
            )
        )
) COMMENT = '포인트유형';

CREATE TABLE
    `post`
(
    `post_id`          BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '게시글ID',
    `title`            VARCHAR(255)                        NOT NULL COMMENT '제목',
    `content`          TEXT                                NOT NULL COMMENT '내용',
    `like_count`       INT       DEFAULT 0                 NOT NULL COMMENT '좋아요개수',
    `report_count`     INT       DEFAULT 0                 NOT NULL COMMENT '신고개수',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `deleted_at`       TIMESTAMP COMMENT '삭제일시',
    `user_id`          BIGINT                              NOT NULL COMMENT '회원ID',
    `post_category_id` BIGINT                              NOT NULL COMMENT '카테고리ID',
    PRIMARY KEY (`post_id`)
) COMMENT = '게시글';

CREATE TABLE
    `post_category`
(
    `post_category_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '카테고리ID',
    `post_category_name` VARCHAR(50)                         NOT NULL COMMENT '카테고리이름',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`post_category_id`),
    UNIQUE KEY (`post_category_name`)
) COMMENT = '게시판카테고리';

CREATE TABLE
    `report`
(
    `report_id`      BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '신고ID',
    `content`        VARCHAR(255) COMMENT '상세내용',
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`        BIGINT                              NOT NULL COMMENT '회원ID',
    `report_type_id` BIGINT                              NOT NULL COMMENT '신고유형ID',
    `comment_id`     BIGINT COMMENT '댓글ID',
    `post_id`        BIGINT COMMENT '게시글ID',
    PRIMARY KEY (`report_id`),
    UNIQUE KEY (`user_id`, `comment_id`),
    UNIQUE KEY (`user_id`, `post_id`),
    CHECK (
        (
            comment_id IS NOT NULL
                AND post_id IS NULL
            )
            OR (
            comment_id IS NULL
                AND post_id IS NOT NULL
            )
        )
) COMMENT = '신고';

CREATE TABLE
    `report_type`
(
    `report_type_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '신고유형ID',
    `report_type_name` VARCHAR(255)                        NOT NULL COMMENT '신고유형이름',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`report_type_id`),
    UNIQUE KEY (`report_type_name`)
) COMMENT = '신고유형';

CREATE TABLE
    `request_time`
(
    `request_time_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '커피챗신청시간ID',
    `event_date`      DATE                                NOT NULL COMMENT '날짜',
    `start_time`      DATETIME                            NOT NULL COMMENT '가능한시간대시작시각',
    `end_time`        DATETIME                            NOT NULL COMMENT '가능한시간대끝시각',
    `coffeechat_id`   BIGINT                              NOT NULL COMMENT '커피챗ID',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`request_time_id`),
    CHECK (start_time < end_time)
) COMMENT = '커피챗신청시간';

CREATE TABLE
    `review`
(
    `review_id`     BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '리뷰ID',
    `rating`        DECIMAL(12, 2)                      NOT NULL COMMENT '별점',
    `comment`       VARCHAR(500) COMMENT '리뷰내용',
    `tip`           INT COMMENT '팁',
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `deleted_at`    TIMESTAMP                           NOT NULL COMMENT '삭제일시',
    `coffeechat_id` BIGINT                              NOT NULL COMMENT '커피챗ID',
    `user_id`       BIGINT COMMENT '회원ID',
    PRIMARY KEY (`review_id`),
    CHECK (rating >= 0),
    CHECK (tip > 0)
) COMMENT = '리뷰';

CREATE TABLE
    `series`
(
    `series_id`    BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '시리즈ID',
    `title`        VARCHAR(255)                        NOT NULL COMMENT '시리즈제목',
    `description`  VARCHAR(255)                        NOT NULL COMMENT '시리즈설명',
    `tumbnail_url` VARCHAR(2048) COMMENT '대표이미지URL',
    `created_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`series_id`)
) COMMENT = '시리즈';

CREATE TABLE
    `sale_history`
(
    `sale_history_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '판매내역ID',
    `is_settled`      BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '정산여부',
    `settled_at`      TIMESTAMP COMMENT '정산일시',
    `sale_amount`     DECIMAL(12, 2)                      NOT NULL COMMENT '판매액',
    `service_type`    ENUM ('COFFEECHAT', 'COLUMN')       NOT NULL COMMENT '서비스유형',
    `service_id`      BIGINT                              NOT NULL COMMENT '서비스ID',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `mentor_id`       BIGINT                              NOT NULL COMMENT '멘토ID',
    PRIMARY KEY (`sale_history_id`)
) COMMENT = '판매내역';

CREATE TABLE
    `subscription_list`
(
    `user_id`    BIGINT                              NOT NULL COMMENT '회원ID',
    `series_id`  BIGINT                              NOT NULL COMMENT '시리즈ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`user_id`, `series_id`)
) COMMENT = '구독목록';

CREATE TABLE
    `suspension`
(
    `suspension_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '정지ID',
    `start_at`      TIMESTAMP                           NOT NULL COMMENT '정지시작일시',
    `end_at`        TIMESTAMP                           NOT NULL COMMENT '정지해제일시',
    `reason`        VARCHAR(255) COMMENT '정지사유',
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `user_id`       BIGINT                              NOT NULL COMMENT '회원ID',
    `admin_user_id` BIGINT COMMENT '관리자ID',
    PRIMARY KEY (`suspension_id`)
) COMMENT = '정지';

CREATE TABLE
    `tag`
(
    `tag_id`     BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '태그ID',
    `name`       VARCHAR(50)                         NOT NULL COMMENT '태그이름',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`tag_id`),
    UNIQUE KEY (`name`)
) COMMENT = '태그';

CREATE TABLE
    `techstack`
(
    `techstack_id`   BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '기술스택ID',
    `techstack_name` VARCHAR(50)                         NOT NULL COMMENT '기술스택이름',
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`techstack_id`),
    UNIQUE KEY (`techstack_name`)
) COMMENT = '기술스택';

CREATE TABLE
    `user`
(
    `user_id`           BIGINT                                                     NOT NULL AUTO_INCREMENT COMMENT '회원ID',
    `email`             VARCHAR(100)                                               NOT NULL COMMENT '이메일',
    `password`          VARCHAR(255)                                               NOT NULL COMMENT '비밀번호',
    `phone_number`      VARCHAR(20)                                                NOT NULL COMMENT '전화번호',
    `user_name`         VARCHAR(50)                                                NOT NULL COMMENT '이름',
    `nickname`          VARCHAR(50)                                                NOT NULL COMMENT '닉네임',
    `point`             INT                                                        NOT NULL COMMENT '보유포인트',
    `diamond`           INT                                                        NOT NULL COMMENT '보유다이아',
    `authority`         ENUM ('USER', 'MENTOR', 'ADMIN') DEFAULT 'USER'            NOT NULL COMMENT '권한',
    `is_suspended`      BOOLEAN                          DEFAULT FALSE             NOT NULL COMMENT '정지여부',
    `profile_image_url` VARCHAR(2048) COMMENT '프로필이미지URL',
    `created_at`        TIMESTAMP                        DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`        TIMESTAMP                        DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    `job_id`            BIGINT COMMENT '직종ID',
    PRIMARY KEY (`user_id`)
) COMMENT = '회원';

CREATE TABLE
    `user_and_techstack`
(
    `user_id`      BIGINT                              NOT NULL COMMENT '회원ID',
    `techstack_id` BIGINT                              NOT NULL COMMENT '기술스택ID',
    `created_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`user_id`, `techstack_id`)
) COMMENT = '회원별기술스택';

CREATE TABLE
    `work_experience`
(
    `work_experience_id` BIGINT                              NOT NULL AUTO_INCREMENT COMMENT '경력ID',
    `job`                VARCHAR(20)                         NOT NULL COMMENT '직무이름',
    `is_employed`        BOOLEAN   DEFAULT FALSE             NOT NULL COMMENT '재직여부',
    `start_date`         DATE                                NOT NULL COMMENT '시작일',
    `end_date`           DATE COMMENT '종료일',
    `company_name`       VARCHAR(20) COMMENT '회사명',
    `job_description`    VARCHAR(255) COMMENT '직무설명',
    `mentor_id`          BIGINT                              NOT NULL COMMENT '멘토ID',
    `created_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일시',
    `updated_at`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일시',
    PRIMARY KEY (`work_experience_id`),
    CHECK (
        end_date IS NULL
            OR start_date <= end_date
        )
    -- end_date가 NULL이면 통과, 그렇지 않으면 시작일 <= 종료일
) COMMENT = '경력사항';

CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    balance_amount DECIMAL(19, 2),
    payment_method VARCHAR(20) NOT NULL,
    order_id VARCHAR(100) NOT NULL UNIQUE,
    order_name VARCHAR(100),
    payment_key VARCHAR(100) UNIQUE,
    user_id BIGINT NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    approved_at TIMESTAMP,
    requested_at TIMESTAMP,
    is_refunded BOOLEAN DEFAULT FALSE,
    vat DECIMAL(19, 2),
    supplied_amount DECIMAL(19, 2),
    tax_free_amount DECIMAL(19, 2)
);

CREATE TABLE IF NOT EXISTS refund (
    refund_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    reason VARCHAR(200),
    refund_key VARCHAR(100),
    refunded_at TIMESTAMP NOT NULL,
    bank_code VARCHAR(10),
    account_number VARCHAR(30),
    holder_name VARCHAR(50),
    is_partial_refund BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id)
);

CREATE INDEX IF NOT EXISTS idx_payment_user_id ON payment(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_order_id ON payment(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_payment_key ON payment(payment_key);
CREATE INDEX IF NOT EXISTS idx_refund_payment_id ON refund(payment_id);

ALTER TABLE `attachment`
    ADD CONSTRAINT `attachment_FK` FOREIGN KEY (`column_id`) REFERENCES `column` (`column_id`) ON DELETE CASCADE;

ALTER TABLE `attachment`
    ADD CONSTRAINT `attachment_FK1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE;

ALTER TABLE `coffeechat`
    ADD CONSTRAINT `coffeechat_FK` FOREIGN KEY (`cancel_reason_id`) REFERENCES `cancel_reason` (`cancel_reason_id`) ON DELETE CASCADE;

ALTER TABLE `coffeechat`
    ADD CONSTRAINT `coffeechat_FK1` FOREIGN KEY (`mentee_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `coffeechat`
    ADD CONSTRAINT `coffeechat_FK2` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`) ON DELETE CASCADE;

ALTER TABLE `coffeeletter`
    ADD CONSTRAINT `coffeeletter_FK` FOREIGN KEY (`sender_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL;

ALTER TABLE `coffeeletter`
    ADD CONSTRAINT `coffeeletter_FK1` FOREIGN KEY (`coffeechat_id`) REFERENCES `coffeechat` (`coffeechat_id`) ON DELETE CASCADE;

ALTER TABLE `coffeeletter`
    ADD CONSTRAINT `coffeeletter_FK2` FOREIGN KEY (`receiver_user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL;

ALTER TABLE `column`
    ADD CONSTRAINT `column_FK` FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`) ON DELETE SET NULL;

ALTER TABLE `column`
    ADD CONSTRAINT `column_FK1` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`) ON DELETE SET NULL;

ALTER TABLE `column_request`
    ADD CONSTRAINT `column_request_FK` FOREIGN KEY (`admin_user_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `column_request`
    ADD CONSTRAINT `column_request_FK1` FOREIGN KEY (`column_id`) REFERENCES `column` (`column_id`) ON DELETE CASCADE;

ALTER TABLE `column_purchase_history`
    ADD CONSTRAINT `column_purchase_history_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `comment`
    ADD CONSTRAINT `comment_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `comment`
    ADD CONSTRAINT `comment_FK1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE;

ALTER TABLE `content_has_tag`
    ADD CONSTRAINT `content_has_tag_FK` FOREIGN KEY (`column_id`) REFERENCES `column` (`column_id`) ON DELETE CASCADE;

ALTER TABLE `content_has_tag`
    ADD CONSTRAINT `content_has_tag_FK1` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`) ON DELETE CASCADE;

ALTER TABLE `content_has_tag`
    ADD CONSTRAINT `content_has_tag_FK2` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE;

ALTER TABLE `diamond_history`
    ADD CONSTRAINT `diamond_history_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `diamond_payment`
    ADD CONSTRAINT `diamond_payment_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `diamond_refund`
    ADD CONSTRAINT `diamond_refund_FK` FOREIGN KEY (`diamond_payment_id`) REFERENCES `diamond_payment` (`diamond_payment_id`);

ALTER TABLE `like`
    ADD CONSTRAINT `like_FK` FOREIGN KEY (`column_id`) REFERENCES `column` (`column_id`) ON DELETE CASCADE;

ALTER TABLE `like`
    ADD CONSTRAINT `like_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `like`
    ADD CONSTRAINT `like_FK2` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE;

ALTER TABLE `login_history`
    ADD CONSTRAINT `login_history_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `mentor`
    ADD CONSTRAINT `mentor_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `monthly_settlement_history`
    ADD CONSTRAINT `monthly_settlement_history_FK` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`) ON DELETE CASCADE;

ALTER TABLE `notification`
    ADD CONSTRAINT `notification_FK` FOREIGN KEY (`notification_type_id`) REFERENCES `notification_type` (`notification_type_id`) ON DELETE CASCADE;

ALTER TABLE `notification`
    ADD CONSTRAINT `notification_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `point_history`
    ADD CONSTRAINT `point_history_FK` FOREIGN KEY (`point_type_id`) REFERENCES `point_type` (`point_type_id`);

ALTER TABLE `point_history`
    ADD CONSTRAINT `point_history_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `post`
    ADD CONSTRAINT `post_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `post`
    ADD CONSTRAINT `post_FK1` FOREIGN KEY (`post_category_id`) REFERENCES `post_category` (`post_category_id`) ON DELETE CASCADE;

ALTER TABLE `report`
    ADD CONSTRAINT `report_FK` FOREIGN KEY (`report_type_id`) REFERENCES `report_type` (`report_type_id`) ON DELETE CASCADE;

ALTER TABLE `report`
    ADD CONSTRAINT `report_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `report`
    ADD CONSTRAINT `report_FK2` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE;

ALTER TABLE `report`
    ADD CONSTRAINT `report_FK3` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE;

ALTER TABLE `request_time`
    ADD CONSTRAINT `request_time_FK` FOREIGN KEY (`coffeechat_id`) REFERENCES `coffeechat` (`coffeechat_id`) ON DELETE CASCADE;

ALTER TABLE `review`
    ADD CONSTRAINT `review_FK` FOREIGN KEY (`coffeechat_id`) REFERENCES `coffeechat` (`coffeechat_id`);

ALTER TABLE `review`
    ADD CONSTRAINT `review_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL;

ALTER TABLE `sale_history`
    ADD CONSTRAINT `sale_history_id_FK` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`) ON DELETE CASCADE;

ALTER TABLE `subscription_list`
    ADD CONSTRAINT `subscription_list_FK` FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`) ON DELETE CASCADE;

ALTER TABLE `subscription_list`
    ADD CONSTRAINT `subscription_list_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `suspension`
    ADD CONSTRAINT `suspension_FK` FOREIGN KEY (`admin_user_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `suspension`
    ADD CONSTRAINT `suspension_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `user`
    ADD CONSTRAINT `user_FK` FOREIGN KEY (`job_id`) REFERENCES `job` (`job_id`) ON DELETE SET NULL;

ALTER TABLE `user_and_techstack`
    ADD CONSTRAINT `user_and_techstack_FK` FOREIGN KEY (`techstack_id`) REFERENCES `techstack` (`techstack_id`) ON DELETE CASCADE;

ALTER TABLE `user_and_techstack`
    ADD CONSTRAINT `user_and_techstack_FK1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

ALTER TABLE `work_experience`
    ADD CONSTRAINT `work_experience_FK` FOREIGN KEY (`mentor_id`) REFERENCES `mentor` (`mentor_id`) ON DELETE CASCADE;