-- 两种方式(批量插入，一条一条插入)插入100万条订单模拟数据

-- 方式一：批量插入
DROP PROCEDURE IF EXISTS fill_order_data_batch;
DELIMITER $
CREATE PROCEDURE fill_order_data_batch()
BEGIN
    DECLARE i INT DEFAULT 1;
    set autocommit = 0;
    WHILE i<=1000000 DO
            insert into mall.m_orders (`user_id`, `merchandise_id`, `create`)
            VALUES (CEILING(rand()*100), CEILING(rand()*100), unix_timestamp(now()));
        SET i = i+1;
    END WHILE;
    commit;
END $
CALL fill_order_data_batch();

-- 耗时 15.014 秒

-- 一条一条执行
DROP PROCEDURE IF EXISTS fill_order_data;
DELIMITER $
CREATE PROCEDURE fill_order_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i<=1000000 DO
            insert into mall.m_orders (`user_id`, `merchandise_id`, `create`)
            VALUES (CEILING(rand()*100), CEILING(rand()*100), unix_timestamp(now()));
        SET i = i+1;
    END WHILE;
END $
CALL fill_order_data();

-- 耗时 3 分 05.68 秒