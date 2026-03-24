CREATE TABLE IF NOT EXISTS `operations` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `valueOne` VARCHAR(255),
    `valueTwo` VARCHAR(255),
    `tipoDeOperacao` INT NOT NULL,
    `result` VARCHAR(255),
    `timeOperation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(`id`)
);
