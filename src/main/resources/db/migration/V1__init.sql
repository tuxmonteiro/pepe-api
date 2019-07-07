SET autocommit=0;
START TRANSACTION;

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `id` int(11) UNSIGNED AUTO_INCREMENT NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `last_modified_by` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `jar` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_driver_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `connection`
--

CREATE TABLE `connection` (
  `id` int(11) UNSIGNED AUTO_INCREMENT NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `last_modified_by` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `driver_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_connection_name` (`name`),
  KEY `FK_connection_driver` (`driver_id`),
  CONSTRAINT `FK_connection_driver` FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `id` int(11) UNSIGNED AUTO_INCREMENT NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `last_modified_by` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_project_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `metric`
--

CREATE TABLE `metric` (
  `id` int(11) UNSIGNED AUTO_INCREMENT NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `last_modified_by` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `query` varchar(255) NOT NULL,
  `connection_id` int(11) UNSIGNED NOT NULL,
  `project_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_metric_name` (`name`),
  KEY `FK_metric_connection` (`connection_id`),
  KEY `FK_metric_project` (`project_id`),
  CONSTRAINT `FK_metric_connection` FOREIGN KEY (`connection_id`) REFERENCES `connection` (`id`),
  CONSTRAINT `FK_metric_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

COMMIT;
SET autocommit=1;