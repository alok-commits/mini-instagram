# Deployment Guide

## Local Docker development

1. Run `python3 scripts/bootstrap.py` (generates `.env`, `frontend/.env.local`, Keycloak realm). Do not commit these files.
2. Run `docker compose up --build -d` from repo root. Docker builds use Maven internally; your host only needs Docker.
3. Inspect `docker compose ps` and log output. Keycloak realm import occurs only when the realm is initially absent.
4. Run `npm install && npm run dev` from `frontend`. Register a user through Keycloak UI, not by POSTing credentials to a Spring API.
5. Check the gateway health `http://localhost:8080/actuator/health`, posts `http://localhost:8080/api/v1/posts`, Grafana `http://localhost:3000` and Keycloak realm discovery `http://localhost:8180/realms/mini-instagram/.well-known/openid-configuration`.

### Port and network model

Host-exposed local endpoints bind to `127.0.0.1`: gateway 8080, Keycloak 8180, Grafana 3000. PostgreSQL/Redis/OTel/Tempo/Loki/service HTTP ports are not published to the host. Services use Compose DNS hostnames. Only Nginx (production overlay) publishes public 80/443.

### Troubleshooting

- Empty `.env`? Run bootstrap. Existing passwords in a populated PostgreSQL volume do **not** change if you edit `.env`; use a proper DB password rotation procedure or an isolated dev reset.
- No registration? Check Keycloak `mini-instagram` realm import and `registrationAllowed`; `docker compose logs keycloak`.
- JWT `invalid issuer`? `JWT_ISSUER` MUST exactly match `iss` in token; Keycloak public hostname must match browser origin. Internal JWKS URL may differ.
- JWT `invalid audience`? Confirm the Keycloak client audience mapper for both `instagram-web` and `like-service` and review the access token's `aud` claim.
- Frontend blocked by CORS? `PUBLIC_WEB_ORIGIN` must exactly match browser origin, including scheme and port; rerender realm and update the running Keycloak client configuration.
- Database migration failure? Check Flyway version/checksum and permissions; never modify an already-applied migration.
- No logs in Grafana? Check `fluent-bit` logs and mounted volume permissions; Logback writes `/app/logs/<service>.json`.
- No traces? Check OTLP Collector logs, receiver/exporter network, sampling and Boot instrumentation.
- Compose version too old? Install Docker Compose V2 with `depends_on.condition: service_completed_successfully` support.

## Production HTTPS single-host example

**Do not execute this against personal production data until security and restore tests have passed.**

1. Provision DNS A/AAAA records `api.example.com`, `auth.example.com`, `app.example.com` (or replace with your domains).
2. Modify `.env` keys `KEYCLOAK_PUBLIC_URL=https://auth.example.com`, `PUBLIC_WEB_ORIGIN=https://app.example.com`, `PUBLIC_API_URL=https://api.example.com`, `PUBLIC_API_HOST=api.example.com`, `PUBLIC_AUTH_HOST=auth.example.com`, `PUBLIC_WEB_HOST=app.example.com`.
3. Run bootstrap again to render the realm and frontend vars. For an **existing** Keycloak realm, update the client redirect URIs, origins, and Keycloak hostname through supported admin tools; a new import does not overwrite an existing realm.
4. Install a valid certificate and private key, readable by the Nginx process, to `infrastructure/nginx/certs/fullchain.pem` and `infrastructure/nginx/certs/privkey.pem`. Ensure certificate covers all three hostnames (or use separate certificates/servers).
5. Configure Keycloak SMTP for email verification and restrict admin access to an out-of-band secure management path. The public Nginx template returns 403 on `/admin/`.
6. Restrict firewall to 80/443 (and controlled SSH/VPN) inbound, protect Docker host, configure backups/offsite copies, retention rules and alert destinations.
7. Run `docker compose -f compose.yaml -f compose.prod.yaml up --build -d`.
8. Test HTTPS redirects, OIDC well-known discovery, JWT issuer/audience, browser code flow and API permissions. Verify Loki/Prometheus/Tempo and run load tests before external user rollout.

**Production notes:** This overlay does not provide autoscaling/HA, managed DB, long-term secrets management, certificates renewal, WAF, automatic migrations/rollbacks, high-volume log storage or automated disaster recovery. All require real operational decisions. TLS termination is Nginx; Keycloak trusts `X-Forwarded-*` supplied by this private edge. The proxy overwrites the client-IP header used for IP quotas. Restrict the gateway container port to loopback only.

## Backup (illustrative manual command)

Create restricted off-host **encrypted** backups of each owned DB including Keycloak, and verify restores periodically. For local dev:

```bash
# Replace SERVICE with one of user_db, post_db, like_db, keycloak_db.
docker compose exec -T postgres pg_dump -U postgres -Fc user_db > user_db.dump
```

On a real environment, coordinate a consistent backup strategy, scheduling, encryption, retention and restoration. Database backups on the same host do not protect against host loss.

## Release process

- CI Maven verify + frontend TypeScript build + configuration checks are mandatory.
- Pin image digests and scan build artifacts before deployment.
- Apply forward-only Flyway migrations; introduce backward-compatible schema changes when doing rolling or staged deployment.
- Deploy to staging using production configuration, run API contract/security/load tests and rehearse rollback.
- Monitor rate limits, p95/p99, error rate, JVM memory, DB pools and backup success.
