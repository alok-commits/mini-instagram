#!/usr/bin/env bash
set -euo pipefail
base="${API_URL:-http://localhost:8080}"
auth="${KEYCLOAK_URL:-http://localhost:8180}"
echo "Checking public endpoints on $base and Keycloak at $auth"
curl --fail --silent --show-error "$base/actuator/health" >/dev/null
curl --fail --silent --show-error "$base/api/v1/posts?page=0&size=5" >/dev/null
curl --fail --silent --show-error "$auth/realms/mini-instagram/.well-known/openid-configuration" >/dev/null
echo 'PASS: gateway health, public posts and Keycloak discovery'
echo 'Protected write APIs require a real user token; see docs/TESTING.md.'
