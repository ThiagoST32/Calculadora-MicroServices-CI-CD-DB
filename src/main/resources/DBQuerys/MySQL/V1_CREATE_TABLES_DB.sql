CREATE TABLE IF NOT EXISTS `operations` (
    `operations_id` INT NOT NULL AUTO_INCREMENT,
    `value_one` VARCHAR(255),
    `value_two` VARCHAR(255),
    `tipo_de_operacao` INT NOT NULL,
    `result` VARCHAR(255),
    `time_operation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(`id`)
);
