&emsp;

&emsp;

<div align=center><img src="https://s1.ax1x.com/2022/10/18/xrKqgK.png" width="500"  align=center /></div>

&emsp;

&emsp;

**新发布release 1.0.0的版本，说明如下：**

新增功能：

1、**客户线索**--支持管理后台和openAPI两种方式，可以通过标签对线索进行分类，详情中可以查看员工跟进线索的记录；

功能优化：

1、**用户角色**--优化超级管理员和企业员工的权限角色，支持多项目管理；



## 

## 1、 MarketGo介绍

**MarketGo 中国式营销自动化开源项目标杆**

> 在介绍本项目之前，首先和大家谈一谈在中国做营销遇到的诸多问题：

## 我想以下这些话可能比一份源代码更重要

> 国外的营销触达通道比较单一，主要以EDM为主。国内就复杂很多，回想一下当前国内做用户的营销触达主要的场景通道都有哪些。

- 企业微信（私聊、社群、朋友圈）
- 微信公众号
- 小程序
- 视频号
- 抖音
- 短信
- 邮件
- 其他

> 虽然国内已经有非常多的 MarTech 公司做出了非常优秀的 SaaS 产品服务，但是国内的市场需求依然没有得到充分满足。主要体现在以下几点：

- 痛企业更注重客户的数据安全，私有化部署意愿强烈。
- 痛经营场景复杂，需要同全域经营场景打通做一体化营销，而不是单纯的买一套系统工具。
- 痛需要源代码，越来越多的企业开始选择自建，以打造出更适合自己业务的营销系统。其背后的主要原因就是市场上直接采购的 SaaS 标准工具“不业务”。

> 基于以上的痛点若稍具规模的企业想要解决以上问题，只有自建一条路，但是选择自建碰到最大的问题就是：

**从 0-1 自建时间和资金成本高**

> 一个复杂的营销自动化系统从 0-1 开发出来并没有想象的那么简单，都需要浪费大量的人力和物力，甚至短期内是看不见什么成果的，很容易导致不了了之。那么有什么好的办法能解决呢？

## 基于以上背景，国内目前暂无好的开源的营销自动化项目，MarketGo 在此背景下特意开源出来，以为国内数字化营销贡献绵薄之力

> MarketGo 更像是一个 SDK 、引擎，通过提供的标准化功能和基础能力，让开发者能快速搭建一个营销自动化系统，快速完成从 0-1 的过程，并且能基于开放的能力和源码，开发深度融合自身业务的营销系统。

> 但一个新产品也不是能一蹴而就的，MarketGo 作为领域的产品新秀，也需要一步一个脚印逐步向前迈进。对未来的发展主要分以下几个阶段：

- **MVP 版本验证**

> 此阶段我们结合了当下私域营销的趋势，选择了优先从企业微信SCRM场景切入。
> 在这个阶段我们会提供个比较基础的企业微信SCRM功能，保留了其灵活的扩展性，便于开发者能够快速基于 MarketGo 开发自己想要的 SCRM 功能。

- **平台基础能力搭建**

> 此阶段我们会重点完善平台的能力，设计出更多的插件机制和平台能力，为开发者的二开提供更多的便利。
> 在这个阶段我们会接入更多的营销场景通道、人群圈选能力、任务下发能力、内容导入能力、自动化能力等。

- **应用快速迭代**

> 此阶段我们会快速基于场景开发出更多应用和打通更多第三方应用。

# 使用本系统进行商业化之前 请注意以下几点

- 本系统不允许做云上的系统直接集成并以此来售卖,如果公司确实需要把本系统集成到项目里,请公司联系我,否则视为侵权
- 任何公司需要集成本项目并以此来售卖,请联系我并提供公司名字,否则视为侵权
- 任何个人需要集成本项目并以此来售卖,请联系我并提供公司名字,否则视为侵权
- 任何国家机关以及公益机构以及学校对本项目可以无限制使用,本人愿意无偿提供其集成以及探讨问题,算是为国家出一份自己微薄的力量

