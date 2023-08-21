/*
 Navicat Premium Data Transfer

 Source Server         : 10.9.187.109
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 10.9.187.109:3306
 Source Schema         : marketgo

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 06/12/2022 12:00:50
*/
CREATE DATABASE IF NOT EXISTS marketgo DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

use marketgo;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tenant_config
-- ----------------------------
DROP TABLE IF EXISTS `tenant_config`;
CREATE TABLE `tenant_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(64) NOT NULL COMMENT '业务主键',
  `name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '租户名称',
  `media_storage_type` varchar(24) NOT NULL DEFAULT 'MYSQL' COMMENT 'mysql, cdn, hdfs',
  `status` VARCHAR(32) NOT NULL,
  `create_time` TIMESTAMP(6) NOT NULL DEFAULT current_TIMESTAMP(6),
  `update_time` TIMESTAMP(6) NOT NULL DEFAULT current_TIMESTAMP(6) ON UPDATE current_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_idx_tenant` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='租户管理';

-- ----------------------------
-- Table structure for project_config
-- ----------------------------
DROP TABLE IF EXISTS `project_config`;
CREATE TABLE `project_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(64) NOT NULL COMMENT '业务主键',
  `name` varchar(255) NOT NULL COMMENT '项目名称',
  `desc` varchar(255) DEFAULT NULL COMMENT '项目描述',
  `type` varchar(32) DEFAULT NULL COMMENT '项目类型 SCRM',
  `tenant_uuid` VARCHAR(128) NOT NULL,
  `status` VARCHAR(32) NOT NULL COMMENT '项目状态 , publish, stop',
  `create_time` TIMESTAMP(6) NOT NULL DEFAULT current_TIMESTAMP(6),
  `update_time` TIMESTAMP(6) NOT NULL DEFAULT current_TIMESTAMP(6) ON UPDATE current_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_idx_project_project_uuid` (`uuid`),
  UNIQUE KEY `unique_idx_project` (`tenant_uuid`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的项目管理';

-- ----------------------------
-- Table structure for wecom_agent_config
-- ----------------------------
DROP TABLE IF EXISTS `wecom_agent_config`;
CREATE TABLE `wecom_agent_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_uuid` varchar(128) NOT NULL COMMENT '项目uuid',
  `name` varchar(255) NOT NULL COMMENT '应用名称',
  `corp_id` varchar(255) NOT NULL COMMENT '企业ID',
  `agent_id` varchar(255) NOT NULL COMMENT '企微应用ID',
  `secret` varchar(255) NOT NULL COMMENT '企业secret',
  `is_chief` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否是主应用 1是主应用 ,0是非主应用',
  `file_name`  varchar(255) DEFAULT NULL COMMENT '可信文件名称',
  `file_content` varchar(255) DEFAULT NULL COMMENT '文件内容',
  `home_page` varchar(255) DEFAULT NULL COMMENT '应用主页',
  `enable_status` varchar(255) DEFAULT NULL COMMENT '应用启用状态',
  `auth_status` varchar(20)  DEFAULT NULL COMMENT '应用授权状态',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_uuid_corp_id` (`project_uuid`,`corp_id`),
  UNIQUE KEY `uk_agent_id_corp_id` (`agent_id`,`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的应用管理';

-- ----------------------------
-- Table structure for wecom_media_resource
-- ----------------------------
DROP TABLE IF EXISTS `wecom_media_resource`;
CREATE TABLE `wecom_media_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uuid` VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid` varchar(64) NOT NULL COMMENT '项目ID',
  `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `name`         varchar(2048)  NOT NULL COMMENT '素材名称',
  `is_temp`      TINYINT(1) NOT NULL DEFAULT 0,
  `storage_type` varchar(24) NOT NULL COMMENT 'mysql, cdn, hdfs',
  `media_type` varchar(24)  NOT NULL COMMENT 'IMAGE、VIDEO、FILE、MINIPROGRAM、LINK、VOICE',
  `file_type` varchar(24)  NOT NULL COMMENT 'WORD、EXCEL、PPT、PDF、PNG、JPEG、MP4',
  `file_size` int(11) NOT NULL  COMMENT '文件大小',
  `media_id`  TEXT NOT NULL  COMMENT '临时素材存：mediaId, 永久素材 (图片）存：url',
  `expire_time` datetime  DEFAULT NULL COMMENT '临时素材过期时间',
  `is_finish`   TINYINT(1) NOT NULL DEFAULT 0,
  `media_data` LONGBLOB DEFAULT NULL  COMMENT '素材数据',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的素材管理';

-- ----------------------------
-- Table structure for wecom_mass_task
-- ----------------------------
DROP TABLE IF EXISTS `wecom_mass_task`;
CREATE TABLE `wecom_mass_task`
(
  `id`                       INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                     VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`             VARCHAR(128) NOT NULL COMMENT '项目ID',
  `corp_id`                  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `name`                     VARCHAR(256)  DEFAULT NULL COMMENT '任务名称',
  `task_type`                VARCHAR(50)   DEFAULT NULL COMMENT '任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT',
  `schedule_type`            VARCHAR(50)   DEFAULT NULL COMMENT '发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME',
  `schedule_time`            datetime     DEFAULT NULL  COMMENT '计划发送时间',
  `user_group_uuid`          VARCHAR(64)   DEFAULT NULL COMMENT '关联人群预估UUID',
  `content`                  TEXT          DEFAULT NULL COMMENT '推送消息内容',
  `task_status`           VARCHAR(50)   DEFAULT NULL COMMENT '任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; 已结束 FINISHED; 执行失败 FAILED',
  `creator_id`               VARCHAR(64) NOT NULL COMMENT '创建人ID',
  `creator_name`            VARCHAR(50)   DEFAULT NULL COMMENT '创建人姓名',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  `remind_time`              datetime  DEFAULT NULL COMMENT '提醒时间',
  `finish_time`              datetime  DEFAULT NULL COMMENT '任务完成时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_uuid` (`uuid`),
  UNIQUE KEY `idx_uniq_project_uuid_corp_id_type_name` (`project_uuid`,`corp_id`, `task_type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的群发活动';

-- ----------------------------
-- Table structure for user_group_estimate
-- ----------------------------
DROP TABLE IF EXISTS `user_group_estimate`;
CREATE TABLE `user_group_estimate`
(
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                 VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`         VARCHAR(64) NOT NULL COMMENT '项目uuid',
  `request_id`           VARCHAR(64) NOT NULL DEFAULT '' COMMENT '请求ID ,前端生成',
  `task_type`            VARCHAR(48) DEFAULT NULL COMMENT '任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT',
  `user_group_type`  VARCHAR(25)  NOT NULL DEFAULT '' COMMENT '人群条件类型 ,WECOM, CDP, OFFLINE',
  `wecom_conditions`  TEXT   DEFAULT NULL COMMENT 'wecom人群圈选条件',
  `cdp_conditions`  TEXT   DEFAULT NULL COMMENT 'cdp人群圈选条件',
  `offline_conditions`  TEXT   DEFAULT NULL COMMENT 'cdp人群圈选条件',
  `conditions_relation`  VARCHAR(25)  NOT NULL DEFAULT '' COMMENT '条件的关系',
  `status`               VARCHAR(25)  NOT NULL DEFAULT '' COMMENT '预估状态',
  `estimate_result`      TEXT         DEFAULT NULL COMMENT '预估结果',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_uuid` (`uuid`),
  UNIQUE KEY `idx_uniq_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT '人群预估';

-- ----------------------------
-- Table structure for wecom_corp_config
-- ----------------------------
DROP TABLE IF EXISTS `wecom_corp_config`;
CREATE TABLE `wecom_corp_config` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `project_uuid`         varchar(128) NOT NULL COMMENT '项目ID',
  `corp_name`            VARCHAR(1024) NOT NULL COMMENT '企微CORP名称',
  `corp_id`              VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `contacts_secret`       varchar(255) DEFAULT NULL COMMENT '通讯录的secret',
  `contacts_token`                varchar(255) DEFAULT NULL COMMENT  'callback 对应验证的token',
  `contacts_encoding_aes_key`     varchar(255) DEFAULT NULL COMMENT  'callback 对应验证的encodingAesKey',
  `external_user_secret`         varchar(255) DEFAULT NULL COMMENT '客户联系的secret',
  `external_user_token`                varchar(255) DEFAULT NULL COMMENT  '客户联系的callback 对应验证的token',
  `external_user_encoding_aes_key`     varchar(255) DEFAULT NULL COMMENT  '客户联系的callback 对应验证的encodingAesKey',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_corp_id` (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微企业信息';

-- ----------------------------
-- Table structure for wecom_mass_task_statistic_member
-- ----------------------------
DROP TABLE IF EXISTS `wecom_mass_task_statistic_member`;
CREATE TABLE `wecom_mass_task_statistic_member` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `project_uuid`         varchar(64) NOT NULL COMMENT '项目ID',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '群发的任务uuid',
  `member_id`              VARCHAR(128) NOT NULL COMMENT '员工的id',
  `member_name`              VARCHAR(128) NOT NULL COMMENT '员工的名称',
  `status`                varchar(32) DEFAULT NULL COMMENT  '员工的任务执行状态',
  `external_user_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户总数',
  `delivered_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户送达总数',
  `non_friend_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户非好友总数',
  `exceed_limit_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户送达达上限总数',
  `send_id`              varchar(255) DEFAULT NULL COMMENT  '给员工发送任务后返回的id ,一个员工对应有可能是多个send_id',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_task_uuid_member_id` (`task_uuid`, `member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微群发任务的员工统计';

-- ----------------------------
-- Table structure for wecom_event_external_user
-- ----------------------------
DROP TABLE IF EXISTS `wecom_event_external_user`;
CREATE TABLE `wecom_event_external_user` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `event_md5`                VARCHAR(64) NOT NULL COMMENT '事件的所有信息的md5值，保证事件不重复',
  `corp_id`              VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `member_id`              VARCHAR(128) NOT NULL COMMENT '员工的id',
  `external_user_id`     varchar(255) DEFAULT NULL COMMENT '客户的ID',
  `event_type`                VARCHAR(64) DEFAULT NULL COMMENT 'callback返回的state',
  `state`                VARCHAR(64) DEFAULT NULL COMMENT 'callback返回的state',
  `welcome_code`         VARCHAR(64) DEFAULT NULL COMMENT 'callback返回的欢迎code',
  `source`               VARCHAR(64) DEFAULT NULL COMMENT '删除客户的操作来源，DELETE_BY_TRANSFER表示此客户是因在职继承自动被转接成员删除',
  `fail_reason`         VARCHAR(64) DEFAULT NULL COMMENT '接替失败的原因, customer_refused-客户拒绝， customer_limit_exceed-接替成员的客户数达到上限',
  `event_time`        datetime     DEFAULT NULL COMMENT '事件产生的时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_event_md5` (`event_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微客户事件列表';

-- ----------------------------
-- Table structure for wecom_event_group_chat
-- ----------------------------
DROP TABLE IF EXISTS `wecom_event_group_chat`;
CREATE TABLE `wecom_event_group_chat` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `event_md5`                VARCHAR(64) NOT NULL COMMENT '事件的所有信息的md5值，保证事件不重复',
  `corp_id`              VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `group_chat_id`              VARCHAR(128) NOT NULL COMMENT '员工的id',
  `update_detail`     varchar(255) DEFAULT NULL COMMENT '客户的ID',
  `event_type`                VARCHAR(64) DEFAULT NULL COMMENT 'callback返回的state',
  `join_scene`               tinyint(2) DEFAULT NULL COMMENT '当是成员入群时有值。表示成员的入群方式. 0 - 由成员邀请入群（包括直接邀请入群和通过邀请链接入群）, 3 - 通过扫描群二维码入群',
  `quit_scene`         tinyint(2) DEFAULT NULL COMMENT '当是成员退群时有值。表示成员的退群方式. 0 - 自己退群, 1 - 群主/群管理员移出',
  `mem_change_cnt`         INT(11) DEFAULT NULL COMMENT '当是成员入群或退群时有值。表示成员变更数量',
  `event_time`        datetime     DEFAULT NULL COMMENT '事件产生的时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_event_md5` (`event_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微客户群事件列表';

-- ----------------------------
-- Table structure for wecom_mass_task_statistic_external_user
-- ----------------------------
DROP TABLE IF EXISTS `wecom_mass_task_statistic_external_user`;
CREATE TABLE `wecom_mass_task_statistic_external_user` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `project_uuid`         varchar(64) NOT NULL COMMENT '项目ID',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '群发的任务uuid',
  `member_id`            VARCHAR(128) NOT NULL COMMENT '员工的id',
  `external_user_id`     varchar(255) DEFAULT NULL COMMENT '客户的ID',
  `external_user_name`     varchar(255) DEFAULT NULL COMMENT '客户的名称',
  `external_user_type`   varchar(24) DEFAULT NULL COMMENT '客户的类型',
  `status`                varchar(64) DEFAULT NULL COMMENT  '客户的任务执行状态',
  `add_comments_time`     datetime     DEFAULT NULL COMMENT '客户的执行时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微群发任务的员工对应的客户统计';

-- ----------------------------
-- Table structure for user_group_detail
-- ----------------------------
DROP TABLE IF EXISTS `wecom_user_group_detail`;
CREATE TABLE `wecom_user_group_detail` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                 VARCHAR(64)                    NOT NULL COMMENT '业务主键',
  `project_uuid`         VARCHAR(64) NOT NULL COMMENT '项目uuid',
  `user_group_uuid`      VARCHAR(64) COLLATE utf8mb4_bin NOT NULL  COMMENT '人群的uuid',
  `member_id`            VARCHAR(128) NOT NULL COMMENT '员工的id',
  `member_name`          VARCHAR(255) DEFAULT NULL COMMENT '员工的名称',
  `deleted`              TINYINT(1) NOT NULL DEFAULT 0,
  `external_users_count` INT(11)  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '客户数量',
  `external_users`       MEDIUMTEXT  DEFAULT NULL COLLATE utf8mb4_bin COMMENT '客户',
  `status`         VARCHAR(48) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '员工的客户同步的状态,SYNCING, UNSEND, SEND',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_group_uuid_member_id` (`user_group_uuid`, `member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发送的员工和客户详情数据';

-- ----------------------------
-- Table structure for wecom_mass_task_statistic_sync
-- ----------------------------
DROP TABLE IF EXISTS `wecom_mass_task_statistic_sync`;
CREATE TABLE `wecom_mass_task_statistic_sync` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                 VARCHAR(64)  NOT NULL COMMENT '业务主键',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '群发的任务uuid',
  `task_type`            VARCHAR(48) DEFAULT NULL COMMENT '任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT',
  `send_id`              varchar(255) DEFAULT NULL COMMENT  '给员工发送任务后返回的id ,一个员工对应有可能是多个send_id',
  `send_id_type`         varchar(255) DEFAULT NULL COMMENT 'send_id的类型。msg_id, job_id, moment_id',
  `deleted`              TINYINT(1) NOT NULL DEFAULT 0,
  `sync_status`         varchar(24) DEFAULT NULL COMMENT  '同步状态 ,INIT, RUNNING, FINISH',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_task_uuid_send_id` (`task_uuid`, `send_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微群发任务的员工统计';

-- ----------------------------
-- Table structure for wecom_members
-- ----------------------------
DROP TABLE IF EXISTS `wecom_members`;
CREATE TABLE `wecom_members` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `member_name` varchar(255) NOT NULL  COMMENT '员工名称',
  `member_id`   varchar(255) NOT NULL  COMMENT '员工ID',
  `alias`      varchar(255) DEFAULT '' COMMENT '员工别名',
  `mobile`      varchar(255) DEFAULT '' COMMENT '员工手机号',
  `avatar`      varchar(1024) DEFAULT '' COMMENT '头像url',
  `thumb_avatar`  varchar(1024) DEFAULT '' COMMENT '头像缩略图url',
  `department`  varchar(2048) DEFAULT NULL COMMENT '部门列表',
  `main_department`  varchar(2048) DEFAULT NULL COMMENT '部门列表',
  `status`       TINYINT(2) NOT NULL DEFAULT 1 COMMENT '员工的状态; 1=已激活 ,2=已禁用 ,4=未激活 ,5=退出企业',
  `qr_code`      varchar(1024) DEFAULT NULL COMMENT '员工个人二维码 ,扫描可添加为客户',
  `open_user_id` varchar(128) DEFAULT NULL COMMENT '全局唯一。对于同一个服务商 ,不同应用获取到企业内同一个成员的open_userid是相同的',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_memeber_id` (`corp_id`, `member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的员工列表';

-- ----------------------------
-- Table structure for wecom_corp_tags
-- ----------------------------
DROP TABLE IF EXISTS `wecom_corp_tags`;
CREATE TABLE `wecom_corp_tags` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `group_id` varchar(128) DEFAULT NULL  COMMENT '标签组id',
  `tag_id`    varchar(128) NOT NULL COMMENT '标签id',
  `name`   varchar(1024) NOT NULL  COMMENT '标签名称',
  `order` INT(11) DEFAULT NULL COMMENT '标签组排序的次序值 ,order值大的排序靠前。有效的值范围是[0, 2^32)',
  `deleted`  TINYINT(1)  DEFAULT NULL COMMENT '标签组是否已经被删除 ,只在指定tag_id进行查询时返回',
  `type`     TINYINT(1) DEFAULT NULL COMMENT '类型 ,0 group 1 tag',
  `add_time`   datetime  DEFAULT NULL   COMMENT '标签组创建时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_tag_id` (`corp_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的企业标签列表';

-- ----------------------------
-- Table structure for wecom_relation_member_external_user
-- ----------------------------
DROP TABLE IF EXISTS `wecom_relation_member_external_user`;
CREATE TABLE `wecom_relation_member_external_user` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `external_user_id`   varchar(255) NOT NULL  COMMENT '客户ID',
  `external_user_name` varchar(255) NOT NULL  COMMENT '客户名称',
  `type`      TINYINT(2) DEFAULT NULL COMMENT '客户类型 ,1表示该外部联系人是微信用户 ,2表示该外部联系人是企业微信用户',
  `avatar`      varchar(1024) DEFAULT '' COMMENT '头像url',
  `unionid`  varchar(128) DEFAULT '' COMMENT '客户unionid',
  `gender`   TINYINT(2) DEFAULT 0 COMMENT '外部联系人性别, 0-未知 1-男性 2-女性',
  `corp_name`      varchar(512) DEFAULT '' COMMENT '外部联系人所在企业的简称',
  `corp_full_name` varchar(1024) DEFAULT NULL COMMENT '外部联系人所在企业的主体名称',
  `member_id`   varchar(255) NOT NULL  COMMENT '员工ID',
  `remark`   varchar(255) DEFAULT NULL  COMMENT '客户备注',
  `description`   varchar(255) DEFAULT NULL  COMMENT '客户描述',
  `relation_type`  TINYINT(2) DEFAULT 0 COMMENT '客户和员工关系, 0-好友 1-非好友',
  `tags` varchar(1024) DEFAULT NULL COMMENT '企业标签列表',
  `add_time` datetime NOT NULL COMMENT '客户添加时间',
  `add_way` SMALLINT (6) DEFAULT NULL COMMENT '客户添加来源',
  `add_source_user_id` varchar(255) DEFAULT NULL COMMENT '发起添加的userId',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_member_id_external_user_id` (`corp_id`, `member_id`, `external_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的员工和客户关系列表';

-- ----------------------------
-- Table structure for wecom_departments
-- ----------------------------
DROP TABLE IF EXISTS `wecom_departments`;
CREATE TABLE `wecom_departments` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `department_id` INT(11) NOT NULL  COMMENT '部门ID',
  `department_name`   varchar(255) NOT NULL  COMMENT '部门名称',
  `department_name_en` varchar(255) DEFAULT NULL  COMMENT '部门英文名称',
  `department_leader` varchar(255) DEFAULT NULL  COMMENT '部门负责人的UserID',
  `parent_id`    INT(11) NOT NULL  COMMENT '父部门id。根部门为1',
  `order`       INT(11) NOT NULL  COMMENT '在父部门中的次序值。order值大的排序靠前。值范围是[0, 2^32)',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_department_id` (`corp_id`, `department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的部门列表';

-- ----------------------------
-- Table structure for wecom_group_chats
-- ----------------------------
DROP TABLE IF EXISTS `wecom_group_chats`;
CREATE TABLE `wecom_group_chats` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `group_chat_name` varchar(255) NOT NULL  COMMENT '客户群名称',
  `group_chat_id`   varchar(255) NOT NULL  COMMENT '客户群ID',
  `owner` varchar(255) NOT NULL  COMMENT '群主ID',
  `notice` varchar(255) DEFAULT NULL  COMMENT '公告',
  `admin_list` varchar(255) DEFAULT NULL  COMMENT '群管理员列表',
  `chat_create_time` datetime NOT NULL,
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_group_chat_id` (`corp_id`, `group_chat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的客户群列表';

-- ----------------------------
-- Table structure for wecom_group_chat_members
-- ----------------------------
DROP TABLE IF EXISTS `wecom_group_chat_members`;
CREATE TABLE `wecom_group_chat_members` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
  `corp_id`  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `group_chat_id` varchar(255) NOT NULL  COMMENT '客户群ID',
  `group_nickname` varchar(255) NOT NULL  COMMENT '群里的昵称',
  `user_id`   varchar(255) NOT NULL  COMMENT '成员ID',
  `type`   TINYINT(2) DEFAULT NULL COMMENT '成员类型 1 - 企业成员, 2 - 外部联系人',
  `union_id` varchar(255) DEFAULT NULL COMMENT '微信unionid',
  `join_time` datetime NOT NULL COMMENT '入群时间',
  `join_scene` TINYINT(2) DEFAULT NULL  COMMENT '入群方式, 1 - 由群成员邀请入群 (直接邀请入群）2 - 由群成员邀请入群 (通过邀请链接入群）,3 - 通过扫描群二维码入群',
  `name`  varchar(255) DEFAULT NULL  COMMENT '微信用户 ,则返回其在微信中设置的名字; 企业微信联系人 ,则返回其设置对外展示的别名或实名',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_corp_id_group_chat_id_user_id` (`corp_id`, `group_chat_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的客户群成员列表';

-- ----------------------------
-- Table structure for wecom_mass_task_send_queue
-- ----------------------------
DROP TABLE IF EXISTS `wecom_mass_task_send_queue`;
CREATE TABLE `wecom_mass_task_send_queue` (
  `id`   INT(11) NOT NULL AUTO_INCREMENT,
   `uuid`  VARCHAR(64)  NOT NULL COMMENT '业务主键',
  `member_md5`  VARCHAR(128) NOT NULL COMMENT '员工ids的md5',
  `task_uuid`  VARCHAR(128) NOT NULL COMMENT '群发的任务uuid',
  `member_id`   TEXT NOT NULL  COMMENT '员工的ids',
  `external_user_ids` TEXT DEFAULT NULL  COMMENT '发送的客户id列表',
  `status` varchar(24) DEFAULT 'UNSENT' COMMENT '发送的状态 ,UNSENT, SENT',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_member_md5_task_uuid` (`member_md5`, `task_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的客户群发的员工发送列表';


-- ----------------------------
-- Table structure for wecom_sys_base_permissions
-- ----------------------------
DROP TABLE IF EXISTS `wecom_sys_base_permissions`;
CREATE TABLE `wecom_sys_base_permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid`  VARCHAR(64)  NOT NULL COMMENT '业务主键',
  `code` varchar(32) DEFAULT NULL COMMENT '功能编码',
  `parent_code` varchar(255) DEFAULT NULL COMMENT '父级功能编码',
  `name` varchar(255) DEFAULT NULL COMMENT '功能名称',
  `parent_name` varchar(255) DEFAULT NULL COMMENT '父功能名称',
  `title` varchar(255) DEFAULT NULL,
   `parent_title` varchar(255) DEFAULT NULL,
   `project_uuid` varchar(64) DEFAULT NULL,
   `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for wecom_sys_base_role
-- ----------------------------
DROP TABLE IF EXISTS `wecom_sys_base_role`;
CREATE TABLE `wecom_sys_base_role` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `uuid`  VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `code` varchar(32) DEFAULT NULL COMMENT '角色编码',
    `desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
    `project_uuid` varchar(64) DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for wecom_sys_corp_role_permissions_link
-- ----------------------------
DROP TABLE IF EXISTS `wecom_sys_corp_role_permissions_link`;
CREATE TABLE `wecom_sys_corp_role_permissions_link` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
    `role_uuid` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL,
    `permissions_uuid` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL,
    `status` varchar(32) DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业微信角色资源表';

-- ----------------------------
-- Table structure for wecom_sys_corp_user_role_link
-- ----------------------------
DROP TABLE IF EXISTS `wecom_sys_corp_user_role_link`;
CREATE TABLE `wecom_sys_corp_user_role_link` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
    `role_uuid` varchar(64) DEFAULT NULL,
    `member_id` varchar(64) DEFAULT NULL,
    `project_uuid` varchar(64) DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='企业微信cropId 用户角色关系表';

-- ----------------------------
-- Table structure for wecom_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `wecom_sys_user`;
CREATE TABLE `wecom_sys_user` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `user_name` varchar(128) NOT NULL,
   `password` varchar(255) NOT NULL,
   `salt` varchar(255) NOT NULL,
   `user_config` varchar(255) DEFAULT NULL,
   `uuid` varchar(64) DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for wecom_tenant_project_link
-- ----------------------------
DROP TABLE IF EXISTS `wecom_tenant_project_link`;
CREATE TABLE `wecom_tenant_project_link` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `tenant_uuid` varchar(64) NOT NULL,
    `project_uuid` varchar(64) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='租户项目关系表';

-- ----------------------------
-- Table structure for wecom_user_tenant_link
-- ----------------------------
DROP TABLE IF EXISTS `wecom_user_tenant_link`;
CREATE TABLE `wecom_user_tenant_link` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_uuid` varchar(64) NOT NULL,
    `tenant_uuid` varchar(64) NOT NULL,
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户租户关系表';


DROP TABLE IF EXISTS `wecom_channel_live_code`;
CREATE TABLE `wecom_channel_live_code` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`  VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `project_uuid` varchar(64) NOT NULL COMMENT '关联项目ID',
    `agent_id` varchar(32) NOT NULL COMMENT 'agent_id',
    `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
    `name` varchar(512) NOT NULL COMMENT '活码名称',
    `skip_verify` tinyint(1) NOT NULL COMMENT '是否跳过验证 1：自动通过 0：手动验证',
    `state` varchar(32) NOT NULL COMMENT '活码状态，1:草稿，2：已发布',
    `welcome_type` tinyint(2) NOT NULL COMMENT '0:不开启 1：渠道欢迎语  2：员工欢迎语',
    `welcome_content` text NOT NULL COMMENT '欢迎消息内容',
    `add_limit_status` tinyint(1) DEFAULT NULL  COMMENT '添加客户上限状态 1：开启 0：关闭',
    `add_limit_members` text DEFAULT NULL  COMMENT '添加客户上限的员工列表',
    `members` text DEFAULT NULL  COMMENT '员工列表',
    `backup_members` text DEFAULT NULL  COMMENT '备用员工列表',
    `tags` text DEFAULT NULL  COMMENT '标签列表',
    `online_type` tinyint(2) DEFAULT NULL  COMMENT '在线状态类型',
    `logo_media`  text  DEFAULT NULL COMMENT '二维码logo的素材',
    `config_id` varchar(64) DEFAULT NULL COMMENT '微信返回的渠道码ID',
    `qr_code` varchar(128) DEFAULT NULL COMMENT '活码地址',
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_uuid`),
    UNIQUE KEY `idx_uniq_uuid` (`uuid`),
    UNIQUE KEY `idx_uniq_project_uuid_corp_id_name` (`project_uuid`,`corp_id`, `name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='活码基本信息表';

-- ----------------------------
-- Table structure for wecom_channel_live_code_members
-- ----------------------------
DROP TABLE IF EXISTS `wecom_channel_live_code_members`;
CREATE TABLE `wecom_channel_live_code_members` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `channel_live_code_uuid` varchar(64) NOT NULL COMMENT '活码主键ID',
    `member_id` varchar(50) DEFAULT NULL COMMENT '成员Id',
    `member_name` varchar(128) DEFAULT NULL COMMENT '成员名称',
    `is_backup` tinyint(1) DEFAULT NULL COMMENT '是否备选， 0:非备选，1:备选',
     `online_status` tinyint(1) DEFAULT NULL COMMENT '在线标识，0:离线，1:在线',
    `online_day` datetime DEFAULT NULL COMMENT '员工在线状态',
    `add_limit_count` int(11) COMMENT '添加客户上限',
    `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `channel_live_code_uuid` (`channel_live_code_uuid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='活码使用员工配置表';

-- ----------------------------
-- Table structure for wecom_channel_live_code_statistic
-- ----------------------------
DROP TABLE IF EXISTS `wecom_channel_live_code_statistic`;
CREATE TABLE `wecom_channel_live_code_statistic` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
   `channel_live_code_uuid` varchar(64) DEFAULT NULL,
   `member_id` varchar(32) DEFAULT NULL,
   `member_name` varchar(64) DEFAULT NULL COMMENT '成员名称',
   `event_date` date DEFAULT NULL,
   `daily_increased_ext_user_count` int(11) DEFAULT '0',
   `daily_decrease_ext_user_count` int(11) DEFAULT '0',
   `channel_live_code_state` varchar(30) DEFAULT NULL,
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_uniq_uuid_member_id_date` (`channel_live_code_uuid`,`member_id`, `event_date`),
   UNIQUE KEY `wecom_channel_live_code_statistic_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;


INSERT INTO `tenant_config` (`uuid`, `name`, `status`) VALUE (replace(uuid(),  '-',  ''), '默认租户', 'publish');

INSERT INTO `project_config` (`uuid`, `name`, `desc`, `type`, `tenant_uuid`, `status`) VALUE (replace(uuid(),  '-',''), '默认项目', '为私域运营团队提供日常运营服务', 'SCRM', (select uuid from tenant_config), 'publish');

INSERT INTO `wecom_sys_user` ( `user_name`, `password`, `salt`, `user_config`, `uuid`) VALUES ( 'admin', '9a99aa5b2ec198ae57aa20164852920c', '41a9b51b535a86b969f931fea422e69e', NULL, MD5(uuid()));

INSERT INTO `wecom_user_tenant_link` ( `user_uuid`, `tenant_uuid`) VALUES (  (SELECT uuid from wecom_sys_user where  user_name='admin'), (SELECT uuid from tenant_config));

INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'home', '', 'home', '', '首页', '', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) , 'channelcode', '', 'channelcode', '', '渠道活码', '', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'management', '', 'management', '', '客户管理', '', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'customerlist', 'management', 'customerlist', 'management', '客户列表', '客户管理', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'customergrouplist', 'management', 'customergrouplist', 'management', '客户群列表', '客户管理', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'marketingplan', '', 'marketingplan', '', '营销计划', '', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'masscustomer', 'marketingplan', 'masscustomer', 'marketingplan', '群发客户', '营销计划', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'masscustomerbase', 'marketingplan', 'masscustomerbase', 'marketingplan', '群发客户群', '营销计划', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'sendgroupfriends', 'marketingplan', 'sendgroupfriends', 'marketingplan', '群发朋友圈', '营销计划', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'settings', '', 'settings', '', '系统设置', '', (SELECT uuid FROM project_config) );
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'membermanagement', 'settings', 'membermanagement', 'settings', '成员管理', '系统设置', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'permissionmanagement', 'settings', 'permissionmanagement', 'settings', '权限管理', '系统设置', (SELECT uuid FROM project_config));




INSERT INTO `wecom_sys_base_role` ( `uuid`, `code`, `desc`,  `project_uuid`) VALUES (MD5(uuid()) , 'super_administrator', '超级管理员', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_role` ( `uuid`, `code`, `desc`,  `project_uuid`) VALUES (MD5(uuid()) , 'administrator', '管理员', (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_role` ( `uuid`, `code`, `desc`,  `project_uuid`) VALUES (MD5(uuid()) , 'department_administrator', '部门管理员',  (SELECT uuid FROM project_config));
INSERT INTO `wecom_sys_base_role` ( `uuid`, `code`, `desc`, `project_uuid`) VALUES (MD5(uuid()) , 'ordinary_employees', '普通员工', (SELECT uuid FROM project_config));




set session sql_mode= 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';


set @@global.sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';


-- version 0.0.2

ALTER TABLE wecom_sys_user ADD auth_status tinyint(1) DEFAULT NULL COMMENT '授权状态，0:关闭授权，1:打开授权';
UPDATE wecom_sys_user SET auth_status=1;


ALTER TABLE tenant_config ADD server_address varchar(1024) DEFAULT NULL COMMENT '服务器地址信息';
UPDATE tenant_config SET server_address='http://domain.com';


-- version 0.0.3

-- ----------------------------
-- Table structure for user_group_offline
-- ----------------------------
DROP TABLE IF EXISTS `user_group_offline`;
CREATE TABLE `user_group_offline` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `uuid`  VARCHAR(64)  NOT NULL COMMENT '人群的UUID',
   `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
   `external_user_id`   varchar(255) NOT NULL  COMMENT '客户ID',
   `member_id` varchar(32) NOT NULL  COMMENT '员工ID',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

ALTER TABLE wecom_mass_task_send_queue MODIFY external_user_ids MEDIUMTEXT DEFAULT NULL COMMENT '发送的客户id列表';
ALTER TABLE `wecom_mass_task` MODIFY content MEDIUMTEXT DEFAULT NULL COMMENT '推送消息内容';




-- version 0.0.5

INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`, `parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'cdpsettings', 'settings', 'cdpsettings', 'settings', '数据接入', '系统设置', (SELECT uuid FROM project_config));

-- ----------------------------
-- Table structure for cdp_config
-- ----------------------------
DROP TABLE IF EXISTS `cdp_config`;
CREATE TABLE `cdp_config` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `uuid` varchar(64) NOT NULL COMMENT 'UUID',
   `project_uuid` varchar(64) NOT NULL COMMENT '关联项目ID',
   `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
   `cdp_type`   varchar(64) NOT NULL  COMMENT 'cdp的类型',
    `api_url` varchar(1024) NOT NULL  COMMENT 'cdp的API请求的url',
   `data_url` varchar(1024) DEFAULT NULL  COMMENT 'cdp的数据上报的url',
   `app_key` varchar(255) DEFAULT NULL  COMMENT 'cdp的AppKey',
   `api_secret` varchar(255) DEFAULT NULL  COMMENT 'cdp的API_ACCESSKEY',
   `project_name` varchar(1024) DEFAULT NULL  COMMENT 'cdp的数据的项目名称',
   `status`     TINYINT(1) DEFAULT NULL COMMENT '是否开启类型 ,0 未开启 1 开启',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_uniq_corp_id_cdp_type` (`corp_id`,`cdp_type`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for cdp_crowd_users_sync
-- ----------------------------
DROP TABLE IF EXISTS `cdp_crowd_users_sync`;
CREATE TABLE `cdp_crowd_users_sync` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `project_uuid` varchar(64) NOT NULL COMMENT '关联项目ID',
   `task_uuid` varchar(64) NOT NULL COMMENT '任务的UUID',
   `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
   `cdp_type`   varchar(64) NOT NULL  COMMENT 'cdp的类型',
   `project_name` varchar(1024) DEFAULT NULL  COMMENT 'cdp的数据的项目名称',
   `crowd_code` varchar(512) NOT NULL  COMMENT 'cdp的人群的code',
   `crowd_name` varchar(1024) DEFAULT NULL  COMMENT 'cdp的人群的名字',
   `user_count`      bigint(20) DEFAULT NULL  COMMENT '人群的数量',
   `sync_user_count` bigint(20) DEFAULT NULL  COMMENT '同步的人群数量',
   `sync_status`  varchar(24) DEFAULT NULL COMMENT  '同步状态 ,UNSTART, SYNCING, FAILED, FINISH',
   `sync_failed_desc`  varchar(1024) DEFAULT NULL COMMENT  '同步状态失败后,记录错误信息',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_uniq_task_uuid_cdp_type_crowd_code` (`task_uuid`,`cdp_type`,`crowd_code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_group_cdp
-- ----------------------------
DROP TABLE IF EXISTS `user_group_cdp`;
CREATE TABLE `user_group_cdp` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `cdp_uuid` varchar(64) NOT NULL COMMENT 'cdp的UUID',
   `corp_id`   VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
   `cdp_type`   varchar(64) NOT NULL  COMMENT 'cdp的类型',
   `project_name` varchar(1024) DEFAULT NULL  COMMENT 'cdp的数据的项目名称',
   `crowd_code` varchar(512) NOT NULL  COMMENT 'cdp的人群的code',
   `external_user_id`   varchar(255) NOT NULL  COMMENT '客户ID',
   `member_id` varchar(32) NOT NULL  COMMENT '员工ID',
   `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
   `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_uniq_cdp_uuid_cdp_type_crowd_code` (`cdp_uuid`,`cdp_type`,`crowd_code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;


-- version 0.0.7

ALTER TABLE wecom_corp_config ADD forward_address TEXT DEFAULT NULL COMMENT '回调转发服务器地址信息';

ALTER TABLE wecom_corp_config ADD forward_customer_address TEXT DEFAULT NULL COMMENT '客户回调转发服务器地址信息';

INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`,`parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'callbacksettings', 'settings', 'callbacksettings', 'settings', '回调配置','系统设置', (SELECT uuid FROM project_config));

-- version 0.0.9

INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`,`parent_title`, `project_uuid`) VALUES  (MD5(uuid()) ,  'taskcenter', '', 'taskcenter', '', '任务中心', '', (SELECT uuid FROM project_config));

ALTER TABLE wecom_sys_base_permissions ADD sort_order bigint(20) DEFAULT NULL COMMENT '菜单的排序id';

UPDATE wecom_sys_base_permissions SET sort_order=100 WHERE code='home';
UPDATE wecom_sys_base_permissions SET sort_order=200 WHERE code='channelcode';
UPDATE wecom_sys_base_permissions SET sort_order=300 WHERE code='management';
UPDATE wecom_sys_base_permissions SET sort_order=400 WHERE code='taskcenter';
UPDATE wecom_sys_base_permissions SET sort_order=500 WHERE code='marketingplan';
UPDATE wecom_sys_base_permissions SET sort_order=600 WHERE code='settings';

UPDATE wecom_sys_base_permissions SET sort_order=610 WHERE code='membermanagement';
UPDATE wecom_sys_base_permissions SET sort_order=620 WHERE code='permissionmanagement';
UPDATE wecom_sys_base_permissions SET sort_order=630 WHERE code='callbacksettings';
UPDATE wecom_sys_base_permissions SET sort_order=640 WHERE code='cdpsettings';


UPDATE wecom_sys_base_permissions SET sort_order=510 WHERE code='masscustomer';
UPDATE wecom_sys_base_permissions SET sort_order=520 WHERE code='masscustomerbase';
UPDATE wecom_sys_base_permissions SET sort_order=530 WHERE code='sendgroupfriends';


UPDATE wecom_sys_base_permissions SET sort_order=330 WHERE code='customerlist';
UPDATE wecom_sys_base_permissions SET sort_order=340 WHERE code='customergrouplist';

-- ----------------------------
-- Table structure for wecom_task_center
-- ----------------------------
DROP TABLE IF EXISTS `wecom_task_center`;
CREATE TABLE `wecom_task_center`
(
  `id`                       INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                     VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`             VARCHAR(128) NOT NULL COMMENT '项目ID',
  `corp_id`                  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `name`                     VARCHAR(256)  DEFAULT NULL COMMENT '任务名称',
  `task_type`                VARCHAR(50)   DEFAULT NULL COMMENT '任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT',
  `schedule_type`            VARCHAR(50)   DEFAULT NULL COMMENT '发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME/周期发送 REPEAT_TIME',
  `repeat_type`              VARCHAR(50)   DEFAULT NULL COMMENT '周期类型:每天 DAY/每周 WEEK/每月 MONTH',
  `repeat_day`               VARCHAR(128)   DEFAULT NULL COMMENT '周期活动执行的日期，每天 0/每周 1-7/每月 1-31',
  `schedule_time`            datetime     DEFAULT NULL  COMMENT '计划发送时间',
  `repeat_start_time`        datetime     DEFAULT NULL  COMMENT '周期活动的开始时间',
  `repeat_end_time`          datetime     DEFAULT NULL  COMMENT '周期活动的结束时间',
  `user_group_uuid`          VARCHAR(64)   DEFAULT NULL COMMENT '关联人群预估UUID',
  `message_type`           VARCHAR(64)   DEFAULT NULL COMMENT '消息类型：【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务',
  `content`                  TEXT          DEFAULT NULL COMMENT '推送消息内容',
  `task_status`           VARCHAR(50)   DEFAULT NULL COMMENT '任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; 已结束 FINISHED; 执行失败 FAILED',
  `creator_id`               VARCHAR(64) NOT NULL COMMENT '创建人ID',
  `creator_name`            VARCHAR(50)   DEFAULT NULL COMMENT '创建人姓名',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  `plan_time`                datetime  DEFAULT NULL COMMENT '计划执行时间',
  `finish_time`              datetime  DEFAULT NULL COMMENT '任务完成时间',
  `target_type`              VARCHAR(50)   DEFAULT NULL COMMENT '目标类型:天 DAY/小时 HOUR/分钟 MINUTE',
  `target_time`              INT(11)   DEFAULT NULL COMMENT '目标时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_uuid` (`uuid`),
  UNIQUE KEY `idx_uniq_project_uuid_corp_id_type_name` (`project_uuid`,`corp_id`, `task_type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微的员工任务';

-- ----------------------------
-- Table structure for wecom_task_center_statistic_member
-- ----------------------------
DROP TABLE IF EXISTS `wecom_task_center_statistic_member`;
CREATE TABLE `wecom_task_center_statistic_member` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                     VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`         varchar(64) NOT NULL COMMENT '项目ID',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '任务uuid',
  `member_id`              VARCHAR(128) NOT NULL COMMENT '员工的id',
  `member_name`              VARCHAR(128) NOT NULL COMMENT '员工的名称',
  `status`                varchar(32) DEFAULT NULL COMMENT  '员工的任务执行状态',
  `external_user_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户总数',
  `delivered_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户送达总数',
  `non_friend_count`    bigint(20) DEFAULT NULL COMMENT '员工的客户非好友总数',
  `plan_time`              datetime  DEFAULT NULL COMMENT '计划执行时间',
  `sent_time`              datetime  DEFAULT NULL COMMENT '执行时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_task_uuid_member_id_sent_time` (`task_uuid`, `member_id`, `sent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微任务中心的员工统计';

-- ----------------------------
-- Table structure for wecom_task_center_statistic_external_user
-- ----------------------------
DROP TABLE IF EXISTS `wecom_task_center_statistic_external_user`;
CREATE TABLE `wecom_task_center_statistic_external_user` (
  `id`                   INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                     VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`         varchar(64) NOT NULL COMMENT '项目ID',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '群发的任务uuid',
  `member_id`            VARCHAR(128) NOT NULL COMMENT '员工的id',
  `external_user_id`     varchar(255) DEFAULT NULL COMMENT '客户的ID',
  `external_user_name`     varchar(255) DEFAULT NULL COMMENT '客户的名称',
  `external_user_type`   varchar(24) DEFAULT NULL COMMENT '客户的类型',
  `status`                varchar(64) DEFAULT NULL COMMENT  '客户的任务执行状态',
  `plan_time`              datetime  DEFAULT NULL COMMENT '计划执行时间',
  `receive_time`              datetime     DEFAULT NULL COMMENT '客户的执行时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微任务中心的员工对应的客户统计';

-- ----------------------------
-- Table structure for wecom_task_center_member
-- ----------------------------
DROP TABLE IF EXISTS `wecom_task_center_member`;
CREATE TABLE `wecom_task_center_member` (
  `id`                       INT(11) NOT NULL AUTO_INCREMENT,
  `uuid`                     VARCHAR(64) NOT NULL COMMENT '业务主键',
  `project_uuid`             VARCHAR(128) NOT NULL COMMENT '项目ID',
  `corp_id`                  VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
  `member_id`            VARCHAR(128) NOT NULL COMMENT '员工的id',
  `type`                 varchar(64) DEFAULT NULL COMMENT '群发任务 MASS_TASK, 任务中心 TASK_CENTER',
  `name`                     VARCHAR(256)  DEFAULT NULL COMMENT '任务名称',
  `task_uuid`            VARCHAR(64) NOT NULL COMMENT '群发的任务uuid',
  `task_type`                VARCHAR(50)   DEFAULT NULL COMMENT '任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT',
  `schedule_type`            VARCHAR(50)   DEFAULT NULL COMMENT '发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME/周期发送 REPEAT_TIME',
  `repeat_type`              VARCHAR(50)   DEFAULT NULL COMMENT '周期类型:每天 DAY/每周 WEEK/每月 MONTH',
  `repeat_day`               VARCHAR(128)   DEFAULT NULL COMMENT '周期活动执行的日期，每天 0/每周 1-7/每月 1-31',
  `schedule_time`            datetime     DEFAULT NULL  COMMENT '计划发送时间',
  `repeat_start_time`        datetime     DEFAULT NULL  COMMENT '周期活动的开始时间',
  `repeat_end_time`          datetime     DEFAULT NULL  COMMENT '周期活动的结束时间',
  `user_group_uuid`          VARCHAR(64)   DEFAULT NULL COMMENT '关联人群预估UUID',
  `message_type`           VARCHAR(64)   DEFAULT NULL COMMENT '消息类型：【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务',
  `content`                  TEXT          DEFAULT NULL COMMENT '推送消息内容',
  `task_status`           VARCHAR(50)   DEFAULT NULL COMMENT '任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; 已结束 FINISHED; 执行失败 FAILED',
  `creator_id`               VARCHAR(64) NOT NULL COMMENT '创建人ID',
  `creator_name`            VARCHAR(50)   DEFAULT NULL COMMENT '创建人姓名',
  `plan_time`              datetime  DEFAULT NULL COMMENT '提醒时间',
  `target_type`              VARCHAR(50)   DEFAULT NULL COMMENT '目标类型:天 DAY/小时 HOUR/分钟 MINUTE',
  `target_time`              INT(11)   DEFAULT NULL COMMENT '目标时间',
  `create_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='企微任务中心的员工对应的客户统计';

ALTER TABLE wecom_group_chats ADD user_count INT(11) DEFAULT NULL COMMENT '客户群的人数';

ALTER TABLE wecom_corp_config ADD cred_file_name VARCHAR(128) DEFAULT NULL COMMENT '可信文件名称';

ALTER TABLE wecom_corp_config ADD cred_file_content VARCHAR(256) DEFAULT NULL COMMENT '可信文件内容';


-- version 0.2.0
INSERT INTO `wecom_sys_corp_user_role_link` ( `corp_id`, `role_uuid`, `member_id`,  `project_uuid`) VALUES ((select uuid from tenant_config) , (SELECT uuid FROM wecom_sys_base_role where code='super_administrator'), 'admin',(select uuid from tenant_config));


-- version 2.0.0
INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`,
`parent_title`, `sort_order`) VALUES  (MD5(uuid()) ,  'customer_link', '', 'customer_link', '',
'客户引流', '', 200 );

UPDATE wecom_sys_base_permissions SET sort_order=210, parent_code='customer_link', parent_name='parent_name',
parent_title='客户引流' WHERE code='channelcode';


INSERT INTO `wecom_sys_base_permissions` ( `uuid`, `code`, `parent_code`, `name`, `parent_name`, `title`,
`parent_title`, `sort_order`) VALUES  (MD5(uuid()) ,  'group_chat_welcome', 'customer_link',
'group_chat_welcome', 'customer_link', '入群欢迎语','客户引流', 225);


-- ----------------------------
-- Table structure for wecom_welcome_msg_group_chat
-- ----------------------------
DROP TABLE IF EXISTS `wecom_welcome_msg_group_chat`;
CREATE TABLE `wecom_welcome_msg_group_chat` (
    `id`               INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uuid`             VARCHAR(64)  NOT NULL COMMENT '业务主键',
    `project_uuid`     varchar(64) NOT NULL COMMENT '关联项目ID',
    `corp_id`          VARCHAR(128) NOT NULL COMMENT '企微CORP ID',
    `name`             varchar(512) NOT NULL COMMENT '欢迎语名称',
    `notify_type`      tinyint(2)  NOT NULL COMMENT '通知类型， 1 全部；  2 部分  0 不通知',
    `members`          text DEFAULT NULL  COMMENT '群主列表',
    `creator_id`       VARCHAR(64) NOT NULL COMMENT '创建人ID',
    `creator_name`     VARCHAR(128)   NOT NULL COMMENT '创建人姓名',
    `welcome_content`  MEDIUMTEXT NOT NULL COMMENT '欢迎语内容',
    `template_id`      varchar(64) DEFAULT NULL COMMENT '欢迎语素材id',
    `create_time`      timestamp(3) NOT NULL DEFAULT current_timestamp(3) COMMENT '创建时间',
    `update_time`      timestamp(3) NOT NULL DEFAULT current_timestamp(3) ON UPDATE current_timestamp(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_uuid`),
    UNIQUE KEY `idx_uniq_uuid` (`uuid`),
    UNIQUE KEY `idx_uniq_project_uuid_corp_id_name` (`project_uuid`,`corp_id`, `name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='客户群欢迎语基本信息表';