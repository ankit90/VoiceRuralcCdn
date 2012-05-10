-- phpMyAdmin SQL Dump
-- version 3.3.2deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 10, 2012 at 10:12 PM
-- Server version: 5.1.41
-- PHP Version: 5.3.2-1ubuntu4.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `innobo`
--

-- --------------------------------------------------------

--
-- Table structure for table `VoiceRuralCDN`
--

CREATE TABLE IF NOT EXISTS `VoiceRuralCDN` (
  `title` mediumtext NOT NULL,
  `desc` mediumtext NOT NULL,
  `tags` mediumtext NOT NULL,
  `comments` mediumtext NOT NULL,
  `conference_time` mediumtext NOT NULL,
  `audio_comments` mediumtext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `VoiceRuralCDN`
--

INSERT INTO `VoiceRuralCDN` (`title`, `desc`, `tags`, `comments`, `conference_time`, `audio_comments`) VALUES
('iitnewroom', 'iit room', 'iit,new,room', 'ugly video', '20:31 8-5-2012', 'default'),
('iit', 'dean', 'hostel', 'default', '20:3 10-5-2012', 'default'),
('newvideo', 'video', 'video', 'default', '21:35 10-5-2012', 'default'),
('just ', 'gallbladder', 'Jojo', 'default', '21:49 10-4-2012', 'default'),
('chmaaach', 'Chalo', 'cochi', 'default', '22:6 10-5-2012', 'default');
