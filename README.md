cdc-demo

```shell
CREATE TABLE `tab_member_score` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` varchar(20) NOT NULL,
  `score` bigint NOT NULL DEFAULT '20000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
  
```