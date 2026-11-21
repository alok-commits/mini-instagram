# Security Design

## Threat model and boundaries

1. Browser -> Keycloak: Authorization Code flow + S256 PKCE, never raw passwords through application services.
2. Browser -> Gateway: access token in `Authorization` header; token must be valid and contain `aud=instagram-api`.
3. Gateway -> services: routed token; each service **independently** checks signature (JWKS), `iss`, `aud` and time validity.
4. Like Service -> Post Service: distinct confidential `like-service` client credentials; Post Service checks verified token `azp=like-service`. The route is not exposed via the gateway.
5. Services -> PostgreSQL: unique service-specific DB users; no interservice foreign keys or direct SQL access to other services' tables.
6. Operators -> Grafana/Keycloak: private or VPN-controlled access. Docker publishes Grafana and Keycloak only on loopback in the local setup.

## Authentication

The Keycloak realm template uses a public `instagram-web` OIDC client, code flow, required PKCE S256, short-lived access tokens, and a client credentials service account. The mapper adds `instagram-api` to access-token audiences. The `USER` and `ADMIN` realm roles exist, but **the mini app does not use role-based admin endpoints**; user ownership checks are authoritative.

The Keycloak realm is initialized **once**. Updating `realm.template.json` after initial import does not update the running realm automatically. Keep secrets out of Git. `.env` is generated from cryptographically strong random bytes; it remains a local development mechanism. In mature deployments, prefer an external secrets manager / Docker secrets or Kubernetes secrets integrated with access controls.

## Authorizations

- `/users/me`: requires JWT, derives user from `sub`.
- Post creation: JWT `sub` becomes immutable authorId; never accept authorId from JSON.
- Post updates/deletion: authorId must equal validated `sub` (HTTP 403 otherwise).
- Like/unlike: JWT `sub` is the user ID and uniqueness constraint prevents duplicates.
- Internal post lookup: requires validated `azp=like-service` service token (and audience).
- Public post/count endpoints remain anonymous; ensure future private-account features revisit every public query and response DTO.

## Rate limits

Redis-backed token buckets in Gateway are keyed by validated JWT subject or remote IP (in production, a protected `X-Client-IP` header overwritten by the trusted Nginx proxy). Do not trust arbitrary forwarded IP headers from internet clients. On Redis outage the limiter rejects requests/errors instead of silently allowing all traffic; configure monitoring and define an operational fallback before availability-critical use.

Nginx production template adds IP token endpoint rate limits, and Keycloak has brute-force protection. Both are defense in depth, not DDoS protection; a provider-level WAF/CDN and anti-abuse monitoring may still be needed.

## Sensitive logging

Do not log authorization headers, cookies, password reset codes, profile content, JWTs, uploaded media or full request/response bodies. The AOP aspect logs operation and outcome and duration only. JSON Logback output carries tracing IDs in MDC. Explicit app/user identifiers in logs must be justified and retained only as needed. Validate sinks and exporters for accidental data exposure.

## Before production

- Enable HTTPS with real certificates and strong DNS records for web, API and auth origins.
- Configure Keycloak SMTP for email verification; setup of emails and anti-spam is not automated.
- Move secrets out of plaintext `.env` where feasible, rotate exposed credentials and restrict Keycloak Admin API/UI to private management access.
- Use off-host backups, encrypted storage, tested restores, retention/deletion policies, resource quotas and active monitoring.
- Add automated dependency and container vulnerability scanning in CI/CD and pin container images by digest.
- Perform API authorization, injection, CORS, CSRF token flow, JWT forgery, abuse/rate-limit and browser security tests.
- Review shared domain requirements and consent/privacy law before processing real user data.

The generated Keycloak realm contains a confidential-client secret and is kept mode `0600` on the host. A one-shot init container copies it to an internal Docker volume for the non-root Keycloak process; never publish or distribute the generated file.
