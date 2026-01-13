-- Drop user first if they exist
DROP USER if exists 'dronepilot'@'%' ;

-- Now create user with prop privileges
CREATE USER 'dronepilot'@'%' IDENTIFIED BY 'dronepilot';

GRANT ALL PRIVILEGES ON * . * TO 'dronepilot'@'%';
