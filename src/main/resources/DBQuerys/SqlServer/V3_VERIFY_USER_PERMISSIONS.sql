SELECT
    dp.state_desc + ' ' + dp.permission_name + ' ON ' +
    SCHEMA_NAME(o.schema_id) + '.' + o.name +
    ' TO ' + prin.name AS PermissionStatement
FROM sys.database_permissions dp
         JOIN sys.objects o ON dp.major_id = o.object_id
         JOIN sys.database_principals prin ON dp.grantee_principal_id = prin.principal_id
WHERE prin.name = 'calcUserDB'
UNION ALL
SELECT
    dp.state_desc + ' ' + dp.permission_name +
    ' TO ' + prin.name AS PermissionStatement
FROM sys.database_permissions dp
         JOIN sys.database_principals prin ON dp.grantee_principal_id = prin.principal_id
WHERE prin.name = 'calcUserDB' AND dp.class_desc = 'DATABASE';