\du+ calcUserDB

SELECT
    grantee,
    table_schema,
    table_name,
    privilege_type,
    is_grantable
FROM information_schema.table_privileges
WHERE grantee = 'calcUserDB';

SELECT
    nspname as schema,
    relname as tabela,
    relkind as tipo,
    array_agg(privilege_type) as permissoes
FROM pg_class
    JOIN pg_namespace ON pg_namespace.oid = pg_class.relnamespace
    LEFT JOIN information_schema.table_privileges tp
    ON tp.table_schema = nspname AND tp.table_name = relname
WHERE tp.grantee = 'calcUserDB' OR tp.grantee = 'PUBLIC'
GROUP BY nspname, relname, relkind;

SELECT
    nspname as schema,
    proname as funcao,
    proargtypes as parametros,
    has_function_privilege('calcUserDB', oid, 'EXECUTE') as pode_executar
FROM pg_proc
    JOIN pg_namespace ON pg_namespace.oid = pg_proc.pronamespace;