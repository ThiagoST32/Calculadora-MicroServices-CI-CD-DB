CREATE DATABASE calc;
GO

USE calc;
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'calcUserDB')
BEGIN
    CREATE USER calcUserDB FOR LOGIN calcUserDB;
    PRINT 'Usuário calcUserDB criado.';
END

CREATE TABLE IF NOT EXISTS `OPERATIONS` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `valueOne` VARCHAR(255),
    `valueTwo` VARCHAR(255),
    `tipoDeOperacao` INT NOT NULL,
    `timeOperation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY(`id`)
);