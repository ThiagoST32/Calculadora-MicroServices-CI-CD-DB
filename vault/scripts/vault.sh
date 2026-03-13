#!/bin/sh
set -e

/usr/local/bin/docker-entrypoint.sh "$@" &

# Wait for Vault to be available
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=${VAULT_ROOT_TOKEN_ID}

until vault status >/dev/null 2>&1; do
  sleep 1
done

echo "✅ Vault is ready, writing secrets..."

vault kv put secret/calcDbSecrets \
  spring.datasource.url=jdbc:mysql://mysql-calc:3306/calc \
  spring.datasource.username=calcUserDB \
  spring.datasource.password=123456

echo "✅ Vault secrets configured successfully!"

wait $!