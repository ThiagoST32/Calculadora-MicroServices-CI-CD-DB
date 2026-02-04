SHOW GRANTS FOR CURRENT_USER();

SELECT
    GRANTEE as usuario,
    TABLE_SCHEMA as banco_dados,
    TABLE_NAME as tabela,
    PRIVILEGE_TYPE as permissao,
    IS_GRANTABLE as pode_conceder
FROM information_schema.TABLE_PRIVILEGES
WHERE GRANTEE = CONCAT("'", 'calcUserDB', "'@'", '%', "'")
ORDER BY TABLE_SCHEMA, TABLE_NAME;

SELECT * FROM mysql.user WHERE User = 'calcUserDB';

SELECT
    ROUTINE_SCHEMA,
    ROUTINE_NAME,
    PRIVILEGE_TYPE
FROM information_schema.ROUTINE_PRIVILEGES
WHERE GRANTEE = CONCAT("'", 'calcUserDB', "'@'", '%', "'");