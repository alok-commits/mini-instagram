# Validation and Testing Plan

## Checks included

- Offline XML/YAML/JSON and endpoint consistency: `python3 scripts/verify_project.py` (requires PyYAML)
- Unit tests with JUnit/Mockito: `mvn -B -ntp verify`
- Public smoke test: `bash scripts/smoke-test.sh`
- Browser TypeScript type check and bundle: `cd frontend && npm install && npm run build`
- Docker Compose file expansion: run `python3 scripts/bootstrap.py && docker compose config -q`
- GitHub Actions runs Maven verification, frontend TypeScript compilation and static config validation.

### Manual integration verification (required after Docker startup)

1. Open `http://localhost:5173`, use **Register**, create a test user, then sign in.
2. Confirm `GET /users/me` provisions a profile; PATCH to change its display name/bio.
3. Create a post; confirm response 201 and `Location` header; list and retrieve by UUID.
4. Edit your post and delete it; verify a deleted post returns 404.
5. Sign in as a second test user; try to edit/delete the first user's post: 403.
6. Like a post twice: both requests should return 204 and count should remain 1 for one user.
7. Unlike twice: both return 204; count is 0.
8. Try invalid, expired, wrong-issuer and wrong-audience JWTs: 401.
9. Access `/internal/posts/<uuid>/exists` without a service token or through the gateway: it must not return successful protected data.
10. Burst requests to public posts and authenticated mutation APIs; verify HTTP 429 and test that a second subject has an independent quota.
11. Stop Post Service (`docker compose stop post-service`); verify Like Service returns 503 or a bounded error instead of hanging; restart it.
12. Check Prometheus targets, then use Grafana Explore to correlate a Like Service trace with its Post Service dependency and Loki logs.
13. Stop Redis; verify gateway does **not** silently bypass rate limiting. Restart Redis.
14. Rebuild and restart services with persistent volumes; ensure posts/profiles/likes survive.
15. Test DB backup restore on an isolated test volume, never by overwriting a live volume.

## Security boundaries to test

- Client passes a spoofed authorId/userId in JSON or headers: server must ignore it.
- Anonymous requests to user and mutation endpoints: 401.
- JWT for an unrelated audience: 401.
- User access token at internal existence endpoint: 403.
- Unauthorized end-user cannot reach other service databases via public ports.
- Token, password and credential values do not appear in application JSON logs.

## Observability checks

- Grafana: `http://localhost:3000`; generated admin credentials in `.env`.
- Prometheus `up` metric for all four Java applications should equal 1.
- Tempo receives spans from the OTLP Collector over its private Docker network.
- Loki query `{job="mini-instagram"} | json` reveals service names in log fields.
- Metrics are scraped every 15 seconds, with a 7-day local retention example.

## Load and failure targets (not verified by this archive)

Establish SLOs before declaring production readiness: p95 latency, sustained RPS, request error budget and DB storage/connection headroom. Use k6 or Gatling to measure and tune gateway limits, Hikari pools, heap percentages and service timeouts. Include cold-start and failure-mode benchmarks.
