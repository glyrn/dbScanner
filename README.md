<img src="logo.png">

[<img src="https://img.shields.io/badge/DBScanner-veryGood-yellow">]()

[//]: # ([<img src="https://img.shields.io/github/languages/top/glyrn/dbScanner.svg">]&#40;https://github.com/glyrn/dbScanner&#41;)

[//]: # ([<img src="https://img.shields.io/github/last-commit/glyrn/dbScanner.svg">]&#40;https://github.com/glyrn/dbScanner&#41;)

[//]: # ([<img src="https://img.shields.io/github/stars/glyrn/dbScanner.svg?style=social&label=Star">]&#40;https://github.com/glyrn/dbScanner/stargazers&#41;)

[//]: # ([<img src="https://img.shields.io/github/watchers/glyrn/dbScanner.svg?style=social&label=Watch">]&#40;https://github.com/glyrn/dbScanner/watchers&#41;)

[//]: # ([<img src="https://img.shields.io/github/forks/glyrn/dbScanner.svg?style=social&label=Fork">]&#40;https://github.com/glyrn/dbScanner/network/members&#41;)

[//]: # ([<img src="https://img.shields.io/github/issues/glyrn/dbScanner.svg">]&#40;https://github.com/glyrn/dbScanner/issues&#41;)

[//]: # ([<img src="https://img.shields.io/github/issues-pr/glyrn/dbScanner.svg">]&#40;https://github.com/glyrn/dbScanner/pulls&#41;)



# 扫表工具简介


该工具专为解决实际工程创建中的繁琐问题而设计。对于新建或修改的数据模型，传统方法需要通过如`Navicat`等工具，手动在MySQL数据库中创建相应的表。
然而，接入`DBScanner`后，这一过程将变得自动化和高效。
您只需在项目中为实体类添加扫表工具提供的特定注解，剩下的工作交给`DBScanner`来处理。

DBScanner将会进行以下操作：
>如果数据库尚未创建，`DBScanner`会为您自动创建数据库并生成所需表；若数据库已存在，会扫描当前数据库中的表结构以及实体类中注解的结构，智能比较并识别出差异，并自动生成相应的SQL语句来同步这些差异。

# `DBScanner`接入步骤
## 依赖接入
1. 下载jar包
```shell
curl http://guolinyun.com:8080/gly/dbScanner-1.0.jar
```

2. 在新的工程中将此jar包添加为依赖

3. 添加jvm参数
```shell
-javaagent:<path to this jar>
```

## 自定义注解接入

### @DBScanId
- 解释: 用来标识主键
- 参数: 无

> 示例
> ```java
> @DBScanId
> private int id;
>```

### @GeneratedValue
- 解释: 自增标记
- 参数: generator

> 示例
> ```java
> @GeneratedValue(generator = "AUTO_INCREMENT")
> private int id;
>```


### @DBScanTable
- 解释: 用来设定表的信息
- 参数: 
  - name (表名)
  - uniqueConstraints (定义联合索引)

> 示例
> ```java
> @DBScanTable(name = "dog", uniqueConstraints = @UniqueConstraint(name = "ageheight", columnNames = {"age", "height"}))
> public class dog {...}
>```

### @DBScanColumn
- 解释: 设定列的信息
- 参数:
  - name (列名字) [String] [必须填]
  - type (字段类型) [默认将自动解析字段类型]
  - size (长度) [int] [默认 0]
  - collate (字段默认值) [String] [默认 空]
  - defaults (字段默认值 $开头标识特殊函数) [String] [默认 空]
  - comment (字段注释) [String] [默认 空]
  - extra (特殊语句) [String] [默认 空]
  - hasDefaults (是否有默认值) [bool] [默认 true]
  - index (是否是索引) [bool] [默认 false] 
  - modifyMode (修改字段比对模式) [SKIP | ONLY_CORE | STRICT] [默认 STRICT]
  - nullable (是否可空) [bool] [默认 true]
  - unique (是否唯一) [bool] [默认 false]
> 示例
> ```java
> @DBScanColumn(name = "playerId", size = 10, index = true)
> private int playerId;
>```


## 配置文件接入

DBScanner默认读取当前项目中的DbCfg.ymal文件中的配置，因此需要在src/resources文件夹下手动创建这个文件，并在配置文件中根据实际情况进行如下配置


```shell
dbUrl: jdbc:mysql://127.0.0.1:3306/hello_2024
usr: root
pwd: 123456
entityPackage: entity4test1, entity4test2
```
> entityPackage如果存在多个，使用逗号隔开


