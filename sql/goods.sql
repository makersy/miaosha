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

 Date: 02/06/2019 13:00:29
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品的图片',
  `goods_detail` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详情介绍',
  `goods_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品单价',
  `goods_stock` int(255) NULL DEFAULT 0 COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, 'iphone7plus', 'Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机', '/img/iphonex.png', 'Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机', 6500.00, 10000);
INSERT INTO `goods` VALUES (2, '美的冰箱', 'Midea/美的 BCD-535WKZM(E)冰箱双开门对开门风冷无霜智能电家用', '/img/meta10.png', '送品牌烤箱，五一大促', 3758.88, 5999);

SET FOREIGN_KEY_CHECKS = 1;
