<img src="logo.png">

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

## 自定义注解接入

## 配置文件接入

DBScanner默认读取当前项目中的DbCfg.ymal文件中的配置，因此您需要在这个文件夹下面手动创建这个文件，并在配置文件中进行如下配置

