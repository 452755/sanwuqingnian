/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : localhost:3306
 Source Schema         : 当归

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 18/05/2022 18:11:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `userName` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `userPwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `username.UNIQUE`(`userName`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (6, '4585', '569748');
INSERT INTO `users` VALUES (7, '4548885', '569748');
INSERT INTO `users` VALUES (8, '454888485', '569748');
INSERT INTO `users` VALUES (9, 'ggbgg', 'kjdghskfh');
INSERT INTO `users` VALUES (10, 'ggb很快就噶还来得及给客户靠的就是g', 'kjdghskfh');

SET FOREIGN_KEY_CHECKS = 1;
