SET autocommit=0;
START TRANSACTION;

ALTER TABLE `metric` ADD COLUMN `last_processing` datetime(6) NULL;
ALTER TABLE `metric` ADD COLUMN `interval` bigint(20) UNSIGNED DEFAULT 30;
ALTER TABLE `metric` ADD COLUMN `enable` int(1) UNSIGNED NOT NULL DEFAULT TRUE;

COMMIT;
SET autocommit=1;