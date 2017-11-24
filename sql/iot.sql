/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50626
 Source Host           : localhost
 Source Database       : iot

 Target Server Type    : MySQL
 Target Server Version : 50626
 File Encoding         : utf-8

 Date: 11/24/2017 18:17:09 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `arduino`
-- ----------------------------
DROP TABLE IF EXISTS `arduino`;
CREATE TABLE `arduino` (
  `IP` varchar(255) NOT NULL,
  `version` varchar(255) NOT NULL,
  `A0` varchar(255) DEFAULT NULL,
  `A1` varchar(255) DEFAULT NULL,
  `A2` varchar(255) DEFAULT NULL,
  `A3` varchar(255) DEFAULT NULL,
  `A4` varchar(255) DEFAULT NULL,
  `A5` varchar(255) DEFAULT NULL,
  `D2` varchar(255) DEFAULT NULL,
  `D3` varchar(255) DEFAULT NULL,
  `D4` varchar(255) DEFAULT NULL,
  `D5` varchar(255) DEFAULT NULL,
  `D6` varchar(255) DEFAULT NULL,
  `D7` varchar(255) DEFAULT NULL,
  `D8` varchar(255) DEFAULT NULL,
  `D9` varchar(255) DEFAULT NULL,
  `D10` varchar(255) DEFAULT NULL,
  `D11` varchar(255) DEFAULT NULL,
  `D12` varchar(255) DEFAULT NULL,
  `D13` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`IP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sensor_data`
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data`;
CREATE TABLE `sensor_data` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `boardIP` varchar(255) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
