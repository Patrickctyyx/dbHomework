# 基于 Spring Boot 与 Hibernate 的社团管理系统后台

标签（空格分隔）： 数据库

---

## 说明

这个系统是这学期数据库的大作业内容，本来我之前一直是用 Python + Flask 来写 REST API 的，这学期向挑战一下自己，于是用 Java 写了这个社团管理系统的 API。

不过毕竟是从零开始，项目中有一大堆垃圾代码，由于时间原因，单元测试也没有编写，有些类型检查也缺少。但是 emmmm，至少可以比较顺利地跑起来 233。

## 介绍

后台系统中一共涉及到了五个表：

- 用户表
- 社团表
- 用户社团关系表
- 通知表
- 申请表

用户和社团之间通过用户社团关系表实现多对多的关系，用户和通知是一对多的关系，社团和通知也是一对多的关系，用户不直接与申请表挂钩，但是申请表与社团是多对一的关系。

## API 文档

[社团管理系统文档][1]

## 运行

由于安全原因， main 目录下的 application.propertites 文件被省去。可以自行按照以下的内容来创建配置文件：

```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driverClassName=
spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=
cty.appid=
cty.appSecret=
```

另外是 WeChatController 是用来和小程序的前端进行交互。

最简单的运行方式就是知道导到 Idea 里面然后运行 HelloWorld.java 即可。

## 最后

和 Python 比起来，Java 后端开发的准备工作要麻烦不少，不过真正有了一些概念后写起 Controller 并不比 Python 来的麻烦多少，不过如果让我选的话以后还是会用 Python 来写 233。毕竟人生苦短，我选 Python~

又想起了那一周熬夜爆肝从零入门的日子了 orz，刚开始学果然是最难的。

## 参考教程

- [Spring Boot 教程][2]
- [Hibernate 关系教程][3]
- 以下省略众多给多很多帮助的教程



  [1]: https://www.kancloud.cn/chengtianyang/aaa
  [2]: http://tengj.top/categories/Spring-Boot%E5%B9%B2%E8%B4%A7%E7%B3%BB%E5%88%97/page/2/
  [3]: http://www.cnblogs.com/luxh/archive/2012/05/27/2520322.html
  