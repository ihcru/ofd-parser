
Парсер чеков из 1-ofd через rest-api

https://org.1-ofd.ru/

№ транзакции ihc берется из наименования товара

Чеки выгружаются в таблицу

```sql
CREATE TABLE `_ofd_receipts` (
  `id` varchar(50) NOT NULL,
  `tx_id` bigint(20) NOT NULL,
  `tx_type` bigint(20) NOT NULL,
  `dt` datetime DEFAULT NULL,
  `full_sum` double(20,6) NOT NULL,
  `price` double(20,6) NOT NULL,
  `quantity` double(20,6) NOT NULL,
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

