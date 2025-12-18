CREATE TABLE IF NOT EXISTS bootcamps (
	id int NOT NULL AUTO_INCREMENT,
	name varchar(100) NOT NULL,
	description VARCHAR(255) NOT NULL,
    launch_date DATE NULL,
    duration_weeks INT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;