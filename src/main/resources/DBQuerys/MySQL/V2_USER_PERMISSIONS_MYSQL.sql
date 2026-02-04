GRANT USAGE ON *.* TO 'calcUserDB'@'%';

GRANT SELECT, INSERT, UPDATE, DELETE ON calculadora_db.*
    TO 'calcUserDB'@'%';

GRANT SELECT, INSERT ON calculadora_db.CalculationHistory
    TO 'calcUserDB'@'%';

GRANT SELECT, INSERT, UPDATE ON calculadora_db.UserOperations
    TO 'calcUserDB'@'%';

GRANT EXECUTE ON PROCEDURE calculadora_db.sp_CalculateOperation
TO 'calcUserDB'@'%';

FLUSH PRIVILEGES;

SHOW GRANTS FOR 'calcUserDB'@'%';