## 2、项目介绍

### 2.1、项目结构

前端项目结构如下：

```
├── babel.config.js       --按需引入配置
├── package-lock.json     --结合了逻辑树和物理树的一个快照
├── package.json          --安装树
├── public                --公共文件
│   ├── favicon.ico       --网页标题的左侧小图标
│   └── index.html        --入口页面
├── src                   --源码文件
│   ├── App.vue           --根组件
│   ├── api               --api配置
│   ├── assets            --资源文件
│   ├── components        --公共组件
│   ├── main.js           --入口文件
│   ├── mixins            --js配置+公共变量
│   ├── router            --路由
│   ├── store             --状态库
│   ├── utils             --公共方法
│   └── views             --视图界面
└── vue.config.js         --配置文件
```

后端项目结构如下：

```
├── marketgo.sql           --系统的sql脚本
├── mktgo-api              --dubbo的接口定义
├── mktgo-biz              --业务module
├── mktgo-common           --公共依赖
├── mktgo-core             --业务核心公共模块，
├── mktgo-gateway          --网关服务，请求企微的接口
├── mktgo-react            --接收外部请求的modul，可以根据自己的客户量级，做成服务
├── mktgo-test             --测试项目
├── mktgo-web              --web服务
└── pom.xml                --公共依赖
```

### 2.2、技术框架

- 核心框架：SpringBoot 2.7.0
- 日志管理：SLF4J 1.7
- 持久层框架：spring-data-jpa   2.7.0
- RPC框架：dubbo 2.7.13
- 项目管理框架: Maven 3.6.0
- 前端框架：Vue 2.6.11
- UI框架: element-ui 2.15.9

### 2.3、开发环境

- IDE: IntelliJ IDEA 2019.2+
- DB: Mysql 5.7.37
- JDK: JDK 1.8
- Maven: Maven 3.6.1
- Redis:7.0.2
- Nginx: 1.12.2

## 3、产品介绍

[产品介绍](https://docs.qq.com/doc/DUlRoUGhLc3VRb1dU)

## 4、版本说明

[发布版本列表](https://docs.qq.com/doc/DUk9hSlhxUlRXb0RV)

## 5、演示环境

[演示环境](http://demo.marketgo.cn/#/login))

用户名：admin

密码：123456

## 6、部署流程

[服务器配置](https://docs.qq.com/doc/DUkNMRmVadGpOT2hD)

[端口配置](https://docs.qq.com/doc/DUnRKWGdIRlNkVU5z)

[环境准备](https://docs.qq.com/doc/DUnlkcGlWY2l2VlJq)

[前端部署](https://docs.qq.com/doc/DUmtMQllqQmVHTVpE)

[后端部署](https://docs.qq.com/doc/DUnBnb0FpWUpPd0dT)

## 7、定制开发

需要基于MarketGo做定制业务需求，请将需求整理成文档和联系方式，发给邮箱：[kevinwangwn0209@gmail.com](kevinwangwn0209@gmail.com)

如果需要技术支持的，请支持299元，并附上你的微信号，按照捐赠的方式支付，我们会联系您并提供项目和安装的相关技术支持。

也可以添加如下微信好友

<center class="half">
    <img src="WechatIMG15AddFriend1.jpeg" width="300"/>
</center>

## 8、开源说明

MarketGo的源码100%开源，遵守GPL-3.0协议。

开发者可以基于MarketGo做二次开发，源码完全免费。

## 9、💪支持作者,捐赠

开源不易，坚持更难！如果您觉得MarketGo不错，可以捐赠一杯奶茶，在此表示感谢^_^。

当前我们接受来自于**微信**、**支付宝**的捐赠，请在捐赠时备注自己的昵称或附言。

您的捐赠将用于支付该项目的一些费用支出，并激励开发者们以便更好的推动项目的发展。

<div align=center><img src="wechatimag.png" width="1000"  align=center /></div>
