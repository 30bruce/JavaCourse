CREATE DATABASE IF NOT EXISTS `mall`;

USE `mall`;

-- 用户表：用户id, 昵称, 密码, 手机号
CREATE TABLE `m_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `nickname` varchar(32) NOT NULL COMMENT '昵称',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `phone` char(16) NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- 商品表：商品id, 名称, 详情描述, 价格
CREATE TABLE `m_merchandises` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(32) NOT NULL COMMENT '商品名称',
  `details` varchar(512) NOT NULL COMMENT '商品详情',
  `price` float(12,10) NOT NULL COMMENT '商品价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- 订单表：订单id, 商品id, 用户id, 购买时间
CREATE TABLE `m_orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `user_id` int(11) NOT NULL COMMENT '商品ID',
  `merchandise_id` int(11) NOT NULL  COMMENT'用户ID',
  `create` bigint(14) NOT NULL COMMENT '购买时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;