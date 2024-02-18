[<img src="logo.png">]()

[<img src="https://img.shields.io/badge/DBScanner-veryGood-yellow">]()
[<img src="https://img.shields.io/github/languages/top/glyrn/dbScanner.svg">](https://github.com/glyrn/dbScanner)
[<img src="https://img.shields.io/github/last-commit/glyrn/dbScanner.svg">](https://github.com/glyrn/dbScanner)
[<img src="https://img.shields.io/github/stars/glyrn/dbScanner.svg?style=social&label=Star">](https://github.com/glyrn/dbScanner/stargazers)
[<img src="https://img.shields.io/github/watchers/glyrn/dbScanner.svg?style=social&label=Watch">](https://github.com/glyrn/dbScanner/watchers)
[<img src="https://img.shields.io/github/forks/glyrn/dbScanner.svg?style=social&label=Fork">](https://github.com/glyrn/dbScanner/network/members)
[<img src="https://img.shields.io/github/issues/glyrn/dbScanner.svg">](https://github.com/glyrn/dbScanner/issues)
[<img src="https://img.shields.io/github/issues-pr/glyrn/dbScanner.svg">](https://github.com/glyrn/dbScanner/pulls)



# 扫表工具简介


该工具专为解决实际工程创建中的繁琐问题而设计。对于新建或修改的数据模型，传统方法需要通过如`Navicat`等工具，手动在MySQL数据库中创建相应的表。
然而，接入`DBScanner`后，这一过程将变得自动化和高效。
您只需在项目中为实体类添加扫表工具提供的特定注解，剩下的工作交给`DBScanner`来处理。通过在启动类中插入这一行代码

```java
DBScanner.getInstance().startWork();
```

DBScanner将会进行以下操作：
>如果数据库尚未创建，`DBScanner`会为您自动创建数据库并生成所需表；若数据库已存在，会扫描当前数据库中的表结构以及实体类中注解的结构，智能比较并识别出差异，并自动生成相应的SQL语句来同步这些差异。

# `DBScanner`接入步骤
1. 下载jar包
> maven和gradle配置方式待更新 
> 其实尝试过maven账号 但是好像不开放


2. 在新的工程中将此jar包添加为依赖

截图添加

## 自定义注解接入

1. 注解介绍

@DBScanEntity

标记实体类

@DBScanTable

标记需要映射到数据库的实体类

@DBScanTableExt

标记字段默认值，以及映射到实体表中的索引

@DBScanId

主键标识，在字段上使用此注解，标记主键

@DBScanColumn

列表示，用来标记字段类型

@DBScanColumnExt

标记列约束，辅助使用


## 配置文件接入

DBScanner默认读取当前项目中的DbCfg.ymal文件中的配置，因此您需要在src/resources文件夹下手动创建这个文件，并在配置文件中根据您的实际情况进行如下配置

> dbUrl: jdbc:mysql://127.0.0.1:3306/hello_2024
> 
> usr: root
> 
> pwd: 123456
> 
> entityPackage: entity4test



