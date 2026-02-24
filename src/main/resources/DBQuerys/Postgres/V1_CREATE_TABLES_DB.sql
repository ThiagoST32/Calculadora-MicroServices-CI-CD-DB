CREATE DATABASE calc;

\c calc

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'calcUserDB') THEN
        CREATE USER calcUserDB WITH PASSWORD 'senha_segura';
END IF;
END
$$;

CREATE TABLE IF NOT EXISTS `OPERATIONS` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `valueOne` VARCHAR(255),
    `valueTwo` VARCHAR(255),
    `tipoDeOperacao` INT NOT NULL,
    `timeOperation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(`id`)
);

