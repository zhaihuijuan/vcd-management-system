-- VCD管理系统数据库删除脚本
-- 适用于SQL Server

-- 检查数据库是否存在
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'vcddb')
BEGIN
    USE vcddb;
    
    -- 禁用外键约束检查
    EXEC sp_MSforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT all"
    
    -- 按照外键依赖关系顺序删除表
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'return_records')
    BEGIN
        DROP TABLE return_records;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'rental_records')
    BEGIN
        DROP TABLE rental_records;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'sales_records')
    BEGIN
        DROP TABLE sales_records;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'purchase_records')
    BEGIN
        DROP TABLE purchase_records;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'vcd_inventory')
    BEGIN
        DROP TABLE vcd_inventory;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'vcd')
    BEGIN
        DROP TABLE vcd;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'vcd_categories')
    BEGIN
        DROP TABLE vcd_categories;
    END
    
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'customers')
    BEGIN
        DROP TABLE customers;
    END
    
    -- 切换到master数据库，以便可以删除vcddb
    USE master;
    
    -- 如果有连接到数据库，将其设为单用户模式
    ALTER DATABASE vcddb SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    
    -- 删除数据库
    DROP DATABASE vcddb;
    
    SELECT '数据库vcddb已成功删除！' AS Message;
END
ELSE
BEGIN
    SELECT '数据库vcddb不存在！' AS Message;
END
