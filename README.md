## DB扫表工具开发

1. 日志系统开发: 记录每次生成的sql记录，记录到日志里面。
2. md5比对表结构差异，在项目起服过程中，比对数据库表结构差异，生成对应的sql同步差异，如果没有库，则直接创建新的数据库
3. 创建abstarct类, 自动记录创建记录时间，修改时间，删除标记，通过单例的db管理器实现异步同步入库同步的功能
4. 抽象出查询，修改，新增方法，并能同步 【3】中的异步同步修改中

---
2023/11/18 
规划总体结构
核心类:
scanner: 扫描表结构差异，组织sql
dbsource: 保持sql会话，记录执行sql（接入日志系统）
核心注解:
@table [表注解]
@id [主键id注解]
@col [普通字段列注解]
@index [索引注解]
---
