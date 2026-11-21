# Verification Record — 2026-11-19

## Automated checks performed during packaging

- Python static checker validates parsable Maven POM XML, YAML/JSON files and core path conventions.
- Confirms each Spring service has an application YAML and JSON logging configuration.
- Confirms exactly ten public `@*Mapping` methods, grouped into six rate-limited gateway routes.
- Confirms both Keycloak OIDC clients receive the `instagram-api` audience mapper.
- Confirms the local bootstrap generates a syntactically valid realm with distinct cryptographic credentials.
- Confirms bundled YAML, compose service references, Flyway SQL assets and full OpenAPI specification are present.
- Confirms ZIP excludes `.env`, generated Keycloak secrets, `.env.local`, Docker images, dependency caches, and build outputs.

## Checks **not** performed in packaging environment

There is no network access to Maven Central, NPM or container registries and the Docker executable is unavailable. Therefore `mvn verify`, `npm run build`, `docker compose config`, pulling images, Keycloak import, database migrations, OAuth browser flow, Prometheus/OTLP ingestion and end-to-end API checks **have not** executed here.

The GitHub Actions workflow and `docs/TESTING.md` describe those required checks. Passing the offline static checker is not equivalent to a successful compilation or deployment. Do not assert the system is production-ready until those checks are green and security/operations release gates are satisfied.
