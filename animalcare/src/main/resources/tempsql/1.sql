/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.7.38-log : Database - db01
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db01` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `db01`;

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `comment_id` int(5) NOT NULL AUTO_INCREMENT,
  `comment_post_id` int(5) NOT NULL,
  `comment_time` bigint(15) DEFAULT NULL,
  `comment_content` varchar(100) NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `comment_post_id` (`comment_post_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`comment_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `comment` */

insert  into `comment`(`comment_id`,`comment_post_id`,`comment_time`,`comment_content`) values 
(1,1,1,'Test for comment');

/*Table structure for table `friendlist` */

DROP TABLE IF EXISTS `friendlist`;

CREATE TABLE `friendlist` (
  `list_id` int(5) NOT NULL AUTO_INCREMENT,
  `user_id` int(5) NOT NULL,
  `friend_username` varchar(20) NOT NULL,
  PRIMARY KEY (`list_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `friendlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `friendlist` */

/*Table structure for table `image` */

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image` (
  `image_id` int(5) NOT NULL AUTO_INCREMENT,
  `image_post_id` int(5) NOT NULL,
  `image_url` varchar(50) NOT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_post_id` (`image_post_id`),
  CONSTRAINT `image_ibfk_1` FOREIGN KEY (`image_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `image` */

insert  into `image`(`image_id`,`image_post_id`,`image_url`) values 
(1,1,'741917776/1.jpg');

/*Table structure for table `pet` */

DROP TABLE IF EXISTS `pet`;

CREATE TABLE `pet` (
  `pet_id` int(5) NOT NULL AUTO_INCREMENT,
  `pet_user_id` int(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  `pet_image_address` varchar(50) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `pet_description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pet_id`),
  KEY `pet_user_id` (`pet_user_id`),
  CONSTRAINT `pet_ibfk_1` FOREIGN KEY (`pet_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `pet` */

insert  into `pet`(`pet_id`,`pet_user_id`,`name`,`pet_image_address`,`category`,`age`,`pet_description`) values 
(1,1,'UMI','catDefault.jpg','cat',3,NULL),
(2,1,'MIKO','catDefault.jpg','cat',3,NULL),
(3,2,'XIXI','dogDefault.jpg','dog',2,NULL);

/*Table structure for table `petimage` */

DROP TABLE IF EXISTS `petimage`;

CREATE TABLE `petimage` (
  `image_id` int(5) NOT NULL AUTO_INCREMENT,
  `image_pet_id` int(5) NOT NULL,
  `image_url` varchar(50) NOT NULL,
  PRIMARY KEY (`image_id`),
  KEY `image_pet_id` (`image_pet_id`),
  CONSTRAINT `petimage_ibfk_1` FOREIGN KEY (`image_pet_id`) REFERENCES `pet` (`pet_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `petimage` */

insert  into `petimage`(`image_id`,`image_pet_id`,`image_url`) values 
(1,1,'741917776/2.jpg');

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `post_id` int(5) NOT NULL AUTO_INCREMENT,
  `post_user_id` int(5) NOT NULL,
  `post_content` varchar(20) NOT NULL,
  `post_time` bigint(15) DEFAULT NULL,
  `topic` varchar(100) DEFAULT NULL,
  `tag` varchar(100) DEFAULT NULL,
  `love` int(5) DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `post_user_id` (`post_user_id`),
  FULLTEXT KEY `idx_content` (`post_content`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`post_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `post` */

insert  into `post`(`post_id`,`post_user_id`,`post_content`,`post_time`,`topic`,`tag`,`love`) values 
(1,1,'LMY YYDS',NULL,NULL,NULL,0),
(2,1,'NO',NULL,NULL,NULL,0);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(40) DEFAULT NULL,
  `email` varchar(20) DEFAULT NULL,
  `uuid` varchar(80) DEFAULT NULL,
  `user_image_address` varchar(50) DEFAULT 'default.jpg',
  `nickname` varchar(20) DEFAULT 'lazy to set name',
  `description` varchar(200) DEFAULT 'this gay is very lazy to write decription',
  `tag` varchar(20) DEFAULT 'Cat',
  `gender` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `USERNAME` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`email`,`uuid`,`user_image_address`,`nickname`,`description`,`tag`,`gender`) values 
(1,'741917776','UBL3wzfOlscd2jT0oxy9QA==',NULL,NULL,'default.jpg','lazy to set name','this gay is very lazy to write decription','Cat',1),
(2,'richard','PDLx+YKFRPT7WSobvjHdrQMb62ozycMd','67252808@qq.com','0d70eb80-603d-43b9-b7d6-8226687f0e5f','default.jpg','lazy to set name','this gay is very lazy to write decription','Cat',1);

/*Table structure for table `userlove` */

DROP TABLE IF EXISTS `userlove`;

CREATE TABLE `userlove` (
  `user_love_id` int(5) NOT NULL AUTO_INCREMENT,
  `post_id` int(5) NOT NULL,
  `user_id` int(5) NOT NULL,
  PRIMARY KEY (`user_love_id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `userlove_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `userlove_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userlove` */

/*Table structure for table `video` */

DROP TABLE IF EXISTS `video`;

CREATE TABLE `video` (
  `video_id` int(5) NOT NULL AUTO_INCREMENT,
  `video_post_id` int(5) NOT NULL,
  `video_url` varchar(50) NOT NULL,
  PRIMARY KEY (`video_id`),
  KEY `video_post_id` (`video_post_id`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`video_post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `video` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
