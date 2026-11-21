# Known Boundaries and Release Gates

This is a substantial source scaffold and deployment example, **not a claim of zero defects or a certified production system**.

### Verification constraints

The creation environment has Java 21 but **no Maven, Docker daemon or external Maven/NPM/Docker registry connectivity**. Source/configuration syntax may be checked offline; running the full Maven/Node compilation, container images, Keycloak realm import and end-to-end authentication is **required on your own host**. CI is included for this purpose and must be green before deployment.

### Intentional product scope

- No real image upload, processing, CDN or object storage; posts accept optional external HTTPS image URLs (tracking/privacy and availability concerns until an allowlisted media proxy is implemented).
- No stories, reels, comments, follower graph, messaging, search or notifications.
- No pagination cursor for very large feeds (currently offset pagination).
- Like rows from soft-deleted posts remain in `like_db`; counts require an active post, but a reliable orphan cleanup/outbox workflow is future work.
- No real-time event transport or Kafka; internal availability is checked through a synchronous Post Service call with timeouts/circuit breakers.
- No single sign-out token revocation in the domain services; JWTs are accepted until expiry, unless keys/issuer change. Short expiration and Keycloak session policies mitigate risk.

### Operational gaps before internet exposure

- The default `compose.yaml` runs Keycloak `start-dev` and uses plaintext `.env` for local secrets. Only use production-mode settings behind TLS, and migrate to secure secret injection.
- Compose is one-host, has no automatic rolling deployment, no multi-node failover, and default local-only volumes.
- Backups/restore and TLS certificate provisioning are documented but **not automated**; set them up and test them.
- Container versions are tagged, not digest-pinned; CI does not yet do image/signature vulnerability scans.
- No production Keycloak SMTP provisioning, external account/email anti-spam or external anti-DDoS.
- Internal Actuator metrics endpoints have no extra OAuth security because they are reachable only on private Docker networks; secure any change in network topology.
- No fully automated integration, concurrency, contract or penetration test suite beyond included unit tests/manual runbooks.
- AOP is proxy-based: self-invocation or non-Spring-managed methods are not intercepted. HTTP trace propagation requires using Boot's auto-configured RestClient builder.
- Docker Desktop / container filesystem and Fluent Bit log permissions must be validated on the target OS.

Treat `compose.prod.yaml` as a one-host **deployment example** requiring ops review, not a drop-in SLA-backed production release.
