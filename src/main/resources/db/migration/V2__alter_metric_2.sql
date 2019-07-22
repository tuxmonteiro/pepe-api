SET autocommit=0;
START TRANSACTION;
ALTER TABLE `metric` MODIFY `metric_enable` bool;

COMMIT;
SET autocommit=1;
