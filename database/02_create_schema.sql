-- MySQL Workbench Forward Engineering

-- Set variables
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- Create schema
CREATE SCHEMA IF NOT EXISTS `fleet`;
USE `fleet`;

-- Table `drone_load`
DROP TABLE IF EXISTS `drone_load`;

CREATE TABLE IF NOT EXISTS `drone_load` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `weight` DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK (`weight` > 0),
  `status` ENUM('PENDING', 'ASSIGNED', 'REJECTED', 'DELIVERED') NOT NULL DEFAULT 'PENDING',
  `message` VARCHAR(255) NULL, 
  PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- Create the drone_model table
DROP TABLE IF EXISTS `drone_model`;

CREATE TABLE IF NOT EXISTS `fleet`.`drone_model` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `model` VARCHAR(50) NOT NULL,
  `max_weight` DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `model_UNIQUE` ON `fleet`.`drone_model` (`model` ASC) VISIBLE;


-- Insert data into the drone_model table
INSERT INTO `drone_model` (`model`, `max_weight`)
VALUES
  ('LightWeight', 200),
  ('MiddleWeight', 300),
  ('CruiserWeight', 400),
  ('HeavyWeight', 500);

-- Table `drone`
DROP TABLE IF EXISTS `drone`;

CREATE TABLE IF NOT EXISTS `fleet`.`drone` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `serial_number` VARCHAR(100) NOT NULL,
  `battery_capacity` INT NOT NULL DEFAULT 0 CHECK (`battery_capacity` >= 0 AND `battery_capacity` <= 100),
  `status` ENUM('IDLE', 'LOADING', 'LOADED', 'DELIVERING', 'DELIVERED', 'RETURNING') NOT NULL DEFAULT 'IDLE',
  `drone_model_id` INT NOT NULL,
  `load_id` INT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_Drone_Drone_Model`
    FOREIGN KEY (`drone_model_id`)
    REFERENCES `fleet`.`drone_model` (`id`),
  CONSTRAINT `fk_Drone_Load`
    FOREIGN KEY (`load_id`)
    REFERENCES `fleet`.`drone_load` (`id`)
) ENGINE = InnoDB;

CREATE INDEX `fk_Drone_Drone_Model_idx` ON `fleet`.`drone` (`drone_model_id` ASC) VISIBLE;
CREATE UNIQUE INDEX `serial_number_UNIQUE` ON `fleet`.`drone` (`serial_number` ASC) VISIBLE;
CREATE INDEX `fk_Drone_Load_idx` ON `fleet`.`drone` (`load_id` ASC) VISIBLE;


-- Table `medication`
DROP TABLE IF EXISTS `medication`;

CREATE TABLE IF NOT EXISTS `medication` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(50) NOT NULL DEFAULT '' CHECK (`code` REGEXP '^[A-Z0-9_]+$'),
  `name` VARCHAR(100) NOT NULL DEFAULT '' CHECK (`name` REGEXP '^[A-Za-z0-9_-]+$'),
  `weight` DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK (`weight` > 0),
  `image_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE UNIQUE INDEX `code_UNIQUE` ON `fleet`.`medication` (`code` ASC) VISIBLE;

-- Table `battery_history`
DROP TABLE IF EXISTS `battery_history`;

CREATE TABLE IF NOT EXISTS `battery_history` (
  `drone_id` INT NOT NULL,
  `recorded_at` TIMESTAMP(3) NOT NULL,
  `remaining_capacity` INT NOT NULL DEFAULT 0 CHECK (`remaining_capacity` >= 0 AND `remaining_capacity` <= 100),
  PRIMARY KEY (`drone_id`, `recorded_at`),
  CONSTRAINT `fk_Battery_History_Drone`
    FOREIGN KEY (`drone_id`)
    REFERENCES `fleet`.`drone` (`id`)
) ENGINE = InnoDB;

CREATE INDEX `fk_Battery_History_Drone_idx` ON `fleet`.`battery_history` (`drone_id` ASC) VISIBLE;

-- Table `drone_load_has_medication`
DROP TABLE IF EXISTS `drone_load_has_medication`;

CREATE TABLE IF NOT EXISTS `drone_load_has_medication` (
  `load_id` INT NOT NULL,
  `medication_id` INT NOT NULL,
  PRIMARY KEY (`load_id`, `medication_id`),
  CONSTRAINT `fk_Load_has_Medication_Load_id`
    FOREIGN KEY (`load_id`)
    REFERENCES `fleet`.`drone_load` (`id`),
  CONSTRAINT `fk_Load_has_Medication_Medication_id`
    FOREIGN KEY (`medication_id`)
    REFERENCES `fleet`.`medication` (`id`)
) ENGINE = InnoDB;

CREATE INDEX `fk_Load_has_Medication_Medication_id_idx` ON `fleet`.`drone_load_has_medication` (`medication_id` ASC) VISIBLE;
CREATE INDEX `fk_Load_has_Medication_Load_id_idx` ON `fleet`.`drone_load_has_medication` (`load_id` ASC) VISIBLE;

-- Reset variables
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
