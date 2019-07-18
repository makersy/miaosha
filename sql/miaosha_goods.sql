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

 Date: 02/06/2019 13:04:31
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for miaosha_goods
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` bigint(20) NULL DEFAULT NULL COMMENT '商品id',
  `miaosha_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价',
  `stock_count` int(11) NULL DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime NULL DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime NULL DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
INSERT INTO `miaosha_goods` VALUES (1, 1, 0.01, 3, '2019-06-01 12:24:01', '2019-06-01 16:16:50');
INSERT INTO `miaosha_goods` VALUES (2, 2, 0.01, 9, '2017-11-12 10:10:20', '2017-11-12 10:10:29');

SET FOREIGN_KEY_CHECKS = 1;
