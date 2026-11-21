#!/bin/bash
set -euo pipefail
# Compose bootstrap creates url-safe secrets. This executes ONLY on first DB-volume initialization.
for name in keycloak user post like; do
 case "$name" in
  keycloak) username=keycloak_app; pass="$KEYCLOAK_DB_PASSWORD";;
  user) username=user_app; pass="$USER_DB_PASSWORD";;
  post) username=post_app; pass="$POST_DB_PASSWORD";;
  like) username=like_app; pass="$LIKE_DB_PASSWORD";;
 esac
 if [[ ! "$pass" =~ ^[a-zA-Z0-9_-]+$ ]]; then
    echo "Invalid password characters for $name; use bootstrap-generated secrets" >&2; exit 1
 fi
 psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres -c "CREATE USER $username WITH ENCRYPTED PASSWORD '$pass'"
 psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres -c "CREATE DATABASE ${name}_db OWNER $username"
done
