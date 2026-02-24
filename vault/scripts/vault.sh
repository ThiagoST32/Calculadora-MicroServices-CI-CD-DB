#!/bin/sh
set -e

# Run the original Vault entrypoint
/usr/local/bin/docker-entrypoint.sh "$@" &

# Wait for Vault to be available
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=vault-root-token

until vault status >/dev/null 2>&1; do
  sleep 1
done

# Inject secrets (example: KV secrets)
vault kv put secret/calcDbSecrets/calc mysql.username=Thiago mysql.password=123456 mysql.url=http://localhost:3306
# Keep container alive
wait $!