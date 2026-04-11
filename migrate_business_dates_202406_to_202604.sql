/*
  将业务日期从 2024 年 6 月迁移到 2026 年 4 月（日号不变）。
  仅更新采购、租赁、归还、销售相关 date 列；不修改 vcd.publish_year 等发行年份。
  条件：YEAR = 2024 AND MONTH = 6，避免误改其他年月。

  使用前请确认数据库名；默认与 script.sql 一致。
*/
USE [vcddb];
GO

SET NOCOUNT ON;

/* 采购记录 */
UPDATE dbo.purchase_records
SET purchase_date = DATEFROMPARTS(2026, 4, DAY(purchase_date))
WHERE YEAR(purchase_date) = 2024
  AND MONTH(purchase_date) = 6;
PRINT 'purchase_records: ' + CAST(@@ROWCOUNT AS varchar(20)) + ' row(s).';
GO

/* 租赁记录：起租日、应还日 */
UPDATE dbo.rental_records
SET rental_date = DATEFROMPARTS(2026, 4, DAY(rental_date))
WHERE YEAR(rental_date) = 2024
  AND MONTH(rental_date) = 6;
PRINT 'rental_records.rental_date: ' + CAST(@@ROWCOUNT AS varchar(20)) + ' row(s).';
GO

UPDATE dbo.rental_records
SET expected_return_date = DATEFROMPARTS(2026, 4, DAY(expected_return_date))
WHERE expected_return_date IS NOT NULL
  AND YEAR(expected_return_date) = 2024
  AND MONTH(expected_return_date) = 6;
PRINT 'rental_records.expected_return_date: ' + CAST(@@ROWCOUNT AS varchar(20)) + ' row(s).';
GO

/* 归还记录（若表中有数据） */
UPDATE dbo.return_records
SET return_date = DATEFROMPARTS(2026, 4, DAY(return_date))
WHERE YEAR(return_date) = 2024
  AND MONTH(return_date) = 6;
PRINT 'return_records.return_date: ' + CAST(@@ROWCOUNT AS varchar(20)) + ' row(s).';
GO

/* 销售记录 */
UPDATE dbo.sales_records
SET sale_date = DATEFROMPARTS(2026, 4, DAY(sale_date))
WHERE YEAR(sale_date) = 2024
  AND MONTH(sale_date) = 6;
PRINT 'sales_records.sale_date: ' + CAST(@@ROWCOUNT AS varchar(20)) + ' row(s).';
GO

PRINT 'Done. vcd.publish_year 未修改。';
GO
