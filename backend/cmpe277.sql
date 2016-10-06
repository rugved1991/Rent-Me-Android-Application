-- MySQL dump 10.13  Distrib 5.5.49, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: cmpe277
-- ------------------------------------------------------
-- Server version	5.5.49-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `favourites`
--

DROP TABLE IF EXISTS `favourites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favourites` (
  `mEmail` char(100) NOT NULL,
  `posts` char(200) DEFAULT NULL,
  PRIMARY KEY (`mEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favourites`
--

LOCK TABLES `favourites` WRITE;
/*!40000 ALTER TABLE `favourites` DISABLE KEYS */;
INSERT INTO `favourites` VALUES ('joji.kubota@sjsu.edu',''),('nikhil.nadagouda@sjsu.edu','test1'),('vishwas.mukund@sjsu.edu','san jose apt');
/*!40000 ALTER TABLE `favourites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifySearch`
--

DROP TABLE IF EXISTS `notifySearch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifySearch` (
  `mEmail` varchar(100) NOT NULL,
  `instance_id` varchar(1000) NOT NULL,
  `posts` char(200) DEFAULT NULL,
  `frequency` varchar(20) DEFAULT 'daily',
  `lastupdate` date DEFAULT NULL,
  PRIMARY KEY (`mEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifySearch`
--

LOCK TABLES `notifySearch` WRITE;
/*!40000 ALTER TABLE `notifySearch` DISABLE KEYS */;
INSERT INTO `notifySearch` VALUES ('nikhil.nadagouda@sjsu.edu','jasdgfdhsgvbslkfhbd bisyvgkjhsag dvrsfbusydhlibgsfybyngifdhgidfnhig','place,santa cruz,apt,3000-5000;epic,san,apt,1500-5000','daily','2016-05-15');
/*!40000 ALTER TABLE `notifySearch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rent`
--

DROP TABLE IF EXISTS `rent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rent` (
  `mPostId` varchar(50) NOT NULL,
  `mStreet` varchar(50) NOT NULL,
  `mCity` varchar(50) NOT NULL,
  `mState` varchar(20) NOT NULL,
  `mZip` varchar(10) NOT NULL,
  `mType` varchar(10) NOT NULL,
  `mRooms` varchar(5) NOT NULL,
  `mBaths` varchar(5) NOT NULL,
  `mSqft` varchar(10) NOT NULL,
  `mRent` varchar(10) NOT NULL,
  `mPhone` varchar(15) NOT NULL,
  `mEmail` varchar(100) NOT NULL,
  `mDescription` varchar(300) NOT NULL,
  `mCounter` int(11) NOT NULL,
  `mStatus` varchar(30) NOT NULL,
  PRIMARY KEY (`mPostId`,`mEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rent`
--

LOCK TABLES `rent` WRITE;
/*!40000 ALTER TABLE `rent` DISABLE KEYS */;
INSERT INTO `rent` VALUES ('hii me','817 N 10th st','san jose','CA','95112','condo','3555','2','1500','3000','6692489974','vishwas.mukund@sjsu.','this place is epic',4,'for rent'),('Place avaialable','817 N 10th st','san jose','CA','95112','condo','3555','2','1500','3000','6692489974','vishwas.mukund@sjsu.','this place is epic',2,'for rent'),('san jose apt','817 N 10th st','san jose','CA','95112','condo','3555','2','1500','3000','6692489974','vishwas.mukund@sjsu.','this place is epic',4,'for rent'),('test','817 N 10th st','san jose','CA','95112','condo','3555','2','1500','3000','6692489974','vishwas.mukund@sjsu.edu','this place is epic',1,'cancelled'),('test me ','817 N 10th st','san jose','CA','95112','condo','3555','2','1500','3000','6692489974','vishwas.mukund@sjsu.edu','this place is epic',1,'cancelled'),('test1','817 N 10th st','san Francisco','CA','95112','apt','32','2','1500','5000','6692489975','vishwas.mukund@sjsu.edu','',1,'for rent');
/*!40000 ALTER TABLE `rent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `savedsearch`
--

DROP TABLE IF EXISTS `savedsearch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `savedsearch` (
  `mEmail` varchar(100) NOT NULL,
  `Ssearch` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`mEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `savedsearch`
--

LOCK TABLES `savedsearch` WRITE;
/*!40000 ALTER TABLE `savedsearch` DISABLE KEYS */;
INSERT INTO `savedsearch` VALUES ('joji.kubota@sjsu.edu','foo,san,foo,foo'),('vishwas.mukund@sjsu.edu','place,santa cruz,apt,3000-5000;epic,san,apt,3000-5000');
/*!40000 ALTER TABLE `savedsearch` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-15 21:58:13
