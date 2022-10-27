# MarketGo

## 特性
```
一、使用vue.js全家桶开发

```
## 项目结构
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
## 环境配置
```
MarketGo版本由@weining适配并维护，支持大多数浏览器的兼容。
访问本项目某某页面下载安装包
一、首先是NodeJS，去官网下载node之后，推荐安装稳定版本（LTS）node官网下载地址https://nodejs.org
安装成功后打开命令窗口查看node安装成功否
node -v 查看node版本
npm -v 查看npm版本
where node 查看node安装位置
注意:node 版本号大于npm版本号，基于之前安装版本过低，后面会报错。
```
## 安装
```
一、安装vue相关依赖
1、安装vue
npm install vue -g
npm install vue-router -g
2、安装vue-cli脚手架
npm install vue-cli -g
```
### 运行项目
```
一、从命令符进入项目所在目录，执行命令安装相关包：
npm install
二、然后执行命令启动项目：
npm run serve
运行完成后访问命令行最后显示出的地址复制访问该项目
三、可能出现的问题：
在执行npm install安装过程中可能出现安装失败的情况，例如：各个包可能存在停止使用的情况或版本对应错误问题，当出现依赖停止使用或版本不合适的情况时，首先在node_modules目录中找到该包，然后删除，再重新选择版本进行安装。

```
### 项目打包及配置
```
npm run build
```