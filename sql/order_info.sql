/*
 Navicat Premium Data Transfer

 Source Server         : 测试
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : miaosha

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 02/06/2019 13:06:10
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `goods_id` bigint(20) NULL DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint(20) NULL DEFAULT NULL COMMENT '收货地址ID',
  `goods_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` int(11) NULL DEFAULT 0 COMMENT '商品数量',
  `goods_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品单价',
  `order_channel` tinyint(4) NULL DEFAULT 0 COMMENT '订单渠道。1pc，2android，3ios',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '订单状态。0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime NULL DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` datetime NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES (12, 15211111111, 1, NULL, 'iphone7plus', 1, 0.01, 1, 0, '2019-06-01 15:01:48', NULL);

SET FOREIGN_KEY_CHECKS = 1;
