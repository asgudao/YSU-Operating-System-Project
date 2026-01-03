/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80021 (8.0.21)
 Source Host           : localhost:3306
 Source Schema         : pagingsystem

 Target Server Type    : MySQL
 Target Server Version : 80021 (8.0.21)
 File Encoding         : 65001

 Date: 03/01/2026 12:58:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for page
-- ----------------------------
DROP TABLE IF EXISTS `page`;
CREATE TABLE `page`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `t_id` int NOT NULL,
  `FIFO_time` int NOT NULL,
  `FIFO_losepage` int NOT NULL,
  `LRU_time` int NOT NULL,
  `LRU-losepage` int NOT NULL,
  `LFU_time` int NOT NULL,
  `LFU-losepage` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `t_id`(`t_id` ASC) USING BTREE,
  CONSTRAINT `t_id` FOREIGN KEY (`t_id`) REFERENCES `test_num` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of page
-- ----------------------------

-- ----------------------------
-- Table structure for test_num
-- ----------------------------
DROP TABLE IF EXISTS `test_num`;
CREATE TABLE `test_num`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `page_num` int NOT NULL,
  `use_TLB` int NOT NULL,
  `TLB_num` int NOT NULL,
  `visit_memory` int NOT NULL DEFAULT 100,
  `visit_TLB` int NOT NULL DEFAULT 10,
  `handle_losepage` int NOT NULL DEFAULT 2000,
  `input_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `output_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_num
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
